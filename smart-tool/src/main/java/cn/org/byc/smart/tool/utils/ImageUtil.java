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

import cn.org.byc.smart.tool.supports.IMultiOutputStream;
import cn.org.byc.smart.tool.supports.ImagePosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * 图片处理工具类，提供丰富的图片操作功能。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>图片读取和转换</li>
 *   <li>图片缩放（按比例或固定尺寸）</li>
 *   <li>图片裁剪</li>
 *   <li>图片分割</li>
 *   <li>格式转换</li>
 *   <li>灰度处理</li>
 *   <li>水印添加（文字和图片）</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 读取图片
 * BufferedImage image = ImageUtil.readImage("input.jpg");
 * 
 * // 缩放图片
 * try (FileOutputStream output = new FileOutputStream("output.jpg")) {
 *     // 按比例缩放到 50%
 *     ImageUtil.zoomScale(image, output, "JPEG", 2.0, false);
 *     
 *     // 缩放到固定尺寸 800x600
 *     ImageUtil.zoomFixed(image, output, "JPEG", 600, 800, true, Color.WHITE);
 * }
 * 
 * // 添加水印
 * try (FileOutputStream output = new FileOutputStream("watermark.jpg")) {
 *     // 添加文字水印
 *     ImageUtil.textStamp(image, output, "JPEG", "Copyright", new Font("Arial", Font.BOLD, 18),
 *         Color.WHITE, ImagePosition.BOTTOM_RIGHT, 10, 10, 0.5f);
 * }
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public final class ImageUtil {
    /**
     * Logger for this class
     */
    private static Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * 默认输出图片类型
     */
    public static final String DEFAULT_IMG_TYPE = "JPEG";

    private ImageUtil() {

    }

    /**
     * 将 BufferedImage 转换为字节数组
     * 
     * <p>使用示例：
     * <pre>{@code
     * BufferedImage image = ImageUtil.readImage("test.jpg");
     * byte[] bytes = ImageUtil.toByteArray(image, "JPEG");
     * }</pre>
     *
     * @param src 源图像
     * @param type 图片类型（如 "JPEG"、"PNG"、"GIF" 等）
     * @return 图片的字节数组
     * @throws IOException 如果转换过程中发生 IO 错误
     */
    public static byte[] toByteArray(BufferedImage src, String type) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(src, defaultString(type, DEFAULT_IMG_TYPE), os);
        return os.toByteArray();
    }

    /**
     * 获取图像内容
     *
     * @param srcImageFile 文件路径
     * @return BufferedImage
     */
    public static BufferedImage readImage(String srcImageFile) {
        try {
            return ImageIO.read(new File(srcImageFile));
        } catch (IOException e) {
            LOGGER.error("Error readImage", e);
        }
        return null;
    }

    /**
     * 获取图像内容
     *
     * @param srcImageFile 文件
     * @return BufferedImage
     */
    public static BufferedImage readImage(File srcImageFile) {
        try {
            return ImageIO.read(srcImageFile);
        } catch (IOException e) {
            LOGGER.error("Error readImage", e);
        }
        return null;
    }

    /**
     * 获取图像内容
     *
     * @param srcInputStream 输入流
     * @return BufferedImage
     */
    public static BufferedImage readImage(InputStream srcInputStream) {
        try {
            return ImageIO.read(srcInputStream);
        } catch (IOException e) {
            LOGGER.error("Error readImage", e);
        }
        return null;
    }

    /**
     * 获取图像内容
     *
     * @param url URL地址
     * @return BufferedImage
     */
    public static BufferedImage readImage(URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            LOGGER.error("Error readImage", e);
        }
        return null;
    }


    /**
     * 缩放图像（按比例缩放）
     *
     * @param src    源图像
     * @param output 输出流
     * @param type   类型
     * @param scale  缩放比例
     * @param flag   缩放选择:true 放大; false 缩小;
     */
    public final static void zoomScale(BufferedImage src, OutputStream output, String type, double scale, boolean flag) {
        try {
            // 得到源图宽
            int width = src.getWidth();
            // 得到源图长
            int height = src.getHeight();
            if (flag) {
                // 放大
                width = Long.valueOf(Math.round(width * scale)).intValue();
                height = Long.valueOf(Math.round(height * scale)).intValue();
            } else {
                // 缩小
                width = Long.valueOf(Math.round(width / scale)).intValue();
                height = Long.valueOf(Math.round(height / scale)).intValue();
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();

            g.drawImage(image, 0, 0, null);
            g.dispose();

            ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), output);

            output.close();
        } catch (IOException e) {
            LOGGER.error("Error in zoom image", e);
        }
    }

    /**
     * 缩放图像（按高度和宽度缩放）
     *
     * @param src       源图像
     * @param output    输出流
     * @param type      类型
     * @param height    缩放后的高度
     * @param width     缩放后的宽度
     * @param bb        比例不对时是否需要补白：true为补白; false为不补白;
     * @param fillColor 填充色，null时为Color.WHITE
     */
    public final static void zoomFixed(BufferedImage src, OutputStream output, String type, int height, int width, boolean bb, Color fillColor) {
        try {
            double ratio = 0.0;
            Image itemp = src.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            // 计算比例
            if (src.getHeight() > src.getWidth()) {
                ratio = Integer.valueOf(height).doubleValue() / src.getHeight();
            } else {
                ratio = Integer.valueOf(width).doubleValue() / src.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            itemp = op.filter(src, null);

            if (bb) {
                //补白
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                Color fill = fillColor == null ? Color.white : fillColor;
                g.setColor(fill);
                g.fillRect(0, 0, width, height);
                if (width == itemp.getWidth(null)) {
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), fill, null);
                } else {
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), fill, null);
                }
                g.dispose();
                itemp = image;
            }
            // 输出为文件
            ImageIO.write((BufferedImage) itemp, defaultString(type, DEFAULT_IMG_TYPE), output);
            // 关闭流
            output.close();
        } catch (IOException e) {
            LOGGER.error("Error in zoom image", e);
        }
    }

    /**
     * 图像裁剪(按指定起点坐标和宽高切割)
     *
     * @param src    源图像
     * @param output 切片后的图像地址
     * @param type   类型
     * @param x      目标切片起点坐标X
     * @param y      目标切片起点坐标Y
     * @param width  目标切片宽度
     * @param height 目标切片高度
     */
    public final static void crop(BufferedImage src, OutputStream output, String type, int x, int y, int width, int height) {
        try {
            // 源图宽度
            int srcWidth = src.getHeight();
            // 源图高度
            int srcHeight = src.getWidth();
            if (srcWidth > 0 && srcHeight > 0) {
                Image image = src.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                // 四个参数分别为图像起点坐标和宽高
                ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
                Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, width, height, null);
                g.dispose();
                // 输出为文件
                ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), output);
                // 关闭流
                output.close();
            }
        } catch (Exception e) {
            LOGGER.error("Error in cut image", e);
        }
    }

    /**
     * 将图像按指定行列数进行分割
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 将图片分割为 3x3 的九宫格
     * ImageUtil.sliceWithNumber(image, multiOutputStream, "JPEG", 3, 3);
     * }</pre>
     *
     * @param src 源图像
     * @param mos 多重输出流，用于输出多个分割后的图片
     * @param type 图片类型（如 "JPEG"、"PNG"、"GIF" 等）
     * @param prows 分割的行数
     * @param pcols 分割的列数
     */
    public final static void sliceWithNumber(BufferedImage src, IMultiOutputStream mos, String type, int prows, int pcols) {
        try {
            int rows = prows <= 0 || prows > 20 ? 2 : prows;
            int cols = pcols <= 0 || pcols > 20 ? 2 : pcols;
            // 源图宽度
            int srcWidth = src.getHeight();
            // 源图高度
            int srcHeight = src.getWidth();
            if (srcWidth > 0 && srcHeight > 0) {
                Image img;
                ImageFilter cropFilter;
                Image image = src.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                // 每张切片的宽度
                int destWidth = (srcWidth % cols == 0) ? (srcWidth / cols) : (srcWidth / cols + 1);
                // 每张切片的高度
                int destHeight = (srcHeight % rows == 0) ? (srcHeight / rows) : (srcHeight / rows + 1);
                // 循环建立切片
                // 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        // 绘制缩小后的图
                        g.drawImage(img, 0, 0, null);
                        g.dispose();
                        // 输出为文件
                        ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), mos.buildOutputStream(i, j));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in slice image", e);
        }
    }

    /**
     * 将图像按指定尺寸进行分割
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 将图片按 200x200 的尺寸进行分割
     * ImageUtil.sliceWithSize(image, multiOutputStream, "JPEG", 200, 200);
     * }</pre>
     *
     * @param src 源图像
     * @param mos 多重输出流，用于输出多个分割后的图片
     * @param type 图片类型（如 "JPEG"、"PNG"、"GIF" 等）
     * @param pdestWidth 分割后的图片宽度
     * @param pdestHeight 分割后的图片高度
     */
    public final static void sliceWithSize(BufferedImage src, IMultiOutputStream mos, String type, int pdestWidth, int pdestHeight) {
        try {
            int destWidth = pdestWidth <= 0 ? 200 : pdestWidth;
            int destHeight = pdestHeight <= 0 ? 150 : pdestHeight;
            // 源图宽度
            int srcWidth = src.getHeight();
            // 源图高度
            int srcHeight = src.getWidth();
            if (srcWidth > destWidth && srcHeight > destHeight) {
                Image img;
                ImageFilter cropFilter;
                Image image = src.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                // 切片横向数量
                int cols = (srcWidth % destWidth == 0) ? (srcWidth / destWidth) : (srcWidth / destWidth + 1);
                // 切片纵向数量
                int rows = (srcHeight % destHeight == 0) ? (srcHeight / destHeight) : (srcHeight / destHeight + 1);
                // 循环建立切片
                // 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        // 绘制缩小后的图
                        g.drawImage(img, 0, 0, null);
                        g.dispose();
                        // 输出为文件
                        ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), mos.buildOutputStream(i, j));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in slice image", e);
        }
    }

    /**
     * 转换图片格式
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 将图片转换为 PNG 格式
     * ImageUtil.convert(image, output, "PNG");
     * }</pre>
     *
     * @param src 源图像
     * @param output 输出流
     * @param formatName 目标格式名称（如 "JPEG"、"PNG"、"GIF" 等）
     */
    public final static void convert(BufferedImage src, OutputStream output, String formatName) {
        try {
            ImageIO.write(src, formatName, output);
        } catch (IOException e) {
            LOGGER.error("Error in convert image", e);
        }
    }

    /**
     * 将图像转换为灰度图
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 转换为灰度图
     * ImageUtil.gray(image, output, "JPEG");
     * }</pre>
     *
     * @param src 源图像
     * @param output 输出流
     * @param type 图片类型（如 "JPEG"、"PNG"、"GIF" 等）
     */
    public final static void gray(BufferedImage src, OutputStream output, String type) {
        try {
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
            ImageIO.write(src, defaultString(type, DEFAULT_IMG_TYPE), output);
        } catch (IOException e) {
            LOGGER.error("Error in gray image", e);
        }
    }

    /**
     * 给图片添加文字水印
     *
     * @param src      源图像
     * @param output   输出流
     * @param type      类型
     * @param text     水印文字
     * @param font     水印的字体
     * @param color    水印的字体颜色
     * @param position 水印位置 {@link ImagePosition}
     * @param x        修正值
     * @param y        修正值
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public final static void textStamp(BufferedImage src, OutputStream output, String type, String text, Font font, Color color
            , int position, int x, int y, float alpha) {
        try {
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(font);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 在指定坐标绘制水印文字
            ImagePosition boxPos = new ImagePosition(width, height, calcTextWidth(text) * font.getSize(), font.getSize(), position);
            g.drawString(text, boxPos.getX(x), boxPos.getY(y));
            g.dispose();
            // 输出为文件
            ImageIO.write((BufferedImage) image, defaultString(type, DEFAULT_IMG_TYPE), output);
            // 关闭流
            output.close();
        } catch (Exception e) {
            LOGGER.error("Error in textStamp image", e);
        }
    }

    /**
     * 给图片添加图片水印
     *
     * @param src      源图像
     * @param output   输出流
     * @param type      类型
     * @param stamp    水印图片
     * @param position 水印位置 {@link ImagePosition}
     * @param x        修正值
     * @param y        修正值
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public final static void imageStamp(BufferedImage src, OutputStream output, String type, BufferedImage stamp
            , int position, int x, int y, float alpha) {
        try {
            int width = src.getWidth();
            int height = src.getHeight();
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            // 水印文件
            int stampWidth = stamp.getWidth();
            int stampHeight = stamp.getHeight();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            ImagePosition boxPos = new ImagePosition(width, height, stampWidth, stampHeight, position);
            g.drawImage(stamp, boxPos.getX(x), boxPos.getY(y), stampWidth, stampHeight, null);
            // 水印文件结束
            g.dispose();
            // 输出为文件
            ImageIO.write((BufferedImage) image, defaultString(type, DEFAULT_IMG_TYPE), output);
            // 关闭流
            output.close();
        } catch (Exception e) {
            LOGGER.error("Error imageStamp", e);
        }
    }

    /**
     * 计算文本的像素宽度
     * 
     * <p>使用示例：
     * <pre>{@code
     * int width = ImageUtil.calcTextWidth("Hello, World!");
     * System.out.println("文本宽度: " + width + "像素");
     * }</pre>
     *
     * @param text 要计算宽度的文本
     * @return 文本的像素宽度
     */
    public final static int calcTextWidth(String text) {
        return text == null ? 0 : text.length() * 6;
    }

    /**
     * 获取默认字符串值
     * 
     * <p>如果字符串为 null 或空，则返回默认值。
     * 
     * <p>使用示例：
     * <pre>{@code
     * String type = ImageUtil.defaultString(null, "JPEG");  // 返回 "JPEG"
     * String type2 = ImageUtil.defaultString("PNG", "JPEG");  // 返回 "PNG"
     * }</pre>
     *
     * @param str 要检查的字符串
     * @param defaultStr 默认值
     * @return 如果 str 为 null 或空则返回 defaultStr，否则返回 str
     */
    public static String defaultString(String str, String defaultStr) {
        return str == null || str.length() == 0 ? defaultStr : str;
    }
}
