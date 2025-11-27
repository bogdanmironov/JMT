package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.order.Order;

import java.util.ArrayList;
import java.util.Collection;

public record Response(Status status, String additionalInfo, Collection<Order> orders) {
    private enum Status {
        OK, CREATED, DECLINED, NOT_FOUND
    }

    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.CREATED and with proper message for additional info
     */
    public static Response create(int id) {
        return new Response(Status.CREATED, String.format("ORDER_ID=%s", id), new ArrayList<>());
    }

    /**
     * Creates a response
     *
     * @param orders the orders which will be returned to the client
     * @return response with status Status.OK and Collection of orders
     */
    public static Response ok(Collection<Order> orders) {
        return new Response(Status.OK, "", orders);
    }

    /**
     * Creates a response
     *
     * @param errorMessage the message which will be sent as additionalInfo
     * @return response with status Status.DECLINED and errorMessage as additionalInfo
     */
    public static Response decline(String errorMessage) {
        return new Response(Status.DECLINED, errorMessage, new ArrayList<>());
    }

    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.NOT_FOUND and with proper message for additional info
     */
    public static Response notFound(int id) {
        return new Response(
            Status.NOT_FOUND,
            String.format("Order with id = %s does not exist.", id),
            new ArrayList<>()
        );
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();

        stringBuilder.append("{\"status\":\"");
        stringBuilder.append(this.status);
        stringBuilder.append("\"");

        switch (this.status) {
            case CREATED:
            case DECLINED:
            case NOT_FOUND:
                stringBuilder.append(", \"additionalInfo\":");
                stringBuilder.append(additionalInfo);
                stringBuilder.append("\"");
                break;
            case OK:
                stringBuilder.append(", \"orders\":");
                stringBuilder.append(orders);
                break;
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}