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

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeansException;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类，提供对 Java 反射操作的增强功能。
 * 继承自 Spring 的 ReflectionUtils 类，提供了更多实用的反射操作方法。
 * 
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>获取 Bean 的 getter/setter 方法</li>
 *   <li>获取属性描述符和类型描述符</li>
 *   <li>获取类的字段和注解信息</li>
 *   <li>支持对继承层次的属性访问</li>
 * </ul>
 * 
 * <p>
 * 该工具类使用 @UtilityClass 注解标记，确保所有方法都是静态的，
 * 并且类不能被实例化。
 * 
 * @author Ken
 * @since 1.0
 */
@UtilityClass
public class ReflectionUtil extends ReflectionUtils {

    /**
     * 获取 Bean 的所有 getter 方法。
     * 
     * <p>
     * 该方法会返回指定类中所有具有读取访问器（getter 方法）的属性描述符。
     * getter 方法通常以 "get" 或 "is" 开头（对于 boolean 类型）。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     private String name;
     *     private int age;
     *     private boolean active;
     *     
     *     public String getName() { return name; }
     *     public int getAge() { return age; }
     *     public boolean isActive() { return active; }
     * }
     * 
     * // 获取所有 getter 方法
     * PropertyDescriptor[] getters = ReflectionUtil.getBeanGetters(User.class);
     * for (PropertyDescriptor pd : getters) {
     *     System.out.println("属性名: " + pd.getName());
     *     System.out.println("Getter方法: " + pd.getReadMethod().getName());
     * }
     * // 输出:
     * // 属性名: name
     * // Getter方法: getName
     * // 属性名: age
     * // Getter方法: getAge
     * // 属性名: active
     * // Getter方法: isActive
     * }</pre>
     * 
     * @param type 要获取 getter 方法的类
     * @return 包含 getter 方法的 PropertyDescriptor 数组
     */
    public static PropertyDescriptor[] getBeanGetters(Class type) {
        return getPropertiesHelper(type, true, false);
    }

    /**
     * 获取 Bean 的所有 setter 方法。
     * 
     * <p>
     * 该方法会返回指定类中所有具有写入访问器（setter 方法）的属性描述符。
     * setter 方法通常以 "set" 开头，并且接受一个参数。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     private String name;
     *     private int age;
     *     
     *     public void setName(String name) { this.name = name; }
     *     public void setAge(int age) { this.age = age; }
     * }
     * 
     * // 获取所有 setter 方法
     * PropertyDescriptor[] setters = ReflectionUtil.getBeanSetters(User.class);
     * for (PropertyDescriptor pd : setters) {
     *     System.out.println("属性名: " + pd.getName());
     *     System.out.println("Setter方法: " + pd.getWriteMethod().getName());
     * }
     * // 输出:
     * // 属性名: name
     * // Setter方法: setName
     * // 属性名: age
     * // Setter方法: setAge
     * }</pre>
     * 
     * @param type 要获取 setter 方法的类
     * @return 包含 setter 方法的 PropertyDescriptor 数组
     */
    public static PropertyDescriptor[] getBeanSetters(Class type) {
        return getPropertiesHelper(type, false, true);
    }

    /**
     * 获取 Bean 的所有属性描述符。
     * 
     * <p>
     * 该方法可以根据需要获取类中的读方法（getter）和写方法（setter）。
     * 通过设置 read 和 write 参数，可以灵活控制返回的属性描述符类型。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     private String name;
     *     private int age;
     *     
     *     public String getName() { return name; }
     *     public void setName(String name) { this.name = name; }
     *     public int getAge() { return age; }
     *     // 注意：没有 setAge 方法
     * }
     * 
     * // 获取所有属性（同时具有 getter 和 setter）
     * PropertyDescriptor[] both = ReflectionUtil.getPropertiesHelper(User.class, true, true);
     * // 只返回 name 属性，因为只有它同时具有 getter 和 setter
     * 
     * // 只获取具有 getter 的属性
     * PropertyDescriptor[] getters = ReflectionUtil.getPropertiesHelper(User.class, true, false);
     * // 返回 name 和 age 属性，因为它们都有 getter
     * 
     * // 只获取具有 setter 的属性
     * PropertyDescriptor[] setters = ReflectionUtil.getPropertiesHelper(User.class, false, true);
     * // 只返回 name 属性，因为只有它有 setter
     * }</pre>
     * 
     * @param type 要获取属性的类
     * @param read 是否包含读方法（getter）
     * @param write 是否包含写方法（setter）
     * @return 符合条件的 PropertyDescriptor 数组
     * @throws CodeGenerationException 如果在获取属性描述符时发生异常
     */
    public static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
        try {
            PropertyDescriptor[] all = BeanUtil.getPropertyDescriptors(type);
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

    /**
     * 获取指定类中某个属性的 Property 对象。
     * 
     * <p>
     * 该方法通过属性名称获取类中对应属性的 Property 对象，
     * Property 对象包含了属性的读写方法等信息。
     * 如果属性不存在，则返回 null。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     private String name;
     *     public String getName() { return name; }
     *     public void setName(String name) { this.name = name; }
     * }
     * 
     * // 获取 name 属性的 Property 对象
     * Property property = ReflectionUtil.getProperty(User.class, "name");
     * if (property != null) {
     *     System.out.println("属性名: " + property.getName());
     *     System.out.println("属性类型: " + property.getType());
     *     System.out.println("Getter方法: " + property.getReadMethod().getName());
     *     System.out.println("Setter方法: " + property.getWriteMethod().getName());
     * }
     * 
     * // 获取不存在的属性
     * Property notExist = ReflectionUtil.getProperty(User.class, "age");
     * // 返回 null
     * }</pre>
     * 
     * @param propertyType 要获取属性的类
     * @param propertyName 属性名称
     * @return 属性对应的 Property 对象，如果属性不存在则返回 null
     */
    @Nullable
    public static Property getProperty(Class<?> propertyType, String propertyName) {
        PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(propertyType, propertyName);
        if (propertyDescriptor == null) {
            return null;
        }
        return ReflectionUtil.getProperty(propertyType, propertyDescriptor, propertyName);
    }

    /**
     * 根据属性描述符创建 Property 对象。
     * 
     * <p>
     * 该方法使用给定的属性描述符创建一个 Property 对象，
     * 包含了属性的完整信息（类型、读写方法等）。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     private String name;
     *     public String getName() { return name; }
     *     public void setName(String name) { this.name = name; }
     * }
     * 
     * // 首先获取属性描述符
     * PropertyDescriptor pd = BeanUtil.getPropertyDescriptor(User.class, "name");
     * 
     * // 创建 Property 对象
     * Property property = ReflectionUtil.getProperty(User.class, pd, "name");
     * System.out.println("属性名: " + property.getName());
     * System.out.println("属性类型: " + property.getType());
     * }</pre>
     * 
     * @param propertyType 属性所属的类
     * @param propertyDescriptor 属性的描述符
     * @param propertyName 属性名称
     * @return 创建的 Property 对象
     */
    public static Property getProperty(Class<?> propertyType, PropertyDescriptor propertyDescriptor, String propertyName) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        return new Property(propertyType, readMethod, writeMethod, propertyName);
    }

    /**
     * 获取指定类中某个属性的类型描述符。
     * 
     * <p>
     * 该方法返回属性的 TypeDescriptor 对象，其中包含了属性的详细类型信息，
     * 包括泛型类型等。如果属性不存在，则返回 null。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     private List<String> roles;
     *     public List<String> getRoles() { return roles; }
     *     public void setRoles(List<String> roles) { this.roles = roles; }
     * }
     * 
     * // 获取 roles 属性的类型描述符
     * TypeDescriptor typeDesc = ReflectionUtil.getTypeDescriptor(User.class, "roles");
     * if (typeDesc != null) {
     *     System.out.println("属性类型: " + typeDesc.getType());
     *     System.out.println("是否为集合: " + typeDesc.isCollection());
     *     System.out.println("集合元素类型: " + typeDesc.getElementTypeDescriptor().getType());
     * }
     * 
     * // 获取不存在的属性
     * TypeDescriptor notExist = ReflectionUtil.getTypeDescriptor(User.class, "age");
     * // 返回 null
     * }</pre>
     * 
     * @param propertyType 要获取属性的类
     * @param propertyName 属性名称
     * @return 属性的类型描述符，如果属性不存在则返回 null
     */
    @Nullable
    public static TypeDescriptor getTypeDescriptor(Class<?> propertyType, String propertyName) {
        Property property = ReflectionUtil.getProperty(propertyType, propertyName);
        if (property == null) {
            return null;
        }
        return new TypeDescriptor(property);
    }

    /**
     * 根据属性描述符创建类型描述符。
     * 
     * <p>
     * 该方法使用给定的属性描述符创建一个 TypeDescriptor 对象，
     * 提供对属性类型的详细描述，包括泛型信息等。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     private Map<String, List<Integer>> data;
     *     public Map<String, List<Integer>> getData() { return data; }
     *     public void setData(Map<String, List<Integer>> data) { this.data = data; }
     * }
     * 
     * // 首先获取属性描述符
     * PropertyDescriptor pd = BeanUtil.getPropertyDescriptor(User.class, "data");
     * 
     * // 创建类型描述符
     * TypeDescriptor typeDesc = ReflectionUtil.getTypeDescriptor(User.class, pd, "data");
     * System.out.println("是否为 Map: " + typeDesc.isMap());
     * System.out.println("Map 键类型: " + typeDesc.getMapKeyTypeDescriptor().getType());
     * System.out.println("Map 值类型: " + typeDesc.getMapValueTypeDescriptor().getType());
     * }</pre>
     * 
     * @param propertyType 属性所属的类
     * @param propertyDescriptor 属性的描述符
     * @param propertyName 属性名称
     * @return 创建的类型描述符
     */
    public static TypeDescriptor getTypeDescriptor(Class<?> propertyType, PropertyDescriptor propertyDescriptor, String propertyName) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        Property property = new Property(propertyType, readMethod, writeMethod, propertyName);
        return new TypeDescriptor(property);
    }

    /**
     * 获取类的字段（包括父类中的字段）。
     * 
     * <p>
     * 该方法会在类的继承层次结构中查找指定名称的字段。
     * 查找过程从当前类开始，如果没有找到，则继续在父类中查找，
     * 直到到达 Object 类或找到指定字段。
     * 
     * <p>示例：
     * <pre>{@code
     * public class Parent {
     *     protected String name;
     * }
     * 
     * public class Child extends Parent {
     *     private int age;
     * }
     * 
     * // 获取当前类的字段
     * Field ageField = ReflectionUtil.getField(Child.class, "age");
     * System.out.println("字段名: " + ageField.getName());
     * System.out.println("字段类型: " + ageField.getType());
     * 
     * // 获取父类的字段
     * Field nameField = ReflectionUtil.getField(Child.class, "name");
     * System.out.println("字段名: " + nameField.getName());
     * System.out.println("字段类型: " + nameField.getType());
     * 
     * // 获取不存在的字段
     * Field notExist = ReflectionUtil.getField(Child.class, "address");
     * // 返回 null
     * }</pre>
     * 
     * @param clazz 要获取字段的类
     * @param fieldName 字段名称
     * @return 找到的字段对象，如果字段不存在则返回 null
     */
    @Nullable
    public static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取类字段上的注解。
     * 
     * <p>
     * 该方法会在类的继承层次结构中查找指定字段，并获取该字段上的指定类型注解。
     * 如果字段不存在或字段上没有指定的注解，则返回 null。
     * 
     * <p>示例：
     * <pre>{@code
     * public class User {
     *     @NotNull
     *     @Size(min = 2, max = 50)
     *     private String username;
     *     
     *     @Email
     *     private String email;
     * }
     * 
     * // 获取字段上的 Size 注解
     * Size sizeAnnotation = ReflectionUtil.getAnnotation(User.class, "username", Size.class);
     * if (sizeAnnotation != null) {
     *     System.out.println("最小长度: " + sizeAnnotation.min());
     *     System.out.println("最大长度: " + sizeAnnotation.max());
     * }
     * 
     * // 获取字段上的 Email 注解
     * Email emailAnnotation = ReflectionUtil.getAnnotation(User.class, "email", Email.class);
     * if (emailAnnotation != null) {
     *     System.out.println("字段有 @Email 注解");
     * }
     * 
     * // 获取不存在的注解
     * NotEmpty notExist = ReflectionUtil.getAnnotation(User.class, "username", NotEmpty.class);
     * // 返回 null
     * }</pre>
     * 
     * @param clazz 要获取注解的类
     * @param fieldName 字段名称
     * @param annotationClass 注解类型
     * @param <T> 注解类型的泛型参数
     * @return 找到的注解对象，如果字段不存在或没有指定注解则返回 null
     */
    @Nullable
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, String fieldName, Class<T> annotationClass) {
        Field field = ReflectionUtil.getField(clazz, fieldName);
        if (field == null) {
            return null;
        }
        return field.getAnnotation(annotationClass);
    }
}
