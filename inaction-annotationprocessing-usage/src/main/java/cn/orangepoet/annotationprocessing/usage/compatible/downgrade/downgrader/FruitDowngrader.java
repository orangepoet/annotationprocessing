package cn.orangepoet.annotationprocessing.usage.compatible.downgrade.downgrader;

import cn.orangepoet.annotationprocessing.processor.compatible.DowngradeProcessor;
import cn.orangepoet.annotationprocessing.usage.compatible.downgrade.Fruit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class FruitDowngrader {

    public interface FruitListDowngrade extends DowngradeProcessor<List<Fruit>> {
    }

    public interface FruitDowngrade extends DowngradeProcessor<Fruit> {
    }

    @Bean
    public FruitListDowngrade downgradeProcessor3_0() {
        return new FruitListDowngrade() {
            @Override
            public String ceiling() {
                return "3.0";
            }

            @Override
            public void process(List<Fruit> fruits) {
                log.info("downgradeProcessor3_0 invoke, remove pear if exists");

                if (CollectionUtils.isEmpty(fruits)) {
                    return;
                }
                fruits.removeIf(fruit -> fruit.getName().equals("pear"));
            }
        };
    }

    @Bean
    public FruitListDowngrade downgradeProcessor2_0() {
        return new FruitListDowngrade() {
            @Override
            public String ceiling() {
                return "2.0";
            }

            @Override
            public void process(List<Fruit> fruitList) {
                log.info("downgradeProcessor2_0 invoke, remove banana if exists");
                if (CollectionUtils.isEmpty(fruitList)) {
                    return;
                }

                fruitList.removeIf(fruit -> fruit.getName().equals("banana"));
            }
        };
    }

    @Bean
    public FruitDowngrade fruitDowngrade2_0() {
        return new FruitDowngrade() {
            @Override
            public String ceiling() {
                return "2.0";
            }

            @Override
            public void process(Fruit fruit) {
                log.info("fruitDowngrade2_0 invoke, set apple to green");

                if (fruit != null && "apple".equals(fruit.getName())) {
                    fruit.setWeight(10);
                    fruit.setPrice(1.1);
                    fruit.setColor("green");
                    fruit.setName("apple");
                }
            }
        };
    }
}
