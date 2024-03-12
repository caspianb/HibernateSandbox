package com.logicalbias.jpa.entity.util.converter;

public interface BaseEnum {
    String getDbValue();

    default <E extends Enum<?> & BaseEnum> E fromDbValue(String dbData, Class<E> enumType) {
        E[] constants = enumType.getEnumConstants();
        for (E e : constants) {
            if (e.getDbValue() == null && (dbData == null || dbData.trim().isEmpty())) return e;
            if (e.getDbValue() != null && e.getDbValue().equalsIgnoreCase(dbData)) return e;
        }
        return null;
    }
}
