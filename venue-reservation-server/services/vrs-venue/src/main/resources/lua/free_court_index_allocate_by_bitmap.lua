-- 定义脚本参数
local stock_key = KEYS[1]
local free_index_bitmap_key = KEYS[2]
-- 用来存储已购买用户的set
local set_name = KEYS[3]

-- 用户ID
local user_id = ARGV[1]
-- 过期时间 (秒)
local expire_time = tonumber(ARGV[2])
local free_court_bit = tonumber(ARGV[3])

-- 检查用户是否已经购买过
if redis.call("SISMEMBER", set_name, user_id) == 1 then
    -- 用户已经购买过，返回 -2 表示失败
    return -2
end

-- 获取库存
local current_inventory = tonumber(redis.call('GET', stock_key) or 0)

-- 尝试消耗库存
if current_inventory < 1 then
    -- 库存不够了，返回-1，代表分配空场号失败
    return -1 -- 失败
end

-- 如果有传入 free_court_bit，则检查该位置是否为 0
if free_court_bit > -1 then
    -- 检查指定位置是否为 0
    local bit_value = redis.call("GETBIT", free_index_bitmap_key, free_court_bit)
    if bit_value == 1 then
        -- 该位置已经被占用，返回 -3 表示失败
        return -3
    end
    -- 占用该场地（将对应位设置为 1）
    redis.call("SETBIT", free_index_bitmap_key, free_court_bit, 1)
else
    -- 如果没有传入 free_court_bit，则查找第一个空闲的场地（位图中第一个为 0 的位）
    free_court_bit = redis.call("BITPOS", free_index_bitmap_key, 0)
    if not free_court_bit or free_court_bit == -1 then
        -- 没有空闲的场号
        return -1 -- 失败
    end
    -- 占用该场地（将对应位设置为 1）
    redis.call("SETBIT", free_index_bitmap_key, free_court_bit, 1)
end

-- 更新库存
redis.call('DECRBY', stock_key, 1)
-- 添加用户到已购买集合
redis.call("SADD", set_name, user_id)
-- 设置过期时间
if expire_time > 0 then
    redis.call("EXPIRE", set_name, expire_time)
end

-- 返回分配的场地索引（注意：位图的位索引从0开始，如果你需要从1开始，这里加1）
return tonumber(free_court_bit)