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

import java.io.OutputStream;
import java.io.IOException;

/**
 * 多输出流工厂接口
 * 
 * <p>用于创建支持多目标的输出流，主要用于：
 * <ul>
 *   <li>同时输出到多个目标（如文件和控制台）</li>
 *   <li>根据参数动态构建输出流</li>
 *   <li>支持自定义输出流组合</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 实现接口
 * public class FileAndConsoleOutputStream implements IMultiOutputStream {
 *     @Override
 *     public OutputStream buildOutputStream(Integer... params) {
 *         // 创建文件输出流
 *         FileOutputStream fileOut = new FileOutputStream("output.log");
 *         // 获取控制台输出流
 *         PrintStream consoleOut = System.out;
 *         // 返回组合输出流
 *         return new MultiOutputStream(fileOut, consoleOut);
 *     }
 * }
 * 
 * // 2. 使用实现类
 * IMultiOutputStream factory = new FileAndConsoleOutputStream();
 * try (OutputStream out = factory.buildOutputStream(1, 2)) {
 *     out.write("Hello World".getBytes());
 * }
 * }</pre>
 *
 * @author Ken
 * @see OutputStream
 */
public interface IMultiOutputStream {

    /**
     * 构建输出流
     * <p>根据提供的参数创建一个组合输出流。参数可以用于：
     * <ul>
     *   <li>指定输出目标</li>
     *   <li>配置缓冲区大小</li>
     *   <li>设置其他输出选项</li>
     * </ul>
     *
     * @param params 可变参数，用于配置输出流
     * @return 构建的输出流
     * @throws IOException 如果创建输出流时发生IO错误
     */
    OutputStream buildOutputStream(Integer... params);

}
