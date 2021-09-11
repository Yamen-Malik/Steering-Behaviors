import java.util.concurrent.ThreadLocalRandom;
public class Vehicle {
    int x;
    int y;
    Vector vel; // velocity
    Vector acc; // acceleration
    double maxVel = 20;
    double maxForce = 1.5;
    double mass = 1;
    int size = 20;
    java.awt.Color color = java.awt.Color.WHITE;
    int defaultPredictionFactor = 5;
    Vehicle target;
    Behavior behavior = Behavior.Wander;
    EdgeMode edgeMode = EdgeMode.Bounce;
    private Vector lastWanderVector;
    public Vehicle(int x, int y) {
        Setup(x,y, null);
    }   
    public Vehicle(int x, int y, Vector initialVelocity) {
        Setup(x, y, initialVelocity);
    }   
    private void Setup(int x, int y, Vector initialVelocity) {
        this.x = x;
        this.y = y;
        if (initialVelocity == null){
            vel = new Vector(0,0);
        }else{
            vel = new Vector(initialVelocity);
        }
        acc =  new Vector(0,0);
        if (vel.getMag() > maxVel){
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
        vel.setMag(ThreadLocalRandom.current().nextInt(7, (int)maxVel+1));
        vel.setAngleInDegrees(ThreadLocalRandom.current().nextInt(1, 360 + 1));    
    }
    

    public void ApplyForce(Vector forceVector){
        forceVector = new Vector(forceVector).divide(mass);
        if(forceVector.getMag() > maxForce){
            forceVector.setMag(maxForce);
        }
        acc.add(forceVector);
    }
    public void Update(){
        if(target != null || behavior == Behavior.Wander){
            switch(behavior){
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
            // default:
            //     throw new IllegalArgumentException("behavior has an invalid value");
            }
        }
        vel.add(acc);
        if(vel.getMag() > maxVel)
            vel.setMag(maxVel);
     
        x +=(int) vel.getXMag();
        y -= (int) vel.getYMag();
        acc.setMag(0);
    }
    /***
     * @param mode available modes ("flip","bounce")
     */
    public void Edges(int x1,int x2,int y1,int y2){
        switch(edgeMode){
            case Flip:
                if(x <= x1){
                    x=x2;
                }
                else if(x >= x2){
                    x = x1;
                }
                if(y <= y1){
                    y = y2;
                }
                else if(y >= y2){
                    y = y1;
                }
            break;
            case Bounce:
                if (x - size <= x1) {
                    vel.setXMag(Math.abs(vel.getXMag()));
                    x = x1 + size;
                } else if (x + size >= x2) {
                    vel.setXMag(-Math.abs(vel.getXMag()));
                    x = x2 -size;
                } 
                if (y - size <= y1) {
                    vel.setYMag(-Math.abs(vel.getYMag()));
                    y = y1 + size;
                } else if (y + size >= y2) {
                    vel.setYMag(Math.abs(vel.getYMag()));
                    y = y2 - size;
                }
            break;
        }
    }

    public Vector GetSteeringForce(double x, double y, boolean flee){
        double desiredMag = CalculateDistance(this.x, this.y, x, y);
        if (desiredMag > maxVel){
            desiredMag = maxVel;
        }
        double desiredAngle = Math.atan2(this.y - y, x - this.x); 
        Vector desiredVel = new Vector(desiredMag, desiredAngle, true);
        if (flee){
            desiredVel.multiply(-1);
        }
        Vector steering = Vector.Subtract(desiredVel, vel);
        return steering;
    }
    public void Seek(double x, double y){
        ApplyForce(GetSteeringForce(x, y, false));
    }
    public void Flee(double x, double y){
        ApplyForce(GetSteeringForce(x, y, true));
    }
    public int[] GetPredictedPosition(Vehicle v, int predictionFactor){
        return new int[] {v.x + (int)(v.vel.getXMag() * predictionFactor ), v.y - (int)(v.vel.getYMag() * predictionFactor)};
    }
    public void Pursue(Vehicle v){
        Pursue(v, defaultPredictionFactor);
    }
    public void Pursue(Vehicle v, int predictionFactor){
        int[] pos = GetPredictedPosition(v, predictionFactor);
        Seek(pos[0], pos[1]);
    }
    public void Evade(Vehicle v){
        Evade(v, defaultPredictionFactor);
    }
    public void Evade(Vehicle v, int predictionFactor){
        int[] pos = GetPredictedPosition(v, predictionFactor);
        Flee(pos[0], pos[1]);
    }
    public void Wander(){
        int wanderingRange = 60;
        int[] predictedPosition = GetPredictedPosition(this, 20);
        int wanderAngle;
        if(lastWanderVector == null){
            wanderAngle = ThreadLocalRandom.current().nextInt(0, 360);
        }
        else{
            wanderAngle = (int)lastWanderVector.getAngleInDegrees() + ThreadLocalRandom.current().nextInt(-wanderingRange/2, (wanderingRange/2) + 1);
        }
        Vector wanderVector = new Vector(20, wanderAngle);
        lastWanderVector = wanderVector;
        Seek(predictedPosition[0] + wanderVector.getXMag(), predictedPosition[1] + wanderVector.getYMag());
    }
    private static double CalculateDistance(double x1, double y1, double x2, double y2){
        return Math.pow(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) , 0.5);
    }
    public static boolean CheckCollition(Vehicle v1, Vehicle v2) {
        return Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2) < Math.pow(v2.size + v1.size, 2);
    }

    enum Behavior{Wander, Seek, Flee, Pursue, Evade}
    enum EdgeMode {Bounce, Flip}
}