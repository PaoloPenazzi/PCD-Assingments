package it.unibo.pcd.assignment.reactive.view;

import it.unibo.pcd.assignment.reactive.model.DummyModel;

import java.awt.event.ActionEvent;

public class ViewController {
    private final ViewFrame view;
    private final DummyModel dummyModel;

    public ViewController(DummyModel dummyModel) {
        this.dummyModel = dummyModel;
        this.view = new ViewFrame(this);
        this.setupClassNumberObserver();
    }

    public void incrementPressed(ActionEvent actionEvent) {
        this.dummyModel.incrementClassNumber();
    }

    public void setupClassNumberObserver(){
        dummyModel.getClassNumberObservable().subscribe(num -> view.getClassCounterTextField().setText("" + num));
    }
}
