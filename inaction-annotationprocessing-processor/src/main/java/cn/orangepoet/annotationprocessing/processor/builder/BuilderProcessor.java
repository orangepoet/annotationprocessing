package cn.orangepoet.annotationprocessing.processor.builder;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chengzhi
 * @date 2020/01/17
 */
@SupportedAnnotationTypes("cn.orangepoet.annotationprocessing.processor.builder.BuilderProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement annotation : annotations) {
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

                Map<Boolean, List<Element>> annotatedMethods = annotatedElements
                        .stream()
                        .collect(Collectors.partitioningBy(element ->
                                ((ExecutableType) element.asType()).getParameterTypes().size() == 1
                                        && element.getSimpleName().toString().startsWith("set")));
                List<Element> setters = annotatedMethods.get(true);
                List<Element> otherMethods = annotatedMethods.get(false);

                if (setters.isEmpty()) {
                    continue;
                }

                otherMethods.forEach(element ->
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "@BuilderProperty must be applied to a setXxx method "
                                        + "with a single argument", element));

                String className = ((TypeElement) setters.get(0)
                        .getEnclosingElement()).getQualifiedName().toString();

                String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
                String packageName = className.substring(0, className.lastIndexOf("."));

                Map<String, TypeMirror> setterMap = setters.stream().collect(Collectors.toMap(
                        setter -> setter.getSimpleName().toString(),
                        setter -> ((ExecutableType) setter.asType()).getParameterTypes().get(0)
                ));

                ClassName typeName = ClassName.get(packageName, simpleClassName);

                String builderType = simpleClassName + "Builder";
                TypeSpec.Builder typeSpecBuilder = TypeSpec
                        .classBuilder(builderType)
                        .addField(
                                FieldSpec.builder(typeName, "instance", Modifier.PRIVATE, Modifier.FINAL)
                                        .initializer("new " + simpleClassName + "()").build())
                        .addMethod(
                                MethodSpec.methodBuilder("build").addModifiers(Modifier.PUBLIC)
                                        .addCode(String.format("return this.instance;%n"))
                                        .returns(typeName).build());

                // fluent set method
                setterMap.forEach((key, value) -> typeSpecBuilder.addMethod(
                        MethodSpec.methodBuilder(key)
                                .addParameter(TypeName.get(value), "arg")
                                .addCode(String.format("this.instance.%s(%s);%n", key, "arg"))
                                .addCode(String.format("return this;%n"))
                                .returns(ClassName.get(packageName, builderType))
                                .addModifiers(Modifier.PUBLIC)
                                .build()));

                JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build()).indent("    ").build();
                javaFile.writeTo(this.filer);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
