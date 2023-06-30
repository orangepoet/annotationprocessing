package cn.orangepoet.annotationprocessing.usage.compatible.downgrade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fruit {
    private String name;
    private String color;
    private int weight;
    private double price;

    @NonNull
    public Long getUserId() {
        return -1L;
    }
}
