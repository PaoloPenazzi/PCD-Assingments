package it.unibo.pcd.assignment.reactive;

import it.unibo.pcd.assignment.reactive.model.DummyModel;
import it.unibo.pcd.assignment.reactive.view.ViewController;

public class Launcher {
    public static void main(String[] args) {
        DummyModel dummyModel = new DummyModel();
        ViewController viewController = new ViewController(dummyModel);
    }
}
