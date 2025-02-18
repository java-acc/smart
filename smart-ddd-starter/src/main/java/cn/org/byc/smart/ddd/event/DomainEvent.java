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

import cn.org.byc.schedule.base.id.IdFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领域事件基类
 * 
 * <p>定义了领域事件的基本属性和行为。每个领域事件包含：
 * <ul>
 *     <li>事件名称 - 用于标识事件类型</li>
 *     <li>事件ID - 全局唯一标识符</li>
 *     <li>发生时间 - 事件发生的时间戳</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>
 * public class UserCreatedEvent extends DomainEvent {
 *     private final String userId;
 *     
 *     public UserCreatedEvent(String userId) {
 *         super("USER_CREATED");
 *         this.userId = userId;
 *     }
 *     
 *     {@literal @}Override
 *     public String key() {
 *         return userId;
 *     }
 * }
 * </pre>
 *
 * @author Ken
 * @see DomainEventBus
 * @see DomainEventHandler
 */
public abstract class DomainEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件名称
     * 使用transient修饰，避免序列化，因为事件名称通常是固定的类属性
     */
    protected transient String eventName;
    
    /**
     * 事件ID，使用IdFactory生成的全局唯一标识符
     */
    private String eventId;
    
    /**
     * 事件发生时间
     */
    private LocalDateTime occurTime;

    /**
     * 构造函数
     *
     * @param eventName 事件名称
     */
    public DomainEvent(String eventName) {
        this.eventName = eventName;
        // 使用IdFactory生成全局唯一的事件ID
        eventId = String.valueOf(IdFactory.getInstance().getLocalId());
        // 记录事件发生时间
        occurTime = LocalDateTime.now();
    }

    /**
     * 获取事件关键字
     * 
     * <p>用于事件路由和分区的关键字，子类必须实现此方法。
     * 例如，用户相关的事件可以返回用户ID作为关键字，
     * 这样同一用户的事件就会被路由到同一个分区。
     *
     * @return 事件关键字
     */
    public abstract String key();

    // Getter和Setter方法
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(LocalDateTime occurTime) {
        this.occurTime = occurTime;
    }
}
