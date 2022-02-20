import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.KeyEvent;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MenuFrame extends JFrame {
    int vehiclesCount = 0;
    int deletedVehiclesCount = 0;
    final int MAX_VEHICLES = 50;
    final JScrollPane vehiclesScrollPane;
    final JPanel vehiclesContainer;
    final Border DEFAULT_BORDER;
    Vehicle[] vehicles = new Vehicle[MAX_VEHICLES];
    String[] behaviors = new String[Vehicle.Behavior.values().length];
    String[] edgeModes = new String[Vehicle.EdgeMode.values().length];
    String[] pathModes = new String[Vehicle.PathMode.values().length];
    String[] targets = new String[MAX_VEHICLES + 1];
    JComboBox<String>[] targetSelectors = new JComboBox[MAX_VEHICLES];
    final String DEFAULT_TARGET = "None";
    final int VPANEL_WIDTH = 400;
    final int VPANEL_HEIGHT = 200;
    final int VPANEL_MARGIN = 5;
    final int INITIAL_WIDTH = 900;
    final int INITIAL_HEIGHT = 700;

    public MenuFrame() {
        setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        setMinimumSize(new Dimension(VPANEL_WIDTH + 80, VPANEL_HEIGHT + 130));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Steering Behaviors Menu");

        DEFAULT_BORDER = BorderFactory.createLineBorder(Color.lightGray, 3, true);
        targets[0] = DEFAULT_TARGET;

        vehiclesContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, VPANEL_MARGIN, VPANEL_MARGIN));
        vehiclesScrollPane = new JScrollPane(vehiclesContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        vehiclesScrollPane.setBorder(new TitledBorder(DEFAULT_BORDER, "Vehicles"));
        vehiclesScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        JButton addButton = new JButton("Add Vehicle");
        JButton startButton = new JButton("Start");
        JButton addRandomButton = new JButton("Add Random Vehicle");

        addButton.setFocusable(false);
        addRandomButton.setFocusable(false);
        startButton.setFocusable(false);
        addButton.addActionListener(e -> AddVehicle());
        addRandomButton.addActionListener(e -> AddRandomVehicle());
        startButton.addActionListener(e -> {
            if (vehiclesCount <= 0)
                return;
            setVisible(false);
            App.Start(Arrays.copyOf(vehicles, vehiclesCount));
        });

        add(vehiclesScrollPane);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        buttonsPanel.add(addButton);
        buttonsPanel.add(addRandomButton);
        buttonsPanel.add(startButton);
        add(buttonsPanel, BorderLayout.PAGE_END);

        // Setup constant arrays for the combo boxes
        for (int i = 0; i < behaviors.length; i++) {
            behaviors[i] = Vehicle.Behavior.values()[i].toString();
        }
        for (int i = 0; i < edgeModes.length; i++) {
            edgeModes[i] = Vehicle.EdgeMode.values()[i].toString();
        }
        for (int i = 0; i < pathModes.length; i++) {
            pathModes[i] = Vehicle.PathMode.values()[i].toString();
        }

        // Update the vehiclesContainer when the frame is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                UpdateVehiclesContainer();
            }
        });

        // Add default vehicle
        AddVehicle();

        setVisible(true);
    }

    /** Adds a vehicle with random parameters */
    private void AddRandomVehicle() {
        Vehicle vehicle = new Vehicle();
        ThreadLocalRandom r = ThreadLocalRandom.current();
        vehicle.mass = r.nextInt(1, 100);
        vehicle.size = r.nextInt(1, 100);
        vehicle.maxVel = r.nextInt(1, 100);
        vehicle.maxForce = r.nextInt(1, 100);
        vehicle.pathLength = r.nextInt(1, 100);
        vehicle.behavior = Vehicle.Behavior.values()[r.nextInt(Vehicle.Behavior.values().length)];
        vehicle.pathMode = Vehicle.PathMode.values()[r.nextInt(Vehicle.PathMode.values().length)];
        vehicle.color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        if (vehicle.behavior != Vehicle.Behavior.Wander && vehiclesCount > 0) {
            vehicle.target = vehicles[r.nextInt(vehiclesCount)];
        }
        if (vehicle.behavior != Vehicle.Behavior.Flee && vehicle.behavior != Vehicle.Behavior.Evade) {
            vehicle.edgeMode = Vehicle.EdgeMode.values()[r.nextInt(Vehicle.EdgeMode.values().length)];
        }

        AddVehicle(vehicle);
    }

    // The tow ways of calling AddVehicle gives me the flexibility to add vehicle
    // presets

    private void AddVehicle() {
        AddVehicle(new Vehicle());
    }

    private void AddVehicle(Vehicle vehicle) {
        if (vehiclesCount == MAX_VEHICLES) {
            JOptionPane.showMessageDialog(null, "You reached the maximum number of vehicles");
            return;
        }
        vehiclesCount++;
        vehicles[vehiclesCount - 1] = vehicle;
        JPanel vPanel = new JPanel(new GridBagLayout());
        vPanel.setPreferredSize(new Dimension(VPANEL_WIDTH, VPANEL_HEIGHT));
        vPanel.setBorder(new TitledBorder(DEFAULT_BORDER, "Vehicle " + (vehiclesCount + deletedVehiclesCount)));

        // #region create lables and set their font
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

        // #endregion

        // #region Create number spinners
        JSpinner massSpinner = new JSpinner();
        JSpinner maxVelSpinner = new JSpinner();
        JSpinner maxForceSpinner = new JSpinner();
        JSpinner sizeSpinner = new JSpinner();
        JSpinner pathLengthSpinner = new JSpinner();

        // setting the model of the spinners to set their starting, min, max and
        // step length (for the spinner buttons)
        massSpinner.setModel(new SpinnerNumberModel(vehicle.mass, 0.5, 100, 0.2));
        maxVelSpinner.setModel(new SpinnerNumberModel(vehicle.maxVel, 0, 100, 1));
        maxForceSpinner.setModel(new SpinnerNumberModel(vehicle.maxForce, 0, 100, 0.1));
        sizeSpinner.setModel(new SpinnerNumberModel(vehicle.size, 0, 100, 1));
        pathLengthSpinner.setModel(new SpinnerNumberModel(vehicle.pathLength, 0, 100, 1));

        // Apply changes to the vehicle object
        massSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                vehicle.mass = (double) massSpinner.getValue();
            }
        });
        maxVelSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                vehicle.maxVel = (double) maxVelSpinner.getValue();
            }
        });
        maxForceSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                vehicle.maxForce = (double) maxForceSpinner.getValue();
            }
        });
        sizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                vehicle.size = (int) sizeSpinner.getValue();
            }
        });
        pathLengthSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                vehicle.pathLength = (int) pathLengthSpinner.getValue();
            }
        });
        // #endregion

        // #region Create combo boxes
        JComboBox<String> behaviorSelector = new JComboBox<>(behaviors);
        JComboBox<String> targetSelector = new JComboBox<>();
        JComboBox<String> edgeModeSelector = new JComboBox<>(edgeModes);
        JComboBox<String> pathModeSelector = new JComboBox<>(pathModes);
        behaviorSelector.setSelectedItem(vehicle.behavior.toString());
        edgeModeSelector.setSelectedItem(vehicle.edgeMode.toString());
        pathModeSelector.setSelectedItem(vehicle.pathMode.toString());
        targetSelectors[vehiclesCount - 1] = targetSelector;
        targetSelector.setPreferredSize(new Dimension(99, 24));

        // region Update the combobox models for all target selectors

        // Add the targets of this vehicle to its combo box
        for (int i = 0; i < vehiclesCount; i++) {
            if (targets[i] == "")
                break;

            targetSelector.addItem(targets[i]);
        }
        targets[vehiclesCount] = "Vehicle " + (vehiclesCount + deletedVehiclesCount);

        // Add the new vehicle to the other combo boxes
        for (int i = 0; i < vehiclesCount - 1; i++) {
            targetSelectors[i].addItem(targets[vehiclesCount]);
        }

        // Set the combo box to the target of the vehicle
        if (vehicle.target != null) {
            targetSelector.setSelectedIndex(Arrays.asList(vehicles).indexOf(vehicle.target) + 1);
        }
        // endregion

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
        targetSelector.addActionListener(e -> {
            int targetIndex = targetSelector.getSelectedIndex() - 1;
            if (targetIndex <= -1) {
                vehicle.target = null;
            } else {
                // increase the target index from the combo box by one if the vehicle index in
                // the vehicles array is less or equal to it, because the combo box for targets
                // doesn't include the vehicle that we are choosing a target for
                if (Arrays.asList(vehicles).indexOf(vehicle) <= targetIndex) {
                    targetIndex++;
                }
                vehicle.target = vehicles[targetIndex];
            }
        });
        edgeModeSelector.addActionListener(e -> {
            vehicle.edgeMode = Vehicle.EdgeMode.valueOf(String.valueOf(edgeModeSelector.getSelectedItem()));
        });
        pathModeSelector.addActionListener(e -> {
            vehicle.pathMode = Vehicle.PathMode.valueOf(String.valueOf(pathModeSelector.getSelectedItem()));
        });
        // #endregion

        // #region Create Text fields
        JTextField colorField = new JTextField(String.format("#%02X%02X%02X", vehicle.color.getRed(),
                vehicle.color.getGreen(), vehicle.color.getBlue()));
        colorField.setPreferredSize(new Dimension(75, 20));
        colorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String fieldText = colorField.getText();
                // Don't add the new character if the amount of characters >= 7 (max for hex,
                // including the "#")
                if (fieldText.length() >= 7) {
                    e.consume();
                } else {
                    // Add the character to the locl variable (fieldText) to test the format
                    fieldText += e.getKeyChar();
                }
                if (fieldText.length() == 7 && fieldText.substring(0, 1).equals("#")) {
                    // try to decode the hex format
                    // and if the hex format is invalid keep the old color
                    try {
                        vehicle.color = Color.decode(fieldText);
                        colorField.setBorder(BorderFactory.createLineBorder(Color.gray));
                        return;
                    } catch (Exception exception) {
                        // do nothing
                    }
                }
                // this indicates to the user that the given format is invalid
                colorField.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
        });
        // #endregion

        // Create the delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            try {
                DeleteVehicle(vehicle);
            } catch (Exception exc) {
                System.out.println(exc.toString());
                System.out.println("Vehcile might be already deleted");
            }
        });

        // #region Add all the components to the vPanel
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.weightx = 20;
        c.weighty = 5;

        // First column
        c.gridx = 0;
        c.gridy = 0;
        vPanel.add(massLable, c);
        c.gridy = 1;
        vPanel.add(sizeLable, c);
        c.gridy = 2;
        vPanel.add(maxVelLable, c);
        c.gridy = 3;
        vPanel.add(maxForceLable, c);
        c.gridy = 4;
        vPanel.add(pathLengthLable, c);

        // Second column
        c.gridx = 1;
        c.gridy = 0;
        vPanel.add(massSpinner, c);
        c.gridy = 1;
        vPanel.add(sizeSpinner, c);
        c.gridy = 2;
        vPanel.add(maxVelSpinner, c);
        c.gridy = 3;
        vPanel.add(maxForceSpinner, c);
        c.gridy = 4;
        vPanel.add(pathLengthSpinner, c);

        // Third column
        c.insets = new Insets(0, 30, 0, 0);

        c.gridx = 2;
        c.gridy = 0;
        vPanel.add(pathModeLable, c);
        c.gridy = 1;
        vPanel.add(BehaviorLable, c);
        c.gridy = 2;
        vPanel.add(targetLable, c);
        c.gridy = 3;
        vPanel.add(edgeModeLable, c);
        c.gridy = 4;
        vPanel.add(colorLable, c);

        // Fourth column
        c.insets = new Insets(0, 0, 0, 0);

        c.gridx = 3;
        c.gridy = 0;
        vPanel.add(pathModeSelector, c);
        c.gridy = 1;
        vPanel.add(behaviorSelector, c);
        c.gridy = 2;
        vPanel.add(targetSelector, c);
        c.gridy = 3;
        vPanel.add(edgeModeSelector, c);
        c.gridy = 4;
        vPanel.add(colorField, c);

        // Add the delete button to the bottom of the panel
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 4;
        vPanel.add(deleteButton, c);

        vehiclesContainer.add(vPanel);
        // #endregion

        // Change the size of the vehiclesContainer according to the amount of vehicles
        UpdateVehiclesContainer();
    }

    /**
     * Enables and Disables the target selector object based on the selected
     * behavior (e.g Wander = disabled)
     * 
     * @param targetSelector the JcomboBox object that selects the target
     * @param behavior       the selected behavior
     */
    private void UpdateTargetSelector(JComboBox<String> targetSelector, String behavior) {
        if (behavior == "Wander") {
            targetSelector.setEnabled(false);
            targetSelector.setSelectedIndex(0);
        } else {
            targetSelector.setEnabled(true);
        }
    }

    /**
     * Deletes the given vehicle from:
     * <ul>
     * <li>vehicles array</li>
     * <li>vehicles target Selectors</li>
     * <li>vehicles Container</li>
     * </ul>
     * <p>
     * then updates the vehicles Container
     * </p>
     * 
     * @param v : instance of Vehicle class
     */
    private void DeleteVehicle(Vehicle v) throws Exception {
        int index = Arrays.asList(vehicles).indexOf(v);
        if (index == -1) {
            throw new Exception("The given instance of Vehicle doesn't exist in the vehicles list");
        }
        for (int i = 0; i < vehiclesCount; i++) {
            if (i == index) {
                vehicles[i] = null;
                continue;
            }
            if (targetSelectors[i].getItemCount() > 1) {
                // The item index in the combo box list (Add 1 to account for the "None" item)
                int j = index + ((i > index) ? 1 : 0);
                if (targetSelectors[i].getSelectedIndex() == j) {
                    targetSelectors[i].setSelectedIndex(0);
                }
                targetSelectors[i].removeItemAt(j);
            }
            if (i > index) {
                // If this vehicle comes after the deleted one then shift it back in the list
                vehicles[i - 1] = vehicles[i];
                vehicles[i] = null;

                targetSelectors[i - 1] = targetSelectors[i];
                targetSelectors[i] = null;

                // i doesn't include the first item in targets -> "None"
                // therefore this vehicle index in the targets array is i+1
                targets[i] = targets[i + 1];
                targets[i + 1] = "";
            }
        }
        vehiclesCount--;
        deletedVehiclesCount++;
        vehiclesContainer.remove(index);
        UpdateVehiclesContainer();
        vehiclesContainer.repaint();

    }

    /**
     * Enables and Disables the edge mode selector object based on the selected
     * behavior (e.g Flee, Evade = disabled)
     * 
     * @param edgeModeSelector the JcomboBox object that selects the edge mode
     * @param behavior         the selected behavior
     */
    private void UpdateEdgeModeSelector(JComboBox<String> edgeModeSelector, String behavior) {
        if (behavior == "Flee" || behavior == "Evade") {
            edgeModeSelector.setEnabled(false);
            edgeModeSelector.setSelectedItem("Bounce");
            ;
        } else {
            edgeModeSelector.setEnabled(true);
        }
    }

    /**
     * <p>
     * Updates the vehiclesContainer size and layout.
     * 
     * <p>
     * This is needed when a new vehicle is added or when the window is resized
     */
    private void UpdateVehiclesContainer() {
        int vehiclesPerRow = (int) (vehiclesScrollPane.getWidth() / (VPANEL_WIDTH + VPANEL_MARGIN));
        vehiclesContainer.setPreferredSize(new Dimension(vehiclesScrollPane.getWidth(),
                (int) java.lang.Math.ceil((float) vehiclesCount / vehiclesPerRow) * (VPANEL_HEIGHT + VPANEL_MARGIN)));
        vehiclesContainer.revalidate();
    }
}