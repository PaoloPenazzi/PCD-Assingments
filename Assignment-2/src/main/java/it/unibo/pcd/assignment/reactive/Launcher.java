package it.unibo.pcd.assignment.reactive;

import it.unibo.pcd.assignment.reactive.model.ReactiveAnalyzer;
import it.unibo.pcd.assignment.reactive.view.ViewController;

public class Launcher {
    public static void main(String[] args) {
        ReactiveAnalyzer reactiveAnalyzer = new ReactiveAnalyzer();
        ViewController viewController = new ViewController();
    }
}
