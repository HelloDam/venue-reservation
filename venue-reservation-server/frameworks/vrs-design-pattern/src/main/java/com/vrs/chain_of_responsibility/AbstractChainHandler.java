package com.vrs.chain_of_responsibility;

/**
 * @Author dam
 * @create 2024/12/11 19:18
 */
public interface AbstractChainHandler<T> {
    /**
     * 由实现类来实现具体的处理方法
     */
    boolean handle(T param);

    /**
     * 名称，用来区分不同的责任链
     */
    String name();

    /**
     * 处理器的排序
     */
    int order();
}
