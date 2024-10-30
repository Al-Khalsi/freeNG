package com.imalchemy.util.converter;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JavaDataTypeConverter {

    public String[] convertListToArray(List<String> list) {
        return list.toArray(new String[0]);
    }

}
