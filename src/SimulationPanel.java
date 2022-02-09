import javax.swing.JPanel;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;
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
        
        //Draw vehicle path
        int pathX[] = new int[v.path.size()], pathY[] = new int[v.path.size()];
        for(int i = 0; i < v.path.size(); i++){
            if(v.pathMode == Vehicle.PathMode.Dotted){
                g2d.drawOval(v.path.get(i)[0], v.path.get(i)[1], 1, 1);
            }
            else if(v.pathMode == Vehicle.PathMode.Line) {
                //If the list contains [-1,-1] draw multiple lines instead of one to avoid drawing a line across the whole screen
                // Note: if the list contains [-1,-1] in i index then it's guaranteed to have an element in i+1
                if(v.path.get(i)[0] == -1 && v.path.get(i)[1] == -1){
                    g2d.drawPolyline(pathX, pathY, i);
                    Arrays.fill(pathX, v.path.get(i+1)[0]);
                    Arrays.fill(pathY, v.path.get(i+1)[1]);
                    continue;
                }
                pathX[i] = v.path.get(i)[0];
                pathY[i] = v.path.get(i)[1];
            }
        }
        if(v.pathMode == Vehicle.PathMode.Line){
            g2d.drawPolyline(pathX, pathY, v.path.size());
        }
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