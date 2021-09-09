import javax.swing.JFrame;

public class MyFrame extends JFrame{
    public MyFrame(int width, int height){
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}