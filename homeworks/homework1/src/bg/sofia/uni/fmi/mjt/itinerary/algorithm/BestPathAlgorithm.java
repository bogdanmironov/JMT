package bg.sofia.uni.fmi.mjt.itinerary.algorithm;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;
import bg.sofia.uni.fmi.mjt.itinerary.exception.JourneyNotFoundException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NodeNotFoundException;

import java.util.SequencedCollection;

public interface BestPathAlgorithm {
    SequencedCollection<Journey> getShortestPath(City from, City to)
        throws NodeNotFoundException, JourneyNotFoundException, NoPathToDestinationException;

}
