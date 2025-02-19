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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
public class JsonUtil {
    public static ObjectMapper getInstance(){
        return JacksonHolder.INSTANCE;
    }

    private static class JacksonHolder{
        private static ObjectMapper INSTANCE = new JacksonObjectMapper();
    }

    private static class JacksonObjectMapper extends ObjectMapper {
        @Serial
        private static final long serialVersionUID = 1L;

        private static final Locale CHINA = Locale.CHINA;

        public JacksonObjectMapper(){
            super();
            super.setLocale(CHINA);
            super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
            super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            // 处理单引号
            super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
            super.findAndRegisterModules();
        }
    }

    /**
     * 对象序列化为Json字符串
     *
     * @param value javaBean
     * @return jsonString
     * @param <T> T 泛型标记
     */
    public static <T> String toJson(T value){
        try {
            return getInstance().writeValueAsString(value);
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
