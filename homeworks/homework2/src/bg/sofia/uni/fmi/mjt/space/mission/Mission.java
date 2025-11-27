package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date,
                      Detail detail, RocketStatus rocketStatus,
                      Optional<Double> cost, MissionStatus missionStatus) {

    //Regex that ignores commas inside quotes
    private static final String DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final String DATETIME_FORMAT = "EEE MMM dd, yyyy";
    private static final int QUOTE_REMOVE_SUBSTRING_VALUE = 1;

    private static final int POS_ID = 0;
    private static final int POS_COMPANY = 1;
    private static final int POS_LOCATION = 2;
    private static final int POS_DATE = 3;
    private static final int POS_DETAIL = 4;
    private static final int POS_ROCKET_STATUS = 5;
    private static final int POS_COST = 6;
    private static final int POS_MISSION_STATUS = 7;

    private static final int INCLUDE_EMPTY_FIELDS_MODIFIER = -1;

    public static Mission of(String line) {
        final String[] tokens = line.split(DELIMITER, INCLUDE_EMPTY_FIELDS_MODIFIER);

        Optional<Double> cost = Optional.empty();

        if (!tokens[POS_COST].isBlank()) {
            cost = Optional.of(parseCost(tokens));
        }

        return new Mission(
            tokens[POS_ID],
            tokens[POS_COMPANY],
            parseLocation(tokens),
            parseDate(tokens),
            Detail.of(tokens[POS_DETAIL]),
            RocketStatus.fromString(tokens[POS_ROCKET_STATUS]),
            cost,
            MissionStatus.fromString(tokens[POS_MISSION_STATUS])
        );
    }

    private static String parseLocation(String[] tokens) {
        return tokens[POS_LOCATION].substring(QUOTE_REMOVE_SUBSTRING_VALUE,
            tokens[POS_LOCATION].length() - QUOTE_REMOVE_SUBSTRING_VALUE);
    }

    private static LocalDate parseDate(String[] tokens) {
        String cleanDateString = tokens[POS_DATE]
            .substring(QUOTE_REMOVE_SUBSTRING_VALUE, tokens[POS_DATE].length() - QUOTE_REMOVE_SUBSTRING_VALUE);

        return LocalDate.parse(cleanDateString, DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    private static double parseCost(String[] tokens) {
        NumberFormat numberFormatter = NumberFormat.getInstance(Locale.getDefault());

        try {
            return numberFormatter
                .parse(tokens[POS_COST]
                    .substring(QUOTE_REMOVE_SUBSTRING_VALUE, tokens[POS_COST].length() - QUOTE_REMOVE_SUBSTRING_VALUE))
                .doubleValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unable to parse cost", e);
        }
    }

}
