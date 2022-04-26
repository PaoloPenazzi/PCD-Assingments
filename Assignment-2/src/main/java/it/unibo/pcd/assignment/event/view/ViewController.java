package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    private JTextArea console;

    public ViewController (){
        ViewFrame view = new ViewFrame(this);
        this.console = view.getConsole();
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.showSaveDialog(fileChooser);
        String path = fileChooser.getSelectedFile().getPath();
        console.append(path);
    }
}
