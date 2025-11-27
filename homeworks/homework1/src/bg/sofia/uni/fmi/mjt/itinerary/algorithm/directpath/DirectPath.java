package bg.sofia.uni.fmi.mjt.itinerary.algorithm.directpath;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;
import bg.sofia.uni.fmi.mjt.itinerary.algorithm.BestPathAlgorithm;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.SequencedCollection;

public class DirectPath implements BestPathAlgorithm {
    private final List<Journey> schedule;

    public DirectPath(List<Journey> schedule) {
        this.schedule = schedule;
    }

    @Override
    public SequencedCollection<Journey> getShortestPath(City from, City to) throws NoPathToDestinationException {
        if (from == null || to == null) {
            throw new IllegalArgumentException("City cannot be null");
        }

        BigDecimal cheapestPrice = null;
        Journey bestPath = null;

        for (Journey journey : schedule) {
            if (from.equals(journey.from()) &&
                to.equals(journey.to())) {
                BigDecimal journeyTax = journey.price().multiply(journey.vehicleType().getGreenTax());
                BigDecimal currentPrice = journey.price().add(journeyTax);

                if (cheapestPrice == null ||
                    (cheapestPrice.compareTo(currentPrice) > 0)) {
                    bestPath = journey;
                    cheapestPrice = currentPrice;
                }
            }
        }

        if (bestPath == null) {
            throw new NoPathToDestinationException("No path to destination found.");
        } else {
            return List.of(bestPath);
        }
    }
}
