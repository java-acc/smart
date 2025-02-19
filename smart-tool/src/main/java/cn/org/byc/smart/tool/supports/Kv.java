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

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.HashMap;

/**
 * @author Ken
 */
public class Kv extends LinkedCaseInsensitiveMap<Object> {
    private Kv(){
        super();
    }

    public static Kv init(){
        return new Kv();
    }

    public static <K,V> HashMap<K,V> newMap(){
        return new HashMap<>(8);
    }

    public Kv set(String key, Object val){
        this.put(key,val);
        return this;
    }

    public Kv setIgnoreNull(String key, Object val){
        if (null != key && null != val){
            set(key, val);
        }
        return this;
    }

    public Object getObj(String key){
        return super.get(key);
    }

    public <T> T get(String key, T defaultValue){
        final Object val = get(key);
        return (T) (val == null ? defaultValue : val);
    }

    public String getStr(String key){
        Object val = get(key, null);
        if (val == null){
            return null;
        }
        return String.valueOf(val);
    }
}
