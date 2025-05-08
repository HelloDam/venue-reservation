package com.vrs.algorithm;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

/**
 * 订单分表基因算法 - 该类用于实现复杂的分片逻辑，特别是当存在多个分片键时。(这里是订单号和用户ID)
 * 它会根据提供的分片值来决定数据应该存储在哪一个分片表中。
 *
 * @Author dam
 * @create 2024/12/6 9:55
 */
@Getter
public class OrderTableGeneAlgorithm implements ComplexKeysShardingAlgorithm {

    private Properties props;

    /**
     * 分片的数量，即总共有多少个分片表
     */
    private int shardingCount;

    /**
     * 配置文件中的分片数量键名
     */
    private static final String SHARDING_COUNT_KEY = "sharding-count";

    /**
     * 根据提供的分片键和分片值来决定将数据分配到哪个分片表中。
     *
     * @param collection               可能的分片表集合
     * @param complexKeysShardingValue 包含分片键及其对应的值
     * @return 返回包含具体分片表名称的集合
     */
    @Override
    public Collection<String> doSharding(Collection collection, ComplexKeysShardingValue complexKeysShardingValue) {
        // 获取分片键与分片值的映射
        Map<String, Collection<Comparable<?>>> columnNameAndShardingValuesMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
        // 初始化结果集，使用 LinkedHashSet 以保持插入顺序
        Collection<String> result = new LinkedHashSet<>(collection.size());
        if (CollUtil.isNotEmpty(columnNameAndShardingValuesMap)) {
            // --if-- 如果有分片键和值，则开始处理
            String userId = "user_id";
            // 获取 'user_id' 对应的分片值集合
            Collection<Comparable<?>> customerUserIdCollection = columnNameAndShardingValuesMap.get(userId);
            if (CollUtil.isNotEmpty(customerUserIdCollection)) {
                // --if-- 'user_id' 存在且不为空，则基于 'user_id' 进行分片
                // 获取第一个分片值
                Comparable<?> comparable = customerUserIdCollection.stream().findFirst().get();
                // 取用户ID的后面六位来进行哈希分片
                String dbSuffix = String.valueOf(hashShardingValue((Long) comparable % 1000000) % shardingCount);
                result.add(complexKeysShardingValue.getLogicTableName() + "_" + dbSuffix);
            } else {
                //  'user_id' 不存在或为空，尝试使用 'order_sn' 作为分片键
                String orderSn = "order_sn";
                Collection<Comparable<?>> orderSnCollection = columnNameAndShardingValuesMap.get(orderSn);
                Comparable<?> comparable = orderSnCollection.stream().findFirst().get();
                if (comparable instanceof String) {
                    // --if-- 如果订单号是字符串类型
                    String actualOrderSn = comparable.toString();
                    String substring = actualOrderSn.substring(Math.max(actualOrderSn.length() - 6, 0));
                    result.add(complexKeysShardingValue.getLogicTableName() + "_" + hashShardingValue(Integer.parseInt(substring)) % shardingCount);
                } else {
                    // --if-- 如果订单号是长整型（我们这个系统肯定不是这个）
                    String dbSuffix = String.valueOf(hashShardingValue((Long) comparable % 1000000) % shardingCount);
                    result.add(complexKeysShardingValue.getLogicTableName() + "_" + dbSuffix);
                }
            }
        }

        // 返回最终确定的分片表名称集合
        return result;
    }

    /**
     * 初始化方法，在创建分片算法实例时被调用，用来设置分片参数。
     *
     * @param props 包含分片配置信息的属性对象
     */
    @Override
    public void init(Properties props) {
        this.props = props;
        shardingCount = getShardingCount(props);
    }

    /**
     * 从配置属性中读取分片数量，如果未找到则抛出异常。
     *
     * @param props 包含分片配置信息的属性对象
     * @return 分片数量
     */
    private int getShardingCount(final Properties props) {
        // 检查是否提供了分片数量，如果没有则抛出异常
        Preconditions.checkArgument(props.containsKey(SHARDING_COUNT_KEY), "分片数量不可以为空");
        // 解析并返回分片数量
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));
    }

    /**
     * 根据给定的分片值计算哈希值，用于确定具体的分片。
     *
     * @param shardingValue 分片值
     * @return 哈希后的分片值
     */
    private long hashShardingValue(final Comparable<?> shardingValue) {
        // 使用分片值的 hashCode 生成一个绝对值的哈希码
        return Math.abs((long) shardingValue.hashCode());
    }
}
