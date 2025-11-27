package bg.sofia.uni.fmi.mjt.space.mission;

public enum MissionStatus {
    SUCCESS("Success"),
    FAILURE("Failure"),
    PARTIAL_FAILURE("Partial Failure"),
    PRELAUNCH_FAILURE("Prelaunch Failure");

    private static final String CSV_MESSAGE_SUCCESS = "Success";
    private static final String CSV_MESSAGE_FAILURE = "Failure";
    private static final String CSV_MESSAGE_PARTIAL_FAILURE = "Partial Failure";
    private static final String CSV_MESSAGE_PRELAUNCH_FAILURE = "Prelaunch Failure";

    private final String value;

    MissionStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static MissionStatus fromString(String value) {
        return switch (value) {
            case CSV_MESSAGE_SUCCESS -> MissionStatus.SUCCESS;
            case CSV_MESSAGE_FAILURE -> MissionStatus.FAILURE;
            case CSV_MESSAGE_PARTIAL_FAILURE -> MissionStatus.PARTIAL_FAILURE;
            case CSV_MESSAGE_PRELAUNCH_FAILURE -> MissionStatus.PRELAUNCH_FAILURE;
            default -> throw new IllegalArgumentException("Invalid value: " + value);
        };
    }
}