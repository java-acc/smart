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

package cn.org.byc.smart.oss.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Ken
 */
@Data
public class OssFile {

    /**
     * 文件地址
     */
    private String link;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件hash值
     */
    private String hash;

    /**
     * 文件大小
     */
    private long length;

    /**
     * 文件上传时间
     */
    private Date putTime;

    /**
     * 文件Content Type
     */
    private String contentType;
}
