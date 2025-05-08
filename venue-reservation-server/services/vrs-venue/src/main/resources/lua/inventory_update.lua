-- 定义脚本参数
local stock_key = KEYS[1]
local free_index_bitmap_key = KEYS[2]

-- 预订场号
local free_court_bit = ARGV[1]

-- 占用该场地（将对应位设置为 1）
redis.call("SETBIT", free_index_bitmap_key, free_court_bit, 1)
-- 更新库存
redis.call('DECRBY', stock_key, 1)

return 0