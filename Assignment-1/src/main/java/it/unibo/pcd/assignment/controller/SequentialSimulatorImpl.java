package it.unibo.pcd.assignment.controller;

public class SequentialSimulatorImpl extends AbstractSequentialSimulator {

    public SequentialSimulatorImpl(int numBodies, int numSteps, int sideLenght) {
        super(numBodies, numSteps, sideLenght);
    }

    @Override
    public void run() {
        double virtualTime = 0;
        long iteration = 0;
        while (iteration < super.getNumSteps()) {
            if(iteration % 500 == 0) {
                System.out.println("Iterazione: " + iteration);
            }
            /*if (iteration == super.getNumSteps() - 1) {
                System.out.println("Calcolo velocità: " + super.getTempoCalcoloVelocita());
                System.out.println("Aggiornamento posizioni: " + super.getTempoAggiornamentoPosizioni());
                System.out.println("Controllo collisioni: " + super.getTempoControlloCollisioni());
            }*/
            super.computeBodies();
            virtualTime = virtualTime + DELTA_TIME;
            iteration++;
        }
    }
}
