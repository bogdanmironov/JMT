package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    private static final String DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final int POS_ID = 0;
    private static final int POS_NAME = 1;
    private static final int POS_WIKI = 2;
    private static final int POS_HEIGHT = 3;

    private static final int INCLUDE_EMPTY_FIELDS_MODIFIER = -1;

    public static Rocket of(String line) {
        final String[] tokens = line.split(DELIMITER, INCLUDE_EMPTY_FIELDS_MODIFIER);

        Optional<String> wiki = Optional.empty();
        Optional<Double> height = Optional.empty();

        if (!tokens[POS_WIKI].isBlank()) {
            wiki = Optional.of(tokens[POS_WIKI]);
        }

        if (!tokens[POS_HEIGHT].isBlank()) {
            height = Optional.of(parseHeightToken(tokens));
        }

        return new Rocket(
            tokens[POS_ID],
            tokens[POS_NAME],
            wiki,
            height
        );
    }

    private static double parseHeightToken(String[] tokens) {
        return Double.parseDouble(tokens[POS_HEIGHT].substring(0, tokens[POS_HEIGHT].length() - 2));
    }
}