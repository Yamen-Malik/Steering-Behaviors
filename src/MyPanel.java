import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel {
    Color bg;
    private Image image;
    private Graphics2D g2d;
    public MyPanel(int width, int height, Color bg){ 
        // setPreferredSize(new Dimension(width, height));
        setSize(width, height);
        this.bg = bg;
        setBackground(bg);
    }
    
    public void DrawVehicle(Vehicle v){
        if (image == null)
            ResetFrame();
        
        // Draw predicted position after 5 frames
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(v.x + (int)(v.vel.getXMag() *5), v.y - (int)(v.vel.getYMag() *5), 3, 3);
        g2d.setColor(v.color);
        // Calculate triangle points
        int headX = v.x + (int)(Math.cos(v.vel.getAngleInRadians()) * v.r);
        int headY = v.y - (int)(Math.sin(v.vel.getAngleInRadians()) * v.r);
        int aX    = v.x + (int)(Math.cos(v.vel.getAngleInRadians() + (Math.toRadians(140))) * v.r);
        int aY    = v.y - (int)(Math.sin(v.vel.getAngleInRadians() + (Math.toRadians(140))) * v.r);
        int bX    = v.x + (int)(Math.cos(v.vel.getAngleInRadians() - (Math.toRadians(140))) * v.r);
        int bY    = v.y - (int)(Math.sin(v.vel.getAngleInRadians() - (Math.toRadians(140))) * v.r);
        g2d.fillPolygon(new int[] {headX, aX, bX}, new int[] {headY, aY, bY}, 3);
    }  
    public void Paint(){
        getGraphics().drawImage(image, 0, 0, this);
        ResetFrame();
    }
    void ResetFrame(){
        image = createImage(getWidth(), getHeight());
        g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(bg);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}