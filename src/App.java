import java.awt.*;
import javax.swing.SwingUtilities;

public class App {    
    static int initialWidth = 600;
    static int initialHeight = 600;
    public static void main(String[] args) throws Exception {
        MyFrame frame = new MyFrame(initialWidth, initialHeight);
        MyPanel panel = new MyPanel(Color.BLACK);
        frame.setTitle("Steering Behaviors");
        frame.add(panel);
        
        Vehicle v = new Vehicle(initialWidth/2, initialHeight/2);
        Vehicle target = new Vehicle(0,0);
        target.RandomizeVehicle(0, 0, initialWidth, initialHeight);
        target.color = Color.RED;


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
                
                // v.Flee(mousePos.getX(), mousePos.getY());
                v.Pursue(target);
                // v.Wander();
                // target.Flee(v.x, v.y);
                // target.Evade(v);
                target.Wander();
                
                if(CheckCollition(target, v)){
                    target.RandomizeVehicle(0, 0, panel.getWidth(), panel.getHeight());
                }
                
                v.Edges(0, panel.getWidth(), 0, panel.getHeight(), "bounce");
                target.Edges(0, panel.getWidth(), 0, panel.getHeight(), "bounce");
                v.Update();
                target.Update();
                panel.DrawVehicle(v);
                panel.DrawVehicle(target);
                panel.Paint();
                delta--;
            }
        }
    }
    
    static boolean CheckCollition(Vehicle v1, Vehicle v2){
        return Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2) < Math.pow(v2.r + v1.r, 2);
    }
}