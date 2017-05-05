/**
 *
 * Created by alex on 9/7/16.
 */
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Ship extends Polygon implements KeyListener {
    private int lives = 4;
    private boolean forward = false;
    private boolean right = false;
    private boolean left = false;
    private boolean fire = false;
    private boolean fired = false;
    private ArrayList<Bullet> bullets;

    public Ship(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
        bullets = new ArrayList<Bullet>();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            forward = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            fire = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            forward = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            fire = false;
            fired = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // ADDED BY ALEX
    // return the bullets
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public int getLives() {
        return lives;
    }

    public void removeLife() {
        if(lives > 0) {
            lives -= 1;
        }
    }

    public boolean shipDestroyed() {
        return lives == 0;
    }

    // ADDED BY ALEX
    // Setup horizontal movement, then rotational
    public void move() {
        // Check forward movement
        if(forward) {
            position.x += 3 * Math.cos(Math.toRadians(rotation));
            position.y += 3 * Math.sin(Math.toRadians(rotation));
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
        // Check rotation to right
        if(right) {
            rotate(2);
        }
        // Check rotation to left
        if(left) {
            rotate(-2);
        }
        // Check bullet fire and only allow one bullet to be fired per press of the space bar
        if(fire && !fired) {
            bullets.add(new Bullet(this.getPoints()[3], rotation));
            fired = true;
        }
    }

    // ADDED BY ALEX
    // Create paint method to paint a ship
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
        brush.fillPolygon(xPoints, yPoints, nPoints);
    }
}
