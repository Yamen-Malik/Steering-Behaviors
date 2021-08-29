import java.awt.Dimension;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MySlider extends JSlider{
    // int minValue;
    // int maxValue;
    // int value;
    public MySlider(int orientation, int min, int max, int init){
        setModel(new DefaultBoundedRangeModel(init, 0, min, max));
        updateUI();
        setOrientation(orientation);
        setPreferredSize(new Dimension(200,100));
        // addChangeListener(this);
        setValue(init);
        setMaximum(max);
        setMinimum(min);
        // setPreferredSize(new Dimension(300,200));
        setPaintTicks(true);
        setPaintLabels(true);
        
    }

    // @Override
    // public void stateChanged(ChangeEvent e){

    // }
}
