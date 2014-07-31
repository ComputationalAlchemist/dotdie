package skaianet.die.gui;

import skaianet.die.back.ATHcessible;
import skaianet.die.front.Color;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Map;

public abstract class Component {

    private static final Font defaultFont;

    static {
        Map<TextAttribute, Object> map =
                new Hashtable<TextAttribute, Object>();
        map.put(TextAttribute.KERNING,
                TextAttribute.KERNING_ON);
        defaultFont = new Font("Courier New", Font.BOLD, 14).deriveFont(map);
    }

    @ATHcessible
    public int x = 0;
    @ATHcessible
    public int y = 0;
    @ATHcessible
    public Color color = new Color(0, 0, 0);
    @ATHcessible
    public double scale = 1;
    private BufferedImage bimg;
    private Graphics2D virtG;

    public void render(Graphics2D g, int width, int height) {
        if (scale == 1) {
            g.setColor(color.toAWTColor());
            g.translate(x, y);
            g.setFont(defaultFont);
            this.renderInternal(g);
            g.translate(-x, -y);
        } else {
            if (this.bimg == null || this.bimg.getWidth() / scale != width || this.bimg.getHeight() / scale != height) {
                this.bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                this.virtG = bimg.createGraphics();
            }
            virtG.setColor(new java.awt.Color(0, 0, 0, 0));
            virtG.fillRect(0, 0, width, height);
            virtG.setColor(color.toAWTColor());
            virtG.setFont(defaultFont);
            AffineTransform transform = virtG.getTransform();
            virtG.translate(x / scale, y / scale);
            this.renderInternal(virtG);
            virtG.setTransform(transform);
            g.drawImage(bimg, AffineTransform.getScaleInstance(scale, scale), null);
        }
    }

    protected void renderText(Graphics2D g, String str, int x, int y) {
        if (str.length() != 0) {
            char[] chars = str.toCharArray();
            FontRenderContext frc = g.getFontRenderContext();
            GlyphVector vector = g.getFont().layoutGlyphVector(frc, chars, 0, chars.length, 0);

            for (int i = 0; i < vector.getNumGlyphs(); i++) {
                Point2D position = vector.getGlyphPosition(i);
                position.setLocation(position.getX() - i, position.getY());
                vector.setGlyphPosition(i, position);
            }

            g.drawGlyphVector(vector, x, y);
        }
    }

    protected abstract void renderInternal(Graphics2D g);
}
