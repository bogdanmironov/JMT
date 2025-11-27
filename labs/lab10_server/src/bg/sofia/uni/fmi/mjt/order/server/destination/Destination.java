package bg.sofia.uni.fmi.mjt.order.server.destination;

public enum Destination {
    EUROPE("EUROPE"),
    NORTH_AMERICA("NORTH_AMERICA"),
    AUSTRALIA("AUSTRALIA"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Destination(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Destination getDestination(String destination) {
        if (destination == null)
            throw new IllegalArgumentException("Destination cannot be null");

        return switch (destination) {
            case "EUROPE" -> EUROPE;
            case "NORTH_AMERICA" -> NORTH_AMERICA;
            case "AUSTRALIA" -> AUSTRALIA;
            default -> UNKNOWN;
        };
    }
}