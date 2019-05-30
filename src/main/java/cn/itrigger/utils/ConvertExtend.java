package cn.itrigger.utils;

/**
 * 个性化字段dto转化
 *
 * @author 91976
 */
public interface ConvertExtend<T, K> {

    /**
     * 个性化字段dto转化
     *
     * @param t
     * @param k
     */
    void extend(T t, K k);

}
