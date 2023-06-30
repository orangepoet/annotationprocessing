package cn.orangepoet.annotationprocessing.usage.compatible.downgrade;

import java.util.Collections;
import java.util.List;

public interface FruitService {
    Fruit showFruit();

    default List<Fruit> listFruit() {
        return Collections.emptyList();
    }
}
