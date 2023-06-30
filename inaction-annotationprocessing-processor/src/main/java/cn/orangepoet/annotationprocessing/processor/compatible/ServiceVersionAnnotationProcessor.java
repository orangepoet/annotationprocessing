package cn.orangepoet.annotationprocessing.processor.compatible;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

/**
 * 生成服务类型的代理类, 基于spring方式实现
 *
 * <p>
 *
 * @Component
 * @Primary public class CustomerProxy implements CustomService {}
 *
 * </p>
 */
@AutoService(Processor.class)
public class ServiceVersionAnnotationProcessor extends AbstractProcessor {
    private static final String SUFFIX = "GeneratedProxy";

    private Messager messager;
    private Filer filer;
    private Elements elements;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(VersionRoute.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<TypeElement> serviceTypes = new LinkedHashSet<>();
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(VersionRoute.class)) {
                ServiceVersionCtx ctx = new ServiceVersionCtx(annotatedElement, elements);
                serviceTypes.add(ctx.getServiceType());
            }

            for (TypeElement serviceType : serviceTypes) {
                generateCode(serviceType);
            }
        } catch (ServiceVersionProcessException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
        }
        return true;
    }

    /**
     * 生成ServiceProxy代码
     *
     * @param serviceType
     */
    private void generateCode(TypeElement serviceType) throws IOException {
        PackageElement pkgElement = elements.getPackageOf(serviceType);
        String packageName = pkgElement.getQualifiedName().toString();

        String serviceName = serviceType.getSimpleName().toString();
        String proxyClassName = serviceName + SUFFIX;

        // classBuilder
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(proxyClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Component.class)
                .addAnnotation(Primary.class)
                .addSuperinterface(ClassName.get(serviceType))
                .addSuperinterface(InitializingBean.class);

        // serviceFactory field
        TypeName serviceFactoryType = ParameterizedTypeName.get(ServiceFactory.class);
        FieldSpec.Builder serviceFactory = FieldSpec.builder(serviceFactoryType, "serviceFactory")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL);
        classBuilder.addField(serviceFactory.build());

        // serviceList
        FieldSpec.Builder serviceList = FieldSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(serviceType.asType())), "serviceList")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class);
        classBuilder.addField(serviceList.build());

        // serviceMap field
        FieldSpec.Builder versionRouteMap = FieldSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(Map.class),
                                TypeName.get(serviceType.asType()), ClassName.get(VersionRoute.class)), "serviceMap")
                .addModifiers(Modifier.PRIVATE);
        classBuilder.addField(versionRouteMap.build());

        // constructor
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(serviceFactoryType, "serviceFactory")
                .addStatement("this.serviceFactory = serviceFactory");

        classBuilder.addMethod(constructor.build());

        // interface methods
        List<? extends Element> enclosedElements = serviceType.getEnclosedElements();
        for (Element e : enclosedElements) {
            if (e.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement ee = (ExecutableElement) e;
            MethodSpec.Builder method = MethodSpec.methodBuilder(ee.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(ee.getReturnType()));

            List<String> parameterList = new ArrayList<>();
            for (VariableElement ve : ee.getParameters()) {
                String pn = ve.getSimpleName().toString();
                method.addParameter(TypeName.get(ve.asType()), pn);
                parameterList.add(pn);
            }

            method.addStatement("$T service = serviceFactory.getService(serviceMap)", serviceType);

            if (ee.getReturnType().getKind() == TypeKind.VOID) {
                method.addStatement("service.$N($L)", ee.getSimpleName().toString(), String.join(",", parameterList));
            } else {
                method.addStatement("return service.$N($L)", ee.getSimpleName().toString(),
                        String.join(",", parameterList));
            }
            classBuilder.addMethod(method.build());
        }

        // afterPropertiesSet
        MethodSpec.Builder afterPropertiesSet = MethodSpec.methodBuilder("afterPropertiesSet")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("this.serviceMap = getServiceMap(this.serviceList)");
        classBuilder.addMethod(afterPropertiesSet.build());

        // private getServiceMap
        MethodSpec.Builder getServiceMap = MethodSpec.methodBuilder("getServiceMap")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(serviceType)), "services")
                .returns(ParameterizedTypeName.get(ClassName.get(Map.class),
                        TypeName.get(serviceType.asType()), ClassName.get(VersionRoute.class)))
                .beginControlFlow("if (services == null || services.isEmpty())")
                .addStatement("return $T.emptyMap()", Collections.class)
                .endControlFlow()
                .addStatement("Map<$T, VersionRoute> result = new $T<>()", serviceType, HashMap.class)
                .beginControlFlow("for ($T service : services)", serviceType)
                .addStatement("VersionRoute versionRoute = service.getClass().getAnnotation(VersionRoute.class)")
                .beginControlFlow(" if (versionRoute != null) ")
                .addStatement("result.put(service, versionRoute)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return result");
        classBuilder.addMethod(getServiceMap.build());

        JavaFile.builder(packageName, classBuilder.build()).indent("    ").build().writeTo(filer);
    }

    private static class ServiceVersionCtx {
        private TypeElement annotationClass;
        private TypeElement serviceType;

        public ServiceVersionCtx(Element element, Elements elements) {
            if (element.getKind() != ElementKind.CLASS) {
                throw new ServiceVersionProcessException(element, "Only classes can be annotated with @%s",
                        VersionRoute.class.getSimpleName());
            }
            this.annotationClass = (TypeElement) element;

            String serviceName;
            try {
                VersionRoute versionRoute = annotationClass.getAnnotation(VersionRoute.class);
                serviceName = versionRoute.serviceType().getCanonicalName();
            } catch (MirroredTypeException e) {
                DeclaredType classTypeMirror = (DeclaredType) e.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                serviceName = classTypeElement.getQualifiedName().toString();
            }
            TypeElement serviceType = elements.getTypeElement(serviceName);
            if (serviceType.getKind() != ElementKind.INTERFACE) {
                throw new ServiceVersionProcessException(annotationClass, "serviceType: %s is not interface",
                        serviceType.getSimpleName());
            }
            this.serviceType = serviceType;
        }

        public TypeElement getServiceType() {
            return serviceType;
        }
    }
}
