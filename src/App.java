import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class App {    
    static int initialWidth = 600;
    static int initialHeight = 600;
    static MenuFrame menuFrame;
    static SimulationFrame frame;
    static SimulationPanel panel;
    static Vehicle[] vehicles;
    static volatile boolean ready = false; // when ready is true the simulation will start
    public static void main(String[] args) throws Exception {
        menuFrame = new MenuFrame();
        
        long lastTime = System.nanoTime();
        double amountOfTicks = 30;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                delta--;
                if(!ready) {continue;}
                for (Vehicle v : vehicles) {
                    if (v == null) { break; }
                    if(v.target != null && (v.behavior == Vehicle.Behavior.Seek || v.behavior == Vehicle.Behavior.Pursue) && Vehicle.CheckCollition(v, v.target)){
                        v.target.Randomize(0, 0, frame.getWidth(), frame.getHeight());
                    }
                    v.Update(0, frame.getWidth(), 0, frame.getHeight());
                    panel.DrawVehicle(v);
                }
                panel.Paint();
            }
        }  
    }
    
    public static void Start(Vehicle[] vehicles){
        frame = new SimulationFrame(initialWidth, initialHeight);
        panel = new SimulationPanel(Color.BLACK);
        frame.add(panel);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) { // when the simulation frame is closed open the menu frame
                ready = false;
                menuFrame.setVisible(true);
                for (Vehicle v : vehicles) {
                    if (v == null) break;
                    v.path.clear();
                    v.vel.setMag(0);
                    v.acc.setMag(0);
                } 
            }
        });
        App.vehicles = vehicles;
        for (Vehicle v : vehicles) {
            if (v == null) {break;}
            v.Randomize(0, 0, frame.getWidth(), frame.getHeight());
        }
        ready = true;
    }
}