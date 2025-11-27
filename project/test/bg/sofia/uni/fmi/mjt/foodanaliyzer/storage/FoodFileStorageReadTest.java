package bg.sofia.uni.fmi.mjt.foodanaliyzer.storage;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.StorageException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FoodFileStorageReadTest {

    private static final Path foodPath = Path.of("project/test/bg/sofia/uni/fmi/mjt/foodanaliyzer/storage/food_test.txt");
    private static final Path wrongFoodPath = Path.of("project/test/bg/sofia/uni/fmi/mjt/foodanaliyzer/storage/NOfood_test.txt");
    private static final Path foodReportPath = Path.of("project/test/bg/sofia/uni/fmi/mjt/foodanaliyzer/storage/food_report_test.txt");
    private static final Path wrongFoodReportPath = Path.of("project/test/bg/sofia/uni/fmi/mjt/foodanaliyzer/storage/NOfood_report_test.txt");

    private static FoodFileStorage foodFileStorage;

    @Test
    void testGetAllFoods() throws StorageException {
        foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);
        assertEquals(105, foodFileStorage.getAllFoods().size(), "There are 105 foods in test set.");
    }

    @Test
    void testGetAllFoodsWrongPath() {
        foodFileStorage = new FoodFileStorage(wrongFoodPath, wrongFoodReportPath);
        assertThrows(StorageException.class, () -> foodFileStorage.getAllFoods());
    }

    @Test
    void testGetAllFoodReports() throws StorageException {
        foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);
        assertEquals(11, foodFileStorage.getAllFoodReports().size(), "There are 11 food reports in test set.");
    }

    @Test
    void testGetAllFoodReportsWrongPath() {
        foodFileStorage = new FoodFileStorage(wrongFoodPath, wrongFoodReportPath);
        assertThrows(StorageException.class, () -> foodFileStorage.getAllFoodReports());
    }

    @Test
    void testSearchFoodByName() throws StorageException {
        foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);

        var result = foodFileStorage.searchFoodByName("chee");
        assertTrue(result.stream().findFirst().isPresent());
        assertEquals(1943515, result.stream().findFirst().get().fdcId(), "There is only one entry containing chee - cheese");
    }

    @Test
    void testSearchFoodByNameWrongPath() {
        foodFileStorage = new FoodFileStorage(wrongFoodPath, wrongFoodReportPath);
        assertThrows(StorageException.class, () -> foodFileStorage.searchFoodByName("name"));
    }

    @Test
    void testSearchFoodReportById() throws StorageException {
        foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);

        assertEquals("CARROT CAKE", foodFileStorage.searchFoodReportById(2078941).description(), "In test data CARROT CAKE corresponds to this id");
    }

    @Test
    void testSearchFoodReportByGtinUpc() throws FoodNotFoundException, StorageException {
        foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);

        assertEquals(770884, foodFileStorage.searchFoodReportByGtinUpc("07622300305024").fdcId(), "This is an item in both tables");
    }

    @Test
    void testSearchFoodReportByGtinUpcNotFound() {
        foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);

        assertThrows(FoodNotFoundException.class, () -> foodFileStorage.searchFoodReportByGtinUpc("00070221009663"));
    }

}
