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

package cn.org.byc.smart.tool.supports;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bean属性描述类
 * 
 * <p>用于描述JavaBean的属性信息，包含属性名称和类型。主要用于：
 * <ul>
 *   <li>动态生成Bean时描述新增属性</li>
 *   <li>Bean属性拷贝时的属性映射</li>
 *   <li>属性类型转换时的类型信息保持</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 创建String类型的name属性
 * BeanProperty nameProperty = new BeanProperty("name", String.class);
 * 
 * // 创建Integer类型的age属性
 * BeanProperty ageProperty = new BeanProperty("age", Integer.class);
 * 
 * // 在动态生成Bean时使用
 * Object bean = generator(superclass, nameProperty, ageProperty);
 * }</pre>
 *
 * @author Ken
 */
@Getter
@AllArgsConstructor
public class BeanProperty {
    
    // 属性名称
    private final String name;
    
    // 属性类型
    private final Class<?> type;
}

