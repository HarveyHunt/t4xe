package gameLogic.map;

import fvs.taxe.actor.StationActor;

public class Station {
    private String name;
    private IPositionable location;
    private StationActor actor;

    public Station(String name, IPositionable location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public IPositionable getLocation() {
        return location;
    }

    public StationActor getActor() {
        return actor;
    }

    public void setActor(StationActor actor) {
        this.actor = actor;
    }

    public boolean equals(Object o) {
        //Allows stations to be compared to each other, to check if they are the same station
        if (o instanceof Station) {
            Station s = (Station) o;
            return getName().equals(s.getName())
                    && getLocation().getX() == s.getLocation().getX()
                    && getLocation().getY() == s.getLocation().getY();
        } else {
            return false;
        }
    }

}
