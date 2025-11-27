package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.algorithm.BestPathAlgorithm;
import bg.sofia.uni.fmi.mjt.itinerary.algorithm.astar.AStar;
import bg.sofia.uni.fmi.mjt.itinerary.algorithm.directpath.DirectPath;
import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.JourneyNotFoundException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NodeNotFoundException;

import java.util.List;
import java.util.SequencedCollection;

public class RideRight implements ItineraryPlanner {
    private final List<Journey> schedule;

    public RideRight(List<Journey> schedule) {
        this.schedule = schedule;
    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
        throws CityNotKnownException, NoPathToDestinationException {

        if (checkCityNotEligible(start) ||
            checkCityNotEligible(destination)) {
            throw new CityNotKnownException("City not in schedule.");
        }

        BestPathAlgorithm algorithm;

        if (allowTransfer) {
            try {
                algorithm = new AStar(schedule);
            } catch (NodeNotFoundException e) {
                throw new CityNotKnownException("Node with city not found.");
            }
        } else {
            algorithm = new DirectPath(schedule);
        }

        try {
            return algorithm.getShortestPath(start, destination);
        } catch (NodeNotFoundException e) {
            throw new CityNotKnownException("Node with city not found.");
        } catch (JourneyNotFoundException e) {
            throw new RuntimeException("There was a problem with the algorithm:\n" + e.getMessage());
        }
    }

    private boolean checkCityNotEligible(City city) {
        for (Journey journey: schedule) {
            if (city.equals(journey.from())
                    || city.equals(journey.to())) {
                return false;
            }
        }

        return true;
    }
}
