package cn.orangepoet.annotationprocessing.usage.getter;

import cn.orangepoet.annotationprocessing.processor.getter.Getter;
import lombok.NonNull;

/**
 * @author chengzhi
 * @date 2020/03/29
 */
@Getter
public class Foo {
    private String name;
    private Integer count;

    public Foo(@NonNull String name, @NonNull Integer count) {
        this.name = name;
        this.count = count;
    }
}
