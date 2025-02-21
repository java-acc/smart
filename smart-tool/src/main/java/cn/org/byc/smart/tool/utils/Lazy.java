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
 *
 * @author Ken
 */
public class Lazy<T> implements Supplier<T>, Serializable {

    @Nullable
    private transient volatile Supplier<? extends T> supplier;
    @Nullable
    private T value;

    /**
     * Creates new instance of Lazy.
     *
     * @param supplier Supplier
     * @param <T>      泛型标记
     * @return Lazy
     */
    public static <T> Lazy<T> of(final Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    private Lazy(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns the value. Value will be computed on first call.
     *
     * @return lazy value
     */
    @Nullable
    @Override
    public T get() {
        return (supplier == null) ? value : computeValue();
    }

    @Nullable
    private synchronized T computeValue() {
        final Supplier<? extends T> s = supplier;
        if (s != null) {
            value = s.get();
            supplier = null;
        }
        return value;
    }

}
