package cn.orangepoet.annotationprocessing.usage.compatible.downgrade.impl;

import cn.orangepoet.annotationprocessing.processor.compatible.Downgrade;
import cn.orangepoet.annotationprocessing.usage.compatible.downgrade.Fruit;
import cn.orangepoet.annotationprocessing.usage.compatible.downgrade.FruitService;
import cn.orangepoet.annotationprocessing.usage.compatible.downgrade.downgrader.FruitDowngrader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FruitServiceImpl implements FruitService {
    @Override
    @Downgrade(FruitDowngrader.FruitDowngrade.class)
    public Fruit showFruit() {
        Fruit apple = new Fruit();
        apple.setWeight(10);
        apple.setPrice(2.0);
        apple.setColor("red");
        apple.setName("apple");
        return apple;
    }

    @Override
    @Downgrade(FruitDowngrader.FruitListDowngrade.class)
    public List<Fruit> listFruit() {
        return new ArrayList<>(Arrays.asList(
                new Fruit("apple", "red", 10, 1.0),
                new Fruit("banana", "yellow", 4, 2.0),
                new Fruit("pear", "yellow", 4, 2.0)
        ));
    }
}
