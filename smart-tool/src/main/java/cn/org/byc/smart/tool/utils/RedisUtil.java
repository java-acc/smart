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

package cn.org.byc.smart.tool.utils;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 操作工具类，封装了对 Redis 的常用操作。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>字符串(String)操作</li>
 *   <li>哈希(Hash)操作</li>
 *   <li>列表(List)操作</li>
 *   <li>集合(Set)操作</li>
 *   <li>有序集合(ZSet)操作</li>
 *   <li>键(Key)过期时间管理</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 字符串操作
 * redisUtil.set("key", "value");
 * redisUtil.set("key", "value", 60);  // 60秒过期
 * String value = (String) redisUtil.get("key");
 * 
 * // Hash操作
 * redisUtil.hset("hash", "item", "value");
 * Map<String, Object> map = new HashMap<>();
 * map.put("field1", "value1");
 * redisUtil.hmset("hash", map);
 * 
 * // 列表操作
 * redisUtil.lSet("list", "value");
 * List<Object> list = redisUtil.lGet("list", 0, -1);
 * 
 * // 设置过期时间
 * redisUtil.expire("key", 60);  // 60秒后过期
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public class RedisUtil {

    private RedisTemplate<String, Object> redisTemplate;

    //=============================common============================

    /**
     * 设置缓存键的过期时间
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 设置键 60 秒后过期
     * redisUtil.expire("key", 60);
     * 
     * // 设置永不过期
     * redisUtil.expire("key", -1);
     * }</pre>
     *
     * @param key 键
     * @param time 过期时间（秒），如果小于等于0则表示永不过期
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取缓存键的剩余过期时间
     * 
     * <p>使用示例：
     * <pre>{@code
     * long ttl = redisUtil.getExpire("key");  // 返回剩余秒数
     * if (ttl == -1) {
     *     System.out.println("键永不过期");
     * } else if (ttl == -2) {
     *     System.out.println("键不存在");
     * } else {
     *     System.out.println("键将在 " + ttl + " 秒后过期");
     * }
     * }</pre>
     *
     * @param key 键
     * @return 剩余时间（秒）
     *         <ul>
     *           <li>大于0：剩余过期时间</li>
     *           <li>-1：永不过期</li>
     *           <li>-2：键不存在</li>
     *         </ul>
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    //============================String=============================

    /**
     * 获取字符串类型的缓存
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 获取字符串值
     * String value = (String) redisUtil.get("key");
     * 
     * // 获取对象（需要自行转换类型）
     * User user = (User) redisUtil.get("user");
     * }</pre>
     *
     * @param key 键
     * @return 值，如果键不存在则返回 null
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置字符串类型的缓存
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 设置字符串
     * redisUtil.set("key", "value");
     * 
     * // 设置对象（需要实现 Serializable 接口）
     * User user = new User();
     * redisUtil.set("user", user);
     * 
     * // 设置带过期时间的缓存
     * redisUtil.set("key", "value", 60);  // 60秒后过期
     * 
     * // 使用自定义时间单位
     * redisUtil.set("key", "value", 1, TimeUnit.HOURS);  // 1小时后过期
     * }</pre>
     *
     * @param key 键
     * @param value 值
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置带过期时间的字符串类型缓存
     *
     * @param key 键
     * @param value 值
     * @param time 过期时间（秒），如果小于等于0则表示永不过期
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置带过期时间和时间单位的字符串类型缓存
     *
     * @param key 键
     * @param value 值
     * @param time 过期时间
     * @param timeUnit 时间单位
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return long
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return long
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 操作 Hash 类型的缓存
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 设置单个 Hash 字段
     * redisUtil.hset("hash", "field", "value");
     * 
     * // 设置多个 Hash 字段
     * Map<String, Object> map = new HashMap<>();
     * map.put("field1", "value1");
     * map.put("field2", "value2");
     * redisUtil.hmset("hash", map);
     * 
     * // 获取 Hash 字段值
     * Object value = redisUtil.hget("hash", "field");
     * 
     * // 获取所有字段和值
     * Map<Object, Object> entries = redisUtil.hmget("hash");
     * }</pre>
     *
     * @param key 键
     * @param item Hash 的字段名
     * @param value 值
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return double
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return double
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return Set
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return long
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return List
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return long
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引
     * @return Object
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 操作列表(List)类型的缓存
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 从左端插入元素
     * redisUtil.lSet("list", "value");
     * 
     * // 获取指定范围的元素
     * List<Object> elements = redisUtil.lGet("list", 0, -1);  // 获取所有元素
     * 
     * // 获取列表长度
     * long size = redisUtil.lGetListSize("list");
     * 
     * // 移除元素
     * redisUtil.lRemove("list", 1, "value");  // 从左到右移除一个值为"value"的元素
     * }</pre>
     *
     * @param key 键
     * @param value 值
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return boolean
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return boolean
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return boolean
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return boolean
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
