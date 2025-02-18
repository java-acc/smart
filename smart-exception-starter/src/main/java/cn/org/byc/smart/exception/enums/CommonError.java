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

package cn.org.byc.smart.exception.enums;

/**
 * 通用错误码枚举
 *
 * <p>定义了系统中常见的错误类型，每个错误包含：
 * <ul>
 *     <li>HTTP状态码 - 符合HTTP协议规范的状态码</li>
 *     <li>业务错误码 - 以"S_"开头的唯一标识符</li>
 *     <li>错误消息 - 描述错误的具体信息</li>
 * </ul>
 *
 * <p>错误码格式说明：
 * <ul>
 *     <li>S_0000: 成功</li>
 *     <li>S_4xx_xxx: 客户端错误，如参数错误、认证失败等</li>
 *     <li>S_5xx_xxx: 服务器错误，如系统异常、数据一致性错误等</li>
 * </ul>
 *
 * @author Ken
 * @see ErrorCode
 */
public enum CommonError implements ErrorCode {
    // 成功状态
    /**
     * 操作成功
     */
    NoError(200, "S_0000", "Success"),

    // 4xx 客户端错误
    /**
     * 数据持久化失败
     */
    PersistentDataError(400, "S_400_001", "Failed to persist data"),

    /**
     * 属性值无效
     */
    InvalidProperty(400, "S_400_002", "Invalid property value"),

    /**
     * 请求参数无效
     */
    RequestParamsInvalid(400, "S_400_003", "Invalid request parameters"),

    /**
     * 参数为空
     */
    ParamIsEmpty(400, "S_400_004", "Parameter is empty"),

    /**
     * 参数类型错误
     */
    ParamTypeError(400, "S_400_005", "Parameter type error"),

    /**
     * 参数格式错误
     */
    ParamFormatError(400, "S_400_006", "Parameter format error"),

    /**
     * 参数值超出范围
     */
    ParamOutOfRange(400, "S_400_007", "Parameter value out of range"),

    /**
     * 未登录或登录已过期
     */
    Unauthorized(401, "S_401_001", "Unauthorized or token expired"),

    /**
     * 认证失败
     */
    AuthenticationFailed(401, "S_401_002", "Authentication failed"),

    /**
     * 无权限访问
     */
    AccessDenied(403, "S_403_001", "Access denied"),

    /**
     * 权限不足
     */
    InsufficientPermissions(403, "S_403_002", "Insufficient permissions"),

    /**
     * 事件总线未找到
     */
    NoEventBus(404, "S_404_001", "Event bus not found"),

    /**
     * 资源未找到
     */
    NoResource(404, "S_404_002", "Resource not found"),

    /**
     * 并发操作冲突
     */
    ConcurrencyConflict(409, "S_409_001", "Concurrent operation conflict detected"),

    // 5xx 服务器错误
    /**
     * 未预期的错误
     */
    UnExpected(500, "S_500_001", "An unexpected error occurred"),

    /**
     * 操作失败
     */
    SmartError(500, "S_500_002", "Smart operation failed"),

    /**
     * 获取本地IP失败
     */
    FailToGetLocalIp(500, "S_500_003", "Failed to obtain local IP address"),

    /**
     * 数据一致性检查失败
     */
    DataConsistencyError(500, "S_500_004", "Data consistency check failed"),

    /**
     * SQL执行错误
     */
    SqlExecutionError(500, "S_500_005", "SQL execution error"),

    /**
     * SQL语法错误
     */
    SqlSyntaxError(500, "S_500_006", "SQL syntax error"),

    /**
     * 数据库连接失败
     */
    DatabaseConnectionError(500, "S_500_007", "Database connection failed"),

    /**
     * 数据库超时
     */
    DatabaseTimeout(500, "S_500_008", "Database operation timeout"),

    /**
     * 数据库死锁
     */
    DatabaseDeadlock(500, "S_500_009", "Database deadlock detected"),

    /**
     * 数据库约束违反
     */
    DatabaseConstraintViolation(500, "S_500_010", "Database constraint violation"),

    /**
     * 远程服务调用失败
     */
    RemoteServiceError(500, "S_500_011", "Remote service call failed"),

    /**
     * 系统资源不足
     */
    InsufficientSystemResources(500, "S_500_012", "Insufficient system resources"),

    /**
     * 缓存操作失败
     */
    CacheOperationError(500, "S_500_013", "Cache operation failed"),

    /**
     * 消息队列操作失败
     */
    MessageQueueError(500, "S_500_014", "Message queue operation failed"),

    /**
     * 文件操作失败
     */
    FileOperationError(500, "S_500_015", "File operation failed"),

    /**
     * 网络连接错误
     */
    NetworkConnectionError(500, "S_500_016", "Network connection error");

    /**
     * HTTP状态码
     */
    private final int status;

    /**
     * 业务错误码
     */
    private final String code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param httpStatus HTTP状态码
     * @param code 业务错误码
     * @param message 错误消息
     */
    CommonError(int httpStatus, String code, String message) {
        this.status = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
