package bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.Food;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodReport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class FDCHttpClientTest {

    private HttpClient mockHttpClient;
    private FDCHttpClient fdcHttpClient;

    private static final String FOOD_REPORT_JSON_URI =
        "project/test/bg/sofia/uni/fmi/mjt/foodanaliyzer/httpclient/foodReport.json";
    private static String foodReportJson;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Path pathToTestDataJson = Paths.get("").toAbsolutePath().resolve(FOOD_REPORT_JSON_URI).toAbsolutePath();

        try (var reader = new BufferedReader(new FileReader(pathToTestDataJson.toString()))) {
            StringBuilder stringBuilder = new StringBuilder();

            reader.lines().forEach(stringBuilder::append);
            foodReportJson = stringBuilder.toString();
            System.out.println(foodReportJson);
        }
    }

    @BeforeEach
    void setUp() {
        mockHttpClient = Mockito.mock(HttpClient.class);
        fdcHttpClient = new FDCHttpClient(mockHttpClient);
    }

    @Test
    void testGetFoodByIdSuccess() throws IOException, InterruptedException, FoodNotFoundException {
        String foodWithTestIdURL =
            "https://api.nal.usda.gov/fdc/v1/food/2341201?api_key=jcdfblAxQ8Ezecy9d010CgTrgBou1TvXw9ExhX2Y";

        HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);

        when(mockResponse.body()).thenReturn(foodReportJson);
        when(mockHttpClient.send(
            argThat(httpRequest -> httpRequest.uri().toString().equals(foodWithTestIdURL)),
            any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        FoodReport foodReport = fdcHttpClient.getFoodById("2341201");

        assertNotNull(foodReport);
        assertEquals("Cheese sandwich, cheddar cheese, on white bread", foodReport.description(),
            "Description must be the same as response from server");
        assertEquals(2341201, foodReport.fdcId(), "FdcId must be the same as response");
    }

    @Test
    void testGetFoodByNameSuccess() throws IOException, InterruptedException {
        String jsonResponse = """
                {
                    "foods": [
                        { "fdcId": 123, "description": "Cheddar Cheese", "gtinUpc": "000012345" },
                        { "fdcId": 456, "description": "Brie Cheese", "gtinUpc": "000054321" }
                    ]
                }
            """;

        HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        Collection<Food> foods = fdcHttpClient.getFoodByName("cheese");

        assertNotNull(foods);
        assertEquals(2, foods.size(), "Both dummy data objects should be returned");
    }

    @Test
    void testGetFoodByIdNoNutrients() throws IOException, InterruptedException, FoodNotFoundException {
        String json = """
            {
                "description": "Cheese sandwich, cheddar cheese, on white bread",
                "fdcId": 2341201
            }
            """;

        HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);

        when(mockResponse.body()).thenReturn(json);
        when(mockHttpClient.send(
            any(HttpRequest.class),
            any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        FoodReport foodReport = fdcHttpClient.getFoodById("randomId");

        assertNotNull(foodReport);
        assertEquals("Cheese sandwich, cheddar cheese, on white bread", foodReport.description(),
            "Description must be the same as response from server");
        assertEquals(2341201, foodReport.fdcId(), "FdcId must be the same as response");
        assertNull(foodReport.fat());
        assertNull(foodReport.carbs());
        assertNull(foodReport.kcal());
        assertNull(foodReport.fibre());
        assertNull(foodReport.protein());
    }

    @Test
    void testClose() {
        fdcHttpClient.close();
        verify(mockHttpClient, times(1)).close();
    }
}