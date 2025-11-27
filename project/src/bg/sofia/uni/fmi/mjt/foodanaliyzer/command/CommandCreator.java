package bg.sofia.uni.fmi.mjt.foodanaliyzer.command;

import java.util.ArrayList;
import java.util.List;

public class CommandCreator {
    private static final int COMMAND_ARGUMENTS_START_POSITION = 1;
    private static final String QUOTE = "\"";
    private static final String QUOTE_REPLACEMENT = "";
    private static final char QUOTE_CHAR = '"';
    private static final char SPACE_CHAR = ' ';

    public static Command newCommand(String clientInput) {
        List<String> tokens = getCommandWithArguments(clientInput);
        List<String> args = tokens.stream().skip(COMMAND_ARGUMENTS_START_POSITION).toList();

        return new Command(tokens.getFirst(), args);
    }

    private static List<String> getCommandWithArguments(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        boolean insideQuote = false;

        for (char currChar : input.toCharArray()) {
            if (currChar == QUOTE_CHAR) {
                insideQuote = !insideQuote;
            }

            if (currChar == SPACE_CHAR && !insideQuote) {
                tokens.add(stringBuilder.toString().replace(QUOTE, QUOTE_REPLACEMENT));
                stringBuilder.delete(0, stringBuilder.length());
            } else {
                stringBuilder.append(currChar);
            }
        }

        tokens.add(stringBuilder.toString().replace(QUOTE, QUOTE_REPLACEMENT));

        return tokens;
    }
}