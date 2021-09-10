import javax.swing.JPanel;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
public class SimulationPanel extends JPanel {
    private Image image;
    private Graphics2D g2d;

    public SimulationPanel(Color bg) {
        setBackground(bg);
    }

    public void DrawVehicle(Vehicle v) {
        if(getWidth() == 0 || getHeight() == 0){ return;}
        if (image == null)
            ResetFrame();

        // Draw predicted position after 5 frames
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(v.x + (int) (v.vel.getXMag() * 5), v.y - (int) (v.vel.getYMag() * 5), 3, 3);
        g2d.setColor(v.color);
        // Calculate triangle points
        int headX = v.x + (int) (Math.cos(v.vel.getAngleInRadians()) * v.size);
        int headY = v.y - (int) (Math.sin(v.vel.getAngleInRadians()) * v.size);
        int aX = v.x + (int) (Math.cos(v.vel.getAngleInRadians() + (Math.toRadians(140))) * v.size);
        int aY = v.y - (int) (Math.sin(v.vel.getAngleInRadians() + (Math.toRadians(140))) * v.size);
        int bX = v.x + (int) (Math.cos(v.vel.getAngleInRadians() - (Math.toRadians(140))) * v.size);
        int bY = v.y - (int) (Math.sin(v.vel.getAngleInRadians() - (Math.toRadians(140))) * v.size);
        g2d.fillPolygon(new int[] { headX, aX, bX }, new int[] { headY, aY, bY }, 3);
    }

    public void Paint() {
        if(getWidth() == 0 || getHeight() == 0){ return;}
        getGraphics().drawImage(image, 0, 0, this);
        ResetFrame();
    }

    void ResetFrame() {
        image = createImage(getWidth(), getHeight());
        g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}