package us.duoproject;

import java.awt.*;

public class Marker extends Component {

    private final int id;
    private final int radius;
    private final Color idleColor;
    private final Color triggerColor;

    private boolean isTriggered = false;

    public Marker(int id, int x, int y, int radius) {
        this(id, x, y, radius, new Color(150, 200, 60), new Color(255, 60, 60));
    }

    public Marker(int id, int x, int y, int radius, Color idleColor, Color triggerColor) {
        this.id = id;
        this.radius = radius;
        this.idleColor = idleColor;
        this.triggerColor = triggerColor;
        setLocation(x, y);
        setVisible(true);
    }

    public int getId() {
        return id;
    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void setTriggered(boolean triggered) {
        isTriggered = triggered;
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(isTriggered ? triggerColor : idleColor);
        graphics.fillRoundRect(getX(), getY(), radius, radius, radius, radius);
    }
}
