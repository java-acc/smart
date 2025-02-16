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

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

/**
 * ID生成器工厂类，基于雪花算法生成分布式唯一ID
 * 
 * <p>该类采用单例模式设计，通过{@link IdGenerator}提供ID生成服务。
 * 主要功能：
 * <ul>
 *     <li>提供全局唯一的ID生成器实例</li>
 *     <li>支持自定义工作节点ID和数据中心ID</li>
 *     <li>支持从配置文件读取节点配置</li>
 * </ul>
 *
 * @author Ken
 * @see IdGenerator
 */
public class IdFactory {
    // 单例实例
    private static final IdFactory INSTANCE = new IdFactory();

    // ID生成器实例
    private final IdGenerator idGenerator;

    /**
     * 私有构造函数，初始化ID生成器
     */
    private IdFactory(){
        this.idGenerator = new IdGenerator();
    }

    /**
     * 获取IdFactory的单例实例
     *
     * @return IdFactory实例
     */
    public static IdFactory getInstance(){
        return INSTANCE;
    }

    /**
     * 使用指定的工作节点ID和数据中心ID生成唯一ID
     * 
     * <p>基于雪花算法生成的ID结构：
     * <ul>
     *     <li>1位符号位</li>
     *     <li>41位时间戳</li>
     *     <li>5位数据中心ID</li>
     *     <li>5位工作节点ID</li>
     *     <li>12位序列号</li>
     * </ul>
     *
     * @param workerId 工作节点ID（0-31）
     * @param datacenterId 数据中心ID（0-31）
     * @return 生成的唯一ID
     * @throws IllegalArgumentException 如果workerId或datacenterId超出范围
     */
    public long getLocalId(long workerId, long datacenterId) {
        return this.idGenerator.nextId(workerId, datacenterId);
    }

    /**
     * 使用配置文件中的节点配置生成唯一ID
     * 
     * <p>从Spring环境配置中读取以下属性：
     * <ul>
     *     <li>snowflake.workerId: 工作节点ID，默认值为1</li>
     *     <li>snowflake.datacenterId: 数据中心ID，默认值为1</li>
     * </ul>
     *
     * @return 生成的唯一ID
     */
    public long getLocalId(){
        // 从Spring环境中获取配置
        Environment env = SpringUtil.getBean(Environment.class);
        // 读取工作节点ID，默认为1
        long workerId = Long.parseLong(env.getProperty("snowflake.workerId", "1"));
        // 读取数据中心ID，默认为1
        long datacenterId = Long.parseLong(env.getProperty("snowflake.datacenterId", "1"));
        return this.getLocalId(workerId, datacenterId);
    }
}
