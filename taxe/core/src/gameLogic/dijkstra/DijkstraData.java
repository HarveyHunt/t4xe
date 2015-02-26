package gameLogic.dijkstra;

import java.util.ArrayList;

public class DijkstraData {
    //This class is an abstract data structure used to be easily searchable and contain the results of running dijkstra's algorithm on the graph
    private Vertex source;
    private Vertex target;
    private double distance;
    private ArrayList<Vertex> shortestPath;

    public DijkstraData(Vertex source, Vertex target, double distance, ArrayList<Vertex> shortestPath) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.shortestPath = shortestPath;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getTarget() {
        return target;
    }

    public double getDistance() {
        return distance;
    }

    public boolean inShortestPath(String stationName) {
        //This method returns whether or not the stationName passed to it is featured in the shortestPath
        for (Vertex v : shortestPath) {
            if (v.getName().equals(stationName)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return source.getName() + " to " + target.getName() + ": " + distance;
    }
}
