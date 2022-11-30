package cn.hhspace.etl.common;

/**
 * @Descriptions: 错误码定义
 * @Author: Jianhuan-LIU
 * @Date: 2021/3/22 2:24 下午
 * @Version: 1.0
 */
public class ErrorCode {

    private ErrorCode() {

    }

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    /**
     * 选择的topic正在运行/调试，请停止运行/调试后再调整offset!
     */
    public static final int KAFKA_TOPIC_RUNNING_MODIFY = 100400;

    /**
     * 需要运行过或调试过才能调整offset
     */
    public static final int KAFKA_TOPIC_UNCOMMITED_MODIFY = 100401;

    /**
     * 值映射资产信息同步错误
     */
    public static final int SOC_ASSET_SNYC_ERROR = 100088;


    /**
     * 服务端异常
     */
    public static final int SERVER_EXCEPTION = 110000;

    /**
     * 参数错误
     */
    public static final int PARAM_INVALID = 110001;

    /**
     * 鉴权失败
     */
    public static final int AUTHENTICATE_FAILED = 110002;

    /**
     * 授权失败
     */
    public static final int AUTHORIZED_FAILED = 110003;

    /**
     * 无权访问资源
     */
    public static final int ACCESS_FORBIDDEN = 110004;

    /**
     * 方法不支持
     */
    public static final int METHOD_NOT_ALLOWED = 110005;

    /**
     * 批量操作中方法为空
     */
    public static final int METHOD_IS_BLANK = 110006;

    /**
     * 批量操作中操作对象id集合为空
     */
    public static final int OBJ_IDS_IS_EMPTY = 110007;

    /**
     * 内部错误，尽量少使用该错误码
     */
    public static final int INNER_ERROR = 110008;
    /**
     * 密码为空
     */
    public static final int EMPTY_PASSWORD = 110009;
    /**
     * 请求超时
     */
    public static final int REQUEST_TIMEOUT = 110011;

    public static final int OLD_PASSWORD_ERROR = 110012;
    public static final int PASSWORD_CHANGE_ERROR = 110013;
    /**
     * 已选的数据，没有可以删除的
     */
    public static final int NO_DELETED_DATA = 110020;
    public static final int NO_START_DATA = 110021;
    public static final int NO_STOP_DATA = 110022;

    /**
     * 部分成功
     */
    public static final int DELETE_PARTIAL_SUCCESS = 110030;
    public static final int START_PARTIAL_SUCCESS = 110031;
    public static final int STOP_PARTIAL_SUCCESS = 110032;
    public static final int CLEAR_PARTIAL_SUCCESS = 110036;
    public static final int START_FAILED = 110033;
    public static final int STOP_FAILED = 110034;
    public static final int CLEAR_FAILED = 110035;


    /**
     * IP非法
     */
    public static final int IS_NOT_IP = 120001;

    /**
     * 端口非法
     */
    public static final int IS_NOT_PORT = 120002;

    /**
     * IP地址不能为空
     */
    public static final int IP_IS_NOT_NULL = 120003;

    /**
     * 枚举类型错误
     */
    public static final int TYPE_IS_ERROR = 120004;

    /**
     * 采集器ID不能为空
     */
    public static final int COLLECTOR_ID_IS_NOT_NULL = 120005;

    /**
     * 采集器名称不能为空
     */
    public static final int COLLECTOR_NAME_IS_NOT_NULL = 120006;

    /**
     * 用户名不能为空
     */
    public static final int USERNAME_IS_NOT_NULL = 120007;

    /**
     * 密码不能为空
     */
    public static final int PASSWORD_IS_NOT_NULL = 120008;

    /**
     * 请输入正确的轮询时间
     */
    public static final int INTERVAL_IS_ERROR = 120009;

    /**
     * 端口不能为空
     */
    public static final int PORT_IS_NOT_NULL = 120010;

    /**
     * 日志采集器注册失败
     */
    public static final int REGISTER_FAILED = 120011;
    /**
     * 此列不支持排序
     */
    public static final int SORT_COLUMN_IS_UNSUPPORT = 120012;
    /**
     * 字符长度不得超过500个字符
     */
    public static final int CHAR_LENGTH_OVER_500 = 120016;

    /**
     * 格式错误
     */
    public static final int FORMAT_ERROR = 120017;

    /**
     * 名称重复
     */
    public static final int REPEAT_NAME = 120018;

    /**
     * IP重复
     */
    public static final int REPEAT_IP = 120019;

    /**
     * 新增配置重复
     */
    public static final int NEW_CONF_REPEAT = 120020;

    /**
     * WMI类型不能为空
     */
    public static final int LOG_TYPE_IS_NOT_NULL = 120021;

    /**
     * 采集器名称包含非法字符
     */
    public static final int COLLECTOR_NAME_CHAR_INVALID = 120022;

    /**
     * 采集器描述包含非法字符
     */
    public static final int COLLECTOR_DESC_CHAR_INVALID = 120023;

    /**
     * SNMP Trap日志用户名包含非法字符
     */
    public static final int SNMPTRAP_NAME_CHAR_INVALID = 120024;

    /**
     * SNMP Trap日志描述包含非法字符
     */
    public static final int SNMPTRAP_DESC_CHAR_INVALID = 120025;

    /**
     * 文本格式日志用户名包含非法字符
     */
    public static final int TEXT_NAME_CHAR_INVALID = 120026;
    /**
     * 文本格式日志描述包含非法字符
     */
    public static final int TEXT_DESC_CHAR_INVALID = 120027;

    /**
     * 数据库日志用户名包含非法字符
     */
    public static final int DB_NAME_CHAR_INVALID = 120028;

    /**
     * 数据库日志描述包含非法字符
     */
    public static final int DB_DESC_CHAR_INVALID = 120029;

    /**
     * WMI日志用户名包含非法字符
     */
    public static final int WMI_NAME_CHAR_INVALID = 120030;

    /**
     * WMI日志描述包含非法字符
     */
    public static final int WMI_DESC_CHAR_INVALID = 120031;

    /**
     * IP TOPIC重复
     */
    public static final int REPEAT_IP_TOPIC = 120032;

    /**
     * 无效的token
     */
    public static final int INVALID_TOKEN = 120033;
    /**
     * 数字签名校验失败
     */
    public static final int INVALID_SIGNATURE = 120034;
    /**
     * 数据源校验失败
     */
    public static final int INVALID_DATASOURCE = 120035;
    /**
     * HTTP日志配置名称包含非法字符
     */
    public static final int HTTP_NAME_CHAR_INVALID = 120036;
    /**
     * HTTP描述包含非法字符
     */
    public static final int HTTP_DESC_CHAR_INVALID = 120037;
    /**
     * HTTP配置名称不能为空
     */
    public static final int CONFIG_NAME_IS_NOT_NULL = 120038;
    /**
     * HTTP的URL不能为空
     */
    public static final int HTTP_URL_IS_NOT_NULL = 120039;
    /**
     * HTTP的模板类型不能为空
     */
    public static final int TEMPLATE_TYPE_IS_NOT_NULL = 120040;
    /**
     * HTTP的请求类型不能为空
     */
    public static final int HTTP_TYPE_IS_NOT_NULL = 120041;
    /**
     * 日志发送的TOPIC不能为空
     */
    public static final int TOPIC_IS_NOT_NULL = 120042;
    /**
     * HTTP的配置名称重复
     */
    public static final int NAME_REPEAT = 120043;
    /**
     * URL不合法
     */
    public static final int URL_IS_INVALID = 120044;
    /**
     * 类型不存在
     */
    public static final int TYPE_NOT_EXIST = 120045;
    /**
     * 请求体类型不能为空
     */
    public static final int CONTENT_TYPE_IS_NOT_NULL = 120046;
    /**
     * 运行周期不能为空
     */
    public static final int EXEC_IS_NOT_NULL = 120047;
    /**
     * 模板不存在
     */
    public static final int TEMPLATE_NOT_EXIST = 120048;
    /**
     * 返回类型不能为空
     */
    public static final int RESPONSE_TYPE_IS_NOT_NULL = 120049;
    /**
     * 调度设置不合法
     */
    public static final int CRON_IS_INVALID = 120050;

    /**
     * 文件夹路径不能为空
     */
    public static final int PATH_IS_NOT_NULL = 121001;

    /**
     * 文件名不能为空
     */
    public static final int FILENAME_IS_NOT_NULL = 121002;

    /**
     * 文件夹路径必须以/结尾
     */
    public static final int PATH_MUST_END_XG = 121003;

    /**
     * 文本格式日志，用户名长度不能超过64个字符
     */
    public static final int USERNAME_LENGTH_OVER_64 = 121004;

    /**
     * 文本格式日志，密码长度不能超过64个字符
     */
    public static final int PASSWORD_LENGTH_OVER_64 = 121005;

    /**
     * 文件编码错误
     */
    public static final int FILE_ENCODE_ERROR = 121006;

    /**
     * 数据库名称不能为空
     */
    public static final int DBNAME_IS_NOT_NULL = 122001;

    /**
     * 请输入正确的轮询时间
     */
    public static final int RECORDS_IS_ERROR = 122002;

    /**
     * 插件类型错
     */
    public static final int PLUGIN_TYPE_ERROR = 122003;

    /**
     * 采集器ID无效
     */
    public static final int COLLECTOR_ID_INVALID = 122004;
    /**
     * 连通性测试ID无效
     */
    public static final int CONNEXITY_TEST_ID_INVALID = 122006;

    /**
     * 导入数据解析失败
     */
    public static final int IMPORT_DATA_PARSE_FAIL = 122007;

    /**
     * xlsx格式错误
     */
    public static final int XLS_FORMAT_ERROR = 122008;

    /**
     * xlsx文件中没有数据
     */
    public static final int XLS_IS_NULL = 122009;

    /**
     * 导入操作无效
     */
    public static final int XLS_IMPORT_OPTION_INVALID = 122010;

    /**
     * 下发配置失败
     */
    public static final int COLLECTOR_CONFIG_ISSUE_ERROR = 123001;


    /**
     * syslog不同协议之间端口不能重复，同一端口不能配置多个topic
     */
    public static final int SYSLOG_PORTS_REPEAT = 123002;

    /***
     * 导入失败
     */
    public static final int IMPORT_ERROR = 123003;

    /**
     * 分页参数必须是数字
     */
    public static final int PAGE_MUST_DIGIT = 140001;

    /**
     * 分页参数不能为空
     */
    public static final int PAGE_MUST_NOT_NULL = 140002;
    /**
     * 错误的JSON格式
     */
    public static final int WRONG_JSON_FORMAT = 140003;
    /**
     * 请输入英文字符
     */
    public static final int PARAM_MUST_LETTER = 140006;

    /**
     * 请求参数不合法
     */
    public static final int REQUEST_PARAM_IVALID = 140007;

    /**
     * 值映射表属性类型填写有误
     */
    public static final int KV_VALUE_ERROR = 140020;

    /**
     * 错误的XML格式
     */
    public static final int WRONG_XML_FORMAT = 140022;

    /**
     * 值映射表不存在
     */
    public static final int KV_NOT_EXIST = 140023;
    /**
     * 值映射表已经存在
     */
    public static final int KV_ALREADY_EXIST = 140024;
    /**
     * 字段不存在
     */
    public static final int FIELD_NOT_EXIST = 140025;
    /**
     * 字段已经被引用，不能修改
     */
    public static final int FIELD_ALREADY_USED = 140026;
    /**
     * 字段已经存在
     */
    public static final int FIELD_ALREADY_EXIST = 140027;
    /**
     * 请输入值映射表名
     */
    public static final int KV_NAME_NULL = 140028;
    /**
     * 请输入值映射表的主键名
     */
    public static final int KV_KEY_NULL = 140029;
    /**
     * 请输入值映射表的属性类型
     */
    public static final int KV_VALUE_TYPE_NULL = 140031;
    /**
     * 值映射表的属性名称和类型数量不匹配
     */
    public static final int KV_VALUE_TYPE_NOT_MATH = 140032;
    /**
     * 值映射表的主键为非法的字段类型
     */
    public static final int KV_KEY_TYPE_ERROR = 140033;
    /**
     * 值映射属性含有非法的字段类型
     */
    public static final int KV_VALUE_TYPE_ERRO = 140034;
    /**
     * 请输入字段名称
     */
    public static final int FIELD_NAME_NULL = 140035;

    /**
     * 请选择字段类型
     */
    public static final int FIELD_TYPE_NULL = 140037;
    /**
     * 非法的字段类型
     */
    public static final int FIELD_TYPE_ERROR = 140038;
    /**
     * 请求id不能为空
     */
    public static final int REQUEST_ID_NULL = 140046;
    /**
     * 日志类型不存在
     */
    public static final int LOG_TYPE_NOT_EXIST = 140054;
    /**
     * 请输入日志类型名称
     */
    public static final int LOG_TYPE_NAME_NULL = 140056;
    /**
     * 该日志类型不能编辑
     */
    public static final int LOG_TYPE_NOT_UPDATE = 400020;

    /**
     * 字段导入导出的sheet名称错误
     */
    public static final int EXPORT_SHEET_DATA_FIELD_ERROR = 140060;
    /**
     * 排序参数错误
     */
    public static final int SORT_PARAM_ERROR = 140062;
    /**
     * 插件已被使用，不能删除
     */
    public static final int PLUGIN_DELETE_FORBID = 140065;

    /**
     * 解析插件名称已存在
     */
    public static final int PLUGIN_ALREADY_EXIST = 140066;

    /**
     * 未知的解析插件类型
     */
    public static final int PLUGIN_TYPE_UNKOWN = 140067;

    /**
     * 没有定义解析插件输出字段
     */
    public static final int PLUGIN_FIELD_NULL = 140068;

    /**
     * 解析插件字段中包含未知的父字段
     */
    public static final int PLUGIN_FIELD_UNKOWN_PARENT = 140069;
    /**
     * IP地址格式不正确
     */
    public static final int INVALID_IP_FORMAT = 140073;
    /**
     * 值映射表已被引用，不能删除
     */
    public static final int KV_DELETE_FORBID = 140076;

    /**
     * 插件子名称为空
     */
    public static final int PLUGIN_SUBNAME_NULL = 140078;

    /**
     * 插件入口类为空
     */
    public static final int PLUGIN_CLASSPATH_NULL = 140079;


    /**
     * 日期格式不正确
     */
    public static final int INVALID_DATE = 140086;
    /**
     * 错误的数值类型值
     */
    public static final int INVALID_NUM = 140087;
    /**
     * 错误的布尔类型值
     */
    public static final int INVALID_BOOLEAN = 140088;
    /**
     * 系统字段和产品公共字段禁止删除
     */
    public static final int SYSTEM_FIELD_DELETE_FORBID = 140089;
    /**
     * 系统名称与系统字段和产品公共字段名称冲突
     */
    public static final int SYSTEM_FIELD_NAME_CONFLICT = 140090;
    /**
     * 只支持xml或zip格式的文件导入
     */
    public static final int MUST_XML_OR_ZIP = 140092;
    /**
     * 日志类型为空
     */
    public static final int DESTLOG_ID_IS_BLANK = 140094;
    /**
     * xml配置文件和jar包必须有相同的文件名
     */
    public static final int PLUGIN_XML_JAR_NOT_MATCH = 140097;


    /**
     * 插件名称不能为空
     */
    public static final int PLUGIN_NAME_NULL = 140127;
    /**
     * 插件名不能超过32个字符
     */
    public static final int PLUGIN_NAME_OVER_32 = 140123;

    /**
     * 解析插件文件读取错误
     */
    public static final int PLUGIN_FILE_IO_ERROR = 140131;
    /**
     * 3061插件配置错误
     */
    public static final int PLUGIN_3061_ERROR = 140132;

    /**
     * 插件文件名不能超过50个字符
     */
    public static final int PLUGIN_FILE_NAME_OVER_50 = 140136;

    /**
     * 未知的插件字段类型
     */
    public static final int PLUGIN_FIELD_UNKONW_TYPE = 140164;
    /**
     *  导入的子插件不包含解析规则里面已引用的插件
     */
    public static final int PLUGIN_USED_NOT_FIND = 140166;
    /**
     *  导入的子插件为空
     */
    public static final int PLUGIN_SUB_NOT_EXIST = 140167;

    /**
     * 值映射表的详情不能为空
     */
    public static final int KV_DETAIL_NULL = 140098;
    /**
     * 值映射表的主键值不能重复
     */
    public static final int KV_KEY_VALUE_DUPLICATE = 140100;
    /**
     * Kafka创建topic失败
     */
    public static final int KAFKA_CREATE_EXCEPTION = 140106;

    /**
     * 值映射表的属性名称不能为空
     */
    public static final int KV_PROPERTY_NAME_NULL = 140109;
    /**
     * 值映射表的属性值不能为空
     */
    public static final int KV_PROPERTY_VALUE_NULL = 140110;
    /**
     * 已经定义的值映射结构不能更改
     */
    public static final int KV_DEF_NOT_ALLOW_CHANGE = 140165;
    /**
     * 仅支持xlsx
     */
    public static final int EXCEL_SUPPORT_ONLY = 140128;


    /**
     * 动态富化函数名称不能为空
     */
    public static final int ENRICH_FUNC_NAME_ISNULL = 140139;
    /**
     * 动态富化函数体不能为空
     */
    public static final int ENRICH_FUNC_BODY_ISNULL = 140140;
    /**
     * 动态富化函数参数不能为空
     */
    public static final int ENRICH_FUNC_PARAMS_ISNULL = 140141;
    /**
     * 动态富化函数返回类型不能为空
     */
    public static final int ENRICH_FUNC_RETURN_TYPE_ISNULL = 140142;
    /**
     * 动态富化函数参数名称不能为空
     */
    public static final int ENRICH_FUNC_PARAM_NAME_ISNULL = 140143;
    /**
     * 动态富化函数参数类型不能为空
     */
    public static final int ENRICH_FUNC_PARAM_TYPE_ISNULL = 140144;
    /**
     * 动态富化函数参数调试样例不能为空
     */
    public static final int ENRICH_FUNC_PARAM_SAMPLE_ISNULL = 140145;
    /**
     * 动态富化函数不存在
     */
    public static final int ENRICH_FUNC_NOT_EXIST = 140146;

    /**
     * 动态富化函数正在被引用，禁止删除
     */
    public static final int ENRICH_FUNC_DELETE_FORBID = 140147;
    /**
     * 动态富化函数参数样例的类型错误
     */
    public static final int ENRICH_FUNC_SAMPLE_TYPE_ERROR = 140148;
    /**
     * 动态富化函数执行错误
     */
    public static final int ENRICH_FUNC_EXECUTE_ERROR = 140149;
    /**
     * 动态富化函数中找不到对应的函数名称
     */
    public static final int ENRICH_FUNC_NO_METHOD_ERROR = 140150;
    /**
     * 动态富化函数的参数类型只能为基础数据类型
     */
    public static final int ENRICH_FUNC_PARAM_TYPE_ERROR = 140151;


    /**
     * 动态富化函数返回类型只能为基础数据类型
     */
    public static final int ENRICH_FUNC_RETURN_TYPE_ERROR = 140152;
    /**
     * 已经存在相同名称的动态富化函数
     */
    public static final int ENRICH_FUNC_NAME_CONFLICT = 140153;
    /**
     * 已经存在相同别名的动态富化函数
     */
    public static final int ENRICH_FUNC_ALIAS_CONFLICT = 140154;

    /**
     * 动态富化函数返回值和返回类型不匹配
     */
    public static final int ENRICH_FUNC_RETURN_TYPE_RESULT_NOT_MATCH = 140160;


    /**
     * 富化函数的结果为无效的数值
     */
    public static final int ENRICH_FUNC_RETURN_NOT_A_NUMBER = 140163;

    /**
     * 字段{0}在日志类型{1}中不存在
     */
    public static final int FIELD_NOT_EXIST_COMMON = 140200;

    /**
     * 过滤规则名称不能为空
     */
    public static final int FILTERRULE_NAME_IS_BLANK = 141000;
    /**
     * 过滤规则名称只能0-32个字符
     */
    public static final int FILTERRULE_NAME_LEN_INVALID = 141001;

    public static final int FILTERRULE_NOT_FOUND = 141002;

    public static final int FILTERRULE_IS_INUSE = 141003;

    public static final int FILTERRULE_DESC_LEN_INVALID = 141004;

    /**
     * 过滤规则名称已存在
     */
    public static final int FILTERRULE_NAME_EXIST = 141005;

    public static final int FILTERRULE_NAME_CHAR_INVALID = 141006;

    public static final int FILTERRULE_DESC_CHAR_INVALID = 141007;

    public static final int FILTERRULE_OPERATOR_NOT_FOUND = 141008;

    public static final int FILTERRULE_ID_IS_BLANK = 141009;

    /**
     * 输入的参数不是IPV6地址
     */
    public static final int VALIDATE_IPV6_ERROR = 141010;

    /**
     * 策略子流程中未找到解析解析节点
     */
    public static final int PARSENODE_NOT_FOUND = 141100;

    /**
     * 导出策略异常
     */
    public static final int EXPORT_POLICY_FAILED = 141101;


    public static final int PARSERULE_NOT_FOUND = 142000;

    public static final int PARSERULE_IS_INUSE = 142001;

    public static final int PARSERULE_NAME_IS_BLANK = 142002;

    public static final int PARSERULE_NAME_LEN_INVALID = 142003;

    public static final int PARSERULE_DESC_LEN_INVALID = 142004;

    public static final int PARSERULE_NAME_EXIST = 142005;

    public static final int PARSERULE_NAME_CHAR_INVALID = 142006;

    public static final int EXPRESSION_INVALID = 142007;

    public static final int PARSERULE_DESC_CHAR_INVALID = 142008;

    public static final int RULE_TYPE_IS_NULL = 142009;

    public static final int RULE_TYPE_OUT_OF_RANGE = 142010;

    public static final int SUB_TYPE_IS_NULL = 142011;

    public static final int SUB_TYPE_OUT_OF_RANGE = 142012;

    public static final int FILTER_TYPE_IS_NULL = 142013;

    public static final int FILTER_TYPE_OUT_OF_RANGE = 142014;

    public static final int FILTER_IS_NULL = 142015;

    /**
     * 值中存在非法IP
     */
    public static final int FILTER_IP_ERROR = 142117;

    /**
     * 值中掩码位填写错误
     */
    public static final int FILTER_IP_MASK_ERROR = 142118;

    /**
     * 值中掩码位不在范围内
     */
    public static final int FILTER_IP_MASK_OUT_ERROR = 142119;


    /**
     * IP起始值中存在非法IPV6
     */
    public static final int FILTER_IPV6_START_ERROR = 142120;

    /**
     * 解析IPV6的值失败
     */
    public static final int FILTER_IPV6_ERROR = 142121;


    /**
     * IP结束值中存在非法IPV6
     */
    public static final int FILTER_IPV6_END_ERROR = 142122;


    public static final int SAMPLE_LEN_INVALID = 142028;

    public static final int PARSE_FAILED = 142017;

    public static final int PARSE_TYPE_IS_BLANK = 142018;

    public static final int PARSE_TYPE_OUT_OF_RANGE = 142019;

    public static final int SAMPLE_IS_EMPTY = 142020;

    public static final int PARSE_FIELDS_IS_EMPTY = 142021;

    public static final int SAMPLE_FIELDS_IS_EMPTY = 142022;

    /**
     * 插件名称不存在。
     */
    public static final int PLUGINNAME_NOT_FOUND = 142023;


    /**
     * 子插件名称不存在。
     */
    public static final int PLUGINUSE_NOT_FOUND = 142024;


    /**
     * 是否包含双引号是必填字段。
     */
    public static final int DOUBLE_QUOTES_IS_MUST = 142025;


    /**
     * 分隔符是必填字段
     */
    public static final int SPLIT_IS_MUST = 142026;


    /**
     * 正则表达式是必填字段。
     */
    public static final int REGEX_IS_MUST = 142027;

    /**
     * 是否样本提取字段填写错误
     */
    public static final int SAMPLE_FILED_ERROR = 142030;

    /**
     * 赋值类型字段填写错误
     */
    public static final int ASSIGN_TYPE_ERROR = 142031;

    /**
     * 值字段不能为空。
     */
    public static final int PARSERULE_VALUE_ERROR = 142035;

    /**
     * 值类型字段不能为空
     */
    public static final int PARSERULE_VALUE_TYPE_ERROR = 142036;


    /**
     * 赋值字段值类型非法
     */
    public static final int PARSERULE_FU_VALUE__ERROR = 142037;

    /**
     * 赋值字段的正则表达式必填
     */
    public static final int PARSERULE_FU_REGEX_VALUE__ERROR = 142038;

    /**
     * 样本字段必须为空
     */
    public static final int PARSERULE_SAMPALE_NULL = 142039;

    /**
     * 样本字段必须填写，请检查是否存在空值
     */

    public static final int PARSERULE_SAMPALE_ERROR = 142040;


    /**
     * 样本字段类型非法
     */

    public static final int PARSERULE_SAMPALE_INVALID = 142041;

    /**
     * 目标字段和类型必须填写，请检查是否存在空值
     */

    public static final int PARSERULE_DST_NULL = 142042;

    /**
     * 是否日志时间字段填写错误
     */

    public static final int PARSERULE_TIMEFILED_ERROR = 142043;

    /**
     * 分隔符选中的样本的下标填写错误
     */
    public static final int DOUBLE_QUOTES_IS_MUST_ERROR = 142044;


    /**
     * 至少需要填写一条样本字段
     */
    public static final int SAMPLE_MUST_ERROR = 142045;

    /**
     * 解析规则的目标字段名称重复
     */
    public static final int PARSE_RULE_TARGET_FIELDS_DUPLICATE = 142032;

    /**
     * 解析规则需要至少包含一条样本字段
     */
    public static final int PARSE_RULE_MUST_CONTAIN_SMAPLE_FIELD = 142029;


    public static final int PARSERULE_ID_IS_BLANK = 142016;

    /**
     * 正则表达式语法错误
     */
    public static final int PARSE_RULE_REGEX_SYNTAX_ERROR = 142033;


    public static final int ENRICH_FIELDS_NOT_FOUND = 143001;
    public static final int ENRICHRULE_IS_INUSE = 143002;

    public static final int ENRICHRULE_NAME_IS_BLANK = 143003;

    public static final int ENRICHRULE_NAME_LEN_INVALID = 143004;

    public static final int ENRICHRULE_DESC_LEN_INVALID = 143005;

    public static final int ENRICHRULE_NAME_EXIST = 143006;

    public static final int ENRICHRULE_NAME_CHAR_INVALID = 143007;

    public static final int ENRICHRULE_NOT_FOUND = 143008;

    public static final int ENRICHRULE_ID_IS_BLANK = 143009;

    public static final int ENRICHRULE_DESC_CHAR_INVALID = 143010;

    public static final int ENRICHRULE_METHOD_NOT_FOUND = 143011;

    /**
     * 目标字段类型和值映射表字段类型不一致
     */
    public static final int ENRICHRULE_KVTYPE_DESTTYPE_UNSAME = 143012;

    /**
     * 标签不存在
     */
    public static final int LABEL_NOT_FOUND = 144001;


    public static final int POLICY_NAME_EXIST = 145000;

    public static final int POLICY_NOT_EXIST = 145001;

    public static final int POLICY_DETAIL_NOT_EXIST = 145002;

    public static final int POLICY_DETAIL_NOT_ASSOCIATED_WITH_POLICY = 145003;

    public static final int POLICY_IS_INUSE = 145004;

    public static final int POLICY_ID_IS_BLANK = 145005;

    public static final int POLICY_NAME_LEN_INVALID = 145006;

    public static final int POLICY_NAME_CHAR_INVALID = 145007;

    public static final int POLICY_DESC_LEN_INVALID = 145008;

    public static final int POLICY_DESC_CHAR_INVALID = 145009;

    public static final int POLICY_NAME_IS_NULL = 145010;

    public static final int POLICY_TYPE_RANGE_INVALID = 145011;

    public static final int POLICY_PICK_TYPE_RANGE_INVALID = 145012;

    public static final int POLICY_MATCH_TYPE_RANGE_INVALID = 145013;

    public static final int POLICY_DETAIL_ENCODE_INVALID = 145014;

    public static final int POLICY_DETAIL_CONFIG_TYPE_RANGE_INVALID = 145015;

    public static final int POLICY_TYPE_IS_NULL = 145016;

    public static final int PICK_TYPE_IS_NULL = 145017;

    public static final int MATCH_TYPE_IS_NULL = 145018;

    public static final int ENCODE_IS_NULL = 145019;

    public static final int CONFIG_TYPE_IS_NULL = 145020;

    public static final int POLICY_DETAIL_PRIORITY_EROOR = 145037;

    public static final int POLICY_DETAIL_ID_IS_BLANK = 145024;

    public static final int POLICY_DETAIL_EXISTS = 145025;

    /**
     * 数据策略配置错误,和子流程相关的错误
     */
    public static final int IMPORT_POLICY_CONFIG_ERROR = 145027;

    /**
     * 数据策略详情的配置类型错误
     */
    public static final int POLICY_DETAIL_CONFIG_TYPE_ERROR = 145030;

    /**
     * 规则子流程必须配置解析规则
     */
    public static final int RULE_SUB_FLOW_MUST_PARSE_RULE = 145031;


    /**
     * 存在相同的策略数据
     */
    public static final int EXIST_SAME_POLICY = 145035;

    /**
     * %s策略名称相同，但存在不同的%s
     */
    public static final int EXIST_SAME_POLICY_1 = 145036;

    /**
     * 节点子流程不存在
     */
    public static final int NODE_SUB_FLOW_NO_EXISTS = 145032;

    /**
     * 规则子流程对应的过滤规则不存在
     */
    public static final int RULE_SUB_FLOW_FILTER_RULE = 145033;


    /**
     * 规则子流程对应的富化规则不存在
     */
    public static final int RULE_SUB_FLOW_ENRICH_RULE = 145034;


    /**
     * 没有可导出的数据
     */
    public static final int NO_EXPORT_DATA = 149001;

    /**
     * 创建子流程失败
     */
    public static final int CREATE_FLOW_FAILED = 149002;

    /**
     * 删除子流程失败
     */
    public static final int DELETE_FLOW_FAILED = 149003;

    /**
     * 删除流程失败
     */
    public static final int DELETE_FLOW_ERROR = 149004;

    /**
     * 相同流程名称
     */
    public static final int FLOW_SAME_NAME_ERROR = 149005;
    /**
     * 获取流程异常
     */
    public static final int FLOW_GET_ERROR = 149006;
    /**
     * 未知的logtype值
     */
    public static final int DEPLOY_UNKNOWN_LOGTYPE = 149007;
    /**
     * 服务器配置pluginType为空
     */
    public static final int SERVER_CONFIG_PLUGINTYPE_NULL = 149008;
    /**
     * 服务器配置name为空
     */
    public static final int SERVER_CONFIG_NAME_NULL = 149009;

    /**
     * 日志类型的第三方引用名称不能为空
     */
    public static final int REF_NAME_NULL = 150001;

    public static final int BAD_BEAN_ID = 160001;
    public static final int UNREGISTERED_BEAN = 160002;
    public static final int BEAN_ID_EXISTS = 160003;
    public static final int BEAN_CAST_ERROR = 160004;
    public static final int BEAN_BAD_JSON = 160005;
    public static final int BEAN_REQUIRE_ID = 160006;
    public static final int BEAN_REQUIRE_PLUGINTYPE = 160007;
    public static final int PLUGIN_EXISTS = 160008;
    public static final int BEAN_NOTFOUND_CLASS = 160009;
    public static final int BEAN_IO_ERROR = 160010;
    public static final int BEAN_INSTANTIATION_ERROR = 160011;
    public static final int BEAN_WRITE_JSON_ERROR = 160012;
    public static final int BEAN_READ_JSON_ERROR = 160013;
    public static final int BEAN_CONFIG_ERROR = 160014;
    public static final int BEAN_TYPEID_ERROR = 160015;

    public static final int BEAN_FACTORY_IOERROR = 160050;

    public static final int DEPLOY_DELETE_ERROR = 161001;
    public static final int DEPLOY_EXPORT_ERROR = 161002;
    public static final int DEPLOY_DUPLICATE_NAME = 161003;
    public static final int DEPLOY_UNABLE_CHANGE = 161004;
    public static final int DEPLOY_UNABLE_START = 161005;
    public static final int DEPLOY_START_ERROR = 161006;
    public static final int DEPLOY_IMPORT_FMT_ERROR = 161007;

    public static final int NODE_BAD_NEXT_NODE = 170001;
    public static final int NODE_BAD_INPUT = 170002;
    public static final int NODE_SCRIPT_ERROR = 170003;
    public static final int NODE_ENCODING_ERROR = 170004;
    public static final int NODE_DECODING_ERROR = 170005;
    public static final int NODE_CONFIG_ERROR = 170006;
    public static final int DATASTRUCT_CONFIG_ERROR = 170007;
    public static final int ES_CONFIG_ERROR = 170008;
    public static final int ES_INDEX_ERROR = 170009;

    public static final int NODE_RUN_ERROR = 170010;
    public static final int NODE_RUN_NETWORK_ERROR = 170011;
    public static final int NODE_RUN_DB_ERROR = 170012;
    public static final int NODE_RUN_ES_ERROR = 170013;
    public static final int NODE_PARSE_ERROR = 170014;
    public static final int KAFKA_ERROR = 170015;
    public static final int ES_FATAL = 170016;
    public static final int NODE_POLICYID_NULL = 170017;
    public static final int NODE_POLICY_NULL = 170018;
    public static final int NODE_PARSER_ERROR = 170019;
    public static final int NODE_FILTERID_NULL = 170020;
    public static final int NODE_FILTER_NULL = 170021;
    public static final int NODE_PARSERID_NULL = 170022;
    public static final int NODE_PARSER_NULL = 170023;
    public static final int NODE_ERICHID_NULL = 170024;
    public static final int NODE_ERICH_ERROR = 170025;
    public static final int NODE_ERICH_NULL = 170026;
    public static final int AES_DECODE_ERR = 170027;
    public static final int AES_ENCODE_ERR = 170028;
    public static final int JAR_NOT_EXIST = 170029;


    public static final int FLOW_NOT_FOUND = 171001;


    /***
     * 地理库位置批量导入失败
     */
    public static final int IMPORT_GEOGRAPHICAL_FAILED = 171002;

    /***
     * 地理库位置修改参数错误
     */
    public static final int UPDATE_GEOGRAPHICAL_LIST_EMPTY = 171003;

    public static final int DYNDATA_TYPE_NOT_FOUND = 172001;
    public static final int TD_TOPIC_NOT_FOUND = 173001;
    public static final int THIRD_TOPIC_NOT_FOUND = 173002;

    /**
     * 上传插件时候定义的类型错误
     */
    public static final int PUBLIC_PLUGIN_TYPE_ERROR = 180001;

    /**
     * 公共插件优先级填写错误
     */
    public static final int PUBLIC_PLUGIN_PRIORITY_ERROR = 180002;

    /**
     * 插件的类路径已经存在
     */
    public static final int PLUGIN_CLASSPATH_ALREADY_EXIST = 180005;

    /**
     * 不同名字相同插件的类路径已经存在
     */
    public static final int PLUGIN_SAME_NAME_CLASSPATH_ALREADY_EXIST = 190005;

    public static final int CK_CONNECT_ERROR = 200001;
    public static final int CK_CLOSE_ERROR = 200002;
    public static final int NODE_RUN_CK_ERROR = 200003;

    public static final int PG_CONNECT_ERROR = 200011;
    public static final int PG_CLOSE_ERROR = 200012;
    public static final int NODE_RUN_PG_ERROR = 200013;


    /**
     * 生成id失败
     */
    public static final int ID_GENERATOR_ERROR = 210009;


    /**
     * 字符长度不得超过32个字符
     */
    public static final int CHAR_LENGTH_OVER_32 = 211001;

    /**
     * 不能包含以下特殊字符：\ / < > & " ' ` # * ^~| , : ; ? [ ] % $
     */
    public static final int SPECIAL_CHAR_FOUND = 211002;

    /**
     * 字符长度超过64位
     */
    public static final int CHAR_LENGTH_OVER_64 = 211003;

    /**
     * 没有可导入的数据，请检查数据内容
     */
    public static final int NO_DATA_IMPORT = 211004;
    /**
     * 导入文件为空
     */
    public static final int IMPORT_CONFIG_FILE_EMPTY = 211005;
    /**
     * 只支持xlsx或zip格式的文件导入
     */
    public static final int MUST_EXCEL_OR_ZIP = 211006;
    /**
     * 暂不支持该种导入格式，请重新选择文件
     */
    public static final int IMPORT_FORMAT_NOT_SUPPORT = 211007;
    /**
     * 只支持json格式的文件导入
     */
    public static final int MUST_JSON_FILE = 211008;
    /**
     * 字符长度超过128位
     */
    public static final int CHAR_LENGTH_OVER_128 = 211009;

    /**
     * 告警策略不存在
     */
    public static final int ALARM_STRATEGY_NOT_EXIST = 230001;
    /**
     * 告警策略已存在
     */
    public static final int ALARM_STRATEGY_IS_EXIST = 230002;
    /**
     * 告警策略名称不能为空
     */
    public static final int ALARM_STRATEGY_NAME_NULL = 230003;
    /**
     * 告警类型不能为空
     */
    public static final int ALARM_STRATEGY_TYPE_NULL = 230004;
    /**
     * 告警内容不能为空
     */
    public static final int ALARM_STRATEGY_CONTENT_NULL = 230005;
    /**
     * 告警策略表达式填写错误
     */
    public static final int ALARM_STRATEGY_EXPRESSION_ERROR = 230006;
    /**
     * 告警级别不能为空
     */
    public static final int ALARM_STRATEGY_LEVEL_NULL = 230007;
    /**
     * 告警级别填写错误
     */
    public static final int ALARM_STRATEGY_LEVEL_ERROR = 230008;
    /**
     * 告警重复次数不能为空
     */
    public static final int ALARM_STRATEGY_REPEAT_NUM_NULL = 230009;
    /**
     * 告警策略是否启用填写错误
     */
    public static final int ALARM_STRATEGY_ENABLE_ERROR = 230010;
    /**
     * 告警接收人不能为空
     */
    public static final int ALARM_STRATEGY_RECEIVER_NULL = 230011;
    /**
     * 告警接收方式不能为空
     */
    public static final int ALARM_STRATEGY_RECEIVE_MODE_NULL = 230012;
    /**
     * 告警策略导入错误码
     */
    public static final int ALARM_STRATEGY_IMPORT_ERROR = 230013;
    /**
     * 告警重复次数范围1-10
     */
    public static final int ALARM_STRATEGY_REPEAT_RANGE = 230014;
    /**
     * 预置的告警策略不允许覆盖导入，请重新设置导入策略！
     */
    public static final int ALARM_STRATEGY_PRESET_NOT_ALLOW_IMPORT = 230015;
    /**
     * 告警持续时间错误
     */
    public static final int ALARM_STRATEGY_CONTINUE_TIME_ERROR = 230016;

    /**
     * 数据结构不存在
     */
    public static final int DATASTRUCTURE_NOT_EXIST = 250001;
    /**
     * 数据结构已存在
     */
    public static final int DATASTRUCTURE_IS_EXIST = 250002;
    /**
     * 数据结构别名已存在
     */
    public static final int DATASTRUCTURE_NAME_ALIAS_IS_EXIST = 250003;
    /**
     * 数据结构导入数据不存在
     */
    public static final int DATASTRUCTURE_IMPORT_DATA_NOT_EXIST = 250004;
    /**
     * 数据结构导入错误
     */
    public static final int DATASTRUCTURE_IMPORT_ERROR = 250005;
    /**
     * 数据结构名称为空
     */
    public static final int DATASTRUCTURE_NAME_NULL = 250006;
    /**
     * 数据结构名称长度不能超过64位
     */
    public static final int DATASTRUCTURE_NAME_LENGTH_OVER_64 = 250007;
    /**
     * 数据结构别名为空
     */
    public static final int DATASTRUCTURE_NAME_ALIAS_NULL = 250008;
    /**
     * 数据结构别名长度不能超过64位
     */
    public static final int DATASTRUCTURE_NAME_ALIAS_LENGTH_OVER_64 = 250009;
    /**
     * 数据结构描述长度不得超过500个字符
     */
    public static final int DATASTRUCTURE_DESC_LENGTH_OVER_500 = 250010;
    /**
     * 数据结构dynamic_templates长度不能超过500个字符
     */
    public static final int DATASTRUCTURE_TEMP_LENGTH_OVER_500 = 250011;
    /**
     * 数据结构存储信息不能为空
     */
    public static final int DATASTRUCTURE_STORE_NULL = 250012;
    /**
     * 数据结构存储类型错误
     */
    public static final int DATASTRUCTURE_STORE_TYPE_ERROR = 250013;
    /**
     * 数据结构分区方式错误
     */
    public static final int DATASTRUCTURE_STORE_PARTITION_TYPE_ERROR = 250014;
    /**
     * 数据结构字段为空
     */
    public static final int DATASTRUCTURE_FIELD_NULL = 250015;
    /**
     * 数据结构字段名称为空
     */
    public static final int DATASTRUCTURE_FIELD_NAME_NULL = 250016;
    /**
     * 数据结构字段名称长度不能超过32个字符
     */
    public static final int DATASTRUCTURE_FIELD_NAME_LENGTH_OVER_32 = 250017;
    /**
     * 数据结构字段名称必须以字母打头
     */
    public static final int DATASTRUCTURE_FIELD_MUST_START_LETTER = 250018;
    /**
     * 数据结构字段别名为空
     */
    public static final int DATASTRUCTURE_FIELD_NAME_ALIAS_NULL = 250019;
    /**
     * 数据结构字段别名长度不能超过32个字符
     */
    public static final int DATASTRUCTURE_FIELD_NAME_ALIAS_LENGTH_OVER_32 = 250020;
    /**
     * 数据结构字段描述不能超过500个字符
     */
    public static final int DATASTRUCTURE_FIELD_DESC_LENGTH_OVER_500 = 250021;
    /**
     * 数据结构字段类型不能为空
     */
    public static final int DATASTRUCTURE_FIELD_TYPE_NULL = 250022;
    /**
     * 字段重复
     */
    public static final int DATASTRUCTURE_FIELD_IS_REPEAT = 250023;

    /**
     * ES分片数必须大于等于2
     */
    public static final int DATASTRUCTURE_STORE_PARTITION_NUM_MIN = 250024;
    /**
     * 最小保留天数取值范围为1-1000
     */
    public static final int DATASTRUCTURE_STORE_SAVING_DAYS_ERROR = 250025;
    /**
     * 同步失败
     */
    public static final int DATASTRUCTURE_TO_DB_ERROR = 250026;
    /**
     * 数据结构名称必须以字母开头
     */
    public static final int DATASTRUCTURE_MUST_START_LETTER = 250027;
    /**
     * 数据结构被引用不允许删除字段
     */
    public static final int DATASTRUCTURE_IS_QUOTE_DELETE_FIELD = 250028;

    /**
     * 重要度枚举值错误
     */
    public static final int DATASTRUCTURE_STORE_PRIORITY_ERROR = 250031;
    /**
     * 期望保留天数取值范围为1-1000
     */
    public static final int DATASTRUCTURE_HOPE_SAVING_DAYS_ERROR = 250032;

    /**
     * 期望保留天数必须大于最小保留天数
     */
    public static final int DATASTRUCTURE_HOPE_GREATER_THAN_MIN_SAVING_DAYS = 250033;

    /**
     * 日志类型字段父节点类型错误
     */
    public static final int DATASTRUCTURE_FIELD_PARENT_TYPE_ERROR = 250034;

    /**
     * 日志类型库名没有在系统预定义变量中配置
     */
    public static final int DATASTRUCTURE_DATABASE_NOT_EXIST = 250035;

    /**
     * 已同步的日志类型不允许对已存在的struct字段进行修改
     */
    public static final int DATASTRUCTURE_FIELD_STRUCT_NOT_UPDATE = 250036;

    /**
     * 数据结构分组全部和未分组节点不允许删除
     */
    public static final int DATASTRUCTURE_GROUP_NOT_DELETE = 251001;
    /**
     * 数据结构分组不能超过5级
     */
    public static final int DATASTRUCTURE_GROUP_HIERARCHY_OVER = 251002;
    /**
     * 数据结构分组不存在
     */
    public static final int DATASTRUCTURE_GROUP_NOT_EXIST = 251003;
    /**
     * "未分组"不能作为父组节点
     */
    public static final int DATASTRUCTURE_GROUP_UNKNOW_AS_PARENT = 251004;
    /**
     * 不能设置自己作为父节点
     */
    public static final int DATASTRUCTURE_GROUP_PARENT_SAME_ITSELF = 251005;
    /**
     * 不能设置子节点作为父节点
     */
    public static final int DATASTRUCTURE_GROUP_USE_CHILD_AS_PARENT = 251006;
    /**
     * 数据结构分组名称不能为空
     */
    public static final int DATASTRUCTURE_GROUP_NAME_NULL = 251007;
    /**
     * 数据结构分组名称长度超过32字符
     */
    public static final int DATASTRUCTURE_GROUP_NAME_LENGTH_OVER_32 = 251008;
    /**
     * 数据结构分组描述长度超过500字符
     */
    public static final int DATASTRUCTURE_GROUP_DESC_LENGTH_OVER_500 = 251009;
    /**
     * 数据结构分组必须是叶子节点
     */
    public static final int DATASTRUCTURE_GROUP_MUST_CHILD_NODE = 251010;
    /**
     * 数据结构分组已存在
     */
    public static final int DATASTRUCTURE_GROUP_IS_EXIST = 251011;

    /**
     * 数据结构ClickHouse分布字段为空，请填写分布字段或分布函数
     */
    public static final int DATASTRUCTURE_STORE_CK_DISTRIBUTED_MUST = 252001;
    /**
     * 数据结构ClickHouse排序字段为空，请填写排序字段或排序函数
     */
    public static final int DATASTRUCTURE_STORE_CK_ORDER_MUST = 252002;
    /**
     * 数据结构ClickHouse样本字段为空，请填写样本字段或样本函数
     */
    public static final int DATASTRUCTURE_STORE_CK_SAMPLE_MUST = 252003;

    /**
     * ClickHouse分布字段不在数据结构中，请确定字段填写正确
     */
    public static final int DATASTRUCTURE_STORE_CK_DISTRIBUTED_FIELD_NOT_EXIST = 252004;
    /**
     * ClickHouse排序字段不在数据结构中，请确定字段填写正确
     */
    public static final int DATASTRUCTURE_STORE_CK_ORDER_FIELD_NOT_EXIST = 252005;
    /**
     * ClickHouse样本字段不在数据结构中，请确定字段填写正确
     */
    public static final int DATASTRUCTURE_STORE_CK_SAMPLE_FIELD_NOT_EXIST = 252006;
    /**
     * ClickHouse分布字段必须是Integer或Long类型的字段
     */
    public static final int DATASTRUCTURE_STORE_CK_DISTRIBUTED_FIELD_MUST_INTEGER = 252007;
    /**
     * ClickHouse抽样字段必须是排序字段中的一个
     */
    public static final int DATASTRUCTURE_STORE_CK_SAMPLE_FIELD_IN_ORDER_FIELD = 252008;


    /**
     * 搜索名称非法
     */
    public static final int SEARCH_CONDITION_NAME_INVALID = 251012;
    /**
     * 搜索名称不能为空
     */
    public static final int SEARCH_CONDITION_NAME_NOT_NULL = 251013;
    /**
     * 搜索条件模式不能为空
     */
    public static final int SEARCH_CONDITION_MODE_NOT_NULL = 251014;
    /**
     * 数据类型不能为空
     */
    public static final int SEARCH_CONDITION_DATATYPE_NOT_NULL = 251015;
    /**
     * 开始时间不能为空
     */
    public static final int SEARCH_CONDITION_START_TIME_NOT_NULL = 251016;
    /**
     * 结束时间不能为空
     */
    public static final int SEARCH_CONDITION_END_TIME_NOT_NULL = 251017;


    /**
     * 无法连接Kafka
     */
    public static final int KAFKA_CONNECT_ERROR = 253001;

    /**
     * 无法连接zookeeper
     */
    public static final int ZOOKEEPER_CONNECT_ERROR = 253002;

    /**
     * 无法连接yarn
     */
    public static final int YARN_CONNECT_ERROR = 254001;

    /**
     * 无法连接ElasticSearch
     */
    public static final int ES_CONNECT_ERROR = 255001;
    /**
     * 错误的fields属性
     */
    public static final int ES_FIELDS_ERROR = 255002;
    /**
     * 错误的dynamic_templates属性
     */
    public static final int ES_DYNAMIC_TEMPLATES_ERROR = 255003;
    /**
     * 缺少必要的ES配置
     */
    public static final int ES_CONFIG_LACK = 255004;
    /**
     * ES不支持该analyzer参数
     */
    public static final int ES_ANALYZER_ERROR = 255005;
    /**
     * ElasticSearch创建模板失败
     */
    public static final int ES_CREATE_EXCEPTION = 255006;
    /**
     * 错误的analyzer配置
     */
    public static final int ES_ANALYZER_CONF_ERROR = 255007;
    /**
     * 其他信息填写错误
     */
    public static final int ES_OTHER_PROPERTIES_ERROR = 255008;

    /**
     * 无法连接Hive
     */
    public static final int HIVE_CONNECT_ERROR = 256001;
    /**
     * Hive建表失败
     */
    public static final int HIVE_CREATE_EXCEPTION = 256002;
    /**
     * 缺少hive.host参数
     */
    public static final int HIVE_PARAM_NOT_HOST = 256003;
    /**
     * 缺少hive.user参数
     */
    public static final int HIVE_PARAM_NOT_USER = 256004;
    /**
     * 缺少hive.passwd参数
     */
    public static final int HIVE_PARAM_NOT_PASSWD = 256005;
    /**
     * 删除表失败
     */
    public static final int HIVE_DROP_EXCEPTION = 256006;

    public static final int CK_CREATE_EXCEPTION = 257002;

    /**
     * 告警阈值必须小于清除阈值
     */
    public static final int CLEAN_MORE_THAN_ALARM = 260001;

    /**
     * 告警阈值必须在合理范围内(30%-70%)
     */
    public static final int ALARM_THRESHOLD_SCOPE_ERROR = 260002;

    /**
     *  清除阈值必须在合理范围内(40%-90%)
     */
    public static final int CLEAN_THRESHOLD_SCOPE_ERROR = 260003;

    /**
     * IP已存在
     */
    public static final int SOURCE_IP_ALREADY_EXIST = 290001;

    /**
     * 数据库执行异常
     */
    public static final int DB_EXECUTE_ERROR = 270001;

    /**
     * BM不可用
     */
    public static final int BM_UNAVAILABLE = 270002;

    /**
     * BM服务接口错误
     */
    public static final int BM_SERVICE_ERROR = 270003;

    /**
     * BM接口轮训
     */
    public static final int BM_SERVICE_TRAINING_IN_ROTATION = 270004;


    /**
     * 采集器已被删除
     */
    public static final int COLLECTOR_HAS_BEAN_DELETED = 270009;

    /**
     * 数据删除失败
     */
    public static final int DATA_DELETE_FAIL = 270010;

    /**
     * 验证码获取失败
     */
    public static final int VERIFY_CODE_ERROR = 270011;

    /**
     * 任务提交失败
     */
    public static final int TASK_SUBMIT_FAIL = 270012;

    /**
     * 数据保存失败
     */
    public static final int DATA_SAVE_FAIL = 270012;
    /**
     * 条件分组名称不能为空
     */
    public static final int CONDITION_GROUP_NAME_IS_NOT_NULL = 270013;
    /**
     * 条件分组不存在
     */
    public static final int CONDITION_GROUP_NOT_EXIST = 270014;
    /**
     * 不能设置自己作为父节点
     */
    public static final int CONDITION_GROUP_PARENT_SAME_ITSELF = 270016;
    /**
     * 不能设置子节点作为父节点
     */
    public static final int CONDITION_GROUP_USE_CHILD_AS_PARENT = 270017;
    /**
     * 查询条件分组不能超过2级
     */
    public static final int CONDITION_GROUP_HIERARCHY_OVER = 270018;
    /**
     * 查询条件分组全部和未分组节点不允许操作
     */
    public static final int CONDITION_GROUP_NOT_OPERATE = 270019;

    /**
     * 验证码校验未通过
     */
    public static final int VERIFY_CODE_FAIL = 270020;

    /**
     * 勾选的数据，不满足删除条件，请检查是否被引用
     */
    public static final int DELETE_FAILED_CHECK_USED = 280002;

    /**
     * 系统内置TOPIC不允许修改或删除
     */
    public static final int DELETE_FAILED_TOPIC = 280003;

    /**
     * TOPIC不存在
     */
    public static final int NO_FOUND_TOPIC = 280004;
    /**
     * 获取KAFKA默认配置参数异常
     */
    public static final int KAFKA_CONFIG_PLUGINTYPE_ERROR = 280005;
    /**
     * topic已存在
     */
    public static final int TOPIC_EXIST_ERROR = 280006;
    /**
     * 调整分区异常
     */
    public static final int KAFKA_REPLICA_ERROR = 280007;
    /**
     * kafka名称异常
     */
    public static final int KAFKA_NAME_ERROR = 280008;
    /**
     * kafka删除异常
     */
    public static final int KAFKA_DELETE_ERROR = 280009;
    /**
     * 同名异常 已经有同名的配置，请修改配置名称
     */
    public static final int SAME_NAME_ERROR = 280010;
    /**
     * key已存在
     */
    public static final int KEY_EXIST_ERROR = 280011;
    /**
     * 删除失败
     */
    public static final int DELETE_FAILED = 280012;
    /**
     * 导入失败
     */
    public static final int IMPORT_FAILED = 280013;
    /**
     * 导入校验失败
     */
    public static final int IMPORT_CHECK_FAILED = 280014;


    /**
     * ES模板属性更新失败
     */
    public static final int ES_TEMPLATE_PUT_SETTING_ERROR = 280015;

    /**
     * ES模板不存在
     */
    public static final int ES_TEMPLATE_NOT_EXIST = 280016;

    /**
     * 连通性测试异常
     */
    public static final int CONNECT_TEST_ERROR = 281000;

    /**
     * 连通性测试异常
     */
    public static final int CONFIG_NAME_NOT_EXIST = 281017;

    /**
     * 连通性测试异常
     */
    public static final int HTTP_ADD_FAILED = 281001;


    /**
     * SQL语句解析错误
     */
    public static final int SEARCH_SQL_ERROR = 290001;
    /**
     * 字段统计支持的范围为1-5000，请查看配置项
     */
    public static final int SEARCH_FIELD_RANK_MAX_RANGE = 290002;
    /**
     * 搜索的时间字段必须配置，请配置后使用
     */
    public static final int SEARCH_FIELD_MUST_EXIST = 290003;

    /**
     * Kafka的Ldap配置类型错误,选择文件或用户名密码的方式
     */
    public static final int KAFKA_LDAP_TYPE_ERROR = 290004;


    /**
     * Kafka的Ldap的文件配置类型，文件路不能为空
     */
    public static final int KAFKA_LDAP_FILEPATH_MUST_NOT_NULL = 290005;


    /**
     * Kafka的Ldap的用户名或者密码为空
     */
    public static final int KAFKA_LDAP_USERNAME_PASSWORD_NULL = 290006;

    /**
     * 审计日志模块名称错误
     */
    public static final int LOG_MODULE_NAME_ERROR = 300001;

    /**
     * 审计日志动作错误
     */
    public static final int LOG_ACTION_ERROR = 300002;

    /**
     * 审计操作对象错误
     */
    public static final int LOG_OBJECT_ERROR = 300003;

    /**
     * 审计操作结果错误
     */
    public static final int LOG_RESUTL_ERROR = 300004;

    /**
     * 查询检索调用历史记录接口异常
     */
    public static final int SEARCH_HISTORY_ERROR = 290008;

    /**
     * 获取查询检索表名异常
     * 查询检索表名存在异常或者sql语句存在语法错误，请修改后重新查询！
     */
    public static final int SEARCH_TABLE_NAME_ERROR = 290009;

    /**
     * 获取导入数据异常，请检查导入数据格式
     */
    public static final int IMPORT_FORMAT_EXCEPTION = 290010;

    /**
     * 获取CK默认配置失败，缺少CK默认配置
     */
    public static final int CK_DEFAULT_MISSING_EXCEPTION = 290100;


    /**
     * 产品名称不能为空
     */
    public static final int PRODUCTNAME_IS_NOT_NULL = 300001;

    /**
     * license值错误
     */
    public static final int LICENSE_IS_NOT_NULL = 400002;

    public static final int MODIFY_LICENSE_ERROR = 400003;

    public static final int LICENSE_NUM_OUT = 400004;

    /**
     * 数据读写或者流关闭错误
     */
    public static final int IO_EXCEPTION = 400010;

    /**
     * 创建备份失败
     */
    public static final int BACKUP_CREATE_FAILED = 400012;


    /**
     * 备份名称不合法
     */
    public static final int BACKUP_NAME_MUST = 400013;

    /**
     * 删除规则备份文件失败
     */
    public static final int DELETE_RULEBACK_ERROR = 400014;

    /**
     * 备份文件被修改
     */
    public static final int BACKFILE_ERROR = 400015;

    public static final int BACKNAME_EXIST= 400016;

    /**
     * 数据源表被占用
     */
    public static final int LOG_SYNC_ERROR = 400017;
    /**
     * 数据源迁移失败
     */
    public static final int DATASOURCE_TRANSFER_ERROR = 400018;
    /**
     * 规则详情不存在
     */
    public static final int DETAILS_NOT_EXIST = 400019;

    /**
     * 开关存储统计失败
     */
    public static final int DESTLOG_COUNT_MODIFYOPEN_ERROR=500001;

    /**
     * 数据统计查询天数有误，最长30天，最短7天
     */
    public static final int DESTLOG_COUNT_QUERY_TIMEOUT=500002;
}
