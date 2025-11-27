package bg.sofia.uni.fmi.mjt.foodanaliyzer.command;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.StorageException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.FDCHttpClient;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.Food;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodFileStorage;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CommandExecutorTest {

    private CommandExecutor executor;
    private FoodFileStorage mockStorage;
    private FDCHttpClient mockHttpClient;
    private Writer mockExceptionLoggerWriter;

    private static final String food1Name = "sandwich";
    private static final int food1Id = 12345;
    private static final int food2Id = 23456;
    private static final Food food1 = new Food(food1Id, "Cheese sandwich, tasty", "00001234");
    private static final Food food2 = new Food(food2Id, "Soup", "42530000");
    private static final FoodReport foodReport1 =
        new FoodReport("Cheese sandwich, tasty", List.of("Cheese", "Bread"), 1.0, 2.0, 3.0, 4.0, 5.0, food1Id);
    private static final FoodReport foodReport2 =
        new FoodReport("Soup", List.of("Carrot", "Onion", "Water"), 2.0, 2.0, 2.0, 2.0, null, food2Id);

    @BeforeEach
    void setUp() {
        mockStorage = mock(FoodFileStorage.class);
        mockHttpClient = mock(FDCHttpClient.class);
        mockExceptionLoggerWriter = mock(Writer.class);
        executor = new CommandExecutor(mockStorage, mockHttpClient, mockExceptionLoggerWriter);
    }


    @Test
    void testGetFoodByNameFromStorage() throws StorageException {
        Collection<Food> mockFoodList = List.of(food1, food2);

        when(mockStorage.searchFoodByName(food1Name)).thenReturn(mockFoodList);

        Command command = new Command("get-food", List.of(food1Name));
        String result = executor.execute(command);

        assertEquals(mockFoodList.toString(), result, "Should return food from storage");
        verifyNoInteractions(mockHttpClient);
    }

    @Test
    void testGetFoodByNameFromHttpClient() throws StorageException, IOException, InterruptedException {
        Collection<Food> mockFoodList = List.of(food1, food2);

        when(mockStorage.searchFoodByName(food1Name)).thenReturn(List.of());
        when(mockHttpClient.getFoodByName(food1Name)).thenReturn(mockFoodList);

        Command command = new Command("get-food", List.of(food1Name));
        String result = executor.execute(command);

        assertEquals(mockFoodList.toString(), result, "Should return food from client");
        verify(mockStorage).searchFoodByName(food1Name);
        verify(mockHttpClient).getFoodByName(food1Name);
        verify(mockStorage).saveFood(food1);
    }

    @Test
    void testGetFoodByNameWrongArguments() {
        Command command = new Command("get-food", List.of("argument1", "argument2"));
        String result = executor.execute(command);

        assertEquals("Invalid count of arguments: \"get-food\" expects 1 arguments. Example: \"get-food <food_name>\"",
            result,
            "Should return formatted message, explaining how to use function");
    }

    @Test
    void testGetFoodByNameStorageSearchException() throws StorageException, IOException {
        Throwable expectedException = new StorageException("Could not search storage.");
        when(mockStorage.searchFoodByName(food1Name)).thenThrow(expectedException);

        Command command = new Command("get-food", List.of(food1Name));
        String result = executor.execute(command);

        //How to test this better. Why was it called 73 times with implNewLine.
        verify(mockExceptionLoggerWriter, atLeastOnce()).write(any(String.class));
        assertEquals("Problem with storage.", result, "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodByNameStorageSaveException() throws StorageException, IOException, InterruptedException {
        Throwable expectedException = new StorageException("Could not save storage.");

        when(mockHttpClient.getFoodByName(food1Name)).thenReturn(List.of(food1));
        doThrow(expectedException).when(mockStorage).saveFood(food1);

        Command command = new Command("get-food", List.of(food1Name));
        String result = executor.execute(command);

        verify(mockExceptionLoggerWriter, atLeastOnce()).write(any(String.class));
        assertEquals("Problem with storage.", result, "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodByNameStorageClientException() throws IOException, InterruptedException {
        when(mockHttpClient.getFoodByName(food1Name)).thenThrow(new IOException("Could not reach server"));
        Command command = new Command("get-food", List.of(food1Name));
        String result = executor.execute(command);

        assertEquals("Could not connect to API.", result,
            "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodReportFromStorage() throws StorageException {
        when(mockStorage.searchFoodReportById(food1Id)).thenReturn(foodReport1);

        Command command = new Command("get-food-report", List.of(String.valueOf(food1Id)));
        String result = executor.execute(command);

        assertEquals(foodReport1.toString(), result, "Should return food report from storage");
        verifyNoInteractions(mockHttpClient);
    }

    @Test
    void testGetFoodReportFromHttpClient()
        throws StorageException, IOException, InterruptedException, FoodNotFoundException {
        when(mockHttpClient.getFoodById(String.valueOf(food1Id))).thenReturn(foodReport1);

        Command command = new Command("get-food-report", List.of(String.valueOf(food1Id)));
        String result = executor.execute(command);

        assertEquals(foodReport1.toString(), result, "Should return food report from client");
        verify(mockStorage).searchFoodReportById(food1Id);
        verify(mockHttpClient).getFoodById(String.valueOf(food1Id));
        verify(mockStorage).saveFoodReport(foodReport1);
    }

    @Test
    void testGetFoodReportWrongArguments() {
        Command command = new Command("get-food-report", List.of("argument1", "argument2"));
        String result = executor.execute(command);

        assertEquals(
            "Invalid count of arguments: \"get-food-report\" expects 1 arguments. Example: \"get-food-report <food_fdcId>\"",
            result,
            "Should return formatted message, explaining how to use function");
    }

    @Test
    void testGetFoodReportStorageSearchException() throws StorageException {
        when(mockStorage.searchFoodReportById(food1Id)).thenThrow(new StorageException("Could not search storage."));

        Command command = new Command("get-food-report", List.of(String.valueOf(food1Id)));
        String result = executor.execute(command);

        assertEquals("Problem with storage.", result, "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodReportStorageSaveException()
        throws StorageException, IOException, InterruptedException, FoodNotFoundException {
        Throwable expectedException = new StorageException("Could not save storage.");

        when(mockHttpClient.getFoodById(String.valueOf(food1Id))).thenReturn(foodReport1);
        doThrow(expectedException).when(mockStorage).saveFoodReport(foodReport1);

        Command command = new Command("get-food-report", List.of(String.valueOf(food1Id)));
        String result = executor.execute(command);

        verify(mockExceptionLoggerWriter, atLeastOnce()).write(any(String.class));
        assertEquals("Problem with storage.", result, "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodReportStorageClientException() throws IOException, InterruptedException, FoodNotFoundException {
        when(mockHttpClient.getFoodById(String.valueOf(food1Id))).thenThrow(new IOException("Could not reach server"));

        Command command = new Command("get-food-report", List.of(String.valueOf(food1Id)));
        String result = executor.execute(command);

        assertEquals("Could not connect to API.", result,
            "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodReportFoodNotFoundException() throws IOException, InterruptedException, FoodNotFoundException {
        when(mockHttpClient.getFoodById(String.valueOf(food1Id))).thenThrow(new FoodNotFoundException("Could not reach server"));

        Command command = new Command("get-food-report", List.of(String.valueOf(food1Id)));
        String result = executor.execute(command);

        assertEquals("Food not found.", result,
            "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodReportArgumentIntegerException() throws IOException, InterruptedException, FoodNotFoundException {
        when(mockHttpClient.getFoodById(String.valueOf(food1Id))).thenThrow(new IOException("Could not reach server"));

        Command command = new Command("get-food-report", List.of("Not Integer"));
        String result = executor.execute(command);

        assertEquals("Argument needs to be integer.", result,
            "Should not throw exception, but return appropriate message.");
    }


    @Test
    void testGetFoodByBarcode() throws StorageException, FoodNotFoundException {
        when(mockStorage.searchFoodReportByGtinUpc("42530000")).thenReturn(foodReport2);

        Command command = new Command("get-food-by-barcode", List.of("--code=" + "42530000"));
        String result = executor.execute(command);

        assertEquals(foodReport2.toString(), result);
        verify(mockStorage).searchFoodReportByGtinUpc("42530000");
    }

    @Test
    void testGetFoodByBarcodeNotFound() throws StorageException, FoodNotFoundException {
        when(mockStorage.searchFoodReportByGtinUpc("42530000")).thenThrow(new FoodNotFoundException("Food not found."));

        Command command = new Command("get-food-by-barcode", List.of("--code=" + "42530000"));
        String result = executor.execute(command);

        assertEquals("Food not found.", result, "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodByBarcodeFoodNotFoundException() throws StorageException, FoodNotFoundException, IOException {
        Throwable expectedException = new FoodNotFoundException("Food not found");

        when(mockStorage.searchFoodReportByGtinUpc("42530000")).thenThrow(expectedException);

        Command command = new Command("get-food-by-barcode", List.of("--code=" + "42530000"));
        String result = executor.execute(command);

        verify(mockExceptionLoggerWriter, atLeastOnce()).write(any(String.class));
        assertEquals("Food not found.", result, "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodByBarcodeStorageException() throws StorageException, FoodNotFoundException, IOException {
        Throwable expectedException = new StorageException("Problem with storage.");

        when(mockStorage.searchFoodReportByGtinUpc("42530000")).thenThrow(expectedException);

        Command command = new Command("get-food-by-barcode", List.of("--code=" + "42530000"));
        String result = executor.execute(command);

        verify(mockExceptionLoggerWriter, atLeastOnce()).write(any(String.class));
        assertEquals("Problem with storage.", result, "Should not throw exception, but return appropriate message.");
    }

    @Test
    void testGetFoodByBarcodeWrongArguments() {
        Command command = new Command("get-food-by-barcode", List.of("argument1", "argument2"));
        String result = executor.execute(command);

        assertEquals(
            "Invalid count of arguments: \"get-food-by-barcode\" expects 1 arguments. Example: \"get-food-by-barcode --code=<gtinUpc_code>|--img=<barcode_image_file>\"",
            result,
            "Should return formatted message, explaining how to use function");
    }


    @Test
    void testUnknownCommand() {
        Command command = new Command("unknown", List.of("argument1", "argument2"));
        String result = executor.execute(command);

        assertEquals("Unknown command.",
            result,
            "Should return appropriate message.");
    }

    @Test
    void testClose() throws IOException {
        executor.close();
        verify(mockHttpClient, times(1)).close();
    }
}