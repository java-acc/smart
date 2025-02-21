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

import cn.hutool.core.text.CharPool;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类，提供丰富的文件处理方法。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>文件读写操作</li>
 *   <li>文件扫描和过滤</li>
 *   <li>文件名和路径处理</li>
 *   <li>临时文件操作</li>
 *   <li>文件移动和删除</li>
 *   <li>MultipartFile 处理</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 文件扫描
 * List<File> files = FileUtil.list("/path/to/dir", "*.txt");
 * 
 * // 文件读写
 * String content = FileUtil.readToString(new File("test.txt"));
 * FileUtil.writeToFile(new File("test.txt"), "Hello World");
 * 
 * // 文件名处理
 * String ext = FileUtil.getFileExtension("test.txt");  // 返回: "txt"
 * String name = FileUtil.getNameWithoutExtension("test.txt");  // 返回: "test"
 * 
 * // 临时文件
 * File tempDir = FileUtil.getTempDir();
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
@UtilityClass
public class FileUtil extends FileCopyUtils {
    /**
     * 默认文件过滤器，接受所有文件
     */
    public static class TrueFilter implements FileFilter, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        public final static TrueFilter TRUE = new TrueFilter();

        @Override
        public boolean accept(File pathname) {
            return true;
        }
    }

    /**
     * 扫描目录下的所有文件
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 扫描目录下所有文件
     * List<File> files = FileUtil.list("/path/to/dir");
     * 
     * // 遍历文件
     * for (File file : files) {
     *     System.out.println(file.getName());
     * }
     * }</pre>
     *
     * @param path 要扫描的目录路径
     * @return 文件列表
     */
    public static List<File> list(String path) {
        File file = new File(path);
        return list(file, TrueFilter.TRUE);
    }

    /**
     * 使用通配符模式扫描目录下的文件
     * 
     * <p>支持的通配符：
     * <ul>
     *   <li>* - 匹配任意数量的字符</li>
     *   <li>? - 匹配单个字符</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 扫描所有 .txt 文件
     * List<File> txtFiles = FileUtil.list("/path/to/dir", "*.txt");
     * 
     * // 扫描特定前缀的文件
     * List<File> logFiles = FileUtil.list("/path/to/dir", "app-*.log");
     * }</pre>
     *
     * @param path 要扫描的目录路径
     * @param fileNamePattern 文件名匹配模式
     * @return 匹配的文件列表
     */
    public static List<File> list(String path, final String fileNamePattern) {
        File file = new File(path);
        return list(file, pathname -> {
            String fileName = pathname.getName();
            return PatternMatchUtils.simpleMatch(fileNamePattern, fileName);
        });
    }

    /**
     * 扫描目录下的文件
     *
     * @param path   路径
     * @param filter 文件过滤
     * @return 文件集合
     */
    public static List<File> list(String path, FileFilter filter) {
        File file = new File(path);
        return list(file, filter);
    }

    /**
     * 扫描目录下的文件
     *
     * @param file 文件
     * @return 文件集合
     */
    public static List<File> list(File file) {
        List<File> fileList = new ArrayList<>();
        return list(file, fileList, TrueFilter.TRUE);
    }

    /**
     * 扫描目录下的文件
     *
     * @param file   文件
     * @param fileNamePattern Spring AntPathMatcher 规则
     * @return 文件集合
     */
    public static List<File> list(File file, final String fileNamePattern) {
        List<File> fileList = new ArrayList<>();
        return list(file, fileList, pathname -> {
            String fileName = pathname.getName();
            return PatternMatchUtils.simpleMatch(fileNamePattern, fileName);
        });
    }

    /**
     * 扫描目录下的文件
     *
     * @param file   文件
     * @param filter 文件过滤
     * @return 文件集合
     */
    public static List<File> list(File file, FileFilter filter) {
        List<File> fileList = new ArrayList<>();
        return list(file, fileList, filter);
    }

    /**
     * 扫描目录下的文件
     *
     * @param file   文件
     * @param filter 文件过滤
     * @return 文件集合
     */
    private static List<File> list(File file, List<File> fileList, FileFilter filter) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    list(f, fileList, filter);
                }
            }
        } else {
            // 过滤文件
            boolean accept = filter.accept(file);
            if (file.exists() && accept) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * 获取文件后缀名
     * @param fullName 文件全名
     * @return {String}
     */
    public static String getFileExtension(String fullName) {
        Assert.notNull(fullName, "file fullName is null.");
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * 获取文件名，去除后缀名
     * @param file 文件
     * @return {String}
     */
    public static String getNameWithoutExtension(String file) {
        Assert.notNull(file, "file is null.");
        String fileName = new File(file).getName();
        int dotIndex = fileName.lastIndexOf(CharPool.DOT);
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    /**
     * Returns the path to the system temporary directory.
     *
     * @return the path to the system temporary directory.
     */
    public static String getTempDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Returns a {@link File} representing the system temporary directory.
     *
     * @return the system temporary directory.
     */
    public static File getTempDir() {
        return new File(getTempDirPath());
    }

    /**
     * 读取文件内容为字符串
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 读取文件内容
     * String content = FileUtil.readToString(new File("test.txt"));
     * 
     * // 使用指定字符集读取
     * String content2 = FileUtil.readToString(new File("test.txt"), Charsets.UTF_8);
     * }</pre>
     *
     * @param file 要读取的文件
     * @return 文件内容
     * @throws RuntimeException 如果读取过程中发生 IO 错误
     */
    public static String readToString(final File file) {
        return readToString(file, Charsets.UTF_8);
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file     the file to read, must not be {@code null}
     * @param encoding the encoding to use, {@code null} means platform default
     * @return the file contents, never {@code null}
     */
    public static String readToString(final File file, final Charset encoding) {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            return IoUtil.toString(in, encoding);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file     the file to read, must not be {@code null}
     * @return the file contents, never {@code null}
     */
    public static byte[] readToByteArray(final File file) {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            return IoUtil.toByteArray(in);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将字符串写入文件
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 写入文件内容（覆盖模式）
     * FileUtil.writeToFile(new File("test.txt"), "Hello World");
     * 
     * // 追加模式写入
     * FileUtil.writeToFile(new File("test.txt"), "New Content", true);
     * 
     * // 使用指定字符集写入
     * FileUtil.writeToFile(new File("test.txt"), "你好", Charsets.UTF_8);
     * }</pre>
     *
     * @param file 目标文件
     * @param data 要写入的内容
     * @throws RuntimeException 如果写入过程中发生 IO 错误
     */
    public static void writeToFile(final File file, final String data) {
        writeToFile(file, data, Charsets.UTF_8, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file   the file to write
     * @param data   the content to write to the file
     * @param append if {@code true}, then the String will be added to the
     *               end of the file rather than overwriting
     */
    public static void writeToFile(final File file, final String data, final boolean append){
        writeToFile(file, data, Charsets.UTF_8, append);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file     the file to write
     * @param data     the content to write to the file
     * @param encoding the encoding to use, {@code null} means platform default
     */
    public static void writeToFile(final File file, final String data, final Charset encoding) {
        writeToFile(file, data, encoding, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file     the file to write
     * @param data     the content to write to the file
     * @param encoding the encoding to use, {@code null} means platform default
     * @param append   if {@code true}, then the String will be added to the
     *                 end of the file rather than overwriting
     */
    public static void writeToFile(final File file, final String data, final Charset encoding, final boolean append) {
        try (OutputStream out = new FileOutputStream(file, append)) {
            IoUtil.write(data, out, encoding);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将 MultipartFile 转换为标准 File
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 在 Spring MVC 控制器中处理上传文件
     * @PostMapping("/upload")
     * public void handleFileUpload(@RequestParam("file") MultipartFile file) {
     *     File dest = new File("/path/to/dest/file.txt");
     *     FileUtil.toFile(file, dest);
     * }
     * }</pre>
     *
     * @param multipartFile Spring 上传的文件对象
     * @param file 要保存到的目标文件
     * @throws RuntimeException 如果转换过程中发生 IO 错误
     */
    public static void toFile(MultipartFile multipartFile, final File file) {
        try {
            toFile(multipartFile.getInputStream(), file);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 转成file
     * @param in InputStream
     * @param file File
     */
    public static void toFile(InputStream in, final File file) {
        try (OutputStream out = new FileOutputStream(file)) {
            FileUtil.copy(in, out);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * Moves a file.
     * <p>
     * When the destination file is on another file system, do a "copy and delete".
     *
     * @param srcFile  the file to be moved
     * @param destFile the destination file
     * @throws NullPointerException if source or destination is {@code null}
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs moving the file
     */
    public static void moveFile(final File srcFile, final File destFile) throws IOException {
        Assert.notNull(srcFile, "Source must not be null");
        Assert.notNull(destFile, "Destination must not be null");
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.exists()) {
            throw new IOException("Destination '" + destFile + "' already exists");
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }
        final boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            FileUtil.copy(srcFile, destFile);
            if (!srcFile.delete()) {
                FileUtil.deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
            }
        }
    }

    /**
     * 安静地删除文件（不抛出异常）
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 删除文件
     * boolean deleted = FileUtil.deleteQuietly(new File("temp.txt"));
     * 
     * // 删除目录及其内容
     * boolean deleted = FileUtil.deleteQuietly(new File("/path/to/dir"));
     * }</pre>
     *
     * @param file 要删除的文件或目录
     * @return 如果删除成功返回 true，否则返回 false
     */
    public static boolean deleteQuietly(@Nullable final File file) {
        if (file == null) {
            return false;
        }
        try {
            return FileSystemUtils.deleteRecursively(file);
        } catch (Exception ignored) {
            return false;
        }
    }
}
