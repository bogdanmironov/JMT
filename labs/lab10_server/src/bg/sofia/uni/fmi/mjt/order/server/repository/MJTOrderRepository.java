package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MJTOrderRepository implements OrderRepository {
    public static int idCounter = 1; //In a real world example we would use UUID
    public static final int INVALID_ORDER_ID = -1;
    List<Order> orders = new LinkedList<>();

    @Override
    public synchronized Response request(String size, String color, String destination) {
        Size sizeEnum = Size.getSize(size);
        Color colorEnum = Color.getColor(color);
        Destination destEnum = Destination.getDestination(destination);

        if (Size.UNKNOWN.equals(sizeEnum) ||
            Color.UNKNOWN.equals(colorEnum) ||
            Destination.UNKNOWN.equals(destEnum)
        ) {
            orders.add(new Order(INVALID_ORDER_ID, new TShirt(sizeEnum, colorEnum), destEnum));
            String declineMessage = getDeclineMessage(sizeEnum, colorEnum, destEnum);
            return Response.decline(declineMessage);
        } else {
            Order createdOrder = new Order(idCounter++, new TShirt(sizeEnum, colorEnum), destEnum);
            orders.add(createdOrder);
            return Response.create(createdOrder.id());
        }
    }

    @Override
    public synchronized Response getOrderById(int id) {
        if (id < 0)
            throw new IllegalArgumentException("getOrderById cannot accept negative integers");

        return orders.stream()
            .filter(it -> it.id() == id)
            .findFirst()
            .map(order -> Response.ok(List.of(order)))
            .orElseGet(() -> Response.notFound(id));
    }

    @Override
    public synchronized Response getAllOrders() {
        return Response.ok(orders);
    }

    @Override
    public synchronized Response getAllSuccessfulOrders() {
        return orders.stream()
            .filter(order -> order.id() != INVALID_ORDER_ID)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Response::ok));
    }

    private static String getDeclineMessage(Size sizeEnum, Color colorEnum, Destination destEnum) {
        StringBuilder declineMessage = new StringBuilder();
        declineMessage.append("invalid: ");
        boolean isFirst = true;

        if (Size.UNKNOWN.equals(sizeEnum)) {
            isFirst = false;
            declineMessage.append("size");
        }

        if (Color.UNKNOWN.equals(colorEnum)) {
            if (!isFirst)
                declineMessage.append(",");
            isFirst = false;
            declineMessage.append("color");
        }

        if (Destination.UNKNOWN.equals(destEnum)) {
            if (!isFirst)
                declineMessage.append(",");
            declineMessage.append("destination");
        }

        return declineMessage.toString();
    }
}
