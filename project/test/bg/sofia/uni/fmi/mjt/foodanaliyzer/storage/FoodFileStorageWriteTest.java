package bg.sofia.uni.fmi.mjt.foodanaliyzer.storage;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FoodFileStorageWriteTest {

    private static Path foodReportPath;
    private static Path foodPath;

    @BeforeEach
    void setUp() throws IOException {
        File foodReportFile = File.createTempFile("TEMPfood_report_test", ".txt", new File("project/test/bg/sofia/uni/fmi/mjt/foodanaliyzer/storage"));
        File foodFile = File.createTempFile("TEMPfood_test", ".txt", new File("project/test/bg/sofia/uni/fmi/mjt/foodanaliyzer/storage"));

        foodReportPath = foodReportFile.toPath();
        foodPath = foodFile.toPath();

        foodFile.deleteOnExit();
        foodReportFile.deleteOnExit();
    }

    @Test
    void testSaveFood() throws IOException, StorageException {
        FoodStorage foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);
        foodFileStorage.saveFood(new Food(123, "name", "12345"));

        try (var reader = Files.newBufferedReader(foodPath)) {
            assertEquals("123,\"name\",12345", reader.readLine(), "Expected saved line in the file");
        }
    }

    @Test
    void testSaveFoodReport() throws IOException, StorageException {
        FoodStorage foodFileStorage = new FoodFileStorage(foodPath, foodReportPath);
        foodFileStorage.saveFoodReport(new FoodReport("name", List.of("ingredient1", "ingredient2"), 1.0, 2.0, 3.0, 4.0, 5.0, 1234));

        try (var reader = Files.newBufferedReader(foodReportPath)) {
            assertEquals("\"name\",\"ingredient1,ingredient2\",1.0,2.0,3.0,4.0,5.0,1234", reader.readLine(), "Expected saved line in the file");
        }
    }

    @Test
    void testSaveFoodFileWrongPath() {
        FoodStorage foodFileStorage = new FoodFileStorage(Path.of("/"), foodReportPath);

        assertThrows(StorageException.class,() -> foodFileStorage.saveFood(new Food(123, "name", "12345")));
    }

    @Test
    void testSaveFoodReportWrongPath() {
        FoodStorage foodFileStorage = new FoodFileStorage(foodPath, Path.of("/"));


        assertThrows(StorageException.class, () ->
            foodFileStorage.saveFoodReport(new FoodReport("name", List.of("ingredient1", "ingredient2"), 1.0, 2.0, 3.0, 4.0, 5.0, 1234))
        );
    }

}
