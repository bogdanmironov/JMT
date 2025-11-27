package bg.sofia.uni.fmi.mjt.foodanaliyzer.command;

import java.util.List;

public record Command(String command, List<String> arguments) {
}