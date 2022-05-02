package it.unibo.pcd.assignment.reactive;

import it.unibo.pcd.assignment.reactive.model.ReactiveAnalyzerImpl;
import it.unibo.pcd.assignment.reactive.controller.ViewController;

public class Launcher {
    public static void main(String[] args) {
        ReactiveAnalyzerImpl reactiveAnalyzerImpl = new ReactiveAnalyzerImpl();
        ViewController viewController = new ViewController();
    }
}
