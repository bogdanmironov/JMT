package bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.dto.foodreport;

import java.util.List;

public record FDCFoodReportResponse(String description, List<FDCFoodNutrientsResponse> foodNutrients,
                                    List<FDCFoodIngredientsResponse> inputFoods, int fdcId) {
}
