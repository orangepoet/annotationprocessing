package cn.orangepoet.annotationprocessing.processor.compatible;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@Configurable
@ConditionalOnBean(VersionContext.class)
public class CompatibleConfiguration {
    @Bean
    public ServiceFactory serviceFactory(VersionContext versionContext) {
        return new ServiceFactory(versionContext);
    }

    @Bean
    public VersionDowngradeAspect versionDowngradeAspect() {
        return new VersionDowngradeAspect();
    }
}
