package bg.sofia.uni.fmi.mjt.order.server.tshirt;

public enum Color {
    BLACK("BLACK"),
    WHITE("WHITE"),
    RED("RED"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Color(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Color getColor(String color) {
        if (color == null)
            throw new IllegalArgumentException("Color cannot be null");

        return switch (color) {
            case "BLACK" -> BLACK;
            case "WHITE" -> WHITE;
            case "RED" -> RED;
            default -> UNKNOWN;
        };
    }
}