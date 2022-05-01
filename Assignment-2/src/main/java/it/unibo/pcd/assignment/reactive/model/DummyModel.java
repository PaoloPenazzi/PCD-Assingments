package it.unibo.pcd.assignment.reactive.model;


import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import it.unibo.pcd.assignment.reactive.view.ViewController;

public class DummyModel {
    public int classNumber;

    Subject<Integer> classNumberObservable = PublishSubject.create();

    public DummyModel() {
        classNumber = 0;
        ViewController viewController = new ViewController(this);
    }

    public void increment() {
        classNumber++;
        classNumberObservable.onNext(this.classNumber);
    }

    public int getClassNumber() {
        return this.classNumber;
    }

    public Subject<Integer> getClassNumberObservable() {
        return classNumberObservable;
    }
}
