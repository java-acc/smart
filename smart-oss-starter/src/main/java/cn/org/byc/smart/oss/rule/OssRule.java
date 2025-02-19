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

package cn.org.byc.smart.oss.rule;

import cn.org.byc.smart.tool.constant.StringPool;
import cn.org.byc.smart.tool.id.IdFactory;
import cn.org.byc.smart.tool.utils.DateUtil;

public interface OssRule {

    /**
     *  获取存储桶名称
     *
     * @param bucketName
     * @return
     */
    String bucketName(String bucketName);

    /**
     * 获取文件名规则
     * @param originalFileName
     * @return
     */
    default String fileName(String originalFileName){
        int dotIndex = originalFileName.lastIndexOf(StringPool.DOT);

        // eg: upload/20250219/1234567.xxx
        return "upload" + StringPool.SLASH +
                DateUtil.today() + StringPool.SLASH +
                IdFactory.getInstance().getLocalId() + StringPool.DOT +
                (dotIndex == -1 ? StringPool.EMPTY : originalFileName.substring(dotIndex + 1));
    }
}
