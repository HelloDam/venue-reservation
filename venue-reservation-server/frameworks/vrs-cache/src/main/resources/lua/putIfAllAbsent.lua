-- 假设 KEYS 是一个由 Redis 客户端动态填充的表（数组），包含了需要检查和设置的键名。
-- 同样地，ARGV 是一个参数表，其中 ARGV[1] 指定了设置键值对时的有效期（以毫秒为单位）。

-- 遍历 KEYS 表中的每一个键名
for i, v in ipairs(KEYS) do
    -- 使用 exists 命令检查当前键是否存在于 Redis 中
    if (redis.call('exists', v) == 1) then
        -- 如果键存在，则直接返回 nil
        return nil;
    end
end

-- 如果上面的循环没有提前返回，说明所有给定的键都不存在
-- 再次遍历 KEYS 表中的每一个键名
for i, v in ipairs(KEYS) do
    -- 使用 set 命令设置键的值为 'default'
    redis.call('set', v, 'default');
    -- 使用 pexpire 命令为每个键设置过期时间，单位是毫秒
    redis.call('pexpire', v, ARGV[1]);
end

-- 所有键都已成功设置且没有提前返回，最后返回 true 表示操作成功
return true;