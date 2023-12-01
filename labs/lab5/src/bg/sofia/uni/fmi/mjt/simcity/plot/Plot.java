package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {
    int buildableArea;
    Map<String, E> buildings;

    public Plot(int buildableArea) {
        this.buildableArea = buildableArea;
        this.buildings = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address should not be empty");
        if (buildable == null) throw new IllegalArgumentException("Buildable should not be null.");

        if (buildings.containsKey(address)) throw new BuildableAlreadyExistsException();

        if (buildableArea - buildable.getArea() < 0) throw new InsufficientPlotAreaException();

        buildings.put(address, buildable);
        buildableArea -= buildable.getArea();
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null || buildables.isEmpty())
            throw new IllegalArgumentException("Buildables cannot be empty.");

        int buildableAreaSum = 0;
        for (Map.Entry<String, E> buildableEntry : buildables.entrySet()) {
            if (buildings.containsKey(buildableEntry.getKey())) throw new BuildableAlreadyExistsException();

            buildableAreaSum += buildableEntry.getValue().getArea();
        }

        if (buildableArea - buildableAreaSum < 0) throw new InsufficientPlotAreaException();

        buildings.putAll(buildables);
        buildableArea -= buildableAreaSum;
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address should not be empty");

        if (!buildings.containsKey(address)) throw new BuildableNotFoundException();

        buildings.remove(address);
    }

    @Override
    public void demolishAll() {
        buildings.clear();
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(buildings);
    }

    @Override
    public int getRemainingBuildableArea() {
//        int plotSum = 0;
//
//        for (E buildable: buildings.values()) {
//            plotSum += buildable.getArea();
//        }
//
//        return plotSum;
        return buildableArea;
    }
}
