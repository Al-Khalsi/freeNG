package com.pixelfreebies.util.converter;

public interface Converter<E, D> {

    E toEntity(D dto);

    D toDto(E entity);

}
