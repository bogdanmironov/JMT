package bg.sofia.uni.fmi.mjt.itinerary.algorithm.astar;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
    private static final double AVG_PRICE_PER_KM = 20;
    private final City city;
    Node parent = null;
    final List<Edge> neighbors;

    double bestPathWithHeuristic = Double.MAX_VALUE;
    double bestPath = Double.MAX_VALUE;

    Node(City city) {
        this.city = city;
        this.neighbors = new ArrayList<>();
    }

    City getCity() {
        return city;
    }

    @Override
    public int compareTo(Node other) {
        var doubleComparison = Double.compare(this.bestPathWithHeuristic, other.bestPathWithHeuristic);

        if (doubleComparison == 0) {
            return this.city.name().compareTo(other.city.name());
        } else {
            return doubleComparison;
        }
    }

    static class Edge {
        final int weight;
        final Node destination;

        Edge(int weight, Node destination) {
            this.weight = weight;
            this.destination = destination;
        }
    }

    void addBranch(Journey journey, Node destination) {
        BigDecimal journeyTax = journey.price().multiply(journey.vehicleType().getGreenTax());
        int weight = journey.price().add(journeyTax).intValue();

        Edge newEdge = new Edge(weight, destination);
        neighbors.add(newEdge);
    }

    double calculateHeuristic(Node target) {
        var absX = Math.abs(target.city.location().x() - this.city.location().x());
        var absY = Math.abs(target.city.location().y() - this.city.location().y());

        return (absX + absY) / AVG_PRICE_PER_KM;
    }

    void reset() {
        bestPath = Double.MAX_VALUE;
        bestPathWithHeuristic = Double.MAX_VALUE;
        parent = null;
    }
}
