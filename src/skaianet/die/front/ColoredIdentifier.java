package skaianet.die.front;

public class ColoredIdentifier {
    public final String name;
    public final Color color;

    public ColoredIdentifier(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public int hashCode() {
        return 3 + name.hashCode() * 7 + color.hashCode() * 13;
    }

    public boolean equals(Object o) {
        return o == this || (o instanceof ColoredIdentifier && name.equals(((ColoredIdentifier) o).name) && color.equals(((ColoredIdentifier) o).color));
    }

    public String toString() {
        return color + name;
    }
}
