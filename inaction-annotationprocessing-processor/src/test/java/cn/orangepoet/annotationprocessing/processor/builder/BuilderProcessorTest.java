package cn.orangepoet.annotationprocessing.processor.builder;

import com.google.common.io.Resources;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;

/**
 * @author chengzhi
 * @date 2020/01/19
 */
public class BuilderProcessorTest {

    @Test
    public void process() {
        Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forResource(Resources.getResource("Person.java")))
                .processedWith(new BuilderProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource(Resources.getResource("PersonBuilder.java")));
    }
}