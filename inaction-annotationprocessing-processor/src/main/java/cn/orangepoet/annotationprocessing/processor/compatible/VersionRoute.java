package cn.orangepoet.annotationprocessing.processor.compatible;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 兼容性服务代理注解, 生成针对不同的版本做不同的处理, 生成代码参见 {@link ServiceVersionAnnotationProcessor}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VersionRoute {
    /**
     * 支持的起始版本, 大于等于此版本才被路由到
     *
     * @return
     */
    String value();

    /**
     * 服务接口
     *
     * @return
     */
    Class<?> serviceType();
}
