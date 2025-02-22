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

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 延迟加载工具类，提供对象的懒加载功能。
 * 
 * <p>
 * 该类实现了 Supplier 接口和 Serializable 接口，提供了一种延迟初始化对象的机制。
 * 对象只有在第一次被请求时才会被创建，这对于创建开销较大的对象特别有用。
 * 
 * <p>
 * 主要特点：
 * <ul>
 *   <li>线程安全的延迟初始化</li>
 *   <li>支持序列化</li>
 *   <li>避免重复计算</li>
 *   <li>内存友好（仅在需要时创建对象）</li>
 * </ul>
 * 
 * @param <T> 延迟加载的对象类型
 * @author Ken
 */
public class Lazy<T> implements Supplier<T>, Serializable {

    // 用于创建延迟加载值的供应商，使用 volatile 保证多线程可见性
    @Nullable
    private transient volatile Supplier<? extends T> supplier;
    
    // 缓存的值，在第一次访问时初始化
    @Nullable
    private T value;

    /**
     * 创建一个新的延迟加载对象。
     * 
     * <p>
     * 该方法接收一个 Supplier 作为参数，用于在需要时创建目标对象。
     * 创建的对象会被缓存，后续的访问将直接返回缓存的对象。
     * 
     * <p>示例：
     * <pre>{@code
     * // 创建一个延迟加载的大对象
     * Lazy<byte[]> lazyData = Lazy.of(() -> {
     *     // 模拟耗时操作
     *     Thread.sleep(1000);
     *     return new byte[1024 * 1024]; // 1MB 数据
     * });
     * 
     * // 数据还未被加载
     * System.out.println("数据未初始化");
     * 
     * // 首次访问时才会创建对象
     * byte[] data = lazyData.get();
     * System.out.println("数据已加载，大小: " + data.length);
     * 
     * // 再次访问时直接返回缓存的对象
     * byte[] cachedData = lazyData.get();
     * System.out.println("使用缓存的数据");
     * 
     * // 在集合中使用
     * List<Lazy<ExpensiveObject>> list = Arrays.asList(
     *     Lazy.of(() -> new ExpensiveObject("A")),
     *     Lazy.of(() -> new ExpensiveObject("B"))
     * );
     * // 只有实际访问对象时才会创建
     * ExpensiveObject obj = list.get(0).get();
     * }</pre>
     * 
     * @param supplier 用于创建目标对象的供应商
     * @param <T> 目标对象的类型
     * @return 新的延迟加载对象
     */
    public static <T> Lazy<T> of(final Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    /**
     * 私有构造函数，创建延迟加载对象。
     * 
     * @param supplier 用于创建目标对象的供应商
     */
    private Lazy(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * 获取延迟加载的值。
     * 
     * <p>
     * 该方法实现了 Supplier 接口的 get 方法。首次调用时会创建对象，
     * 后续调用将直接返回已创建的对象。该方法是线程安全的。
     * 
     * <p>示例：
     * <pre>{@code
     * // 创建延迟加载的数据库连接
     * Lazy<Connection> lazyConn = Lazy.of(() -> {
     *     System.out.println("创建数据库连接...");
     *     return DriverManager.getConnection("jdbc:mysql://localhost/db");
     * });
     * 
     * // 在实际需要时才创建连接
     * try {
     *     Connection conn = lazyConn.get();
     *     // 使用连接...
     * } catch (Exception e) {
     *     e.printStackTrace();
     * }
     * }</pre>
     * 
     * @return 延迟加载的对象，可能为 null
     */
    @Nullable
    @Override
    public T get() {
        return (supplier == null) ? value : computeValue();
    }

    /**
     * 计算并缓存值的内部方法。
     * 
     * <p>
     * 该方法使用 synchronized 关键字确保线程安全，
     * 防止多个线程同时初始化值。一旦值被计算出来，
     * supplier 将被设置为 null 以释放资源。
     * 
     * @return 计算得到的值，可能为 null
     */
    @Nullable
    private synchronized T computeValue() {
        // 再次检查 supplier 是否为 null，因为在获得锁之前可能已经被其他线程初始化
        final Supplier<? extends T> s = supplier;
        if (s != null) {
            // 调用 supplier 获取值并缓存
            value = s.get();
            // 清空 supplier 释放资源
            supplier = null;
        }
        return value;
    }
}
