package bg.sofia.uni.fmi.mjt.foodanaliyzer.storage;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.StorageException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public class FoodFileStorage implements FoodStorage {
    private final Path foodFilePath;
    private final Path foodReportFilePath;

    public FoodFileStorage(Path foodFilePath, Path foodReportFilePath) {
        this.foodFilePath = foodFilePath;
        this.foodReportFilePath = foodReportFilePath;
    }

    @Override
    public void saveFood(Food food) throws StorageException {
        try (var bufferedWriter = new BufferedWriter(
            Files.newBufferedWriter(
                foodFilePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            )
        )) {
            bufferedWriter.write(food.toFileString());
        } catch (IOException e) {
            throw new StorageException("Problem with storage.");
        }
    }

    @Override
    public void saveFoodReport(FoodReport foodReport) throws StorageException {
        try (var bufferedWriter = new BufferedWriter(
            Files.newBufferedWriter(
                foodReportFilePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            )
        )) {
            bufferedWriter.write(foodReport.toFileString());
        } catch (IOException e) {
            throw new StorageException("Problem with storage.");
        }
    }

    @Override
    public Collection<Food> getAllFoods() throws StorageException {
        try (var bufferedReader = new BufferedReader(Files.newBufferedReader(foodFilePath))) {
            return bufferedReader
                .lines()
                .map(Food::of)
                .toList();
        } catch (IOException e) {
            throw new StorageException("Problem with storage.");
        }
    }

    @Override
    public Collection<FoodReport> getAllFoodReports() throws StorageException {
        try (var bufferedReader = new BufferedReader(
            Files.newBufferedReader(foodReportFilePath))) {
            return bufferedReader
                .lines()
                .map(FoodReport::of)
                .toList();
        } catch (IOException e) {
            throw new StorageException("Problem with storage.");
        }
    }

    @Override
    public Collection<Food> searchFoodByName(String foodName) throws StorageException {
        try (var bufferedReader = new BufferedReader(Files.newBufferedReader(foodFilePath))) {
            return bufferedReader
                .lines()
                .map(Food::of)
                .filter(food -> food.description().toLowerCase().contains(foodName.toLowerCase()))
                .toList();
        } catch (IOException e) {
            throw new StorageException("Problem with storage.");
        }
    }

    @Override
    public FoodReport searchFoodReportById(int foodId) throws StorageException {
        return getAllFoodReports().stream().filter(it -> foodId == it.fdcId()).findFirst().orElse(null);
    }

    @Override
    public FoodReport searchFoodReportByGtinUpc(String foodGtinUpc) throws FoodNotFoundException, StorageException {
        Food food = getAllFoods().stream().filter(it -> it.gtinUpc().contains(foodGtinUpc)).findFirst()
            .orElseThrow(() -> new FoodNotFoundException("Food not found"));

        return getAllFoodReports().stream().filter(it -> food.fdcId() == it.fdcId()).findFirst()
            .orElseThrow(() -> new FoodNotFoundException("Food not found"));
    }
}
