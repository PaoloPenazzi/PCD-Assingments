package it.unibo.pcd.assignment.reactive.view;

import it.unibo.pcd.assignment.reactive.model.DummyModel;

import java.awt.event.ActionEvent;

public class ViewController {
    private final ViewFrame view;
    private final DummyModel dummyModel;

    public ViewController(DummyModel dummyModel) {
        this.dummyModel = dummyModel;
        this.view = new ViewFrame(this);
        this.setupPackageNumberObserver();
        this.setupClassNumberObserver();
        this.setupInterfaceNumberObserver();
    }

    /**
     * Dummy method to simulate an increase in class number.
     * @param actionEvent
     */
    public void incrementPressed(ActionEvent actionEvent) {
        this.dummyModel.incrementClassNumber();
    }

    public void startPressed(ActionEvent actionEvent) {
    }

    private void setupClassNumberObserver(){
        dummyModel.getClassNumberObservable()
                .subscribe(num -> view.getClassCounterTextField().setText("" + num));
    }

    private void setupPackageNumberObserver() {
        this.dummyModel.getPackageNumberObservable()
                .subscribe(num -> view.getPackageCounterTextField().setText("" + num));
    }

    private void setupInterfaceNumberObserver() {
        this.dummyModel.getInterfaceNumberObservable()
                .subscribe(num -> view.getInterfaceCounterTextField().setText("" + num));
    }
}
