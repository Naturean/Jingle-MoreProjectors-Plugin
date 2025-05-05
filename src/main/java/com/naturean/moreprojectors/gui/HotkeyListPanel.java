package com.naturean.moreprojectors.gui;

import com.naturean.moreprojectors.MoreProjectors;
import com.naturean.moreprojectors.hotkey.ProjectorSettingHotkey;
import xyz.duncanruns.jingle.hotkey.Hotkey;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class HotkeyListPanel extends JPanel {
    private final JDialog owner;
    public LinkedHashSet<ProjectorSettingHotkey> hotkeys;

    public HotkeyListPanel(JDialog owner, LinkedHashSet<ProjectorSettingHotkey> hotkeys) {
        this.owner = owner;
        this.hotkeys = new LinkedHashSet<>(hotkeys);
        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.reload();
    }

    public void reload() {
        this.removeAll();

        GridBagConstraints constraints = new GridBagConstraints(
                GridBagConstraints.RELATIVE, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0
        );

        List<JComponent> componentsToAdd = new ArrayList<>();

        for(final ProjectorSettingHotkey hotkey: hotkeys) {
            JButton hotkeyButton = getHotkeyButton(hotkey);
            JCheckBox ignoreModifiersCheckBox = getIgnoreModifiersCheckBox(hotkey);
            JButton removeButton = getRemoveButton(hotkey);

            hotkeyButton.setMinimumSize(new Dimension(100, hotkeyButton.getPreferredSize().height));

            componentsToAdd.addAll(Arrays.asList(hotkeyButton, ignoreModifiersCheckBox, removeButton));
        }

        int componentIndex = 0;
        for (int i = 0; i < hotkeys.size(); i++) {
            constraints.gridy++;

            // Hotkey
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.add(componentsToAdd.get(componentIndex++), constraints.clone());

            // Ignore Modifiers
            constraints.fill = GridBagConstraints.CENTER;
            this.add(componentsToAdd.get(componentIndex++), constraints.clone());

            // Remove
            constraints.fill = GridBagConstraints.NONE;
            this.add(componentsToAdd.get(componentIndex++), constraints.clone());
        }

        constraints.gridy++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JButton addButton = getAddButton();
        this.add(addButton, constraints.clone());
        addButton.setMinimumSize(new Dimension(120, addButton.getPreferredSize().height));

        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.CENTER;
        this.add(new JLabel("Hotkey"), constraints.clone());
        this.add(new JLabel("<html>Ignore<br>Modifiers</html>"), constraints.clone());

        this.revalidate();
        this.repaint();
    }

    private JButton getAddButton() {
        JButton addButton = new JButton("+");
        addButton.addActionListener(e -> {
            synchronized (MoreProjectors.class) {
                if (!this.hotkeys.add(new ProjectorSettingHotkey())) {
                    JOptionPane.showMessageDialog(this.owner, "Please set previous keys before adding more!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
                this.reload();
                this.owner.pack();
            }
        });
        return addButton;
    }

    private JCheckBox getIgnoreModifiersCheckBox(ProjectorSettingHotkey hotkey) {
        JCheckBox ignoreModifiersCheckBox = new JCheckBox();
        ignoreModifiersCheckBox.setSelected(hotkey.isIgnoreModifiers());
        ignoreModifiersCheckBox.addActionListener(e -> {
            synchronized (MoreProjectors.class) {
                for (ProjectorSettingHotkey k: this.hotkeys) {
                    if (k.equals(hotkey)) {
                        k.setIgnoreModifiers(ignoreModifiersCheckBox.isSelected());
                    }
                }
            }
        });
        return ignoreModifiersCheckBox;
    }

    private JButton getHotkeyButton(ProjectorSettingHotkey hotkey) {
        JButton hotkeyButton = new JButton();
        hotkeyButton.setText(Hotkey.formatKeys(hotkey.getKeys()));
        if (hotkeyButton.getText().isEmpty()) hotkeyButton.setText("None");
        hotkeyButton.addActionListener(e -> {
            synchronized (this) {
                hotkeyButton.setText("...");
                hotkeyButton.setEnabled(false);
                Hotkey.onNextHotkey(() -> this.owner.isVisible() && MoreProjectors.isRunning(), h -> {
                    synchronized (this) {
                        hotkey.setKeys(h.getKeys());
                        this.hotkeys.forEach(k -> {
                            if (k != hotkey && Objects.equals(k.getKeys(), hotkey.getKeys())) {
                                JOptionPane.showMessageDialog(this.owner, "Keys already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                                hotkey.setKeys(Collections.emptyList());
                            }
                        });
                        hotkeyButton.setText(Hotkey.formatKeys(hotkey.getKeys()));
                        if (hotkeyButton.getText().isEmpty()) hotkeyButton.setText("None");
                        hotkeyButton.setEnabled(true);
                        this.owner.pack();
                    }
                });
            }
        });
        return hotkeyButton;
    }

    private JButton getRemoveButton(ProjectorSettingHotkey hotkey) {
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener( e -> {
            synchronized (MoreProjectors.class) {
                this.hotkeys.removeIf(k -> k.equals(hotkey));
                this.reload();
                this.owner.pack();
            }
        });
        return removeButton;
    }
}
