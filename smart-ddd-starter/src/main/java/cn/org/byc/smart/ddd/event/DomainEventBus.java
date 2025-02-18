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

package cn.org.byc.smart.ddd.event;

/**
 * 领域事件总线接口
 * 
 * <p>定义了领域事件的发布和订阅机制。主要职责：
 * <ul>
 *     <li>发布事件 - 将领域事件发送给所有注册的处理器</li>
 *     <li>注册处理器 - 允许事件处理器注册以接收特定类型的事件</li>
 * </ul>
 * 
 * <p>实现此接口的类需要确保：
 * <ul>
 *     <li>线程安全 - 支持并发的事件发布和处理器注册</li>
 *     <li>异常处理 - 妥善处理事件处理过程中的异常</li>
 *     <li>有序性 - 保证同一个key的事件按顺序处理</li>
 * </ul>
 *
 * @author Ken
 * @see DomainEvent
 * @see DomainEventHandler
 */
public interface DomainEventBus {
    
    /**
     * 发布领域事件
     * 
     * <p>将事件发送给所有注册的处理器。实现类应该：
     * <ul>
     *     <li>异步处理事件，避免阻塞调用方</li>
     *     <li>确保事件至少被处理一次</li>
     *     <li>记录事件处理的日志</li>
     * </ul>
     *
     * @param event 要发布的领域事件
     */
    void post(DomainEvent event);

    /**
     * 注册事件处理器
     * 
     * <p>注册一个处理器以接收特定类型的事件。实现类应该：
     * <ul>
     *     <li>支持运行时动态注册</li>
     *     <li>避免重复注册同一个处理器</li>
     *     <li>维护处理器的生命周期</li>
     * </ul>
     *
     * @param handler 要注册的事件处理器
     */
    void register(DomainEventHandler handler);
}
