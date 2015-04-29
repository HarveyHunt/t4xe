package gameLogic.map;

import Util.Pair;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gameLogic.resource.ResourceManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

//This is a new class that handles all of the importing from the JSON file for the map.
//This was done to separate the logic as it did not feel appropriate that the map class also handled the JSON importing
public class JSONImporter {
    public JSONImporter(Map map) {
        JsonReader jsonReader = new JsonReader();

        //Defines the file to parse
        JsonValue jsonVal = jsonReader.parse("");
        try {
            jsonVal = jsonReader.parse(new FileInputStream("stations.json"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //Parses the stations and adds them to the map
        parseStations(jsonVal, map);

        //Parses the connections and adds them to the map
        parseConnections(jsonVal, map);
    }

    public JSONImporter(ResourceManager resourceManager) {
        JsonReader jsonReader = new JsonReader();

        /**
         * Originally, this was done using Gdx.files.local, but that was fucking
         * up when we were running tests. Good old FileInputStream has saved us.
         *
         * This is a nasty hack and we should really solve the problem, but we
         * have to hand in worryingly soon...
         *
         * If anyone is reading this - I am sorry for my sins against Junit / Java.
         */
        JsonValue jsonVal = jsonReader.parse("");
        try {
            jsonVal = jsonReader.parse(new FileInputStream("trains.json"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        ArrayList<Pair<String, Integer>> trains = new ArrayList<Pair<String, Integer>>();

        //Loads each train from the JSON file
        for (JsonValue train = jsonVal.getChild("trains"); train != null; train = train.next()) {
            String name = "";
            int speed = 50;
            for (JsonValue val = train.child; val != null; val = val.next()) {
                if (val.name.equalsIgnoreCase("name")) {
                    //If the field name is "name" then the name is set as the val
                    name = val.asString();
                } else {
                    //Otherwise the speed is set as the val
                    speed = val.asInt();
                }
            }

            //Sets the trains loaded in from the JSON as the trains used by the resource manager passed to the method
            trains.add(new Pair<String, Integer>(name, speed));
        }

        resourceManager.setTrains(trains);
    }

    void parseConnections(JsonValue jsonVal, Map map) {
        //Iterates through all the connections stored in the JSON array associated with the connections attribute
        for (JsonValue connection = jsonVal.getChild("connections"); connection != null; connection = connection.next) {
            String station1 = "";
            String station2 = "";

            for (JsonValue val = connection.child; val != null; val = val.next) {
                //Checks which station each value represents and sets the station variables appropriately
                if (val.name.equalsIgnoreCase("station1")) {
                    station1 = val.asString();
                } else {
                    station2 = val.asString();
                }
            }

            //Adds the connection the map passed to the function
            map.addConnection(station1, station2);
        }

        for (JsonValue connection = jsonVal.getChild("disabledConnections"); connection != null; connection = connection.next) {
            String station1 = "";
            String station2 = "";

            for (JsonValue val = connection.child; val != null; val = val.next) {
                //Checks which station each value represents and sets the station variables appropriately
                if (val.name.equalsIgnoreCase("station1")) {
                    station1 = val.asString();
                } else {
                    station2 = val.asString();
                }
            }

            //Adds the connection the map passed to the function
            map.addConnection(map.getStationByName(station1), map.getStationByName(station2), ConnectionType.DISABLED);
        }
    }

    private void parseStations(JsonValue jsonVal, Map map) {
        //Iterates through all the stations stored in the JSON array associated with the stations attribute
        for (JsonValue station = jsonVal.getChild("stations"); station != null; station = station.next) {
            String name = "";
            int x = 0;
            int y = 0;
            boolean isJunction = false;

            //Checks which attribute val represents and setting the appropriate variables based on which value is read
            for (JsonValue val = station.child; val != null; val = val.next) {
                if (val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else if (val.name.equalsIgnoreCase("x")) {
                    x = val.asInt();
                } else if (val.name.equalsIgnoreCase("y")) {
                    y = val.asInt();
                } else {
                    isJunction = val.asBoolean();
                }
            }

            //If the station is a junction then it adds it to the map as a junction, otherwise added as a station
            if (isJunction) {
                map.addJunction(name, new Position(x, y));
            } else {
                map.addStation(name, new Position(x, y));
            }
        }
    }
}