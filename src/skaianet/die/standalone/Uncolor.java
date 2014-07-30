package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;
import skaianet.die.front.Color;

public class Uncolor {

    @ATHcessible
    public int red;
    @ATHcessible
    public int green;
    @ATHcessible
    public int blue;

    public Uncolor(Color color) {
        this.red = color.red & 0xFF;
        this.green = color.green & 0xFF;
        this.blue = color.blue & 0xFF;
    }

    @ATHcessible
    public Color get() {
        return new Color(red, green, blue);
    }
}
