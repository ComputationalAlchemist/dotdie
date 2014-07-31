package skaianet.die.gui;

import skaianet.die.back.ATHcessible;

import java.awt.*;

public class Icon extends Component {
    @ATHcessible
    public Image image;

    @Override
    protected void renderInternal(Graphics2D g) {
        if (image == null) {
            g.drawRect(0, 0, 8, 8);
        } else {
            g.drawImage(image, 0, 0, null);
        }
    }
}
