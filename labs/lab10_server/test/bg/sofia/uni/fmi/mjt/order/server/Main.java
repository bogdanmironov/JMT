package bg.sofia.uni.fmi.mjt.order.server;


import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start.");
//        System.out.println(Response.create(5));
//        System.out.println(Response.ok(List.of()));
//
//        Order order = new Order(-1, new TShirt(Size.L, Color.BLACK), Destination.EUROPE);
//        Order order2 = new Order(2, new TShirt(Size.L, Color.BLACK), Destination.EUROPE);
//
//        System.out.println(Response.ok(List.of(order)));
//        System.out.println(Response.ok(List.of(order, order2)));
//        //{"id":-1, "tShirt":{"size":"L", "color":"UNKNOWN"}, "destination":"EUROPE"}
//        //{"id":1, "tShirt":{size":"L", "color":"BLACK"}, "destination":"EUROPE"}
        TShirtShopServer server = new TShirtShopServer();
        server.run();

        System.out.println("Fin.");
    }
}
