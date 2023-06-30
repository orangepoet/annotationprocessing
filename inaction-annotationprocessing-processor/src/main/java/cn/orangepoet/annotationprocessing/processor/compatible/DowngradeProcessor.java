package cn.orangepoet.annotationprocessing.processor.compatible;

/**
 * 降级处理器
 *
 * @param <T>
 */
public interface DowngradeProcessor<T> {
    /**
     * 需要降级的版本, 小于此版本时做处理
     *
     * @return
     */
    String ceiling();

    /**
     * 降级处理
     *
     * @param data
     */
    void process(T data);
}
