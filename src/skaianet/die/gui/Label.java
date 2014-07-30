package skaianet.die.gui;

import skaianet.die.back.ATHcessible;

import java.awt.*;

public class Label extends Component {
    @ATHcessible
    public String text;

    public Label(String text) {
        this.text = text;
    }

    @Override
    protected void renderInternal(Graphics2D g) {
        g.drawString(text, 0, g.getFontMetrics().getAscent());
    }
}
