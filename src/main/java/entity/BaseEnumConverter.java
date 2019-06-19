package entity;

import javax.persistence.AttributeConverter;

public abstract class BaseEnumConverter<E extends Enum<E> & BaseEnum> implements AttributeConverter<E, String> {

    private final Class<E> enumType;

    protected BaseEnumConverter(Class<E> enumType) {
        this.enumType = enumType;
    }

    public String convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.getDbValue() : null;
    }

    public E convertToEntityAttribute(String dbData) {
        E[] constants = enumType.getEnumConstants();
        for (E e : constants) {
            if (e.getDbValue().equalsIgnoreCase(dbData)) return e;
        }
        return null;
    }
}
