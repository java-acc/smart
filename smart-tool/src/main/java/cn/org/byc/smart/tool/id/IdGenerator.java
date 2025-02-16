/*
 * Copyright 2025 Ken(kan.zhang-cn@hotmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.org.byc.smart.tool.id;

/**
 * 基于雪花算法的分布式ID生成器实现类
 * 
 * <p>雪花算法（Snowflake）是由Twitter开源的分布式ID生成算法，结构如下：
 * <pre>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * |   |------------------------------------------|   |-----|  |-----| |------------|
 * |                     时间戳                      |   数据中心ID   机器ID     序列号
 * 符号位
 * </pre>
 * 
 * <p>各部分说明：
 * <ul>
 *     <li>1位符号位：始终为0，表示正数</li>
 *     <li>41位时间戳：精确到毫秒，可使用69年</li>
 *     <li>5位数据中心ID：支持32个数据中心</li>
 *     <li>5位机器ID：每个数据中心支持32个机器</li>
 *     <li>12位序列号：每毫秒内可生成4096个ID</li>
 * </ul>
 *
 * <p>优点：
 * <ul>
 *     <li>毫秒数在高位，自增序列在低位，整个ID趋势递增</li>
 *     <li>不依赖数据库等第三方系统，以服务的方式部署，稳定性更高</li>
 *     <li>可以根据数据中心ID和机器ID来部署多个内部服务</li>
 * </ul>
 *
 * @author Ken
 */
public class IdGenerator {
    /**
     * 开始时间截 (2025-01-01)
     * 用作时间基准，可以使用69年
     */
    private final long twitterEpoch = 1733716865881L;
    /**
     * 机器ID所占的位数：5位
     * 支持最大机器数：2^5 = 32
     */
    private final long workerIdBits = 5L;

    /**
     * 数据中心ID所占的位数：5位
     * 支持最大数据中心数：2^5 = 32
     */
    private final long datacenterIdBits = 5L;

    /**
     * 支持的最大机器ID：31
     * 用位运算计算最大值，结果是31（11111）
     */
    private final long maxWorkerId = ~(-1L << this.workerIdBits);

    /**
     * 支持的最大数据中心ID：31
     * 用位运算计算最大值，结果是31（11111）
     */
    private final long maxDatacenterId = ~(-1L << this.datacenterIdBits);

    /**
     * 序列号所占的位数：12位
     * 每毫秒内可生成的ID数：2^12 = 4096
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID的偏移量：12位
     * 即序列号的位数
     */
    private final long workerIdShift = this.sequenceBits;

    /**
     * 数据中心ID的偏移量：17位
     * 即序列号的位数 + 机器ID的位数
     */
    private final long datacenterIdShift = this.sequenceBits + this.workerIdBits;

    /**
     * 时间戳的偏移量：22位
     * 即序列号的位数 + 机器ID的位数 + 数据中心ID的位数
     */
    private final long timestampLeftShift = this.sequenceBits + this.workerIdBits
            + this.datacenterIdBits;

    /**
     * 生成序列的掩码：4095
     * 用于序列号的与运算，保证序列号不会超出范围
     * 二进制表示为：111111111111
     */
    private final long sequenceMask = ~(-1L << this.sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;

    /**
     * 毫秒内序列(0~4095)
     * 用于在同一毫秒内生成不同的ID
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间戳
     * 用于判断是否需要生成新的序列号
     */
    private long lastTimestamp = -1L;

    /**
     * 生成下一个ID（线程安全）
     *
     * @param workerId 工作机器ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     * @return 生成的ID
     * @throws IllegalArgumentException 当工作机器ID或数据中心ID超出范围时
     * @throws RuntimeException 当系统时钟回退时
     */
    public synchronized long nextId(long workerId, long datacenterId) {
        if ((workerId > this.maxWorkerId) || (workerId < 0)) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
        if ((datacenterId > this.maxDatacenterId) || (datacenterId < 0)) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", this.maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;

        long timestamp = this.timeGen();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < this.lastTimestamp) {
            throw new RuntimeException(
                    String.format(
                            "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                            this.lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & this.sequenceMask;
            // 毫秒内序列溢出
            if (this.sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            this.sequence = 0L;
        }

        // 上次生成ID的时间截
        this.lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - this.twitterEpoch) << this.timestampLeftShift
        ) | (this.datacenterId << this.datacenterIdShift
        ) | (this.workerId << this.workerIdShift
        ) | this.sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * 用于处理同一毫秒内序列号用尽的情况
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 新的时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳（毫秒）
     * 
     * @return 当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
