package bg.sofia.uni.fmi.mjt.itinerary.algorithm.astar;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;
import bg.sofia.uni.fmi.mjt.itinerary.algorithm.BestPathAlgorithm;
import bg.sofia.uni.fmi.mjt.itinerary.exception.JourneyNotFoundException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NodeNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SequencedCollection;

public class AStar implements BestPathAlgorithm {

    private final List<Node> nodes = new ArrayList<>();
    private final List<Journey> schedule;

    public AStar(List<Journey> schedule) throws NodeNotFoundException {
        buildNodes(schedule);
        buildEdges(schedule);
        this.schedule = schedule;
    }

    @Override
    public SequencedCollection<Journey> getShortestPath(City from, City to)
        throws NodeNotFoundException, JourneyNotFoundException, NoPathToDestinationException {
        Node start = null;
        Node end = null;

        for (Node node : nodes) {
            node.reset();

            if (from.equals(node.getCity())) {
                start = node;
            }

            if (to.equals(node.getCity())) {
                end = node;
            }
        }

        if (start == null) {
            throw new NodeNotFoundException("Could not find node.");
        }

        if (end == null) {
            throw new NodeNotFoundException("Could not find node.");
        }

        Node foundDestination = aStar(start, end);

        return getPath(foundDestination);
    }

    private Node aStar(Node start, Node target) throws NoPathToDestinationException {
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        start.bestPath = 0;
        start.bestPathWithHeuristic = start.bestPath + start.calculateHeuristic(target);
        openList.add(start);

        while (!openList.isEmpty()) {
            Node currentEdge = openList.peek();
            if (currentEdge == target) {
                return currentEdge;
            }

            checkEdges(target, currentEdge, openList, closedList);

            openList.remove(currentEdge);
            closedList.add(currentEdge);
        }

        throw new NoPathToDestinationException("No path to destination found.");
    }

    private void checkEdges(Node target, Node n, PriorityQueue<Node> openList, PriorityQueue<Node> closedList) {
        for (Node.Edge edge : n.neighbors) {
            Node m = edge.destination;
            double totalWeight = n.bestPath + edge.weight;

            if (!openList.contains(m) && !closedList.contains(m)) {
                m.parent = n;
                m.bestPath = totalWeight;
                m.bestPathWithHeuristic = m.bestPath + m.calculateHeuristic(target);
                openList.add(m);
            } else {
                if (totalWeight < m.bestPath) {
                    m.parent = n;
                    m.bestPath = totalWeight;
                    m.bestPathWithHeuristic = m.bestPath + m.calculateHeuristic(target);

                    if (closedList.contains(m)) {
                        closedList.remove(m);
                        openList.add(m);
                    }
                }
            }
        }
    }

    private SequencedCollection<Journey> getPath(Node target) throws JourneyNotFoundException {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null.");
        }

        Node currentNode = target;

        SequencedCollection<Journey> path = new ArrayList<>();

        while (currentNode.parent != null) {
            Journey journey = getJourneyForNode(currentNode, currentNode.parent);
            path.add(journey);

            currentNode = currentNode.parent;
        }

        return path.reversed();
    }

    private Journey getJourneyForNode(Node currentNode, Node currentNodeParent) throws JourneyNotFoundException {
        for (Journey connection : schedule) {
            if (currentNode.getCity().equals(connection.to())
                && currentNodeParent.getCity().equals(connection.from())) {
                return connection;
            }
        }

        throw new JourneyNotFoundException("Journey not found.");
    }

    private void buildEdges(List<Journey> schedule) throws NodeNotFoundException {
        for (Node node : nodes) {
            for (Journey connection : schedule) {
                if (node.getCity().equals(connection.from())) {
                    Node destinationNode = null;

                    for (Node destinationNodeCandidate : nodes) {
                        if (connection.to().equals(destinationNodeCandidate.getCity())) {
                            destinationNode = destinationNodeCandidate;
                        }
                    }

                    if (destinationNode == null) {
                        throw new NodeNotFoundException("Could not find node.");
                    }

                    node.addBranch(connection, destinationNode);
                }
            }
        }
    }

    private void buildNodes(List<Journey> schedule) {
        for (Journey connection : schedule) {
            boolean alreadyAddedFrom = false;
            boolean alreadyAddedTo = false;

            for (Node node : nodes) {
                if (connection.from().equals(node.getCity())) {
                    alreadyAddedFrom = true;
                }

                if (connection.to().equals(node.getCity())) {
                    alreadyAddedTo = true;
                }
            }

            if (!alreadyAddedFrom) {
                nodes.add(new Node(connection.from()));
            }

            if (!alreadyAddedTo) {
                nodes.add(new Node(connection.to()));
            }
        }
    }
}
