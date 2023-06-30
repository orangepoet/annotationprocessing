package cn.orangepoet.annotationprocessing.processor.getter;

import com.google.common.io.Resources;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;

/**
 * @author chengzhi
 * @date 2020/03/29
 */
public class GetterProcessorTest {

    @Test
    public void process() {
        Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forResource(Resources.getResource("Foo.java")))
                .processedWith(new GetterProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource(Resources.getResource("Foo2.java")));
    }
}