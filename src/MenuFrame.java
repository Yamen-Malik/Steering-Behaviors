import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JOptionPane;

import java.awt.Component;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MenuFrame extends JFrame{
    int vehiclesCount = 0;
    int maxVehicles = 50;
    JScrollPane vehiclesScrollPane;
    JPanel vehiclesContainer;
    SpringLayout vehiclesLayout;
    Border defaultBorder;
    Vehicle[] vehicles = new Vehicle[maxVehicles];
    String[] behaviors; 
    String[] edgeModes; 
    String[] pathModes; 
    JComboBox<String>[] targetSelectorsList = new JComboBox[maxVehicles];
    String defaultTargetListText = "None";  // or "Select target"
    JButton startButton;
    final int VPANEL_WIDTH = 400;
    final int VPANEL_HEIGHT = 200;
    final int VPANEL_MARGIN = 5;
    int initialWidth = 1100;
    int initialHeight = 700;
    
    public MenuFrame(){
        setSize(initialWidth, initialHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Steering Behaviors Menu");
        setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5));
        
        defaultBorder = BorderFactory.createLineBorder(Color.lightGray, 3, true);
       
        vehiclesLayout = new SpringLayout();
        vehiclesContainer = new JPanel(vehiclesLayout);
        vehiclesScrollPane = new JScrollPane(vehiclesContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        vehiclesScrollPane.setBorder(new TitledBorder(defaultBorder, "Vehicles"));
        vehiclesScrollPane.setPreferredSize(new Dimension(getWidth(), getHeight() - 90));
        vehiclesScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        JButton addButton = new JButton("Add vehicle");
        startButton = new JButton("Start");
        JButton addRandomButton = new JButton("Add Random vehicle");
        
        addButton.setFocusable(false);
        addRandomButton.setFocusable(false);
        startButton.setFocusable(false);
        startButton.setEnabled(false); // diable the start button until we add vehicles
        addButton.addActionListener(e -> AddVehicle());
        addRandomButton.addActionListener(e -> AddRandomVehicle());
        startButton.addActionListener(e -> {
            setVisible(false);
            App.Start(vehicles);
        });

        add(vehiclesScrollPane);
        add(addButton);
        add(addRandomButton);
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
        pathModes = new String[Vehicle.PathMode.values().length];
        for(int i = 0; i < pathModes.length; i++){
            pathModes[i] = Vehicle.PathMode.values()[i].toString();
        }

        //Update the vehiclesContainer when the frame is resized
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                UpdateVehiclesContainer();
            }
        });
        addWindowStateListener(new WindowStateListener(){
            public void windowStateChanged(WindowEvent e) {
                UpdateVehiclesContainer();
            }
        });

        // Add default vehicle
        AddVehicle();
    }
    
    /**Adds a vehicle with random parameters*/ 
    private void AddRandomVehicle(){
        Vehicle vehicle = new Vehicle(0, 0);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        vehicle.mass = r.nextInt(1,100);
        vehicle.size = r.nextInt(1,100);
        vehicle.maxVel = r.nextInt(1,100);
        vehicle.maxForce = r.nextInt(1,100);
        vehicle.pathLength = r.nextInt(1,100);
        vehicle.behavior = Vehicle.Behavior.values()[r.nextInt(Vehicle.Behavior.values().length)];
        vehicle.pathMode = Vehicle.PathMode.values()[r.nextInt(Vehicle.PathMode.values().length)];
        vehicle.color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        if(vehicle.behavior != Vehicle.Behavior.Wander){
            vehicle.target = vehicles[r.nextInt(vehiclesCount)];
        }
        if(vehicle.behavior != Vehicle.Behavior.Flee && vehicle.behavior != Vehicle.Behavior.Evade){
            vehicle.edgeMode = Vehicle.EdgeMode.values()[r.nextInt(Vehicle.EdgeMode.values().length)];
        }
        
        AddVehicle(vehicle);
    }
    
    // The tow ways of calling AddVehicle gives me the flexibility to add vehicle presets
    private void AddVehicle() {
        AddVehicle(new Vehicle(0, 0));
    }
    
    private void AddVehicle(Vehicle vehicle){
        if(vehiclesCount == maxVehicles){
            JOptionPane.showMessageDialog(null, "You reached the maximum number of vehicles");
            return;
        }
        vehiclesCount++;
        vehicles[vehiclesCount -1]  = vehicle;
        
        JPanel vPanel = new JPanel(new GridBagLayout());
        vPanel.setPreferredSize(new Dimension(VPANEL_WIDTH, VPANEL_HEIGHT));
        vPanel.setBorder(new TitledBorder(defaultBorder, "Vehicle " + vehiclesCount));
        
        //#region create lables and set their font
        JLabel massLable = new JLabel("Mass:");
        JLabel maxVelLable = new JLabel("Max Velocity:");
        JLabel maxForceLable = new JLabel("Max Force: ");
        JLabel sizeLable = new JLabel("size:");
        JLabel colorLable = new JLabel("Color [Hex]:");
        JLabel BehaviorLable = new JLabel("Behavior:");
        JLabel targetLable = new JLabel("Target:");
        JLabel edgeModeLable = new JLabel("Edge Mode:");
        JLabel pathModeLable = new JLabel("Path Mode:");
        JLabel pathLengthLable = new JLabel("Path Length:");
        
        Font font = new Font("Dialog", Font.PLAIN, 12);
        massLable.setFont(font);
        maxVelLable.setFont(font);
        maxForceLable.setFont(font);
        sizeLable.setFont(font);
        colorLable.setFont(font);
        BehaviorLable.setFont(font);
        targetLable.setFont(font);
        edgeModeLable.setFont(font);
        pathModeLable.setFont(font);
        pathLengthLable.setFont(font);

        //#endregion

        //#region Create number spinners
        JSpinner massSpinner = new JSpinner();
        JSpinner maxVelSpinner = new JSpinner();
        JSpinner maxForceSpinner = new JSpinner();
        JSpinner sizeSpinner = new JSpinner();
        JSpinner pathLengthSpinner = new JSpinner();
        
        //setting the model of the spinners to set their starting,min and max value and step length (for the spinner buttons)
        massSpinner.setModel(new SpinnerNumberModel(vehicle.mass, 0.5, 100, 0.2));
        maxVelSpinner.setModel(new SpinnerNumberModel(vehicle.maxVel, 0, 100, 1));
        maxForceSpinner.setModel(new SpinnerNumberModel(vehicle.maxForce, 0, 100, 0.1));
        sizeSpinner.setModel(new SpinnerNumberModel(vehicle.size, 0, 100, 1));
        pathLengthSpinner.setModel(new SpinnerNumberModel(vehicle.pathLength, 0, 100, 1));

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
        pathLengthSpinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e){
                vehicle.pathLength = (int)pathLengthSpinner.getValue();
            }
        });
        //#endregion
        
        //#region Create combo boxes
        JComboBox<String> behaviorSelector = new JComboBox<>(behaviors);
        JComboBox<String> targetSelector = new JComboBox<>();
        JComboBox<String> edgeModeSelector = new JComboBox<>(edgeModes);
        JComboBox<String> pathModeSelector = new JComboBox<>(pathModes);
        behaviorSelector.setSelectedItem(vehicle.behavior.toString());
        edgeModeSelector.setSelectedItem(vehicle.edgeMode.toString());
        pathModeSelector.setSelectedItem(vehicle.pathMode.toString());
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
        pathModeSelector.setFocusable(false);
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
        pathModeSelector.addActionListener(e ->{
            vehicle.pathMode = Vehicle.PathMode.valueOf(String.valueOf(pathModeSelector.getSelectedItem()));
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
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.weightx = 20;
        c.weighty = 5;
        
        // First column
        c.gridx = 0;
        c.gridy = 0;
        vPanel.add(massLable,c);
        c.gridy = 1;
        vPanel.add(sizeLable,c);
        c.gridy = 2;
        vPanel.add(maxVelLable,c);
        c.gridy = 3;
        vPanel.add(maxForceLable,c);
        c.gridy = 4;
        vPanel.add(pathLengthLable,c);
        
        // Second column
        c.gridx = 1;
        c.gridy = 0;
        vPanel.add(massSpinner,c);
        c.gridy = 1;
        vPanel.add(sizeSpinner,c);
        c.gridy = 2;
        vPanel.add(maxVelSpinner,c);
        c.gridy = 3;
        vPanel.add(maxForceSpinner,c);
        c.gridy = 4;
        vPanel.add(pathLengthSpinner,c);        
   
        // Third column
        c.insets = new Insets(0,30,0,0);
   
        c.gridx = 2;
        c.gridy = 0;
        vPanel.add(pathModeLable,c);
        c.gridy = 1;
        vPanel.add(BehaviorLable,c);
        c.gridy = 2;
        vPanel.add(targetLable,c);
        c.gridy = 3;
        vPanel.add(edgeModeLable,c);
        c.gridy = 4;
        vPanel.add(colorLable,c);        
        
        // Fourth column
        c.insets = new Insets(0,0,0,0);

        c.gridx = 3;
        c.gridy = 0;
        vPanel.add(pathModeSelector,c);
        c.gridy = 1;
        vPanel.add(behaviorSelector,c);
        c.gridy = 2;
        vPanel.add(targetSelector,c);
        c.gridy = 3;
        vPanel.add(edgeModeSelector,c);
        c.gridy = 4;
        vPanel.add(colorField,c);

        vehiclesContainer.add(vPanel);
        //#endregion
        
        //Arrange the vPanel to fit in the screen next to the other vPanels
        int vehiclesPerRow = (int)(vehiclesContainer.getWidth()/(VPANEL_WIDTH+VPANEL_MARGIN));
        ArrangeVPanel(vehiclesCount-1, vehiclesPerRow);
        
        // Change the size of the vehiclesContainer according to the amount of vehicles
        vehiclesContainer.setPreferredSize(new Dimension(vehiclesContainer.getWidth(), (int)java.lang.Math.ceil((float)vehiclesCount/vehiclesPerRow) * (VPANEL_HEIGHT+VPANEL_MARGIN)));
        
        // Update the scrollPane view to refresh the scrollbar
        vehiclesScrollPane.getViewport().setView(vehiclesContainer);
        
        startButton.setEnabled(true); // enable the start button when adding a new vehicle
        setVisible(true);
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

    private void UpdateVehiclesContainer(){
        setVisible(true);
        vehiclesScrollPane.setPreferredSize(new Dimension(getWidth(), getHeight() - 90));
        vehiclesContainer.setSize(getWidth(), vehiclesContainer.getHeight());
        int vehiclesPerRow = (int)(vehiclesContainer.getWidth()/(VPANEL_WIDTH+VPANEL_MARGIN));
        vehiclesContainer.setPreferredSize(new Dimension(getWidth(), (int)java.lang.Math.ceil((float)vehiclesCount/vehiclesPerRow) * (VPANEL_HEIGHT+VPANEL_MARGIN)));
       
      for (int i=0; i < vehiclesCount; i++){
            ArrangeVPanel(i, vehiclesPerRow);
        }
    } 
    
    private void ArrangeVPanel(int index, int vehiclesPerRow){
        if (index > 0){
            Component vPanel = vehiclesContainer.getComponent(index);
            Component last_vPanel = vehiclesContainer.getComponent(index-1);
            if ( (index) % vehiclesPerRow == 0){
                vehiclesLayout.putConstraint(SpringLayout.NORTH, vPanel, VPANEL_MARGIN, SpringLayout.SOUTH, last_vPanel);
                vehiclesLayout.putConstraint(SpringLayout.WEST, vPanel, 0, SpringLayout.WEST, vehiclesContainer);
                
            } else {
                vehiclesLayout.putConstraint(SpringLayout.WEST, vPanel, VPANEL_MARGIN, SpringLayout.EAST, last_vPanel);
                vehiclesLayout.putConstraint(SpringLayout.NORTH, vPanel, 0, SpringLayout.NORTH, last_vPanel);
            }
        }
    }
}