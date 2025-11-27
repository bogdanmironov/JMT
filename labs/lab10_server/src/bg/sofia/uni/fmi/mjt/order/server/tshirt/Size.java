package bg.sofia.uni.fmi.mjt.order.server.tshirt;

public enum Size {
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Size(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Size getSize(String size) {
        if (size == null)
            throw new IllegalArgumentException("Size cannot be null");

        return switch (size) {
            case "S" -> Size.S;
            case "M" -> Size.M;
            case "L" -> Size.L;
            case "XL" -> Size.XL;
            default -> Size.UNKNOWN;
        };
    }

}