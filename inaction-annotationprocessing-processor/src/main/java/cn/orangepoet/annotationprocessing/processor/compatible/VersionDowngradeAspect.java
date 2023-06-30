package cn.orangepoet.annotationprocessing.processor.compatible;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Aspect
public class VersionDowngradeAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    private Map<Class<? extends DowngradeProcessor>, List<DowngradeProcessor>> downGradeProcessorMap = new HashMap<>();
    private VersionContext vctx;

    @Pointcut("@annotation(cn.orangepoet.annotationprocessing.processor.compatible.Downgrade) ")
    public void downgradeProcess() {
    }

    @AfterReturning(value = "@annotation(downgrade)", returning = "data")
    public void compatibleProcess(Downgrade downgrade, Object data) {
        String requestVersion = vctx.getRequestVersion();
        if (StringUtils.isBlank(requestVersion)) {
            return;
        }
        Class<? extends DowngradeProcessor> downGradeClass = downgrade.value();

        List<DowngradeProcessor> processors = downGradeProcessorMap.computeIfAbsent(downGradeClass, (dgc) -> {
            Map<String, ? extends DowngradeProcessor> processorBeanMap = applicationContext.getBeansOfType(dgc);
            return processorBeanMap.values().stream()
                    .filter(p -> StringUtils.isNotBlank(p.ceiling()))
                    .sorted((p1, p2) -> -1 * vctx.compare(p1.ceiling(), p2.ceiling())).collect(toList());
        });
        processors.forEach(p -> {
            if (vctx.compare(requestVersion, p.ceiling()) < 0)
                p.process(data);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setVersionContext(VersionContext vctx) {
        this.vctx = vctx;
    }
}
