package cn.orangepoet.annotationprocessing.processor.factory;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * @Author: chengzhi
 * @Date: 2020/3/28
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("cn.orangepoet.annotationprocessing.processor.factory.Factory")
@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elementsAnnotatedWithFactory = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elementsAnnotatedWithFactory) {
                String className = ((TypeElement) element).getQualifiedName().toString();

                try {
                    writeFactoryFile(className);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void writeFactoryFile(String classFullName) throws IOException {
        String packageName = classFullName.substring(0, classFullName.lastIndexOf("."));
        String className = classFullName.substring(classFullName.lastIndexOf(".") + 1);
        String factoryName = className + "Factory";

        JavaFileObject sourceFile = this.processingEnv.getFiler().createSourceFile(packageName + "." + factoryName);
        try (PrintWriter printWriter = new PrintWriter(sourceFile.openWriter())) {

            printWriter.println("package " + packageName + ";");
            printWriter.println();
            printWriter.println("public class " + factoryName + " {");
            printWriter.println();
            printWriter.println("   public static " + classFullName + " newInstance() {");
            printWriter.println("       return new " + className + "();");
            printWriter.println("   }");
            printWriter.println();
            printWriter.println("}");
        }
    }
}
