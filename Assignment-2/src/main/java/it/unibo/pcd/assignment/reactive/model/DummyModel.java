package it.unibo.pcd.assignment.reactive.model;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class DummyModel {
    private int packageNumber;
    private int classNumber;
    private int interfaceNumber;
    private final Subject<Integer> packageNumberObservable = PublishSubject.create();
    private final Subject<Integer> classNumberObservable = PublishSubject.create();
    private final Subject<Integer> interfaceNumberObservable = PublishSubject.create();

    public DummyModel() {
        this.packageNumber = 0;
        this.classNumber = 0;
        this.interfaceNumber = 0;
    }

    public void incrementPackageNumber() {
        packageNumber++;
        packageNumberObservable.onNext(this.packageNumber);
    }

    public void incrementClassNumber() {
        classNumber++;
        classNumberObservable.onNext(this.classNumber);
    }

    public void incrementInterfaceNumber() {
        interfaceNumber++;
        interfaceNumberObservable.onNext(this.interfaceNumber);
    }

    public int getPackageNumber() {
        return packageNumber;
    }

    public int getClassNumber() {
        return this.classNumber;
    }

    public int getInterfaceNumber() {
        return interfaceNumber;
    }

    public Subject<Integer> getClassNumberObservable() {
        return classNumberObservable;
    }
}
