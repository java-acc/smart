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

package cn.org.byc.smart.ddd.aggregate;

/**
 * 聚合根基类，提供了版本控制和事件发布的基础功能。
 *
 * <p>该类实现了以下核心功能：
 * <ul>
 *   <li>版本控制：通过版本号实现乐观锁机制，防止并发修改</li>
 *   <li>事件发布：提供领域事件的发布能力</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>
 * public class Order extends AggregateBase {
 *     private OrderId orderId;
 *     private OrderStatus status;
 *
 *     public void cancel() {
 *         // 修改状态
 *         this.status = OrderStatus.CANCELLED;
 *         // 增加版本号
 *         incVersion();
 *         // 发布事件
 *         emitEvent(new OrderCancelledEvent(orderId));
 *     }
 * }
 * </pre>
 *
 * @author Ken
 */
public abstract class AggregateBase {
    /**
     * 聚合版本号，用于实现乐观锁机制，防止并发修改。
     * 每次修改聚合状态时都应该调用 {@link #incVersion()} 方法增加版本号。
     */
    protected long version;

    /**
     * 标识版本号是否已增加的标志。
     * 用于在保存聚合时判断是否需要更新版本号。
     */
    private boolean versionInc = false;

    /**
     * 领域事件总线，用于发布领域事件。
     * 通过 {@link #setEventBus(DomainEventBus)} 方法注入。
     */
    protected DomainEventBus eventBus;

    /**
     * 增加聚合版本号。
     * 在修改聚合状态时必须调用此方法，以确保乐观锁机制正常工作。
     */
    protected void incVersion() {
        version++;
        versionInc = true;
    }

    /**
     * 获取当前聚合版本号。
     *
     * @return 当前版本号
     */
    public long getVersion() {
        return version;
    }

    /**
     * 判断版本号是否已经增加。
     * 用于在保存聚合时判断是否需要更新版本号。
     *
     * @return 如果版本号已增加返回true，否则返回false
     */
    public boolean isVersionInc() {
        return versionInc;
    }

    /**
     * 设置领域事件总线。
     * 通常由框架自动注入，不需要手动调用。
     *
     * @param eventBus 领域事件总线实例
     */
    protected void setEventBus(DomainEventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * 发布领域事件。
     * 如果事件总线未设置，则事件不会被发布。
     *
     * @param event 要发布的领域事件
     */
    protected void emitEvent(DomainEvent event) {
        if (eventBus != null) {
            eventBus.post(event);
        }
    }
}
