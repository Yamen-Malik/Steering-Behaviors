import java.awt.*;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import java.util.concurrent.ThreadLocalRandom;

public class App {    
    static int width = 800;
    static int height = 700;
    public static void main(String[] args) throws Exception {
        MyFrame frame = new MyFrame(width, height);
        MyPanel panel = new MyPanel(width, height, Color.BLACK);
        Vehicle v = new Vehicle(width/2, height/2);
        Vehicle target = new Vehicle(0,0);
        ResetTarget(target);
        target.color = Color.RED;
        frame.add(panel);


        long lastTime = System.nanoTime();
        double amountOfTicks = 30;
        double ns = 1000000000/amountOfTicks;
        double delta = 0;
        while(true){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1)
            {
                // Point mousePos = MouseInfo.getPointerInfo().getLocation();
                // SwingUtilities.convertPointFromScreen(mousePos, panel);
                
                // Seek(mousePos.getX(), mousePos.getY());
                v.Pursue(target);
                // v.Wander();
                // target.Flee(v.x, v.y);
                // target.Evade(v);
                target.Wander();
                
                if(CheckCollition(target, v)){
                    ResetTarget(target);
                }
                
                v.Edges(0, width, 0, height, "bounce");
                target.Edges(0, width, 0, height, "bounce");
                v.Update();
                target.Update();
                panel.DrawVehicle(v);
                panel.DrawVehicle(target);
                panel.Paint();
                delta--;
            }
        }
    }
    static void ResetTarget(Vehicle target){
        target.vel.setMag(ThreadLocalRandom.current().nextInt(1,15));
        target.vel.setAngleInDegrees(ThreadLocalRandom.current().nextInt(1, 360 + 1));
        target.x = ThreadLocalRandom.current().nextInt(50, width - 49);
        target.y = ThreadLocalRandom.current().nextInt(50, height - 49);
    }
    
    static boolean CheckCollition(Vehicle v1, Vehicle v2){
        return Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2) < Math.pow(v2.r + v1.r, 2);
    }
}