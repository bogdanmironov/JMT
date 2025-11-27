package bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.dto.food.FDCFoodListResponse;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.dto.foodreport.FDCFoodIngredientsResponse;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.dto.foodreport.FDCFoodNutrientsResponse;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.Food;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodReport;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.dto.foodreport.FDCFoodReportResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class FDCHttpClient implements AutoCloseable {
    private static final String FDC_SEARCH_ENDPOINT = "https://api.nal.usda.gov/fdc/v1/foods/search";
    private static final String FDC_GET_BY_ID_ENDPOINT = "https://api.nal.usda.gov/fdc/v1/food/";
    //TODO Link to properties file or insert here actual api key
    private static final String FDC_API_KEY = "random API KEY that does not work atm";
    private static final String FDC_QUERY_PARAM_API_KEY = "api_key=" + FDC_API_KEY;
    private static final String URI_QUERY_START = "?";
    private static final String URI_QUERY_DELIMITER = "&";
    private static final String FDC_SEARCH_FOOD_QUERY_KEYWORD = "query=";

    private static final String FDC_NUTRIENT_PROTEIN = "Protein";
    private static final String FDC_NUTRIENT_FAT = "Total lipid (fat)";
    private static final String FDC_NUTRIENT_CARBS = "Carbohydrate, by difference";
    private static final String FDC_NUTRIENT_FIBRE = "Fiber, total dietary";
    private static final String FDC_NUTRIENT_KCAL = "Energy";

    private static final String URI_UNFRIENDLY_SPACE = " ";
    private static final String URI_FRIENDLY_SPACE = "%20";

    private final HttpClient httpClient;

    public FDCHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public FoodReport getFoodById(String id) throws IOException, InterruptedException, FoodNotFoundException {
        HttpResponse<String> foodResponse = makeGetFoodByIdRequest(id);

        FDCFoodReportResponse foodReportResponse = getFoodReportResponseFromJson(foodResponse);

        if (foodReportResponse == null) {
            throw new FoodNotFoundException("Food with such id was not found.");
        }

        List<String> ingredients = List.of();
        if (foodReportResponse.inputFoods() != null) {
            ingredients = foodReportResponse.inputFoods()
                .stream()
                .map(FDCFoodIngredientsResponse::foodDescription)
                .toList();
        }

        return new FoodReport(
            foodReportResponse.description(),
            ingredients,
            getNutrientAmount(foodReportResponse, FDC_NUTRIENT_KCAL),
            getNutrientAmount(foodReportResponse, FDC_NUTRIENT_PROTEIN),
            getNutrientAmount(foodReportResponse, FDC_NUTRIENT_FAT),
            getNutrientAmount(foodReportResponse, FDC_NUTRIENT_CARBS),
            getNutrientAmount(foodReportResponse, FDC_NUTRIENT_FIBRE),
            foodReportResponse.fdcId()
        );
    }

    //Intentionally looking at one page worth of info.
    //"Cheddar cheese" has 63702 hits. Unsure if that is ok for file caching or caching at all.
    public Collection<Food> getFoodByName(String name) throws IOException, InterruptedException {
        HttpResponse<String> foodResponse = makeGetFoodByNameRequest(name);

        FDCFoodListResponse foodListResponse = getFoodListResponseFromJson(foodResponse);

        if (foodListResponse.foods() == null) {
            return new ArrayList<>();
        }

        return foodListResponse
            .foods()
            .stream()
            .map(it -> new Food(it.fdcId(), it.description(), it.gtinUpc()))
            .toList();
    }

    private FDCFoodReportResponse getFoodReportResponseFromJson(HttpResponse<String> foodResponse) {
        Gson gson = new Gson();
        return gson.fromJson(foodResponse.body(), FDCFoodReportResponse.class);
    }

    private HttpResponse<String> makeGetFoodByIdRequest(String id) throws IOException, InterruptedException {
        URI getFoodByIdURI = URI.create(FDC_GET_BY_ID_ENDPOINT +
            id +
            URI_QUERY_START +
            FDC_QUERY_PARAM_API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(getFoodByIdURI)
            .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    private HttpResponse<String> makeGetFoodByNameRequest(String foodName)
        throws IOException, InterruptedException {
        URI getFoodByIdURI = URI.create(FDC_SEARCH_ENDPOINT +
            URI_QUERY_START +
            FDC_SEARCH_FOOD_QUERY_KEYWORD +
            foodName.replace(URI_UNFRIENDLY_SPACE, URI_FRIENDLY_SPACE) +
            URI_QUERY_DELIMITER +
            FDC_QUERY_PARAM_API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(getFoodByIdURI)
            .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    @Override
    public void close() {
        httpClient.close();
    }

    private FDCFoodListResponse getFoodListResponseFromJson(HttpResponse<String> foodResponse) {
        Gson gson = new Gson();
        return gson.fromJson(foodResponse.body(), FDCFoodListResponse.class);
    }

    private Double getNutrientAmount(FDCFoodReportResponse foodReportResponse,
                                     String nutrientType) {

        if (foodReportResponse.foodNutrients() == null) {
            return null;
        }

        return foodReportResponse
            .foodNutrients()
            .stream()
            .filter(Objects::nonNull)
            .filter(it -> it.nutrient().name().equals(nutrientType))
            .map(FDCFoodNutrientsResponse::amount)
            .findFirst()
            .orElse(null);
    }
}
