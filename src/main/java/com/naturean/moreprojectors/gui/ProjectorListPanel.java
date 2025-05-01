package com.naturean.moreprojectors.gui;

import com.naturean.moreprojectors.MoreProjectors;
import com.naturean.moreprojectors.hotkey.ProjectorHotkeyManager;
import com.naturean.moreprojectors.projector.Projector;
import com.naturean.moreprojectors.projector.ProjectorSettings;
import xyz.duncanruns.jingle.hotkey.Hotkey;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectorListPanel extends JPanel {
    private final JFrame owner;

    public ProjectorListPanel(JFrame owner) {
        this.owner = owner;
        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.reload();
    }

    private static JComponent getButtonSpacer() {
        return new JComponent() {
            private final Dimension size = new Dimension(5, 0);

            @Override
            public Dimension getPreferredSize() {
                return this.size;
            }

            @Override
            public Dimension getMaximumSize() {
                return this.size;
            }

            @Override
            public Dimension getMinimumSize() {
                return this.size;
            }
        };
    }

    public void reload() {
        this.removeAll();

        GridBagConstraints constraints = new GridBagConstraints(
                GridBagConstraints.RELATIVE, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 10, 5, 10), 0, 0
        );

        List<Projector> projectors;
        synchronized (MoreProjectors.class) {
            projectors = MoreProjectors.options == null ? Collections.emptyList() : MoreProjectors.options.projectors;
        }

        if(projectors.isEmpty()) {
            this.add(new JLabel("No projectors added!"));
        }
        else {
            for(final Projector projector: projectors) {
                constraints.gridy++;

                // Projector
                JLabel projectorLabel = new JLabel(String.format("%s", projector.name));
                this.add(projectorLabel, constraints.clone());
                // Hotkey
                JLabel hotkeyLabel = new JLabel((projector.settings.ignoreModifiers ? "* " : "") + Hotkey.formatKeys(projector.settings.hotkeys));
                this.add(hotkeyLabel, constraints.clone());
                // Enable
                JCheckBox enableCheckBox = new JCheckBox();
                enableCheckBox.setSelected(projector.enable);
                enableCheckBox.addActionListener(e -> ProjectorListPanel.this.onEnable(projector, enableCheckBox));
                this.add(enableCheckBox, constraints.clone());
                // Buttons: Edit, Remove
                JPanel buttonsPanel = new JPanel();
                buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

                JButton editButton = this.getEditButton(projector);
                JButton removeButton = this.getRemoveButton(projector);
                buttonsPanel.add(editButton);
                buttonsPanel.add(getButtonSpacer());
                buttonsPanel.add(removeButton);
                this.add(buttonsPanel, constraints.clone());
            }

            constraints.gridy = 1;
            this.add(new JLabel("Projector"), constraints.clone());
            this.add(new JLabel("Hotkey"), constraints.clone());
            this.add(new JLabel("Enable"), constraints.clone());
        }

        this.revalidate();
        this.repaint();
    }

    private JButton getEditButton(Projector projector) {
        JButton editButton = new JButton("Edit");
        editButton.addActionListener( a -> {
            synchronized (MoreProjectors.class) {
                EditProjectorDialog dialog = new EditProjectorDialog(this.owner, projector);
                dialog.setVisible(true);
                if(dialog.cancelled) return;

                MoreProjectors.options.projectors = (
                        MoreProjectors.options.projectors.stream()
                                .map(p -> p.equals(projector) ? new Projector(dialog.name, p.enable, new ProjectorSettings(
                                        dialog.autoOpen, dialog.alwaysActivate, dialog.ignoreModifiers, dialog.shouldBorderless,
                                        dialog.topWhenActive, dialog.minimizeWhenInactive, dialog.inactivateWhenOther,
                                        dialog.geometry, dialog.hotkeys, dialog.allowedInstanceStates, dialog.allowedInWorldStates
                                )) : p).collect(Collectors.toList())
                );

                this.reload();
                ProjectorHotkeyManager.reload();
                MoreProjectors.options.save();
            }
        });
        return editButton;
    }

    private JButton getRemoveButton(Projector projector) {
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener( a -> {
            synchronized (MoreProjectors.class) {
                MoreProjectors.options.projectors = (
                    MoreProjectors.options.projectors.stream()
                    .filter(p -> !p.equals(projector))
                    .collect(Collectors.toList())
                );
                this.reload();
                ProjectorHotkeyManager.reload();
                MoreProjectors.options.save();
            }
        });
        return removeButton;
    }

    private void onEnable(Projector projector, JCheckBox enableCheckBox) {
        synchronized (MoreProjectors.class) {
            boolean isEnable = enableCheckBox.isSelected();
            if (!isEnable) projector.close();

            MoreProjectors.options.projectors = (
                    MoreProjectors.options.projectors.stream()
                            .peek(p -> {
                                if(p.equals(projector)) p.enable = isEnable;
                            }).collect(Collectors.toList())
            );

            ProjectorHotkeyManager.reload();
            MoreProjectors.options.save();
        }
    }
}
