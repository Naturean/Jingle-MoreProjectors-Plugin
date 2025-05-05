package com.naturean.moreprojectors.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Code from <a href="https://github.com/DuncanRuns/Julti/blob/main/src/main/java/xyz/duncanruns/julti/gui/DownloadProgressFrame.java">Julti</a>
 * @author DuncanRuns
 */
public class DownloadProgressFrame extends JFrame {
    private final JProgressBar bar;

    public DownloadProgressFrame(JFrame owner) {
        this.setLayout(new GridBagLayout());
        JLabel text = new JLabel("Downloading MoreProjectors Plugin...");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        this.add(text, gbc);
        this.bar = new JProgressBar(0, 100);
        this.add(this.bar, gbc);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DownloadProgressFrame.this.dispose();
            }
        });

        this.setSize(300, 100);
        this.setTitle("MoreProjectors update");
        this.setLocationRelativeTo(owner);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    public JProgressBar getBar() {
        return this.bar;
    }
}
