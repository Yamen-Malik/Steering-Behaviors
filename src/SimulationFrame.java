import javax.swing.JFrame;
import java.awt.Dimension;
public class SimulationFrame extends JFrame {
    public SimulationFrame(int width, int height, Integer... minimum) {
        int minWidth, minHeight;
        minWidth = minimum.length == 2? minimum[0] : (int) width/2;
        minHeight = minimum.length == 2? minimum[1] : (int) height/2;
        setMinimumSize(new Dimension(minWidth, minHeight));
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Steering Behaviors");
        setVisible(true);
    }
}