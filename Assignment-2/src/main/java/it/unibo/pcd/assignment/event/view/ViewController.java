package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    private JTextArea console;
    private ViewFrame view;

    public ViewController (){
        this.view = new ViewFrame(this);
        this.console = view.getConsole();
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.showSaveDialog(fileChooser);
        String path = fileChooser.getSelectedFile().getPath();
        view.getFileSelected().setText(path);
    }

    public void log(String message) {
        this.console.append(message + "\n");
    }
}
