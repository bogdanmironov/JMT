package bg.sofia.uni.fmi.mjt.foodanaliyzer.storage;

public record Food(int fdcId,
                   String description,
                   String gtinUpc) {
    private static final String DELIMITER_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String DELIMITER = ",";
    private static final String QUOTE_DELIMITER = "\"";
    private static final String QUOTE_DELIMITER_REPLACEMENT = "";


    private static final int POS_FDC_ID = 0;
    private static final int POS_DESCRIPTION = 1;
    private static final int POS_GTIN_UPC = 2;

    public static Food of(String line) {
        String[] split = line.split(DELIMITER_REGEX);

        return new Food(
            Integer.parseInt(split[POS_FDC_ID]),
            split[POS_DESCRIPTION].replaceAll(QUOTE_DELIMITER, QUOTE_DELIMITER_REPLACEMENT),
            split[POS_GTIN_UPC]
        );
    }

    public String toFileString() {
        return fdcId + DELIMITER +
            QUOTE_DELIMITER + description + QUOTE_DELIMITER + DELIMITER +
            gtinUpc + System.lineSeparator();
    }
}
