package cn.orangepoet.annotationprocessing.usage.compatible;

import cn.orangepoet.annotationprocessing.processor.compatible.VersionContext;
import org.springframework.stereotype.Component;

@Component
public class ClientVersionHolder implements VersionContext {
    private ThreadLocal<String> versionHolder = new InheritableThreadLocal<>();

    @Override
    public String getRequestVersion() {
        return versionHolder.get();
    }

    public void setRequestVersion(String version) {
        versionHolder.set(version);
    }

    @Override
    public int compare(String version1, String version2) {
        return version1.compareTo(version2);
    }
}
