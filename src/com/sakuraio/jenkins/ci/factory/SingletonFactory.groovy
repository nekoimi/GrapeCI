package com.sakuraio.jenkins.ci.factory

import com.sakuraio.jenkins.ci.core.Initialization

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
/**
 * <p>单例Factory</p>
 *
 * @author nekoimi 2023/02/10
 */
class SingletonFactory implements Serializable {

    private static final SingletonFactory INSTANCE = new SingletonFactory()

    /**
     * 实例缓存
     */
    private ConcurrentMap<Class<?>, Object> instanceMap = new ConcurrentHashMap<>()

    /**
     * <p>获取单例</p>
     *
     * @param jenkins Jenkins对象
     * @param targetClazz 目标类型
     * @param <T>
     * @return
     */
    static <T> T singletonInstance(jenkins, Class<T> targetClazz, Object... args) {
        T finalValue
        def newValue = newInstance(targetClazz, args)
        def oldValue = INSTANCE.instanceMap.get(targetClazz)
        if (newValue) {
            oldValue = INSTANCE.instanceMap.putIfAbsent(targetClazz, newValue)
            if (!oldValue) {
                finalValue = newValue
                if (finalValue instanceof Initialization) {
                    // 第一次 -> 执行初始化
                    (finalValue as Initialization).initialization(jenkins)
                }
            } else {
                finalValue = oldValue as T
            }
        } else {
            finalValue = oldValue as T
        }
        return finalValue
    }

    /**
     * <p>获取实例</p>
     *
     * @param clazz 类型
     * @param args 构造函数参数
     * @param <T>      泛型
     * @return
     */
    private static <T> T newInstance(Class<T> clazz, Object... args) {
        return clazz.getDeclaredConstructor()?.newInstance(args)
    }
}
