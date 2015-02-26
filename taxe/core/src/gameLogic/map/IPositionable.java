package gameLogic.map;

abstract public class IPositionable {
    //This abstract class allows custom positions to be defined throughout the program
    //If you want to generate a new position to compare to other objects' positions then use the Position class intead!
    public abstract int getX();

    public abstract int getY();

    public abstract boolean equals(Object o);
}