package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    private final JTextArea outputConsole;
    private final ViewFrame view;

    public ViewController (){
        this.view = new ViewFrame(this);
        this.outputConsole = view.getConsoleTextArea();
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.showSaveDialog(fileChooser);
        String path = fileChooser.getSelectedFile().getPath();
        view.getFileSelectedLabel().setText(path);
    }

    public void log(String message) {
        this.outputConsole.append(message + "\n");
    }
}
