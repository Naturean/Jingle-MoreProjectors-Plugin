package com.naturean.moreprojectors.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.naturean.moreprojectors.MoreProjectors;
import com.naturean.moreprojectors.hotkey.ProjectorSettingHotkey;
import com.naturean.moreprojectors.instance.InstanceStateUtils;
import com.naturean.moreprojectors.projector.Projector;
import org.apache.commons.lang3.math.NumberUtils;
import xyz.duncanruns.jingle.instance.InstanceState;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashSet;

public class EditProjectorDialog extends JDialog {
    private JPanel editPanel;
    private JCheckBox alwaysActivateCheckBox;
    private JButton manageHotkeysButton;
    private JCheckBox borderlessCheckBox;
    private JCheckBox topCheckBox;
    private JCheckBox minimizeCheckBox;
    private JTextField projectorPosX;
    private JTextField projectorPosY;
    private JTextField projectorSizeW;
    private JTextField projectorSizeH;
    private JButton OKButton;
    private JButton cancelButton;
    private JLabel projectorPosLabel;
    private JLabel projectorSizeLabel;
    private JPanel buttonPanel;
    private JPanel mainPanel;
    private JPanel positionPanel;
    private JPanel sizePanel;
    private JCheckBox autoOpenCheckBox;
    private JPanel namePanel;
    private JTextField nameTextField;
    private JLabel nameLabel;
    private JLabel instanceStateLabel;
    private JPanel instanceStatePanel;
    private JButton instanceStateButton;
    private JButton inWorldStateButton;
    private JPanel inWorldStatePanel;
    private JLabel inWorldStateLabel;
    private JPanel allowedStatesPanel;
    private JPanel GeometryPanel;
    private JCheckBox inactivateWhenOtherCheckBox;

    public boolean cancelled;

    public String name;
    public boolean autoOpen;
    public boolean alwaysActivate;
    public boolean shouldBorderless;
    public boolean topWhenActive;
    public boolean minimizeWhenInactive;
    public boolean inactivateWhenOther;
    public int[] geometry;
    public LinkedHashSet<ProjectorSettingHotkey> hotkeys;

    public LinkedHashSet<InstanceState> allowedInstanceStates;
    public LinkedHashSet<InstanceState.InWorldState> allowedInWorldStates;

    public EditProjectorDialog(JFrame owner, Projector projector) {
        super(owner);

        this.name = projector.name;
        this.autoOpen = projector.settings.autoOpen;
        this.alwaysActivate = projector.settings.alwaysActivate;
        this.shouldBorderless = projector.settings.shouldBorderless;
        this.topWhenActive = projector.settings.topWhenActive;
        this.minimizeWhenInactive = projector.settings.minimizeWhenInactive;
        this.inactivateWhenOther = projector.settings.inactivateWhenOther;
        this.geometry = projector.settings.geometry;
        this.hotkeys = projector.settings.hotkeys;
        this.allowedInstanceStates = projector.settings.allowedInstanceStates;
        this.allowedInWorldStates = projector.settings.allowedInWorldStates;

        this.$$$setupUI$$$();
        this.setContentPane(this.editPanel);

        // To avoid operation on owner when dialog showing
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);

        // Default button when dialog shows
        this.getRootPane().setDefaultButton(this.OKButton);

        this.setTitle("Edit Projector");

        manageHotkeysButton.addActionListener(e -> EditProjectorDialog.this.onManageHotkeys());
        OKButton.addActionListener(e -> EditProjectorDialog.this.onOK());
        cancelButton.addActionListener(e -> EditProjectorDialog.this.onCancel());
        alwaysActivateCheckBox.addActionListener(e -> EditProjectorDialog.this.onAlwaysActivateCheck());

        KeyAdapter onlyDigit = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        };
        projectorPosX.addKeyListener(onlyDigit);
        projectorPosY.addKeyListener(onlyDigit);
        projectorSizeW.addKeyListener(onlyDigit);
        projectorSizeH.addKeyListener(onlyDigit);

        // Click cross icon for cancel.
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                EditProjectorDialog.this.onCancel();
            }
        });


        this.finalizeComponents();
        this.pack();

        // Set location at the center of owner
        this.setLocationRelativeTo(owner);
    }

    private void finalizeComponents() {
        this.nameTextField.setText(this.name);
        this.autoOpenCheckBox.setSelected(this.autoOpen);
        this.alwaysActivateCheckBox.setSelected(this.alwaysActivate);
        this.borderlessCheckBox.setSelected(this.shouldBorderless);
        this.topCheckBox.setSelected(this.topWhenActive);
        this.minimizeCheckBox.setSelected(this.minimizeWhenInactive);
        this.inactivateWhenOtherCheckBox.setSelected(this.inactivateWhenOther);

        if (geometry != null && geometry.length != 0) {
            this.projectorPosX.setText(String.valueOf(geometry[0]));
            this.projectorPosY.setText(String.valueOf(geometry[1]));
            this.projectorSizeW.setText(String.valueOf(geometry[2]));
            this.projectorSizeH.setText(String.valueOf(geometry[3]));
        }

        this.instanceStateButton.setText(InstanceStateUtils.formatInstanceStates(this.allowedInstanceStates));
        if (this.instanceStateButton.getText().isEmpty()) this.instanceStateButton.setText("None");
        this.instanceStateButton.addActionListener(e -> {
            synchronized (MoreProjectors.class) {
                InstanceStatesDialog dialog = new InstanceStatesDialog(this, this.allowedInstanceStates);
                dialog.setVisible(true);
                if (dialog.cancelled) return;

                this.allowedInstanceStates = dialog.allowedInstanceStates;
                this.instanceStateButton.setText(InstanceStateUtils.formatInstanceStates(this.allowedInstanceStates));
                if (this.instanceStateButton.getText().isEmpty()) this.instanceStateButton.setText("None");

                inWorldStateButton.setEnabled(this.allowedInstanceStates.contains(InstanceState.INWORLD));

                this.pack();
            }
        });

        inWorldStateButton.setEnabled(this.allowedInstanceStates.contains(InstanceState.INWORLD));
        this.inWorldStateButton.setText(InstanceStateUtils.formatInWorldStates(this.allowedInWorldStates));
        if (this.inWorldStateButton.getText().isEmpty()) this.inWorldStateButton.setText("None");
        this.inWorldStateButton.addActionListener(e -> {
            synchronized (MoreProjectors.class) {
                InWorldStatesDialog dialog = new InWorldStatesDialog(this, this.allowedInWorldStates);
                dialog.setVisible(true);
                if (dialog.cancelled) return;

                this.allowedInWorldStates = dialog.allowedInWorldStates;
                this.inWorldStateButton.setText(InstanceStateUtils.formatInWorldStates(this.allowedInWorldStates));
                if (this.inWorldStateButton.getText().isEmpty()) this.inWorldStateButton.setText("None");

                this.pack();
            }
        });

        this.onAlwaysActivateCheck();
    }

    private void onManageHotkeys() {
        synchronized (MoreProjectors.class) {
            ManageHotkeysDialog dialog = new ManageHotkeysDialog(this, this.hotkeys);
            dialog.setVisible(true);
            if (dialog.cancelled) return;

            this.hotkeys = dialog.hotkeys;

            this.pack();
        }
    }

    private void onOK() {
        this.name = this.nameTextField.getText();
        assert name != null;

        this.autoOpen = this.autoOpenCheckBox.isSelected();
        this.alwaysActivate = this.alwaysActivateCheckBox.isSelected();
        this.shouldBorderless = this.borderlessCheckBox.isSelected();
        this.topWhenActive = this.topCheckBox.isSelected();
        this.minimizeWhenInactive = this.minimizeCheckBox.isSelected();
        this.inactivateWhenOther = this.inactivateWhenOtherCheckBox.isSelected();

        this.geometry = new int[]{
                NumberUtils.toInt(this.projectorPosX.getText(), 0),
                NumberUtils.toInt(this.projectorPosY.getText(), 0),
                NumberUtils.toInt(this.projectorSizeW.getText(), 0),
                NumberUtils.toInt(this.projectorSizeH.getText(), 0)
        };

        this.dispose();
    }

    private void onCancel() {
        this.cancelled = true;
        this.dispose();
    }

    private void onAlwaysActivateCheck() {
        this.manageHotkeysButton.setEnabled(!this.alwaysActivateCheckBox.isSelected());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        editPanel = new JPanel();
        editPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 10, 10, 10), -1, -1));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        editPanel.add(buttonPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        buttonPanel.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        OKButton = new JButton();
        OKButton.setText("OK");
        buttonPanel.add(OKButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(10, 1, new Insets(0, 5, 5, 5), -1, -1));
        editPanel.add(mainPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        alwaysActivateCheckBox = new JCheckBox();
        alwaysActivateCheckBox.setText("Always activate");
        mainPanel.add(alwaysActivateCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        borderlessCheckBox = new JCheckBox();
        borderlessCheckBox.setText("Borderless");
        mainPanel.add(borderlessCheckBox, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        topCheckBox = new JCheckBox();
        topCheckBox.setText("Top projector when active");
        mainPanel.add(topCheckBox, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minimizeCheckBox = new JCheckBox();
        minimizeCheckBox.setText("Minimize projector when inactive");
        mainPanel.add(minimizeCheckBox, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoOpenCheckBox = new JCheckBox();
        autoOpenCheckBox.setText("Open projector automatically");
        mainPanel.add(autoOpenCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        namePanel = new JPanel();
        namePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(namePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        nameTextField = new JTextField();
        namePanel.add(nameTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nameLabel = new JLabel();
        nameLabel.setText("Name");
        namePanel.add(nameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(65, -1), new Dimension(65, -1), new Dimension(65, -1), 0, false));
        allowedStatesPanel = new JPanel();
        allowedStatesPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.add(allowedStatesPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        allowedStatesPanel.setBorder(BorderFactory.createTitledBorder(null, "Activate only when", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        instanceStatePanel = new JPanel();
        instanceStatePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        allowedStatesPanel.add(instanceStatePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        instanceStateButton = new JButton();
        instanceStateButton.setText("");
        instanceStatePanel.add(instanceStateButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        instanceStateLabel = new JLabel();
        instanceStateLabel.setText("Instance states");
        instanceStatePanel.add(instanceStateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(90, -1), null, 0, false));
        inWorldStatePanel = new JPanel();
        inWorldStatePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        allowedStatesPanel.add(inWorldStatePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        inWorldStateButton = new JButton();
        inWorldStateButton.setText("");
        inWorldStatePanel.add(inWorldStateButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inWorldStateLabel = new JLabel();
        inWorldStateLabel.setText("In-world states");
        inWorldStatePanel.add(inWorldStateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(90, -1), null, 0, false));
        GeometryPanel = new JPanel();
        GeometryPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.add(GeometryPanel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        GeometryPanel.setBorder(BorderFactory.createTitledBorder(null, "Geometry", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        positionPanel = new JPanel();
        positionPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        GeometryPanel.add(positionPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        projectorPosY = new JTextField();
        positionPanel.add(projectorPosY, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectorPosX = new JTextField();
        positionPanel.add(projectorPosX, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectorPosLabel = new JLabel();
        projectorPosLabel.setHorizontalAlignment(10);
        projectorPosLabel.setText("Position:");
        positionPanel.add(projectorPosLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(65, -1), null, 0, false));
        sizePanel = new JPanel();
        sizePanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        GeometryPanel.add(sizePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        projectorSizeH = new JTextField();
        sizePanel.add(projectorSizeH, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectorSizeW = new JTextField();
        sizePanel.add(projectorSizeW, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectorSizeLabel = new JLabel();
        projectorSizeLabel.setHorizontalAlignment(10);
        projectorSizeLabel.setHorizontalTextPosition(11);
        projectorSizeLabel.setText("Size: ");
        sizePanel.add(projectorSizeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(65, -1), null, 0, false));
        inactivateWhenOtherCheckBox = new JCheckBox();
        inactivateWhenOtherCheckBox.setText("Inactivate when different hotkeys are activated");
        mainPanel.add(inactivateWhenOtherCheckBox, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manageHotkeysButton = new JButton();
        manageHotkeysButton.setText("Manage hotkeys");
        mainPanel.add(manageHotkeysButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return editPanel;
    }

}
