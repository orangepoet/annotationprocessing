package cn.orangepoet.annotationprocessing.usage.lombok;

import lombok.Value;

@Value
public class ValueUse {
    String name;
    Integer amount;

    public static void main(String[] args) {
        ValueUse v1 = new ValueUse("a", 1);
        ValueUse v2 = new ValueUse("a", 1);
        boolean e1 = v1.hashCode() == v2.hashCode();
        System.out.println(e1);

        String s1 = "ab9whf9hw9fhw9h9zhfwf";
        String s2 = "ab9whf9hw9fhw9h9zhfwf";
        boolean e2 = s1.hashCode() == s2.hashCode();
        System.out.println(e2);
    }
}
