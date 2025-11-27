package bg.sofia.uni.fmi.mjt.foodanaliyzer.storage;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.StorageException;

import java.util.Collection;

//TODO Java doc
public interface FoodStorage {
    void saveFood(Food food) throws StorageException;

    void saveFoodReport(FoodReport foodReport) throws StorageException;

    Collection<Food> getAllFoods() throws StorageException;

    Collection<FoodReport> getAllFoodReports() throws StorageException;

    Collection<Food> searchFoodByName(String foodName) throws StorageException;

    FoodReport searchFoodReportById(int foodId) throws StorageException;

    FoodReport searchFoodReportByGtinUpc(String foodGtinUpc) throws FoodNotFoundException, StorageException;
}
