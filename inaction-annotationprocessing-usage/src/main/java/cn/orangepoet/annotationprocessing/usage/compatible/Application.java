package cn.orangepoet.annotationprocessing.usage.compatible;

import cn.orangepoet.annotationprocessing.usage.compatible.dispatcher.GreetService;
import cn.orangepoet.annotationprocessing.usage.compatible.downgrade.Fruit;
import cn.orangepoet.annotationprocessing.usage.compatible.downgrade.FruitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author chengzhi
 * @date 2020/01/20
 */

@Slf4j
@SpringBootApplication
public class Application {

    @Autowired
    private GreetService greetService;

    @Autowired
    private FruitService fruitService;

    @Autowired
    private ClientVersionHolder clientVersionHolder;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner dispatcher() {
        return (args) -> {
            log.info("-----------------dispatcher---------------------");
            clientVersionHolder.setRequestVersion("3.0");
            log.info("current version: 3.0");
            greetService.greet("hello, world");

            clientVersionHolder.setRequestVersion("2.0");
            log.info("current version: 2.0");
            greetService.greet("hello, world");

            clientVersionHolder.setRequestVersion("1.0");
            log.info("current version: 1.0");
            greetService.greet("hello, world");
        };
    }

    @Bean
    public CommandLineRunner downgrade() {
        return (args) -> {
            log.info("-----------------downgrade---------------------");
            fruitCompatible0("4.0");
            fruitCompatible0("3.0");
            fruitCompatible0("2.0");
            fruitCompatible0("1.0");
        };
    }

    private void fruitCompatible0(String currentVersion) {
        log.info("current version: " + currentVersion);
        clientVersionHolder.setRequestVersion(currentVersion);


        Fruit fruit = fruitService.showFruit();
        log.info("fruit: " + fruit.toString());

        List<Fruit> fruits = fruitService.listFruit();
        log.info("fruits" + fruits.toString());
    }
}
