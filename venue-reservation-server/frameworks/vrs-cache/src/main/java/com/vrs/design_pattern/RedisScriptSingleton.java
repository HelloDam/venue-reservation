package com.vrs.design_pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @Author dam
 * @create 2024/9/5 20:36
 */
public class RedisScriptSingleton {
    //volatile：在Java中，volatile关键字可以保证变量的内存可见性。
    //当一个变量被声明为volatile时，编译器和处理器会注意到这个变量可能会被其他线程并发地访问。
    //这样可以避免线程之间的数据竞争，并确保多线程环境下变量的值是最新的。
    private static volatile DefaultRedisScript instance;

    private static final String LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH = "lua/putIfAllAbsent.lua";

    private RedisScriptSingleton() {
    }

    //提供一个静态的公有方法，加入双重检查代码，解决线程安全问题, 同时解决懒加载问题
    //同时保证了效率, 推荐使用
    public static DefaultRedisScript getInstance() {
        if (instance == null) {
            synchronized (RedisScriptSingleton.class) {
                if (instance == null) {
                    DefaultRedisScript redisScript = new DefaultRedisScript();
                    // 设置脚本源，从类路径资源中加载Lua脚本
                    redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH)));
                    // 设置脚本预期的结果类型为Boolean
                    redisScript.setResultType(Boolean.class);
                    // 返回新创建的脚本实例
                    return redisScript;
                }
            }
        }
        return instance;
    }
}
