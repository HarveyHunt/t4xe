package gameLogic.map;

import com.badlogic.gdx.math.Vector2;
import gameLogic.Game;
import gameLogic.dijkstra.Dijkstra;
import gameLogic.player.Player;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private List<Station> stations;

    private List<Connection> enabledConnections;
    private List<Connection> disabledConnections;

    private Random random = new Random();
    private Dijkstra dijkstra;
    private JSONImporter jsonImporter;

    public Map() {
        stations = new ArrayList<Station>();

        enabledConnections = new ArrayList<Connection>();
        disabledConnections = new ArrayList<Connection>();

        //Imports all values from the JSON file using the JSONImporter
        jsonImporter = new JSONImporter(this);

        //Analyses the graph using Dijkstra's algorithm
        dijkstra = new Dijkstra(this);
    }

    public boolean doesConnectionExist(String stationName, String anotherStationName, ConnectionType t) {
        // Determine which list of connections to use
        List<Connection> connections;

        if (t == ConnectionType.ENABLED)
            connections = enabledConnections;
        else
            connections = disabledConnections;

        //Returns whether or not the connection exists by checking the two station names passed to it
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            //Checks whether or not the connection has station 1 and station 2 in its attributes, if so returns true, if not returns false
            if (s1.equals(stationName) && s2.equals(anotherStationName)
                    || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return true;
            }
        }

        return false;
    }

    public boolean doesConnectionExist(String stationName, String anotherStationName) {
        // If no flag is specified, return the list of enabled connections
        return doesConnectionExist(stationName, anotherStationName, ConnectionType.ENABLED);
    }

    public Connection getConnection(Station station1, Station station2, ConnectionType t) {
        // Determine which list of connections to use
        List<Connection> connections;

        if (t == ConnectionType.ENABLED)
            connections = enabledConnections;
        else
            connections = disabledConnections;

        //Returns the connection that connects station1 and station2 if it exists
        String stationName = station1.getName();
        String anotherStationName = station2.getName();

        //Iterates through every connection and checks them
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            //Checks whether the connection is between station1 and station2 by comparing the start and end to their names
            if (s1.equals(stationName) && s2.equals(anotherStationName)
                    || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return connection;
            }
        }

        return null;
    }

    public Connection getConnection(Station station1, Station station2) {
        // If no flag is specified, return the list of enabled connections
        return getConnection(station1, station2, ConnectionType.ENABLED);
    }


    public Station getRandomStation() {
        //Returns a random station
        return stations.get(random.nextInt(stations.size()));
    }

    public Station addStation(String name, Position location) {
        //This routine adds a new station the list of stations
        Station newStation = new Station(name, location);
        stations.add(newStation);
        return newStation;
    }

    public CollisionStation addJunction(String name, Position location) {
        //This routine adds a new junction to the list of stations
        CollisionStation newJunction = new CollisionStation(name, location);
        stations.add(newJunction);
        return newJunction;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Connection> getEnabledConnections() {
        return enabledConnections;
    }

    public List<Connection> getDisabledConnections() {
        return disabledConnections;
    }

    public void disableConnection(Connection target) {
        // Attempt to move the connection from an enabled to a disabled state
        int i = enabledConnections.indexOf(target);

        if (i > -1) {
            disabledConnections.add(enabledConnections.remove(i));
        }
    }

    public void enableConnection(Connection target) {
        // Attempt to move the connection from a disabled to an enabled state
        int i = disabledConnections.indexOf(target);

        if (i > -1) {
            enabledConnections.add(disabledConnections.remove(i));
        }
    }


    public Connection addConnection(Station station1, Station station2, ConnectionType t) {
        //Adds a new connection the map

        // Determine which list of connections to use
        List<Connection> connections;

        if (t == ConnectionType.ENABLED)
            connections = enabledConnections;
        else
            connections = disabledConnections;

        //This addConnection adds a connection based on stations
        Connection newConnection = new Connection(station1, station2);
        connections.add(newConnection);
        return newConnection;
    }

    public Connection addConnection(Station station1, Station station2) {
        // If no connection type is specified, operate on the enabled list
        return addConnection(station1, station2, ConnectionType.ENABLED);
    }

    //Add Connection by Names
    public Connection addConnection(String station1, String station2) {
        Station st1 = getStationByName(station1);
        Station st2 = getStationByName(station2);
        return addConnection(st1, st2);
    }


    public Station getStationByName(String name) {
        //Returns the station whose name matches the string passed to the method
        int i = 0;
        while (i < stations.size()) {
            if (stations.get(i).getName().equals(name)) {
                return stations.get(i);
            } else {
                i++;
            }
        }
        return null;
    }

    public Station getStationFromPosition(IPositionable position) {
        //Returns the station located at the position passed to the method
        for (Station station : stations) {
            if (station.getLocation().equals(position)) {
                return station;
            }
        }

        return null;
    }

    public List<Station> createRoute(List<IPositionable> positions) {
        //Takes a list of positions and uses these to find the stations at these positions
        //These stations are then added to a list which acts as the route
        List<Station> route = new ArrayList<Station>();

        for (IPositionable position : positions) {
            route.add(getStationFromPosition(position));
        }

        return route;
    }

    public void decrementBlockedConnections() {
        //This is called every turn and decrements every connection's blocked attribute
        for (Connection connection : enabledConnections) {
            connection.decrementBlocked();
        }
    }

    public Connection getRandomConnection() {
        //Returns a random connection, used for blocking a random connection
        int index = random.nextInt(enabledConnections.size());
        return enabledConnections.get(index);
    }

    public void blockRandomConnection() {
        //This blocks a random connection
        int rand = random.nextInt(2);
        if (rand > 0) {
            //50% chance of connection being blocked
            Connection toBlock;
            boolean canBlock;
            do {
                canBlock = true;
                toBlock = getRandomConnection();
                for (Player player : Game.getInstance().getPlayerManager().getAllPlayers()) {
                    for (Train train : player.getTrains()) {
                        //In a try catch statement as unplaced trains do not have a nextStation, resulting in null pointer exceptions
                        try {
                            //If a train is found to be on the connection to block, the boolean is set to false.
                            if ((train.getNextStation() == toBlock.getStation1() && train.getLastStation() == toBlock.getStation2())
                                    || (train.getNextStation() == toBlock.getStation2() && train.getLastStation() == toBlock.getStation1())) {
                                canBlock = false;
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            } while (!canBlock);

            toBlock.setBlocked(5);

        }

    }

    public void blockConnection(Station station1, Station station2, int turnsBlocked) {
        //This method sets a connection to be blocked
        //Takes the parameter turnsBlocked which in our implementation is not necessary as we always block for 5 turns, you may wish to randomise the number of turns it is blocked for though
        //This method will allow you to do that easily
        if (doesConnectionExist(station1.getName(), station2.getName())) {
            Connection connection = getConnection(station1, station2);
            connection.setBlocked(turnsBlocked);
        }
    }

    public boolean isConnectionBlocked(Station station1, Station station2) {
        //Iterates through all the enabledConnections and finds the connection that links station1 and station2. Returns if this connection is blocked.
        for (Connection connection : enabledConnections) {
            if (connection.getStation1() == station1)
                if (connection.getStation2() == station2)
                    return connection.isBlocked();
            if (connection.getStation1() == station2)
                if (connection.getStation2() == station1)
                    return connection.isBlocked();
        }

        //Reaching here means a connection has been added to the route where a connection doesn't exist
        return true;
    }

    public float getDistance(Station s1, Station s2) {
        //Uses vector maths to find the absolute distance between two stations' locations in pixels
        return Vector2.dst(s1.getLocation().getX(), s1.getLocation().getY(), s2.getLocation().getX(), s2.getLocation().getY());
    }

    public double getShortestDistance(Station s1, Station s2) {
        //This calls the relevant method from the Dijkstra's algorithm which finds the smallest distance between two stations
        return dijkstra.findMinDistance(s1, s2);
    }

    public boolean inShortestPath(Station s1, Station s2, Station s3) {
        //This method calls the relevant method from Dijkstra's algorithm which checks whether or not s3 is in the shortest path from s1 to s2
        return dijkstra.inShortestPath(s1, s2, s3);
    }
}