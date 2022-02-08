import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Arrays;
public class MenuFrame extends JFrame{
    int vehiclesCount = 0;
    int maxVehicles = 10;
    JPanel vehiclesContainer;
    Border defaultBorder;
    Vehicle[] vehicles = new Vehicle[maxVehicles];
    String[] behaviors; 
    String[] edgeModes; 
    JComboBox<String>[] targetSelectorsList = new JComboBox[maxVehicles];
    String defaultTargetListText = "None";  // or "Select target"
    JButton startButton;
    final int width = 1100;
    final int height = 700;
    public MenuFrame(){
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Steering Behaviors Menu");
        setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5));
        setResizable(false);
        
        defaultBorder = BorderFactory.createLineBorder(Color.lightGray, 3, true);
        
        vehiclesContainer = new JPanel(new GridLayout(maxVehicles,1));
        vehiclesContainer.setBorder(new TitledBorder(defaultBorder, "Vehicles"));
        vehiclesContainer.setPreferredSize(new Dimension(width - 10, height - 100));

        JButton addButton = new JButton("Add vehicle");
        startButton = new JButton("Start");
        
        addButton.setFocusable(false);
        startButton.setFocusable(false);
        startButton.setEnabled(false); // diable the start button until we add vehicles
        addButton.addActionListener(e -> AddVehicle());
        startButton.addActionListener(e -> {
            setVisible(false);
            App.Start(vehicles);
        });

        add(vehiclesContainer);
        add(addButton);
        add(startButton);
        setVisible(true);

        behaviors = new String[Vehicle.Behavior.values().length];
        for(int i = 0; i < behaviors.length; i++){
            behaviors[i] = Vehicle.Behavior.values()[i].toString();
        }
        edgeModes = new String[Vehicle.EdgeMode.values().length];
        for(int i = 0; i < edgeModes.length; i++){
            edgeModes[i] = Vehicle.EdgeMode.values()[i].toString();
        }
        // Add default vehicle
        AddVehicle();
    }
    // The tow ways of calling AddVehicle gives me the flexibility to add vehicle presets if I ever needed to
    private void AddVehicle(){
        AddVehicle(new Vehicle(0, 0));
    }
    private void AddVehicle(Vehicle vehicle){
        if(vehiclesCount == maxVehicles){
            JOptionPane.showMessageDialog(null, "You reached the maximum number of vehicles");
            return;
        }
        vehiclesCount++;
        vehicles[vehiclesCount -1]  = vehicle;
        JPanel vPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 5));
        vPanel.setBorder(new TitledBorder(defaultBorder, "Vehicle " + vehiclesCount));
        
        //#region create lables and set their font
        JLabel massLable = new JLabel("Mass:");
        JLabel maxVelLable = new JLabel("<html><center> Maximum <br/> Velocity:</center></html>", 0);
        JLabel maxForceLable = new JLabel("<html><center> Maximum <br/> Force: </center></html>" , 0);
        JLabel sizeLable = new JLabel("size:");
        JLabel ColorLable = new JLabel("<html><center>Color<br>[Hex]:<center></html>");
        JLabel BehaviorLable = new JLabel("Behavior:");
        JLabel targetLable = new JLabel("Target:");
        JLabel edgeModeLable = new JLabel("<html><center>Edge<br/>Mode:</center></html>");
        
        Font font = new Font("Dialog", Font.PLAIN, 12);
        massLable.setFont(font);
        maxVelLable.setFont(font);
        maxForceLable.setFont(font);
        sizeLable.setFont(font);
        ColorLable.setFont(font);
        BehaviorLable.setFont(font);
        targetLable.setFont(font);
        edgeModeLable.setFont(font);
        //#endregion

        //#region Create number spinners
        JSpinner massSpinner = new JSpinner();
        JSpinner maxVelSpinner = new JSpinner();
        JSpinner maxForceSpinner = new JSpinner();
        JSpinner sizeSpinner = new JSpinner();
        
        //setting the model of the spinners to set their starting,min and max value and step length (for the spinner buttons)
        massSpinner.setModel(new SpinnerNumberModel(vehicle.mass, 0.5, 100, 0.2));
        maxVelSpinner.setModel(new SpinnerNumberModel(vehicle.maxVel, 0, 100, 1));
        maxForceSpinner.setModel(new SpinnerNumberModel(vehicle.maxForce, 0, 100, 0.1));
        sizeSpinner.setModel(new SpinnerNumberModel(vehicle.size, 0, 100, 1));

        // Apply changes to the vehicle object 
        massSpinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                vehicle.mass = (double) massSpinner.getValue();
            }
        });
        maxVelSpinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e){
                vehicle.maxVel = (double)maxVelSpinner.getValue();
            }
        });
        maxForceSpinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e){
                vehicle.maxForce = (double)maxForceSpinner.getValue();
            }
        });
        sizeSpinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e){
                vehicle.size = (int)sizeSpinner.getValue();
            }
        });
        //#endregion
        
        //#region Create combo boxes
        JComboBox<String> behaviorSelector = new JComboBox<>(behaviors);
        JComboBox<String> targetSelector = new JComboBox<>();
        JComboBox<String> edgeModeSelector = new JComboBox<>(edgeModes);
        behaviorSelector.setSelectedItem(vehicle.behavior.toString());
        edgeModeSelector.setSelectedItem(vehicle.edgeMode.toString());
        targetSelectorsList[vehiclesCount -1] = targetSelector;
        targetSelector.setPreferredSize(new Dimension(99, 24));
        
        // Update the combobox models for all target selectors  (every time a new vehicle in added)
        for (int i = 0; i < vehiclesCount; i++){
            String[] targetsArray = new String[vehiclesCount];
            targetsArray[0] = defaultTargetListText;
            boolean shiftIndex = false;
            for(int j = 1; j < vehiclesCount + 1; j++){  // skip adding the name of the current vehicle
                if (j == i+1){
                    shiftIndex = true;          // shift all next elements in the array by -1 (because this code skips 1 index in the array)
                    continue;
                }
                targetsArray[(shiftIndex)? j-1 : j] = "Vehicle " + j;
            }
            int selectedItemIndex = targetSelectorsList[i].getSelectedIndex(); // keep a backup of the curren selected item (setting an new model will reset the slected item)
            targetSelectorsList[i].setModel(new DefaultComboBoxModel<String>(targetsArray)); // Apply the updated targets list tom the combo box
            targetSelectorsList[i].setSelectedIndex(selectedItemIndex);
        }
        if (vehicle.target != null) { // if the vehicle object has a target set the combo box to that target
            targetSelector.setSelectedItem("Vehicle " + (Arrays.asList(vehicles).indexOf(vehicle.target) + 1));
        }else{
            targetSelector.setSelectedIndex(0);
        }

        UpdateTargetSelector(targetSelector, String.valueOf(behaviorSelector.getSelectedItem()));

        behaviorSelector.setFocusable(false);
        targetSelector.setFocusable(false);
        edgeModeSelector.setFocusable(false);
        behaviorSelector.addActionListener(e -> {
            String behaviorText = String.valueOf(behaviorSelector.getSelectedItem());
            UpdateTargetSelector(targetSelector, behaviorText);
            UpdateEdgeModeSelector(edgeModeSelector, behaviorText);
            vehicle.behavior = Vehicle.Behavior.valueOf(behaviorText);
        });
        targetSelector.addActionListener(e ->{
            int targetIndex = targetSelector.getSelectedIndex()-1;
            if(targetIndex <= -1){
                vehicle.target = null;
            } else{
                // increade the target index from the combo box by one if the vehicle index in
                // the vehicles array is less or equal to it, because the combo box for targets
                // doesn't include the vehicle that we are choosing a target for
                if(Arrays.asList(vehicles).indexOf(vehicle) <= targetIndex) {targetIndex++;}
                vehicle.target = vehicles[targetIndex];
            }
        });
        edgeModeSelector.addActionListener(e ->{
            vehicle.edgeMode = Vehicle.EdgeMode.valueOf(String.valueOf(edgeModeSelector.getSelectedItem()));
        });
        //#endregion
        
        //#region Create Text fields
        JTextField colorField = new JTextField(String.format("#%02X%02X%02X",vehicle.color.getRed(), vehicle.color.getGreen(), vehicle.color.getBlue()));
        colorField.setPreferredSize(new Dimension(75, 20));
        colorField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(KeyEvent e){
                String fieldText = colorField.getText();
                //Don't add the new character if the amount of characters >= 7 (max for hex, including the "#")
                if(fieldText.length() >= 7){
                    e.consume();
                }else{ // if it can add typed character add it to the locl variable (fieldText) to test the format 
                    fieldText += e.getKeyChar();
                }
                if(fieldText.length() == 7 && fieldText.substring(0, 1).equals("#")){
                    try {  // try to decode the hex format
                        vehicle.color = Color.decode(fieldText);
                        colorField.setBorder(BorderFactory.createLineBorder(Color.gray));
                        return;
                    } catch (Exception exception) { // if the hex format is invalid keep the old color
                        // do nothing
                    }
                }
                colorField.setBorder(BorderFactory.createLineBorder(Color.RED));    // this indicates to the use that the given format is invalid
            }
        });
        //#endregion



        //#region Add all the components to the vPanel
        vPanel.add(massLable);
        vPanel.add(massSpinner);
        vPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        vPanel.add(sizeLable);
        vPanel.add(sizeSpinner);
        vPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        vPanel.add(maxVelLable);
        vPanel.add(maxVelSpinner);
        vPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        vPanel.add(maxForceLable);
        vPanel.add(maxForceSpinner);
        vPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        vPanel.add(BehaviorLable);
        vPanel.add(behaviorSelector);
        vPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        vPanel.add(targetLable);
        vPanel.add(targetSelector);
        vPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        vPanel.add(edgeModeLable);
        vPanel.add(edgeModeSelector);
        vPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        vPanel.add(ColorLable);
        vPanel.add(colorField);
        vehiclesContainer.add(vPanel);
        setVisible(true);
        //#endregion
        startButton.setEnabled(true); // enable the start button when adding a new vehicle
    }
    
    /**
     * Enables and Disables the target selector object based on the selected
     * behavior (e.g Wander = disabled)
     * 
     * @param targetSelector the JcomboBox object that selects the target
     * @param behavior       the selected behavior
     */
    private void UpdateTargetSelector(JComboBox<String> targetSelector, String behavior){
        if(behavior == "Wander"){
            targetSelector.setEnabled(false);
            targetSelector.setSelectedIndex(0);
        }else{
            targetSelector.setEnabled(true);
        }
    }    
    
    /**
     * Enables and Disables the edge mode selector object based on the selected
     * behavior (e.g Flee, Evade = disabled)
     * 
     * @param edgeModeSelector the JcomboBox object that selects the edge mode
     * @param behavior       the selected behavior
     */
    private void UpdateEdgeModeSelector(JComboBox<String> edgeModeSelector, String behavior){
        if(behavior == "Flee" || behavior == "Evade"){
            edgeModeSelector.setEnabled(false);
            edgeModeSelector.setSelectedItem("Bounce");;
        }else{
            edgeModeSelector.setEnabled(true);
        }
    }
}