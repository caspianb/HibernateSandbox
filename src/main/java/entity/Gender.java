package entity;

public enum Gender implements BaseEnum {
    UNKNOWN,
    MALE,
    FEMALE;

    @Override
    public String getDbValue() {
        if (this == MALE) return "M";
        if (this == FEMALE) return "F";
        return null;
    }

    public static class Converter extends BaseEnumConverter<Gender> {
        public Converter() {
            super(Gender.class);
        }
    }

}
