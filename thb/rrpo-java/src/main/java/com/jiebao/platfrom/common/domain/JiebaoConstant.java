package com.jiebao.platfrom.common.domain;

/**
 * JIEBAO常量
 */
public class JiebaoConstant {

    //wechatMini code
    public static final String MINI_LOGIN_TEMP_TOKEN = "wechat.mini.login.temp.token.";
    public static final Integer STATUS_CODE_ERROR = 400005;    //处理异常
    public static final Integer STATUS_CODE_NOT_REGISTRY = 400001;     //未注册
    public static final Integer STATUS_CODE_SUCCESS = 1;   //操作成功

    //dept缓存
    public static final String DEPT_CACHE = "jiebao.cache.dept";

    // user缓存前缀
    public static final String USER_CACHE_PREFIX = "jiebao.cache.user.";
    // user角色缓存前缀
    public static final String USER_ROLE_CACHE_PREFIX = "jiebao.cache.user.role.";
    // user权限缓存前缀
    public static final String USER_PERMISSION_CACHE_PREFIX = "jiebao.cache.user.permission.";
    // user个性化配置前缀
    public static final String USER_CONFIG_CACHE_PREFIX = "jiebao.cache.user.config.";
    // token缓存前缀
    public static final String TOKEN_CACHE_PREFIX = "jiebao.cache.token.";

    // 存储在线用户的 zset前缀
    public static final String ACTIVE_USERS_ZSET_PREFIX = "jiebao.user.active";

    // 排序规则： descend 降序
    public static final String ORDER_DESC = "descend";
    // 排序规则： ascend 升序
    public static final String ORDER_ASC = "ascend";

    // 按钮
    public static final String TYPE_BUTTON = "1";
    // 菜单
    public static final String TYPE_MENU = "0";

    // 网络资源 Url
    public static final String MEIZU_WEATHER_URL = "http://aider.meizu.com/app/weather/listWeather";
    public static final String MRYW_TODAY_URL = "https://interface.meiriyiwen.com/article/today";
    public static final String MRYW_DAY_URL = "https://interface.meiriyiwen.com/article/day";
    public static final String TIME_MOVIE_HOT_URL = "https://api-m.mtime.cn/Showtime/LocationMovies.api";
    public static final String TIME_MOVIE_DETAIL_URL = "https://ticket-api-m.mtime.cn/movie/detail.api";
    public static final String TIME_MOVIE_COMING_URL = "https://api-m.mtime.cn/Movie/MovieComingNew.api";
    public static final String TIME_MOVIE_COMMENTS_URL = "https://ticket-api-m.mtime.cn/movie/hotComment.api";

}
