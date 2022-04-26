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
        console.append("BELLAAAAA");
    }
}
