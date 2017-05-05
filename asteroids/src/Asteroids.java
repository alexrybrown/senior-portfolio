/*
CLASS: AsteroidsGame
DESCRIPTION: Extending Game, Asteroids is all in the paint method.
NOTE: This class is the metaphorical "main method" of your program,
      it is your control center.
Original code by Dan Leyzberg and Art Simon
*/
import java.awt.*;
import java.util.ArrayList;

public class Asteroids extends Game {
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    private static final int SHIP_WIDTH = 40;
    private static final int SHIP_HEIGHT = 25;
    static int counter = 0;
    static boolean collision = false;
    static int collisionTime = 100;
    private Ship ship;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Asteroid> asteroidRemovals;
    private ArrayList<Bullet> bulletRemovals;
    private Star[] stars;

    public Asteroids() {
        super("Asteroids!",SCREEN_WIDTH,SCREEN_HEIGHT);
        this.setFocusable(true);
        this.requestFocus();
        // ADDED BY ALEX:
        this.ship = createShip();
        this.addKeyListener(ship);
        this.asteroids = createRandomAsteroids(10, 60, 30, Math.PI * 1/2, Math.PI * 1/5);
        this.asteroidRemovals = new ArrayList<Asteroid>();
        this.bulletRemovals = new ArrayList<Bullet>();
        this.stars = createStars(200, 6);
    }

    // ADDED BY ALEX: Create dimensions of Ship POSSIBLE SPOT FOR STUDENTS TO CHANGE SHIP SHAPE
    private Ship createShip() {
        // Look of ship
        Point[] shipShape = {
                new Point(0, 0),
                new Point(SHIP_WIDTH/3.5, SHIP_HEIGHT/2),
                new Point(0, SHIP_HEIGHT),
                new Point(SHIP_WIDTH, SHIP_HEIGHT/2)
        };
        // Set ship at the middle of the screen
        Point startingPosition = new Point((width-SHIP_WIDTH)/2, (height-SHIP_HEIGHT)/2);
        int startingRotation = 0; // Start facing to the right
        return new Ship(shipShape, startingPosition, startingRotation);
    }

    // ADDED BY ALEX: Create an array of random asteroids
    private ArrayList<Asteroid> createRandomAsteroids(int numberOfAsteroids, int maxAsteroidRadius,
                                                      int minAsteroidRadius, double maxAsteroidAngle,
                                                      double minAsteroidAngle) {
        ArrayList<Asteroid> asteroids = new ArrayList<>(numberOfAsteroids);
        for(int i = 0; i < numberOfAsteroids; ++i) {
            // Create random asteroids by sampling points on a circle
            // Find the radius first.
            int radius = (int) (Math.random() * maxAsteroidRadius);
            if(radius < minAsteroidRadius) {
                radius += minAsteroidRadius;
            }
            // Find the circles angle
            double angle = (Math.random() * maxAsteroidAngle);
            if(angle < minAsteroidAngle) {
                angle += minAsteroidAngle;
            }
            // Sample and store points around that circle
            ArrayList<Point> asteroidSides = new ArrayList<Point>();
            double originalAngle = angle;
            while(angle < 2*Math.PI) {
                double x = Math.cos(angle) * radius;
                double y = Math.sin(angle) * radius;
                asteroidSides.add(new Point(x, y));
                angle += originalAngle;
            }
            // Set everything up to create the asteroid
            Point[] inSides = asteroidSides.toArray(new Point[asteroidSides.size()]);
            Point inPosition = new Point(Math.random() * SCREEN_WIDTH, Math.random() * SCREEN_HEIGHT);
            double inRotation = Math.random() * 360;
            asteroids.add(new Asteroid(inSides, inPosition, inRotation));
        }
        return asteroids;
    }

    // ADDED BY ALEX:
    // Create a certain number of stars with a given max radius
    public Star[] createStars(int numberOfStars, int maxRadius) {
        Star[] stars = new Star[numberOfStars];
        for(int i = 0; i < numberOfStars; ++i) {
            Point center = new Point(Math.random() * SCREEN_WIDTH, Math.random() * SCREEN_HEIGHT);
            int radius = (int) (Math.random() * maxRadius);
            if(radius < 1) {
                radius = 1;
            }
            stars[i] = new Star(center, radius);
        }
        return stars;
    }

    public void paint(Graphics brush) {
        brush.setColor(Color.black);
        brush.fillRect(0,0,width,height);

        // sample code for printing message for debugging
        // counter is incremented and this message printed
        // each time the canvas is repainted
        counter++;
        brush.setColor(Color.white);
        brush.drawString("Counter is " + counter,10,10);
        brush.drawString("Lives " + ship.getLives(), 10, 20);
        // ADDED BY ALEX
        // Track asteroids and check collisions
        for(Asteroid asteroid : asteroids) {
            asteroid.paint(brush, Color.white);
            asteroid.move();
            if(!collision) {
                collision = asteroid.collision(ship);
                if(collision) {
                    ship.removeLife();
                }
            }
        }
        // If there is a collision paint the ship different and track collision time.
        if(collision) {
            ship.paint(brush, Color.red);
            collisionTime -= 1;
            if(collisionTime <= 0) {
                collision = false;
                collisionTime = 100;
            }
        } else {
            ship.paint(brush, Color.magenta);
        }
        ship.move();
        // Track bullets
        for(Bullet bullet : ship.getBullets()) {
            bullet.paint(brush, Color.red);
            bullet.move();
            // Check to remove bullet if off screen
            if(bullet.outOfBounds()) {
                bulletRemovals.add(bullet);
            }
            for(Asteroid asteroid : asteroids) {
                if(asteroid.contains(bullet.getCenter())) {
                    asteroidRemovals.add(asteroid);
                    bulletRemovals.add(bullet);
                }
            }
        }
        // Create stars
        for(Star star : stars) {
            star.paint(brush, new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        }
        // Remove asteroids
        for(Asteroid asteroid : asteroidRemovals) {
            asteroids.remove(asteroid);
        }
        // Remove bullets
        for(Bullet bullet : bulletRemovals) {
            ship.getBullets().remove(bullet);
        }
        asteroidRemovals.clear();
        bulletRemovals.clear();
        // Create Winning screen before turning off game.
        if(asteroids.size() == 0) {
            brush.setColor(Color.black);
            brush.fillRect(0,0,width,height);
            brush.setColor(Color.green);
            brush.drawString("YOU WON",SCREEN_WIDTH/2,SCREEN_HEIGHT/2);
            this.on = false;
        } else if (ship.shipDestroyed()) {
            brush.setColor(Color.black);
            brush.fillRect(0,0,width,height);
            brush.setColor(Color.red);
            brush.drawString("YOU LOST",SCREEN_WIDTH/2,SCREEN_HEIGHT/2);
            this.on = false;
        }
    }

    public static void main (String[] args) {
        Asteroids game = new Asteroids();
        game.repaint();
    }
}
