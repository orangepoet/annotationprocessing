package cn.orangepoet.annotationprocessing.usage.builder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chengzhi
 * @date 2020/01/20
 */

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);

        PersonBuilder personBuilder = new PersonBuilder();
        Person orange = personBuilder.setAge(12).setName("orange").build();
        System.out.println(orange);
    }
}
