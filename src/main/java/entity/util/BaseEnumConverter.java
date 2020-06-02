package entity.util;

import javax.persistence.AttributeConverter;

public abstract class BaseEnumConverter<E extends Enum<E> & BaseEnum> implements AttributeConverter<E, String> {

    private final Class<E> enumType;
    private final E enumObj;

    protected BaseEnumConverter(Class<E> enumType) {
        this.enumType = enumType;
        this.enumObj = enumType.getEnumConstants()[0];
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.getDbValue() : null;
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        return enumObj.fromDbValue(dbData, enumType);
    }
}
