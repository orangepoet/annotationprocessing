package cn.orangepoet.annotationprocessing.usage.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author chengzhi
 * @date 2019/09/19
 */
@Mapper
public interface MapStructUse {
    MapStructUse INSTANCE = Mappers.getMapper(MapStructUse.class);

    @Mappings(
            @Mapping(target = "name", expression = "java(mapName(woo.getName()))")
    )
    Foo convert2Foo(Woo woo);

    default String mapName(String name) {
        if ("foo".equalsIgnoreCase(name)) {
            return "woo";
        } else if ("woo".equalsIgnoreCase(name)) {
            return "foo";
        }
        return name;
    }
}
