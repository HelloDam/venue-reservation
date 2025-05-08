package com.vrs.chain_of_responsibility;

import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author dam
 * @create 2024/12/11 19:20
 */
@Component
public class ChainContext<T> implements ApplicationContextAware, CommandLineRunner {
    /**
     * 通过 Spring IOC 获取 Bean 实例
     */
    private ApplicationContext applicationContext;
    /**
     * key：责任链名称
     * value：责任链
     */
    private final Map<String, List<AbstractChainHandler>> chainContainer = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        // 从 Spring IOC 容器中获取接口对应的 Spring Bean 集合
        Map<String, AbstractChainHandler> chainFilterMap = applicationContext.getBeansOfType(AbstractChainHandler.class);
        chainFilterMap.forEach((beanName, bean) -> {
            // 判断 name 是否已经存在抽象责任链容器中
            // 如果已经存在直接向集合新增
            // 如果不存在，创建对应的集合
            List<AbstractChainHandler> abstractChainHandlers = chainContainer.getOrDefault(bean.name(), new ArrayList<>());
            abstractChainHandlers.add(bean);
            chainContainer.put(bean.name(), abstractChainHandlers);
        });
        chainContainer.forEach((mark, unsortedChainHandlers) -> {
            // 对每个责任链的实现类根据order升序排序
            Collections.sort(unsortedChainHandlers, ((o1, o2) -> {
                return Integer.compare(o1.order(), o2.order());
            }));
        });
    }

    /**
     * 责任链组件执行
     *
     * @param name         责任链组件标识
     * @param requestParam 请求参数
     */
    public void handler(String name, T requestParam) {
        // 根据 name 从责任链容器中获取对应的责任链
        List<AbstractChainHandler> abstractChainHandlers = chainContainer.get(name);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new RuntimeException(name + "对应的责任链不存在");
        }

        // 遍历责任链处理器
        for (AbstractChainHandler handler : abstractChainHandlers) {
            if (handler.handle(requestParam)) {
                // --if-- 如果处理器返回 true，表示已经处理完成，退出责任链
                return;
            }
        }
    }
}
