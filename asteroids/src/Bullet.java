/**
 *
 * Created by alex on 9/8/16.
 */
import java.awt.*;

public class Bullet extends Circle {
    private static final int RADIUS = 5;
    private double rotation;

    public Bullet(Point center, double rotation) {
        super(center, RADIUS);
        this.rotation = rotation;
    }

    public Point getCenter() {
        return center;
    }

    public void move() {
        center.x += 2 * Math.cos(Math.toRadians(rotation));
        center.y += 2 * Math.sin(Math.toRadians(rotation));
    }

    public void paint(Graphics brush, Color color) {
        brush.setColor(color);
        brush.fillOval((int) center.x, (int) center.y, RADIUS, RADIUS);
    }

    public boolean outOfBounds() {
        return (center.x < 0 || center.x > Asteroids.SCREEN_WIDTH) && (center.y < 0 || center.y > Asteroids.SCREEN_HEIGHT);
    }
}
