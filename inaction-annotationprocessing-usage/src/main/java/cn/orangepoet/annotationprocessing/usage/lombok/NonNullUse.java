package cn.orangepoet.annotationprocessing.usage.lombok;

import lombok.NonNull;

public class NonNullUse {
    public void test(@NonNull Integer i) {
        System.out.println(i);
    }
}
