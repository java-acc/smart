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

import cn.org.byc.smart.tool.constant.StringPool;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 日期时间工具类，提供丰富的日期时间处理功能。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>日期时间格式化和解析</li>
 *   <li>日期时间计算（加减年月日时分秒）</li>
 *   <li>日期时间转换（Date、LocalDateTime、Instant等类型互转）</li>
 *   <li>时间间隔计算</li>
 *   <li>常用日期时间格式常量</li>
 * </ul>
 * 
 * <p>特点：
 * <ul>
 *   <li>线程安全的日期格式化器</li>
 *   <li>支持多种日期时间类型</li>
 *   <li>丰富的日期时间操作方法</li>
 *   <li>性能优化的实现</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 获取当前日期时间
 * Date now = DateUtil.now();
 * 
 * // 格式化日期时间
 * String formatted = DateUtil.formatDateTime(new Date());  // 使用默认格式 yyyy-MM-dd HH:mm:ss
 * 
 * // 解析日期时间字符串
 * Date date = DateUtil.parse("2023-12-31", "yyyy-MM-dd");
 * 
 * // 日期计算
 * Date tomorrow = DateUtil.plusDays(new Date(), 1);
 * Date lastMonth = DateUtil.minusMonths(new Date(), 1);
 * 
 * // 计算时间间隔
 * Duration duration = DateUtil.between(startDate, endDate);
 * long days = duration.toDays();
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
@UtilityClass
public class DateUtil {
    /**
     * 默认日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式：yyyy-MM-dd
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /**
     * 默认时间格式：HH:mm:ss
     */
    public static final String PATTERN_TIME = "HH:mm:ss";

    /**
     * 线程安全的默认日期时间格式化器
     */
    public static final ConcurrentDateFormat DATETIME_FORMAT = ConcurrentDateFormat.of(PATTERN_DATETIME);

    /**
     * 线程安全的默认日期格式化器
     */
    public static final ConcurrentDateFormat DATE_FORMAT = ConcurrentDateFormat.of(PATTERN_DATE);

    /**
     * 线程安全的默认时间格式化器
     */
    public static final ConcurrentDateFormat TIME_FORMAT = ConcurrentDateFormat.of(PATTERN_TIME);

    /**
     * 默认日期时间格式化器
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATETIME);

    /**
     * 默认日期格式化器
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATE);

    /**
     * 默认时间格式化器
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_TIME);

    /**
     * 获取当前日期时间
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date now = DateUtil.now();
     * System.out.println(DateUtil.formatDateTime(now));  // 输出当前日期时间
     * }</pre>
     *
     * @return 当前日期时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 将日期格式化为默认格式（yyyy-MM-dd HH:mm:ss）的字符串
     * 
     * <p>使用示例：
     * <pre>{@code
     * String formatted = DateUtil.formatDateTime(new Date());  // 如：2023-12-31 23:59:59
     * }</pre>
     *
     * @param date 要格式化的日期
     * @return 格式化后的字符串，如果输入为null则返回null
     */
    public static String formatDateTime(Date date) {
        return format(date, PATTERN_DATETIME);
    }

    /**
     * 将字符串解析为日期对象
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.parse("2023-12-31", "yyyy-MM-dd");
     * Date datetime = DateUtil.parse("2023-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
     * }</pre>
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return 解析后的日期对象
     * @throws RuntimeException 如果解析失败
     */
    public static Date parse(String dateStr, String pattern) {
        return parse(dateStr, ConcurrentDateFormat.of(pattern));
    }

    /**
     * 计算两个日期之间的时间间隔
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date start = DateUtil.parse("2023-01-01", "yyyy-MM-dd");
     * Date end = DateUtil.parse("2023-12-31", "yyyy-MM-dd");
     * Duration duration = DateUtil.between(start, end);
     * System.out.println("相差天数：" + duration.toDays());
     * }</pre>
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 时间间隔
     */
    public static Duration between(Date startDate, Date endDate) {
        return Duration.between(startDate.toInstant(), endDate.toInstant());
    }

    /**
     * 日期加上指定年数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextYear = DateUtil.plusYears(date, 1);  // 加一年
     * }</pre>
     *
     * @param date 基准日期
     * @param yearsToAdd 要增加的年数
     * @return 增加后的新日期
     */
    public static Date plusYears(Date date, int yearsToAdd) {
        return set(date, Calendar.YEAR, yearsToAdd);
    }

    /**
     * 日期加上指定月数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextMonth = DateUtil.plusMonths(date, 1);  // 加一个月
     * }</pre>
     *
     * @param date 基准日期
     * @param monthsToAdd 要增加的月数
     * @return 增加后的新日期
     */
    public static Date plusMonths(Date date, int monthsToAdd) {
        return set(date, Calendar.MONTH, monthsToAdd);
    }

    /**
     * 日期加上指定周数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextWeek = DateUtil.plusWeeks(date, 1);  // 加一周
     * }</pre>
     *
     * @param date 基准日期
     * @param weeksToAdd 要增加的周数
     * @return 增加后的新日期
     */
    public static Date plusWeeks(Date date, int weeksToAdd) {
        return set(date, Calendar.WEEK_OF_YEAR, weeksToAdd);
    }

    /**
     * 日期加上指定天数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date tomorrow = DateUtil.plusDays(date, 1);  // 加一天
     * }</pre>
     *
     * @param date 基准日期
     * @param daysToAdd 要增加的天数
     * @return 增加后的新日期
     */
    public static Date plusDays(Date date, long daysToAdd) {
        return set(date, Calendar.DAY_OF_YEAR, (int) daysToAdd);
    }

    /**
     * 日期加上指定小时数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextHour = DateUtil.plusHours(date, 1);  // 加一小时
     * }</pre>
     *
     * @param date 基准日期
     * @param hoursToAdd 要增加的小时数
     * @return 增加后的新日期
     */
    public static Date plusHours(Date date, long hoursToAdd) {
        return set(date, Calendar.HOUR_OF_DAY, (int) hoursToAdd);
    }

    /**
     * 日期加上指定分钟数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextMinute = DateUtil.plusMinutes(date, 1);  // 加一分钟
     * }</pre>
     *
     * @param date 基准日期
     * @param minutesToAdd 要增加的分钟数
     * @return 增加后的新日期
     */
    public static Date plusMinutes(Date date, long minutesToAdd) {
        return set(date, Calendar.MINUTE, (int) minutesToAdd);
    }

    /**
     * 日期加上指定秒数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextSecond = DateUtil.plusSeconds(date, 1);  // 加一秒
     * }</pre>
     *
     * @param date 基准日期
     * @param secondsToAdd 要增加的秒数
     * @return 增加后的新日期
     */
    public static Date plusSeconds(Date date, long secondsToAdd) {
        return set(date, Calendar.SECOND, (int) secondsToAdd);
    }

    /**
     * 日期加上指定毫秒数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextMillis = DateUtil.plusMillis(date, 1000);  // 加1000毫秒
     * }</pre>
     *
     * @param date 基准日期
     * @param millisToAdd 要增加的毫秒数
     * @return 增加后的新日期
     */
    public static Date plusMillis(Date date, long millisToAdd) {
        return set(date, Calendar.MILLISECOND, (int) millisToAdd);
    }

    /**
     * 日期加上指定纳秒数
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date nextNano = DateUtil.plusNanos(date, 1000000);  // 加1000000纳秒
     * }</pre>
     *
     * @param date 基准日期
     * @param nanosToAdd 要增加的纳秒数
     * @return 增加后的新日期
     */
    public static Date plusNanos(Date date, long nanosToAdd) {
        return set(date, Calendar.MILLISECOND, (int) (nanosToAdd / 1000000));
    }

    /**
     * 日期加上指定时间量
     * 
     * <p>使用示例：
     * <pre>{@code
     * Date date = DateUtil.now();
     * Date result = DateUtil.plus(date, Duration.ofDays(1));  // 加一天
     * result = DateUtil.plus(date, Period.ofMonths(1));  // 加一个月
     * }</pre>
     *
     * @param date 基准日期
     * @param amount 要增加的时间量
     * @return 增加后的新日期
     */
    public static Date plus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.plus(amount));
    }

    /**
     * 减少年
     *
     * @param date  时间
     * @param years 减少的年数
     * @return 设置后的时间
     */
    public static Date minusYears(Date date, int years) {
        return DateUtil.set(date, Calendar.YEAR, -years);
    }

    /**
     * 减少月
     *
     * @param date   时间
     * @param months 减少的月数
     * @return 设置后的时间
     */
    public static Date minusMonths(Date date, int months) {
        return DateUtil.set(date, Calendar.MONTH, -months);
    }

    /**
     * 减少周
     *
     * @param date  时间
     * @param weeks 减少的周数
     * @return 设置后的时间
     */
    public static Date minusWeeks(Date date, int weeks) {
        return DateUtil.minus(date, Period.ofWeeks(weeks));
    }

    /**
     * 减少天
     *
     * @param date 时间
     * @param days 减少的天数
     * @return 设置后的时间
     */
    public static Date minusDays(Date date, long days) {
        return DateUtil.minus(date, Duration.ofDays(days));
    }

    /**
     * 减少小时
     *
     * @param date  时间
     * @param hours 减少的小时数
     * @return 设置后的时间
     */
    public static Date minusHours(Date date, long hours) {
        return DateUtil.minus(date, Duration.ofHours(hours));
    }

    /**
     * 减少分钟
     *
     * @param date    时间
     * @param minutes 减少的分钟数
     * @return 设置后的时间
     */
    public static Date minusMinutes(Date date, long minutes) {
        return DateUtil.minus(date, Duration.ofMinutes(minutes));
    }

    /**
     * 减少秒
     *
     * @param date    时间
     * @param seconds 减少的秒数
     * @return 设置后的时间
     */
    public static Date minusSeconds(Date date, long seconds) {
        return DateUtil.minus(date, Duration.ofSeconds(seconds));
    }

    /**
     * 减少毫秒
     *
     * @param date   时间
     * @param millis 减少的毫秒数
     * @return 设置后的时间
     */
    public static Date minusMillis(Date date, long millis) {
        return DateUtil.minus(date, Duration.ofMillis(millis));
    }

    /**
     * 减少纳秒
     *
     * @param date  时间
     * @param nanos 减少的纳秒数
     * @return 设置后的时间
     */
    public static Date minusNanos(Date date, long nanos) {
        return DateUtil.minus(date, Duration.ofNanos(nanos));
    }

    /**
     * 日期减少时间量
     *
     * @param date   时间
     * @param amount 时间量
     * @return 设置后的时间
     */
    public static Date minus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.minus(amount));
    }

    /**
     * 设置日期属性
     *
     * @param date          时间
     * @param calendarField 更改的属性
     * @param amount        更改数，-1表示减少
     * @return 设置后的时间
     */
    private static Date set(Date date, int calendarField, int amount) {
        Assert.notNull(date, "The date must not be null");
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 日期格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     * 日期格式化
     *
     * @param date    时间
     * @param pattern 表达式
     * @return 格式化后的时间
     */
    public static String format(Date date, String pattern) {
        return ConcurrentDateFormat.of(pattern).format(date);
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatDateTime(TemporalAccessor temporal) {
        return DATETIME_FORMATTER.format(temporal);
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatDate(TemporalAccessor temporal) {
        return DATE_FORMATTER.format(temporal);
    }

    /**
     * java8 时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatTime(TemporalAccessor temporal) {
        return TIME_FORMATTER.format(temporal);
    }

    /**
     * java8 日期格式化
     *
     * @param temporal 时间
     * @param pattern  表达式
     * @return 格式化后的时间
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(temporal);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param format  ConcurrentDateFormat
     * @return 时间
     */
    public static Date parse(String dateStr, ConcurrentDateFormat format) {
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public static <T> T parse(String dateStr, String pattern, TemporalQuery<T> query) {
        return DateTimeFormatter.ofPattern(pattern).parse(dateStr, query);
    }

    /**
     * 时间转 Instant
     *
     * @param dateTime 时间
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Instant 转 时间
     *
     * @param instant Instant
     * @return Instant
     */
    public static LocalDateTime toDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 转换成 date
     *
     * @param dateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(DateUtil.toInstant(dateTime));
    }

    /**
     * 转换成 date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts local date time to Calendar.
     */
    public static Calendar toCalendar(final LocalDateTime localDateTime) {
        return GregorianCalendar.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()));
    }

    /**
     * localDateTime 转换成毫秒数
     *
     * @param localDateTime LocalDateTime
     * @return long
     */
    public static long toMilliseconds(final LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * localDate 转换成毫秒数
     *
     * @param localDate LocalDate
     * @return long
     */
    public static long toMilliseconds(LocalDate localDate) {
        return toMilliseconds(localDate.atStartOfDay());
    }

    /**
     * 转换成java8 时间
     *
     * @param calendar 日历
     * @return LocalDateTime
     */
    public static LocalDateTime fromCalendar(final Calendar calendar) {
        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zid);
    }

    /**
     * 转换成java8 时间
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime fromInstant(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 转换成java8 时间
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime fromDate(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 转换成java8 时间
     *
     * @param milliseconds 毫秒数
     * @return LocalDateTime
     */
    public static LocalDateTime fromMilliseconds(final long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }

    /**
     * 比较2个时间差，跨度比较小
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 时间间隔
     */
    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive);
    }

    /**
     * 比较2个时间差，跨度比较大，年月日为单位
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔
     */
    public static Period between(LocalDate startDate, LocalDate endDate) {
        return Period.between(startDate, endDate);
    }

    /**
     * 将秒数转换为日时分秒
     *
     * @param second 秒数
     * @return 时间
     */
    public static String secondToTime(Long second) {
        // 判断是否为空
        if (second == null || second == 0L) {
            return StringPool.EMPTY;
        }
        //转换天数
        long days = second / 86400;
        //剩余秒数
        second = second % 86400;
        //转换小时
        long hours = second / 3600;
        //剩余秒数
        second = second % 3600;
        //转换分钟
        long minutes = second / 60;
        //剩余秒数
        second = second % 60;
        if (days > 0) {
            return StringUtil.format("{}天{}小时{}分{}秒", days, hours, minutes, second);
        } else {
            return StringUtil.format("{}小时{}分{}秒", hours, minutes, second);
        }
    }

    /**
     * 获取今天的日期
     *
     * @return 时间
     */
    public static String today() {
        return format(new Date(), "yyyyMMdd");
    }
}
