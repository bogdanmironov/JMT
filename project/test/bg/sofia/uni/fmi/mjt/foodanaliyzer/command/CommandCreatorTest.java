package bg.sofia.uni.fmi.mjt.foodanaliyzer.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandCreatorTest {

    @Test
    void testCreateGetFoodCommand() {
        String srcCommand = "get-food \"cheese sandwich\"";
        Command desiredCommand = new Command("get-food", List.of("cheese sandwich"));

        Command result = CommandCreator.newCommand(srcCommand);

        assertEquals(desiredCommand, result, "Commands should be the same");
    }

    @Test
    void testCreateGetFoodReportCommand() {
        String srcCommand = "get-food-report 12345";
        Command desiredCommand = new Command("get-food-report", List.of("12345"));

        Command result = CommandCreator.newCommand(srcCommand);

        assertEquals(desiredCommand, result, "Commands should be the same");
    }

    @Test
    void testCreateGetFoodByBarcodeCommand() {
        String srcCommand = "get-food-by-barcode --code=00004444";
        Command desiredCommand = new Command("get-food-by-barcode", List.of("--code=00004444"));

        Command result = CommandCreator.newCommand(srcCommand);

        assertEquals(desiredCommand, result, "Commands should be the same");
    }
}
