package bg.sofia.uni.fmi.mjt.space.mission;

public record Detail(String rocketName, String payload) {
    private static final String DELIMITER = "\\|";

    private static final int POS_ROCKET_NAME = 0;
    private static final int POS_PAYLOAD = 1;

    public static Detail of(String line) {
        String[] tokens = line.split(DELIMITER);

        return new Detail(
            tokens[POS_ROCKET_NAME].trim(),
            tokens[POS_PAYLOAD].trim()
        );
    }
}