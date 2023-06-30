//package cn.orangepoet.annotationprocessing.usage.compatible;
//
//import cn.orangepoet.annotationprocessing.processor.compatible.ServiceFactory;
//import cn.orangepoet.annotationprocessing.processor.compatible.ServiceVersion;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
////@Primary
//public class MyHandlerProxy implements MyHandler {
//
//    private final Map<MyHandler, ServiceVersion> serviceMap;
//
//    public MyHandlerProxy(List<MyHandler> services) {
//        this.serviceMap = getServiceMap(services);
//    }
//
//    private Map<MyHandler, ServiceVersion> getServiceMap(List<MyHandler> services) {
//        if (CollectionUtils.isEmpty(services)) {
//            return Collections.emptyMap();
//        }
//        Map<MyHandler, ServiceVersion> result = new HashMap<>();
//        for (MyHandler service : services) {
//            ServiceVersion serviceVersion = service.getClass().getAnnotation(ServiceVersion.class);
//            if (serviceVersion != null) {
//                result.put(service, serviceVersion);
//            }
//        }
//        return result;
//    }
//
//
//    @Override
//    public String getName() {
//        MyHandler service = ServiceFactory.getService(serviceMap);
//        return service.getName();
//    }
//
//    @Override
//    public void sayHi() {
//        MyHandler service = ServiceFactory.getService(serviceMap);
//        service.sayHi();
//    }
//}
