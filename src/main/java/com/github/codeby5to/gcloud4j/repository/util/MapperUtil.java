package com.github.codeby5to.gcloud4j.repository.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MapperUtil {
    private static final Logger log = Logger.getLogger(MapperUtil.class.getName());

    private MapperUtil() {
        throw new IllegalCallerException("cant instantiate an utility class");
    }

    public static Map<String, Object> convertToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        if (object == null) {
            return map;
        }

        Class<?> clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if(!field.getName().equals("id")) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object fieldValue = field.get(object);
                    map.put(fieldName, fieldValue);
                }
            } catch (IllegalAccessException e) {
                log.severe(e.getMessage());
            }
        }
        return map;
    }



}
