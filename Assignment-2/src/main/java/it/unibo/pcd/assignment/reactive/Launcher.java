package it.unibo.pcd.assignment.reactive;

import it.unibo.pcd.assignment.reactive.controller.ViewController;
import it.unibo.pcd.assignment.reactive.model.ReactiveAnalyzerImpl;

public class Launcher {
    public static void main(String[] args) {
        ReactiveAnalyzerImpl reactiveAnalyzerImpl = new ReactiveAnalyzerImpl();
        ViewController viewController = new ViewController();
    }
}
