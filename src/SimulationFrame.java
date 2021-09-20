import javax.swing.JFrame;
public class SimulationFrame extends JFrame {
    public SimulationFrame(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Steering Behaviors");
        setVisible(true);
    }
}