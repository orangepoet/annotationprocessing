package cn.orangepoet.annotationprocessing.usage.builder;

import cn.orangepoet.annotationprocessing.processor.builder.BuilderProperty;
import lombok.ToString;


@ToString
public class Person {

    private String name;

    private int age;

    public int getAge() {
        return age;
    }

    @BuilderProperty
    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    @BuilderProperty
    public void setName(String name) {
        this.name = name;
    }

}
