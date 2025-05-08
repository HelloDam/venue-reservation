-- 获取 Redis 中存储用户访问次数的键名
local accessKey = KEYS[1]
-- 时间窗口，单位：秒
local timeWindow = tonumber(ARGV[1])

-- 原子递增访问次数，并获取递增后的值
local currentAccessCount = redis.call("INCR", accessKey)
-- 设置键的过期时间
redis.call("EXPIRE", accessKey, timeWindow)

-- 返回当前访问次数
return currentAccessCount