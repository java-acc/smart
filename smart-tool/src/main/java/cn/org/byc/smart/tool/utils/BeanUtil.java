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

import cn.org.byc.smart.tool.supports.BaseBeanCopier;
import cn.org.byc.smart.tool.supports.BeanProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean 工具类，提供丰富的 JavaBean 操作功能。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>Bean 实例化</li>
 *   <li>Bean 属性操作（获取/设置）</li>
 *   <li>Bean 深度复制</li>
 *   <li>Bean 属性复制</li>
 * </ul>
 * 
 * <p>特点：
 * <ul>
 *   <li>支持通过类名动态实例化对象</li>
 *   <li>支持属性的安全访问和修改</li>
 *   <li>支持深度复制和浅复制</li>
 *   <li>支持链式 Bean 的属性复制</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 实例化对象
 * User user = BeanUtil.newInstance(User.class);
 * 
 * // 获取和设置属性
 * String name = (String) BeanUtil.getProperty(user, "name");
 * BeanUtil.setProperty(user, "name", "张三");
 * 
 * // 深度复制
 * User copy = BeanUtil.clone(user);
 * 
 * // 属性复制
 * UserDTO dto = new UserDTO();
 * BeanUtil.copyProperties(user, dto);
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public class BeanUtil extends BeanUtils {

    /**
     * 实例化对象。
     * 
     * <p>此方法通过反射创建类的新实例。要求目标类必须有无参构造函数。
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 创建 User 类的实例
     * User user = BeanUtil.newInstance(User.class);
     * 
     * // 创建泛型类型的实例
     * List<String> list = BeanUtil.newInstance(ArrayList.class);
     * }</pre>
     *
     * @param clazz 要实例化的类
     * @param <T> 目标类型的泛型标记
     * @return 新创建的实例
     * @throws RuntimeException 如果实例化失败（例如：类没有无参构造函数）
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        return (T) instantiateClass(clazz);
    }

    /**
     * 通过类名实例化对象。
     * 
     * <p>此方法先通过类名加载类，然后创建该类的新实例。要求：
     * <ul>
     *   <li>类必须存在于类路径中</li>
     *   <li>类必须有无参构造函数</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 通过完整类名创建实例
     * User user = BeanUtil.newInstance("com.example.User");
     * 
     * // 创建集合类实例
     * List<String> list = BeanUtil.newInstance("java.util.ArrayList");
     * }</pre>
     *
     * @param clazzStr 类的完整名称（包含包名）
     * @param <T> 目标类型的泛型标记
     * @return 新创建的实例
     * @throws RuntimeException 如果类不存在或实例化失败
     */
    public static <T> T newInstance(String clazzStr) {
        try {
            Class<?> clazz = Class.forName(clazzStr);
            return newInstance(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 Bean 的属性值。
     * 
     * <p>此方法通过属性名获取 Bean 的属性值。支持：
     * <ul>
     *   <li>公共字段直接访问</li>
     *   <li>通过 getter 方法访问</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * User user = new User();
     * user.setName("张三");
     * 
     * // 获取属性值
     * String name = (String) BeanUtil.getProperty(user, "name");  // 返回："张三"
     * }</pre>
     *
     * @param bean Bean 对象
     * @param propertyName 属性名
     * @return 属性值
     * @throws IllegalArgumentException 如果 bean 为 null
     */
    public static Object getProperty(Object bean, String propertyName) {
        Assert.notNull(bean, "bean Could not null");
        return BeanMap.create(bean).get(propertyName);
    }

    /**
     * 设置 Bean 的属性值。
     * 
     * <p>此方法通过属性名设置 Bean 的属性值。支持：
     * <ul>
     *   <li>公共字段直接设置</li>
     *   <li>通过 setter 方法设置</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * User user = new User();
     * 
     * // 设置属性值
     * BeanUtil.setProperty(user, "name", "张三");
     * BeanUtil.setProperty(user, "age", 25);
     * }</pre>
     *
     * @param bean Bean 对象
     * @param propertyName 属性名
     * @param value 要设置的值
     * @throws IllegalArgumentException 如果 bean 为 null
     */
    public static void setProperty(Object bean, String propertyName, Object value) {
        Assert.notNull(bean, "bean Could not null");
        BeanMap.create(bean).put(propertyName, value);
    }

    /**
     * 深度复制 Bean 对象。
     * 
     * <p>此方法创建源对象的深度复制。注意：
     * <ul>
     *   <li>不支持链式 Bean 的复制</li>
     *   <li>源对象必须有无参构造函数</li>
     *   <li>会复制所有可访问的属性</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * User user = new User();
     * user.setName("张三");
     * user.setAge(25);
     * 
     * // 创建深度复制
     * User copy = BeanUtil.clone(user);
     * }</pre>
     *
     * @param source 源对象
     * @param <T> 对象类型的泛型标记
     * @return 复制后的新对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(T source) {
        return (T) BeanUtil.copy(source, source.getClass());
    }

    /**
     * 复制对象属性到另一个对象。
     * 
     * <p>此方法将源对象的属性复制到目标类型的新实例中。特点：
     * <ul>
     *   <li>默认不使用类型转换</li>
     *   <li>不支持链式 Bean 的复制</li>
     *   <li>目标类必须有无参构造函数</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 实体对象转 DTO
     * User user = new User();
     * user.setName("张三");
     * user.setAge(25);
     * 
     * UserDTO dto = BeanUtil.copy(user, UserDTO.class);
     * }</pre>
     *
     * @param source 源对象
     * @param clazz 目标类
     * @param <T> 目标类型的泛型标记
     * @return 复制后的新对象
     */
    public static <T> T copy(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        T target = newInstance(clazz);
        copyProperties(source, target);
        return target;
    }

    /**
     * 拷贝对象
     * </p>
     * 注意：不支持链式Bean，链式用 copyProperties
     *
     * @param source 源对象
     * @param targetBean 需要赋值的对象
     */
    public static void copy(Object source, Object targetBean) {
        BaseBeanCopier copier = BaseBeanCopier
                .create(source.getClass(), targetBean.getClass(), false);

        copier.copy(source, targetBean, null);
    }

    /**
     * Copy the property values of the given source bean into the target class.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     * @param source the source bean
     * @param target the target bean class
     * @param <T> 泛型标记
     * @throws BeansException if the copying failed
     * @return T
     */
    public static <T> T copyProperties(Object source, Class<T> target) throws BeansException {
        T to = newInstance(target);
        cn.hutool.core.bean.BeanUtil.copyProperties(source, to);
        return to;
    }

    /**
     * 将对象装成map形式
     * @param bean 源对象
     * @return {Map}
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object bean) {
        return BeanMap.create(bean);
    }

    /**
     * 将map 转为 bean
     * @param beanMap map
     * @param valueType 对象类型
     * @param <T> 泛型标记
     * @return {T}
     */
    public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
        T bean = BeanUtil.newInstance(valueType);
        BeanMap.create(bean).putAll(beanMap);
        return bean;
    }

    /**
     * 给一个Bean添加字段
     * @param superBean 父级Bean
     * @param props 新增属性
     * @return  {Object}
     */
    public static Object generator(Object superBean, BeanProperty... props) {
        Class<?> superclass = superBean.getClass();
        Object genBean = generator(superclass, props);
        BeanUtil.copy(superBean, genBean);
        return genBean;
    }

    /**
     * 给一个class添加字段
     * @param superclass 父级
     * @param props 新增属性
     * @return {Object}
     */
    public static Object generator(Class<?> superclass, BeanProperty... props) {
        BeanGenerator generator = new BeanGenerator();
        generator.setSuperclass(superclass);
        generator.setUseCache(true);
        for (BeanProperty prop : props) {
            generator.addProperty(prop.getName(), prop.getType());
        }
        return generator.create();
    }

    /**
     * 获取 Bean 的所有 get方法
     * @param type 类
     * @return PropertyDescriptor数组
     */
    public static PropertyDescriptor[] getBeanGetters(Class type) {
        return getPropertiesHelper(type, true, false);
    }

    /**
     * 获取 Bean 的所有 set方法
     * @param type 类
     * @return PropertyDescriptor数组
     */
    public static PropertyDescriptor[] getBeanSetters(Class type) {
        return getPropertiesHelper(type, false, true);
    }

    private static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
        try {
            PropertyDescriptor[] all = cn.hutool.core.bean.BeanUtil.getPropertyDescriptors(type);
            if (read && write) {
                return all;
            } else {
                List<PropertyDescriptor> properties = new ArrayList<>(all.length);
                for (PropertyDescriptor pd : all) {
                    if (read && pd.getReadMethod() != null) {
                        properties.add(pd);
                    } else if (write && pd.getWriteMethod() != null) {
                        properties.add(pd);
                    }
                }
                return properties.toArray(new PropertyDescriptor[0]);
            }
        } catch (BeansException ex) {
            throw new CodeGenerationException(ex);
        }
    }

}