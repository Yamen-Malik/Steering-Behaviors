import java.util.concurrent.ThreadLocalRandom;
import java.util.LinkedList;

public class Vehicle {
    int x;
    int y;
    Vector vel; // velocity
    Vector acc; // acceleration
    double maxVel = 20;
    double maxForce = 1.5;
    double mass = 1;
    int size = 20;
    int pathLength = 50;
    LinkedList<int[]> path = new LinkedList<>();
    java.awt.Color color = java.awt.Color.WHITE;
    int defaultPredictionFactor = 5;
    Vehicle target;
    Behavior behavior = Behavior.Wander;
    EdgeMode edgeMode = EdgeMode.Bounce;
    PathMode pathMode = PathMode.Line;
    private Vector lastWanderVector;

    /**
     * Constructs a new {@code Vehicle} at (0, 0) with the velocity and acceleration
     * set to 0
     * 
     */
    public Vehicle() {
        this(0, 0);
    }

    /**
     * Constructs a new {@code Vehicle} at the given postion with the velocity and
     * acceleration set to 0
     * 
     * @param x the x coordinates
     * @param y the y coordinates
     */
    public Vehicle(int x, int y) {
        this(x, y, new Vector());
    }

    /**
     * Constructs a new {@code Vehicle} at the given postion and velocity
     * with the acceleration set to 0
     * 
     * @param x               the x coordinates
     * @param y               the y coordinates
     * @param initialVelocity the initial velocity of the vehicle
     * 
     * @see Vector
     */
    public Vehicle(int x, int y, Vector initialVelocity) {
        this.x = x;
        this.y = y;
        if (initialVelocity == null) {
            vel = new Vector(0, 0);
        } else {
            vel = new Vector(initialVelocity);
        }
        acc = new Vector(0, 0);
        if (vel.getMag() > maxVel) {
            vel.setMag(maxVel);
        }
    }

    /***
     * Sets a random speed and position to the vehicle object
     * 
     * @param v  the target object
     * @param x1 the minimum x position to the vehicle
     * @param y1 the minimum y position to the vehicle
     * @param x2 the maximum x position to the vehicle
     * @param y2 the maximum x position to the vehicle
     */
    public void Randomize(int x1, int y1, int x2, int y2) {
        RandomizePosition(x1, y1, x2, y2);
        RandomizeVelocity();
    }

    /***
     * Sets a random position to the vehicle object
     * 
     * @param v  the target object
     * @param x1 the minimum x position to the vehicle
     * @param y1 the minimum y position to the vehicle
     * @param x2 the maximum x position to the vehicle
     * @param y2 the maximum x position to the vehicle
     */
    public void RandomizePosition(int x1, int y1, int x2, int y2) {
        x = ThreadLocalRandom.current().nextInt(x1, x2);
        y = ThreadLocalRandom.current().nextInt(y1, y2);
    }

    /**
     * Sets a random Velociy to the vehicle object
     */
    public void RandomizeVelocity() {
        vel.setMag(ThreadLocalRandom.current().nextInt((int) (maxVel * 0.1), (int) maxVel + 1));
        vel.setAngleInDegrees(ThreadLocalRandom.current().nextInt(1, 360 + 1));
    }

    public void ApplyForce(Vector forceVector) {
        forceVector = new Vector(forceVector).divide(mass);
        if (forceVector.getMag() > maxForce) {
            forceVector.setMag(maxForce);
        }
        acc.add(forceVector);
    }

    /***
     * Updates the position of the vehicle and limits the position to the given
     * boundary
     * 
     * @param x1 the minimum x position to the vehicle
     * @param y1 the minimum y position to the vehicle
     * @param x2 the maximum x position to the vehicle
     * @param y2 the maximum x position to the vehicle
     */
    public void Update(int x1, int x2, int y1, int y2) {
        if (target != null || behavior == Behavior.Wander) {
            switch (behavior) {
                case Wander:
                    Wander();
                    break;
                case Seek:
                    Seek(target.x, target.y);
                    break;
                case Flee:
                    Flee(target.x, target.y);
                    break;
                case Pursue:
                    Pursue(target);
                    break;
                case Evade:
                    Evade(target);
                    break;
                default:
                    throw new IllegalArgumentException("behavior has an invalid value");
            }
        }
        vel.add(acc);
        if (vel.getMag() > maxVel)
            vel.setMag(maxVel);

        x += (int) vel.getXMag();
        y -= (int) vel.getYMag();
        acc.setMag(0);

        // Constrain the position with the edges of the screen
        Edges(x1, x2, y1, y2);

        // Add the current position to the path list
        if (path.size() >= pathLength) {
            path.poll();
        }
        if (!path.isEmpty()
                && (Math.abs(x - path.getLast()[0]) > Math.abs(vel.getXMag())
                        || Math.abs(y - path.getLast()[1]) > Math.abs(vel.getYMag()))) {
            // Add [-1, -1] to indicate the end of the previous path line and start a new
            // line
            path.poll();
            path.add(new int[] { -1, -1 });
        }
        path.add(new int[] { x, y });
    }

    public void Edges(int x1, int x2, int y1, int y2) {
        switch (edgeMode) {
            case Wrap:
                if (x <= x1)
                    x = x2;
                else if (x >= x2)
                    x = x1;
                if (y <= y1)
                    y = y2;
                else if (y >= y2)
                    y = y1;
                break;
            case Bounce:
                if (x <= x1) {
                    vel.setXMag(Math.abs(vel.getXMag()));
                    x = x1;
                } else if (x >= x2 - 1) {
                    vel.setXMag(-Math.abs(vel.getXMag()));
                    x = x2 - 1;
                }
                if (y <= y1) {
                    vel.setYMag(-Math.abs(vel.getYMag()));
                    y = y1;
                } else if (y >= y2 - 38) { // 38 is apparently the magic number
                    vel.setYMag(Math.abs(vel.getYMag()));
                    y = y2 - 38;
                }
                break;
        }
    }

    public Vector GetSteeringForce(double x, double y, boolean flee) {
        double desiredMag = CalculateDistance(this.x, this.y, x, y);
        if (desiredMag > maxVel) {
            desiredMag = maxVel;
        }
        double desiredAngle = Math.atan2(this.y - y, x - this.x);
        Vector desiredVel = new Vector(desiredMag, desiredAngle, true);
        if (flee) {
            desiredVel.multiply(-1);
        }
        Vector steering = Vector.Subtract(desiredVel, vel);
        return steering;
    }

    public void Seek(double x, double y) {
        ApplyForce(GetSteeringForce(x, y, false));
    }

    public void Flee(double x, double y) {
        ApplyForce(GetSteeringForce(x, y, true));
    }

    public int[] GetPredictedPosition(Vehicle v, int predictionFactor) {
        return new int[] { v.x + (int) (v.vel.getXMag() * predictionFactor),
                v.y - (int) (v.vel.getYMag() * predictionFactor) };
    }

    public void Pursue(Vehicle v) {
        Pursue(v, defaultPredictionFactor);
    }

    public void Pursue(Vehicle v, int predictionFactor) {
        int[] pos = GetPredictedPosition(v, predictionFactor);
        Seek(pos[0], pos[1]);
    }

    public void Evade(Vehicle v) {
        Evade(v, defaultPredictionFactor);
    }

    public void Evade(Vehicle v, int predictionFactor) {
        int[] pos = GetPredictedPosition(v, predictionFactor);
        Flee(pos[0], pos[1]);
    }

    public void Wander() {
        int wanderingRange = 60;
        int[] predictedPosition = GetPredictedPosition(this, 20);
        int wanderAngle;
        if (lastWanderVector == null) {
            wanderAngle = ThreadLocalRandom.current().nextInt(0, 360);
        } else {
            wanderAngle = (int) lastWanderVector.getAngleInDegrees()
                    + ThreadLocalRandom.current().nextInt(-wanderingRange / 2, (wanderingRange / 2) + 1);
        }
        Vector wanderVector = new Vector(20, wanderAngle);
        lastWanderVector = wanderVector;
        Seek(predictedPosition[0] + wanderVector.getXMag(), predictedPosition[1] + wanderVector.getYMag());
    }

    private static double CalculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static boolean CheckCollision(Vehicle v1, Vehicle v2) {
        return Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2) < Math.pow(v2.size + v1.size, 2);
    }

    enum Behavior {
        Wander, Seek, Flee, Pursue, Evade
    }

    enum EdgeMode {
        Bounce, Wrap
    }

    enum PathMode {
        Dotted, Line
    }
}