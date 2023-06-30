package cn.orangepoet.annotationprocessing.processor.compatible;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * 多版本服务工厂
 */
public class ServiceFactory {
    private final VersionContext versionContext;

    public ServiceFactory(VersionContext versionContext) {
        this.versionContext = versionContext;
    }

    /**
     * 从ProcessorMap中查找适合当前版本的Processor并执行
     *
     * @param serviceMap
     * @return
     */
    public <TService> TService getService(Map<TService, VersionRoute> serviceMap) {
        Optional<TService> service = serviceMap.entrySet()
                .stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getValue().value()))
                .sorted((entry1, entry2) -> -1 * versionContext.compare(entry1.getValue().value(), entry2.getValue().value()))
                .filter(entry -> versionContext.compare(versionContext.getRequestVersion(), entry.getValue().value()) >= 0)
                .map(Map.Entry::getKey)
                .findFirst();
        if (service.isPresent()) {
            return service.get();
        }
        throw new ServiceVersionNoMatchedException(String.format("target version [%s] not matched", versionContext.getRequestVersion()));
    }

}