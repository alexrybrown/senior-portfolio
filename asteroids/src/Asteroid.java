/**
 *
 * Created by alex on 9/8/16.
 */
import java.awt.*;

public class Asteroid extends Polygon {

    public Asteroid(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
    }

    public void move() {
        position.x += Math.cos(Math.toRadians(rotation));
        position.y += Math.sin(Math.toRadians(rotation));
        if(position.x > Asteroids.SCREEN_WIDTH) {
            position.x -= Asteroids.SCREEN_WIDTH;
        } else if(position.x < 0) {
            position.x += Asteroids.SCREEN_WIDTH;
        }
        if(position.y > Asteroids.SCREEN_HEIGHT) {
            position.y -= Asteroids.SCREEN_HEIGHT;
        } else if(position.y < 0) {
            position.y += Asteroids.SCREEN_HEIGHT;
        }
    }

    public void paint(Graphics brush, Color color) {
        Point[] points = getPoints();
        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];
        int nPoints = points.length;
        for(int i = 0; i < nPoints; ++i) {
            xPoints[i] = (int) points[i].x;
            yPoints[i] = (int) points[i].y;
        }
        brush.setColor(color);
        brush.drawPolygon(xPoints, yPoints, nPoints);
    }
}
