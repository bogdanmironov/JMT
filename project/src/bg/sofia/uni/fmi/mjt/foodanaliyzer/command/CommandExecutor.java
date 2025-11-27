package bg.sofia.uni.fmi.mjt.foodanaliyzer.command;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.barcodescanner.BarcodeScanner;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.BarcodeImageException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.CommandFormattingException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.StorageException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.FDCHttpClient;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.logger.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.logger.SimpleCharacterExceptionLogger;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.Food;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodFileStorage;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodReport;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodStorage;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

public class CommandExecutor implements AutoCloseable {
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
        "Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"";

    private static final String GET_FOOD_BY_NAME = "get-food";
    private static final String GET_FOOD_REPORT = "get-food-report";
    private static final String GET_FOOD_BY_BARCODE = "get-food-by-barcode";

    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command.";
    private static final String CLIENT_PROBLEM_MESSAGE = "Could not connect to API.";
    private static final String FOOD_NOT_FOUND_MESSAGE = "Food not found.";
    private static final String STORAGE_PROBLEM_MESSAGE = "Problem with storage.";
    private static final String ARGUMENT_NOT_INTEGER_PROBLEM_MESSAGE = "Argument needs to be integer.";
    private static final String COULD_NOT_DECODE_IMAGE_MESSAGE = "Could not decode image.";

    private static final String UPC_DELIMITER = "=";
    private static final int UPC_ARGUMENT_POS = 0;
    private static final int UPC_VALUE_POS = 1;
    private static final String UPC_CODE_ARGUMENT = "--code";
    private static final String UPC_IMG_ARGUMENT = "--img";
    private static final String UPC_CODE_HOW_TO = " --code=<gtinUpc_code>|--img=<barcode_image_file>";
    private static final String FOOD_HOW_TO = " <food_name>";
    private static final String FOOD_REPORT_HOW_TO = " <food_fdcId>";

    private static final int TOKENS_IN_FOOD_BY_BARCODE_ARG = 2;
    private static final int NUM_ARGS_NAME_REPORT = 1;
    private static final int MAX_NUM_ARGS_BARCODE = 2;

    private final FoodStorage storage;
    private final FDCHttpClient fdcHttpClient;
    private final ExceptionLogger exceptionLogger;

    public CommandExecutor(FoodFileStorage storage, FDCHttpClient fdcHttpClient, Writer exceptionLoggerWriter) {
        this.storage = storage;
        this.fdcHttpClient = fdcHttpClient;
        this.exceptionLogger = new SimpleCharacterExceptionLogger(exceptionLoggerWriter);
    }

    public String execute(Command cmd) {
        return switch (cmd.command()) {
            case GET_FOOD_BY_NAME -> getFoodByName(cmd.arguments());
            case GET_FOOD_REPORT -> getFoodReport(cmd.arguments());
            case GET_FOOD_BY_BARCODE -> getFoodByBarcode(cmd.arguments());
            default -> UNKNOWN_COMMAND_MESSAGE;
        };
    }

    private String getFoodByName(List<String> args) {
        if (args.size() != NUM_ARGS_NAME_REPORT) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT,
                GET_FOOD_BY_NAME, NUM_ARGS_NAME_REPORT, GET_FOOD_BY_NAME + FOOD_HOW_TO);
        }

        try {
            Collection<Food> storedFood = storage.searchFoodByName(args.getFirst());

            if (!storedFood.isEmpty()) {
                return storedFood.toString();
            }

            Collection<Food> foods = fdcHttpClient.getFoodByName(args.getFirst());

            for (var food : foods) {
                storage.saveFood(food);
            }

            return foods.toString();
        } catch (IOException | InterruptedException e) {
            exceptionLogger.logException(e);
            return CLIENT_PROBLEM_MESSAGE;
        } catch (StorageException e) {
            exceptionLogger.logException(e);
            return STORAGE_PROBLEM_MESSAGE;
        }
    }

    private String getFoodReport(List<String> args) {
        if (args.size() != NUM_ARGS_NAME_REPORT) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, GET_FOOD_REPORT, NUM_ARGS_NAME_REPORT,
                GET_FOOD_REPORT + FOOD_REPORT_HOW_TO);
        }

        try {
            FoodReport storedReport = storage.searchFoodReportById(Integer.parseInt(args.getFirst()));
            if (storedReport != null) {
                return storedReport.toString();
            }

            FoodReport report = fdcHttpClient.getFoodById(args.getFirst());
            storage.saveFoodReport(report);

            return report.toString();
        } catch (IOException | InterruptedException e) {
            exceptionLogger.logException(e);
            return CLIENT_PROBLEM_MESSAGE;
        } catch (StorageException e) {
            exceptionLogger.logException(e);
            return STORAGE_PROBLEM_MESSAGE;
        } catch (FoodNotFoundException e) {
            exceptionLogger.logException(e);
            return FOOD_NOT_FOUND_MESSAGE;
        } catch (NumberFormatException e) {
            exceptionLogger.logException(e);
            return ARGUMENT_NOT_INTEGER_PROBLEM_MESSAGE;
        }
    }

    private String getFoodByBarcode(List<String> args) {
        try {
            String upcCode = getCodeFromArgs(args);

            return storage.searchFoodReportByGtinUpc(upcCode).toString();
        } catch (FoodNotFoundException e) {
            exceptionLogger.logException(e);
            return FOOD_NOT_FOUND_MESSAGE;
        } catch (StorageException e) {
            exceptionLogger.logException(e);
            return STORAGE_PROBLEM_MESSAGE;
        } catch (BarcodeImageException e) {
            exceptionLogger.logException(e);
            return COULD_NOT_DECODE_IMAGE_MESSAGE;
        } catch (CommandFormattingException _) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, GET_FOOD_BY_BARCODE, NUM_ARGS_NAME_REPORT,
                GET_FOOD_BY_BARCODE + UPC_CODE_HOW_TO);
        }
    }

    private String getCodeFromArgs(List<String> args) throws BarcodeImageException, CommandFormattingException {
        if (args.size() > MAX_NUM_ARGS_BARCODE) {
            throw new CommandFormattingException("Too many arguments.");
        }

        //If there are 2 arguments we always get --code=<gtinUpc_code>
        if (args.size() == MAX_NUM_ARGS_BARCODE) {
            args = args.stream().filter(it -> it.contains(UPC_CODE_ARGUMENT)).toList();
        }

        if (args.isEmpty()) {
            throw new CommandFormattingException("Arguments not formatted correctly.");
        }

        String[] tokens = args.getFirst().split(UPC_DELIMITER);
        if (tokens.length != TOKENS_IN_FOOD_BY_BARCODE_ARG) {
            throw new CommandFormattingException("Arguments not formatted correctly.");
        }

        return switch (tokens[UPC_ARGUMENT_POS]) {
            case UPC_CODE_ARGUMENT -> tokens[UPC_VALUE_POS];
            case UPC_IMG_ARGUMENT -> BarcodeScanner.scanImage(tokens[UPC_VALUE_POS]);
            default -> throw new CommandFormattingException("Arguments not formatted correctly.");
        };
    }

    @Override
    public void close() throws IOException {
        fdcHttpClient.close();
        exceptionLogger.close();
    }
}