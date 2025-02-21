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

package cn.org.byc.smart.tool.jackson;

import cn.org.byc.smart.tool.utils.DateTimeUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SmartJavaTimeModule extends SimpleModule {
    public SmartJavaTimeModule() {
        super(PackageVersion.VERSION);
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeUtil.DATETIME_FORMAT));
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeUtil.DATE_FORMAT));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeUtil.TIME_FORMAT));
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeUtil.DATETIME_FORMAT));
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeUtil.DATE_FORMAT));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeUtil.TIME_FORMAT));
    }
}
