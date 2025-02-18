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
 * 领域事件处理器抽象基类
 * 
 * <p>定义了领域事件处理的基本框架。特点：
 * <ul>
 *     <li>类型安全 - 使用泛型确保类型安全的事件处理</li>
 *     <li>简化实现 - 子类只需实现单个事件处理方法</li>
 *     <li>避免类型转换 - 通过泛型自动完成事件类型转换</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>
 * public class UserCreatedEventHandler extends DomainEventHandler&lt;UserCreatedEvent&gt; {
 *     {@literal @}Override
 *     public void onApplicationEvent(UserCreatedEvent event) {
 *         // 处理用户创建事件
 *         String userId = event.getUserId();
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @param <T> 处理器支持的领域事件类型
 * @author Ken
 * @see DomainEvent
 * @see DomainEventBus
 */
public abstract class DomainEventHandler<T extends DomainEvent> {

    /**
     * 处理领域事件
     * 
     * <p>子类需要实现此方法来处理特定类型的领域事件。
     * 实现时应注意：
     * <ul>
     *     <li>异常处理 - 妥善处理可能发生的异常</li>
     *     <li>幂等性 - 确保重复处理同一事件是安全的</li>
     *     <li>性能 - 避免在处理方法中执行耗时操作</li>
     * </ul>
     *
     * @param event 要处理的领域事件
     */
    public abstract void onApplicationEvent(T event);
}
