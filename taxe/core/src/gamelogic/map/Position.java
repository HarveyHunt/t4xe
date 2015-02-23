package gameLogic.map;

public class Position extends IPositionable {
    //This is the class that implements IPositionable and allows you to create new Positions that can be compared to existing ones
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position pos = (Position) o;
            return (x == pos.getX() && y == pos.getY());
        }
        return false;

    }
}

