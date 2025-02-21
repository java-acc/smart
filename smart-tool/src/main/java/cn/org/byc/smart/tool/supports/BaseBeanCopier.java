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

import cn.org.byc.smart.tool.utils.BeanUtil;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Converter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础Bean拷贝器
 * 
 * <p>该类是对Spring CGLIB BeanCopier的增强实现，主要提供以下功能：
 * <ul>
 *   <li>支持链式bean的属性拷贝</li>
 *   <li>自定义的类加载器处理，解决Spring Boot和CGLIB类加载器不一致问题</li>
 *   <li>支持属性类型自动转换</li>
 *   <li>缓存生成的拷贝器实例提高性能</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 基本使用
 * BaseBeanCopier copier = BaseBeanCopier.create(Source.class, Target.class, false);
 * Target target = new Target();
 * copier.copy(source, target, null);
 * 
 * // 2. 使用转换器
 * BaseBeanCopier copier = BaseBeanCopier.create(Source.class, Target.class, true);
 * Converter converter = new SmartConvert(Target.class);
 * copier.copy(source, target, converter);
 * 
 * // 3. 使用自定义类加载器
 * ClassLoader customLoader = Thread.currentThread().getContextClassLoader();
 * BaseBeanCopier copier = BaseBeanCopier.create(Source.class, Target.class, customLoader, false);
 * }</pre>
 *
 * <p>注意事项：
 * <ul>
 *   <li>不支持循环引用的Bean拷贝</li>
 *   <li>使用转换器会影响性能，建议只在必要时启用</li>
 *   <li>属性名称必须完全匹配才能进行拷贝</li>
 * </ul>
 *
 * @author Ken
 * @see org.springframework.cglib.beans.BeanCopier
 * @see SmartConvert
 */
public abstract class BaseBeanCopier {
    private static final BeanCopierKey KEY_FACTORY = (BeanCopierKey) KeyFactory.create(BeanCopierKey.class);
    private static final Type CONVERTER = TypeUtils.parseType("org.springframework.cglib.core.Converter");
    private static final Type BEAN_COPIER = TypeUtils.parseType(BaseBeanCopier.class.getName());
    private static final Signature COPY = new Signature("copy", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER});
    private static final Signature CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");

    /**
     * BeanCopier工厂接口
     * <p>用于创建BeanCopier实例的工厂方法定义
     */
    interface BeanCopierKey {
        /**
         * 创建新的BeanCopier实例
         *
         * @param source 源类名
         * @param target 目标类名
         * @param useConverter 是否使用转换器
         * @return BeanCopier实例的唯一标识
         */
        Object newInstance(String source, String target, boolean useConverter);
    }

    /**
     * 创建BeanCopier实例
     *
     * @param source 源类
     * @param target 目标类
     * @param useConverter 是否使用转换器
     * @return BeanCopier实例
     */
    public static BaseBeanCopier create(Class source, Class target, boolean useConverter) {
        return BaseBeanCopier.create(source, target, null, useConverter);
    }

    /**
     * 创建BeanCopier实例
     *
     * @param source 源类
     * @param target 目标类
     * @param classLoader 自定义类加载器
     * @param useConverter 是否使用转换器
     * @return BeanCopier实例
     */
    public static BaseBeanCopier create(Class source, Class target, ClassLoader classLoader, boolean useConverter) {
        Generator gen;
        if (classLoader == null) {
            gen = new Generator();
        } else {
            gen = new Generator(classLoader);
        }
        gen.setSource(source);
        gen.setTarget(target);
        gen.setUseConverter(useConverter);
        return gen.create();
    }

    /**
     * 执行Bean拷贝操作
     *
     * @param from 源对象
     * @param to 目标对象
     * @param converter 类型转换器（可选）
     */
    abstract public void copy(Object from, Object to, Converter converter);

    /**
     * BeanCopier生成器
     * <p>负责生成具体的BeanCopier实现类
     */
    public static class Generator extends AbstractClassGenerator {
        // 用于生成BeanCopier的源标识
        private static final Source SOURCE = new Source(BaseBeanCopier.class.getName());
        // 自定义类加载器
        private final ClassLoader classLoader;
        // 源类
        private Class source;
        // 目标类
        private Class target;
        // 是否使用转换器
        private boolean useConverter;

        /**
         * 默认构造函数，使用默认类加载器
         */
        Generator() {
            super(SOURCE);
            this.classLoader = null;
        }

        /**
         * 使用指定类加载器的构造函数
         *
         * @param classLoader 自定义类加载器
         */
        Generator(ClassLoader classLoader) {
            super(SOURCE);
            this.classLoader = classLoader;
        }

        /**
         * 设置源类
         * <p>如果源类不是public的，设置名称前缀
         *
         * @param source 源类
         */
        public void setSource(Class source) {
            // 如果源类不是public的，设置生成类的名称前缀
            if (!Modifier.isPublic(source.getModifiers())) {
                setNamePrefix(source.getName());
            }
            this.source = source;
        }

        /**
         * 设置目标类
         * <p>如果目标类不是public的，设置名称前缀
         *
         * @param target 目标类
         */
        public void setTarget(Class target) {
            // 如果目标类不是public的，设置生成类的名称前缀
            if (!Modifier.isPublic(target.getModifiers())) {
                setNamePrefix(target.getName());
            }
            this.target = target;
        }

        /**
         * 设置是否使用转换器
         *
         * @param useConverter 是否使用转换器
         */
        public void setUseConverter(boolean useConverter) {
            this.useConverter = useConverter;
        }

        /**
         * 获取默认的类加载器
         * <p>优先使用目标类的类加载器
         *
         * @return 类加载器
         */
        @Override
        protected ClassLoader getDefaultClassLoader() {
            // 优先使用目标类的类加载器
            return target.getClassLoader();
        }

        /**
         * 获取保护域
         * <p>使用源类的保护域
         *
         * @return 保护域
         */
        @Override
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(source);
        }

        /**
         * 创建BeanCopier实例
         *
         * @return BeanCopier实例
         */
        public BaseBeanCopier create() {
            // 生成唯一标识key
            Object key = KEY_FACTORY.newInstance(source.getName(), target.getName(), useConverter);
            return (BaseBeanCopier) super.create(key);
        }

        /**
         * 生成BeanCopier类的字节码
         *
         * @param v ClassVisitor用于生成类
         */
        @Override
        public void generateClass(ClassVisitor v) {
            // 创建源类和目标类的Type
            Type sourceType = Type.getType(source);
            Type targetType = Type.getType(target);
            
            // 创建类生成器
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_2,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    BEAN_COPIER,
                    null,
                    Constants.SOURCE_FILE);

            // 生成默认构造函数
            EmitUtils.null_constructor(ce);
            
            // 生成copy方法
            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);

            // 获取源类的getter方法和目标类的setter方法
            PropertyDescriptor[] getters = BeanUtil.getBeanGetters(source);
            PropertyDescriptor[] setters = BeanUtil.getBeanSetters(target);
            
            // 创建属性名映射
            Map<String, Object> names = new HashMap<>(16);
            for (PropertyDescriptor getter : getters) {
                names.put(getter.getName(), getter);
            }

            // 创建本地变量
            Local targetLocal = e.make_local();
            Local sourceLocal = e.make_local();
            
            // 加载并转换参数
            e.load_arg(1);
            e.checkcast(targetType);
            e.store_local(targetLocal);
            e.load_arg(0);
            e.checkcast(sourceType);
            e.store_local(sourceLocal);

            // 遍历所有setter方法，查找匹配的getter方法并生成复制代码
            for (PropertyDescriptor setter : setters) {
                PropertyDescriptor getter = (PropertyDescriptor) names.get(setter.getName());
                if (getter != null) {
                    MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
                    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
                    
                    // 根据是否使用转换器生成不同的复制代码
                    if (useConverter) {
                        // 使用转换器进行类型转换
                        Type setterType = write.getSignature().getArgumentTypes()[0];
                        e.load_local(targetLocal);
                        e.load_arg(2);
                        e.load_local(sourceLocal);
                        e.invoke(read);
                        e.box(read.getSignature().getReturnType());
                        EmitUtils.load_class(e, setterType);
                        e.push(write.getSignature().getName());
                        e.invoke_interface(CONVERTER, CONVERT);
                        e.unbox_or_zero(setterType);
                        e.invoke(write);
                    } else if (compatible(getter, setter)) {
                        // 直接复制兼容的属性
                        e.load_local(targetLocal);
                        e.load_local(sourceLocal);
                        e.invoke(read);
                        e.invoke(write);
                    }
                }
            }
            
            // 生成返回语句
            e.return_value();
            e.end_method();
            ce.end_class();
        }

        /**
         * 检查getter和setter的属性类型是否兼容
         *
         * @param getter getter方法描述符
         * @param setter setter方法描述符
         * @return 如果类型兼容返回true
         */
        private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
            // 检查setter的属性类型是否可以接收getter的属性类型
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
        }

        /**
         * 创建第一个实例
         *
         * @param type 生成的类型
         * @return 新创建的实例
         */
        @Override
        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        /**
         * 创建后续实例
         * <p>直接返回已有实例，因为BeanCopier是无状态的
         *
         * @param instance 已有实例
         * @return 相同的实例
         */
        @Override
        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
