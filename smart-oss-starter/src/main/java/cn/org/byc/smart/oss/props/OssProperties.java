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

package cn.org.byc.smart.oss.props;

import cn.org.byc.smart.tool.supports.Kv;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Data
@ConditionalOnProperty(prefix = "smart.oss")
public class OssProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 对象存储名称
     */
    private String name;

    /**
     * 是否开启租户模式
     */
    private Boolean tenantMode;

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * access key
     */
    private String accessKey;

    /**
     * secret key
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName="smart";

    /**
     * 其他的自定义属性
     */
    private Kv args;
}
