-- 定义脚本参数
local stock_key = KEYS[1]
local free_index_bitmap_key = KEYS[2]
-- 用来存储已购买用户的set
local set_name = KEYS[3]

-- 用户ID
local user_id = ARGV[1]
-- 场地索引
local court_index = tonumber(ARGV[2])

-- 检查用户是否已经购买过
if redis.call("SISMEMBER", set_name, user_id) == 0 then
    -- 用户没有购买过，返回 -2 表示失败
    return -2
end

-- 检查场地索引是否有效
if not court_index or court_index < 0 then
    -- 无效的场地索引，返回 -1 表示失败
    return -1
end

-- 检查场号是否本来就是处于空闲状态
local is_free = redis.call("GETBIT", free_index_bitmap_key, court_index)
if is_free == 0 then
    -- 场号本身就处于空闲状态，所以无需释放库存，返回 -3 表示错误
    return -3
end

-- 释放场号（将对应位设置为 0）
redis.call("SETBIT", free_index_bitmap_key, court_index, 0)

-- 更新库存（增加库存）
redis.call('INCRBY', stock_key, 1)

-- 移除用户
redis.call("SREM", set_name, user_id)

-- 返回成功
return 0 -- 成功