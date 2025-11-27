package bg.sofia.uni.fmi.mjt.foodanaliyzer.storage;

import java.util.List;

public record FoodReport(String description,
                         List<String> ingredients,
                         Double kcal,
                         Double protein,
                         Double fat,
                         Double carbs,
                         Double fibre,
                         int fdcId) {

    private static final String DELIMITER_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String DELIMITER = ",";
    private static final String QUOTE_DELIMITER = "\"";
    private static final String QUOTE_DELIMITER_REPLACEMENT = "";

    private static final int POS_DESCRIPTION = 0;
    private static final int POS_INGREDIENTS = 1;
    private static final int POS_KCAL = 2;
    private static final int POS_PROTEIN = 3;
    private static final int POS_FAT = 4;
    private static final int POS_CARBS = 5;
    private static final int POS_FIBRE = 6;
    private static final int POS_FDC_ID = 7;

    public static FoodReport of(String line) {
        String[] split = line.split(DELIMITER_REGEX);

        return new FoodReport(
            split[POS_DESCRIPTION].replaceAll(QUOTE_DELIMITER, QUOTE_DELIMITER_REPLACEMENT),
            parseIngredients(split[POS_INGREDIENTS]),
            Double.parseDouble(split[POS_KCAL]),
            Double.parseDouble(split[POS_PROTEIN]),
            Double.parseDouble(split[POS_FAT]),
            Double.parseDouble(split[POS_CARBS]),
            Double.parseDouble(split[POS_FIBRE]),
            Integer.parseInt(split[POS_FDC_ID])
        );
    }

    private static List<String> parseIngredients(String ingredients) {
        String[] split = ingredients.replaceAll(QUOTE_DELIMITER, QUOTE_DELIMITER_REPLACEMENT).split(DELIMITER);

        return List.of(split);
    }

    public String toFileString() {
        return QUOTE_DELIMITER + description + QUOTE_DELIMITER + DELIMITER +
            getIngredientsFileString() + DELIMITER +
            (kcal == null ? -1.0 : kcal) + DELIMITER +
            (protein == null ? -1.0 : protein) + DELIMITER +
            (fat == null ? -1.0 : fat) + DELIMITER +
            (carbs == null ? -1.0 : carbs) + DELIMITER +
            (fibre == null ? -1.0 : fibre) + DELIMITER +
            fdcId + System.lineSeparator();

    }

    private String getIngredientsFileString() {
        return QUOTE_DELIMITER + String.join(DELIMITER, ingredients) + QUOTE_DELIMITER;
    }
}
