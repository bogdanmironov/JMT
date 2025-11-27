package bg.sofia.uni.fmi.mjt.space.rocket;

public enum RocketStatus {
    STATUS_RETIRED("StatusRetired"),
    STATUS_ACTIVE("StatusActive");

    private final String value;

    private static final String CSV_MESSAGE_ACTIVE = "StatusActive";
    private static final String CSV_MESSAGE_RETIRED = "StatusRetired";

    RocketStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static RocketStatus fromString(String value) {
        return switch (value) {
            case CSV_MESSAGE_RETIRED -> STATUS_RETIRED;
            case CSV_MESSAGE_ACTIVE -> STATUS_ACTIVE;
            default -> throw new IllegalArgumentException("Invalid value: " + value);
        };
    }
}