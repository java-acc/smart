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

/**
 * 图片位置计算工具类
 * 
 * <p>用于计算图片中水印、文字等元素的位置。支持以下功能：
 * <ul>
 *   <li>支持9个基本定位点（上中下 x 左中右）</li>
 *   <li>支持边距自动计算</li>
 *   <li>支持组合定位（如右上角、左下角等）</li>
 *   <li>提供像素级精确定位</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 创建位置计算器（参数：图片宽高、水印宽高、位置样式）
 * ImagePosition pos = new ImagePosition(1000, 800, 100, 50, 
 *     ImagePosition.TOP | ImagePosition.RIGHT); // 右上角
 * 
 * // 2. 获取计算后的坐标
 * int x = pos.getX();  // X坐标
 * int y = pos.getY();  // Y坐标
 * 
 * // 3. 使用偏移量微调位置
 * int adjustedX = pos.getX(10);  // 向右偏移10像素
 * int adjustedY = pos.getY(-5);  // 向上偏移5像素
 * }</pre>
 *
 * @author Ken
 */
public class ImagePosition {

    // 垂直位置常量
    /** 图片顶部位置 */
    public static final int TOP = 32;
    /** 图片中部位置 */
    public static final int MIDDLE = 16;
    /** 图片底部位置 */
    public static final int BOTTOM = 8;

    // 水平位置常量
    /** 图片左侧位置 */
    public static final int LEFT = 4;
    /** 图片居中位置 */
    public static final int CENTER = 2;
    /** 图片右侧位置 */
    public static final int RIGHT = 1;

    // 边距常量
    /** 水平方向边距（左右） */
    private static final int PADDING_HORI = 6;
    /** 垂直方向边距（上下） */
    private static final int PADDING_VERT = 6;

    // 计算后的坐标
    /** 图片中盒[左上角]的x坐标 */
    private int boxPosX;
    /** 图片中盒[左上角]的y坐标 */
    private int boxPosY;

    /**
     * 构造函数，计算元素在图片中的位置
     *
     * @param width 图片宽度
     * @param height 图片高度
     * @param boxWidth 元素宽度
     * @param boxHeight 元素高度
     * @param style 位置样式（组合使用位置常量，如TOP | RIGHT）
     */
    public ImagePosition(int width, int height, int boxWidth, int boxHeight, int style) {
        // 计算水平位置
        switch (style & 7) {
            case LEFT:
                // 左对齐，加上左边距
                boxPosX = PADDING_HORI;
                break;
            case RIGHT:
                // 右对齐，减去右边距和元素宽度
                boxPosX = width - boxWidth - PADDING_HORI;
                break;
            case CENTER:
            default:
                // 水平居中
                boxPosX = (width - boxWidth) / 2;
        }

        // 计算垂直位置
        switch (style >> 3 << 3) {
            case TOP:
                // 顶部对齐，加上上边距
                boxPosY = PADDING_VERT;
                break;
            case MIDDLE:
                // 垂直居中
                boxPosY = (height - boxHeight) / 2;
                break;
            case BOTTOM:
            default:
                // 底部对齐，减去下边距和元素高度
                boxPosY = height - boxHeight - PADDING_VERT;
        }
    }

    /**
     * 获取X坐标
     *
     * @return X坐标值
     */
    public int getX() {
        return getX(0);
    }

    /**
     * 获取带偏移量的X坐标
     *
     * @param x 水平偏移量（正值向右偏移，负值向左偏移）
     * @return 计算偏移后的X坐标值
     */
    public int getX(int x) {
        return this.boxPosX + x;
    }

    /**
     * 获取Y坐标
     *
     * @return Y坐标值
     */
    public int getY() {
        return getY(0);
    }

    /**
     * 获取带偏移量的Y坐标
     *
     * @param y 垂直偏移量（正值向下偏移，负值向上偏移）
     * @return 计算偏移后的Y坐标值
     */
    public int getY(int y) {
        return this.boxPosY + y;
    }
}
