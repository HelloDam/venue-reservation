-- 定义了三个本地变量：
-- hashKey：表示在Redis中存储工作ID和数据中心ID的哈希表（Hash）的键名
-- dataCenterIdKey 和 workIdKey：分别表示哈希表中存储数据中心ID和工作ID的字段名
local hashKey = KEYS[1]
local dataCenterIdKey = 'dataCenterId'
local workIdKey = 'workId'

-- 首先，检查哈希表hashKey是否存在。
-- 如果不存在（即首次初始化），则创建该哈希表并使用hincrby命令初始化dataCenterIdKey和workIdKey字段，初始值均为0
-- 然后返回一个数组 { 0, 0 }，表示当前工作ID和数据中心ID均为0
if (redis.call('exists', hashKey) == 0) then
    redis.call('hincrby', hashKey, dataCenterIdKey, 0)
    redis.call('hincrby', hashKey, workIdKey, 0)
    return { 0, 0 }
end

-- 若哈希表已存在，从哈希表中获取当前的dataCenterId和workId值，并将其转换为数字类型
local dataCenterId = tonumber(redis.call('hget', hashKey, dataCenterIdKey))
local workId = tonumber(redis.call('hget', hashKey, workIdKey))

-- 定义最大值常量max为31，用于判断ID是否达到上限
local max = 31
-- 定义两个局部变量resultWorkId和resultDataCenterId，用于存储最终要返回的新工作ID和数据中心ID
local resultWorkId = 0
local resultDataCenterId = 0

-- 如果两者均达到上限（dataCenterId == max且workId == max），将它们重置为0
if (dataCenterId == max and workId == max) then
    redis.call('hset', hashKey, dataCenterIdKey, '0')
    redis.call('hset', hashKey, workIdKey, '0')

-- 若只有工作ID未达上限（workId ~= max），递增工作ID（hincrby），并将新的工作ID作为结果，数据中心ID保持不变
elseif (workId ~= max) then
    resultWorkId = redis.call('hincrby', hashKey, workIdKey, 1)
    resultDataCenterId = dataCenterId

-- 若只有数据中心ID未达上限（dataCenterId ~= max），递增数据中心ID，将新的数据中心ID作为结果，同时将工作ID重置为0
elseif (dataCenterId ~= max) then
    resultWorkId = 0
    resultDataCenterId = redis.call('hincrby', hashKey, dataCenterIdKey, 1)
    redis.call('hset', hashKey, workIdKey, '0')
end

return { resultWorkId, resultDataCenterId }
