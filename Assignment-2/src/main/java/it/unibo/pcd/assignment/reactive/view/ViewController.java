package it.unibo.pcd.assignment.reactive.view;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import it.unibo.pcd.assignment.event.ProjectAnalyzerImpl;
import it.unibo.pcd.assignment.reactive.model.DummyModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ViewController {
    private final ViewFrame view;
    private final DummyModel dummyModel;

    public ViewController(DummyModel dummyModel) {
        this.dummyModel = dummyModel;
        this.view = new ViewFrame(this);
        this.setupObserver();
    }

    public void incrementPressed(ActionEvent actionEvent) {
        this.dummyModel.increment();
    }

    public void setupObserver(){
        dummyModel.getClassNumberObservable().subscribe(num -> view.getTextClass().setText("" + num));
    }
}
