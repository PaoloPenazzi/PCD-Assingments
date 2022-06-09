package it.unibo.pcd.assignment.view;

import it.unibo.pcd.assignment.controller.ViewController;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    private final List<JButton> buttonsList;
    private final ViewController controller;

    public ControlPanel(int width, int height, ViewController controller) {
        setSize(width, height);
        this.controller = controller;
        this.buttonsList = new ArrayList<>();
        this.createButtons();
        this.addButtonsToPanel();
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
    }

    private void createButtons() {
        buttonsList.add(new JButton("PLAY"));
        buttonsList.add(new JButton("PAUSE"));
        buttonsList.add(new JButton("+"));
        buttonsList.add(new JButton("-"));
    }

    private void addButtonsToPanel() {
        for (JButton button : buttonsList) {
            this.add(button);
            button.addActionListener(controller::actionPerformed);
        }
    }
}
