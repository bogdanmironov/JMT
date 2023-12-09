package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Player(String name, String fullName, LocalDate birthDate, int age, double heightCm, double weightKg,
                     List<Position> positions, String nationality, int overallRating, int potential, long valueEuro,
                     long wageEuro, Foot preferredFoot) {

    private static final String PEAK_ATTRIBUTE_DELIMITER = ";";

    private static final int POS_NAME = 0;
    private static final int POS_FULL_NAME = 1;
    private static final int POS_BIRTH_DATE = 2;
    private static final int POS_AGE = 3;
    private static final int POS_HEIGHT = 4;
    private static final int POS_WEIGHT = 5;
    private static final int POS_POSITIONS = 6;
    private static final int POS_NATIONALITY = 7;
    private static final int POS_RATING = 8;
    private static final int POS_POTENTIAL = 9;
    private static final int POS_VALUE = 10;
    private static final int POS_WAGE = 11;
    private static final int POS_PREF_FOOT = 12;

    public static Player of(String line) {
        final String[] tokens = line.split(PEAK_ATTRIBUTE_DELIMITER);
        return new Player(
            tokens[POS_NAME],
            tokens[POS_FULL_NAME],
            LocalDate.parse(tokens[POS_BIRTH_DATE], DateTimeFormatter.ofPattern("M/d/yyyy")),
            Integer.parseInt(tokens[POS_AGE]),
            Double.parseDouble(tokens[POS_HEIGHT]),
            Double.parseDouble(tokens[POS_WEIGHT]),
            Arrays.stream(tokens[POS_POSITIONS].split(",")).map(Position::valueOf).collect(Collectors.toList()),
            tokens[POS_NATIONALITY],
            Integer.parseInt(tokens[POS_RATING]),
            Integer.parseInt(tokens[POS_POTENTIAL]),
            Long.parseLong(tokens[POS_VALUE]),
            Long.parseLong(tokens[POS_WAGE]),
            Foot.valueOf(tokens[POS_PREF_FOOT].toUpperCase())
        );
    }
}
