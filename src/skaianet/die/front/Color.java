package skaianet.die.front;

public class Color {
    public static final Color NO_THREAD = new Color(0, 0, 0);
    public final byte red;
    public final byte green;
    public final byte blue;

    public Color(byte red, byte green, byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(int red, int green, int blue) {
        this((byte) red, (byte) green, (byte) blue);
        if ((this.red & 0xFF) != red || (this.green & 0xFF) != green || (this.blue & 0xFF) != blue) {
            throw new IllegalArgumentException("Arguments must fit in bytes!");
        }
    }

    private static String to2Hex(byte b) {
        return new String(new char[]{to1Hex((b >> 4) & 0xF), to1Hex(b & 0xF)});
    }

    private static char to1Hex(int i) {
        return (char) ((i >= 10) ? ('A' + i - 10) : (i + '0'));
    }

    public static Color parse(char r, char g, char b) throws ParsingException {
        return new Color(fromExt1Hex(r), fromExt1Hex(g), fromExt1Hex(b));
    }

    private static byte fromExt1Hex(char b) throws ParsingException {
        byte temp = from1Hex(b);
        return (byte) ((temp << 4) | temp);
    }

    private static byte from1Hex(char b) throws ParsingException {
        if (b >= '0' && b <= '9') {
            return (byte) (b - '0');
        } else if (b >= 'A' && b <= 'F') {
            return (byte) (b - 'A' + 10);
        } else if (b >= 'a' && b <= 'f') {
            return (byte) (b - 'a' + 10);
        } else {
            throw new ParsingException("Invalid hex segment: " + b);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Color && isSameColor((Color) o));
    }

    @Override
    public int hashCode() {
        return (red & 0xFF) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 16);
    }

    @Override
    public String toString() {
        return "@" + to2Hex(red) + to2Hex(green) + to2Hex(blue) + "@";
    }

    private boolean isSameColor(Color o) {
        return red == o.red && green == o.green && blue == o.blue;
    }

    public java.awt.Color toAWTColor() {
        return new java.awt.Color(red & 0xFF, green & 0xFF, blue & 0xFF);
    }
}
