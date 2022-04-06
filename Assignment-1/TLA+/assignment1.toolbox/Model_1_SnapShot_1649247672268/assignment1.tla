---------------------------- MODULE assignment1 ----------------------------
EXTENDS TLC, Integers, Sequences
CONSTANTS NUMBER_OF_WORKERS, STEPS

(*--algorithm assignment1

variable iteration = 0,
positions = [position \in 1..NUMBER_OF_WORKERS |-> 0 ], \* 1 0, 2 0
creation = 0,
barrierNumber = 0,
latchNumber = NUMBER_OF_WORKERS;

define
\* attraverso la variabile pc (predefinita) possiamo controllare alcuni stati del program counter
\* es: pc[master] /= evaluateWhile, ovvero vogliamo che il program counter del processo master non esegua mai l'istruzione
\* con la label evaluateWhile
\* prima o poi sempre incontriamo .. <>[]
PositionComputation == <>[](\A n \in NUMBER_OF_WORKERS : positions[n] = 1)
SimTermination == <>[](iteration = STEPS)
end define;

macro startLatch(latch) begin
    latch := NUMBER_OF_WORKERS;
end macro; 

macro signalLatch(latch) begin
    latch := latch - 1;
end macro;

macro waitLatch(latch) begin
    await latch = 0;
end macro;

macro startBarrier(barrier) begin
    barrier := 0;
end macro; 

macro signalBarrier(barrier) begin
    barrier := barrier + 1;
end macro;

macro waitBarrier(barrier) begin
    await barrier = NUMBER_OF_WORKERS;
end macro;

\* con fair+ trattiamo i processi con uno strongly fair scheduling quindi che non ci sia scattering (cioè se il processo può eseguire un certo statement
\* prima o poi lo deve eseguire)
fair+ process master = 0
begin

evaluateWhile:
    \* while TRUE per while infinito
    while iteration < STEPS do
        
        \* startiamo il latch e barrier
        startLatchBarrier:
            startLatch(latchNumber);
            startBarrier(barrierNumber);
    
        \* startiamo i worker
        startWorkers:
            creation := 1;
        
        \* aspettiamo che i worker finiscano e poi rimettiamo le posizioni a 0 e il flag di creazione a 0
        waitLatchReady:
            waitLatch(latchNumber);
            positions := [position \in 1..NUMBER_OF_WORKERS |-> 0 ];
            creation := 0;
            
        \* ogni etichetta esegue l'azione atomicamente
        \* incremento delle iterazioni
        updateIteration:
            iteration := iteration + 1;
            
    end while;
end process;

\* Definiamo più processi di uno stesso tipo
fair+ process worker \in 1..NUMBER_OF_WORKERS
variable myIteration = 0;
begin

evaluateWhileWorker:
    while myIteration < STEPS do
    
        \* aspetto che mi crei il master
        waitCreationWorkers:
            await creation = 1;
            
        \* calcolo velocità
        computeVelocity:
            skip;
            signalBarrier(barrierNumber);
        
        \* wait barriera
        waitBarrier:
            waitBarrier(barrierNumber);
        
        \* computo posizione
        computePositionCollision:
            positions[self] := 1;
            
            
        updateMyIteration:
             myIteration := myIteration + 1;
        
        signalLatch:
            signalLatch(latchNumber);
        
        awitFinishWork:
            await myIteration = iteration;
                
    end while;
end process;

end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "c445ed14" /\ chksum(tla) = "48002bca")
VARIABLES iteration, positions, creation, barrierNumber, latchNumber, pc

(* define statement *)
PositionComputation == <>[](\A n \in NUMBER_OF_WORKERS : positions[n] = 1)
SimTermination == <>[](iteration = STEPS)

VARIABLE myIteration

vars == << iteration, positions, creation, barrierNumber, latchNumber, pc, 
           myIteration >>

ProcSet == {0} \cup (1..NUMBER_OF_WORKERS)

Init == (* Global variables *)
        /\ iteration = 0
        /\ positions = [position \in 1..NUMBER_OF_WORKERS |-> 0 ]
        /\ creation = 0
        /\ barrierNumber = 0
        /\ latchNumber = NUMBER_OF_WORKERS
        (* Process worker *)
        /\ myIteration = [self \in 1..NUMBER_OF_WORKERS |-> 0]
        /\ pc = [self \in ProcSet |-> CASE self = 0 -> "evaluateWhile"
                                        [] self \in 1..NUMBER_OF_WORKERS -> "evaluateWhileWorker"]

evaluateWhile == /\ pc[0] = "evaluateWhile"
                 /\ IF iteration < STEPS
                       THEN /\ pc' = [pc EXCEPT ![0] = "startLatchBarrier"]
                       ELSE /\ pc' = [pc EXCEPT ![0] = "Done"]
                 /\ UNCHANGED << iteration, positions, creation, barrierNumber, 
                                 latchNumber, myIteration >>

startLatchBarrier == /\ pc[0] = "startLatchBarrier"
                     /\ latchNumber' = NUMBER_OF_WORKERS
                     /\ barrierNumber' = 0
                     /\ pc' = [pc EXCEPT ![0] = "startWorkers"]
                     /\ UNCHANGED << iteration, positions, creation, 
                                     myIteration >>

startWorkers == /\ pc[0] = "startWorkers"
                /\ creation' = 1
                /\ pc' = [pc EXCEPT ![0] = "waitLatchReady"]
                /\ UNCHANGED << iteration, positions, barrierNumber, 
                                latchNumber, myIteration >>

waitLatchReady == /\ pc[0] = "waitLatchReady"
                  /\ latchNumber = 0
                  /\ positions' = [position \in 1..NUMBER_OF_WORKERS |-> 0 ]
                  /\ creation' = 0
                  /\ pc' = [pc EXCEPT ![0] = "updateIteration"]
                  /\ UNCHANGED << iteration, barrierNumber, latchNumber, 
                                  myIteration >>

updateIteration == /\ pc[0] = "updateIteration"
                   /\ iteration' = iteration + 1
                   /\ pc' = [pc EXCEPT ![0] = "evaluateWhile"]
                   /\ UNCHANGED << positions, creation, barrierNumber, 
                                   latchNumber, myIteration >>

master == evaluateWhile \/ startLatchBarrier \/ startWorkers
             \/ waitLatchReady \/ updateIteration

evaluateWhileWorker(self) == /\ pc[self] = "evaluateWhileWorker"
                             /\ IF myIteration[self] < STEPS
                                   THEN /\ pc' = [pc EXCEPT ![self] = "waitCreationWorkers"]
                                   ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                             /\ UNCHANGED << iteration, positions, creation, 
                                             barrierNumber, latchNumber, 
                                             myIteration >>

waitCreationWorkers(self) == /\ pc[self] = "waitCreationWorkers"
                             /\ creation = 1
                             /\ pc' = [pc EXCEPT ![self] = "computeVelocity"]
                             /\ UNCHANGED << iteration, positions, creation, 
                                             barrierNumber, latchNumber, 
                                             myIteration >>

computeVelocity(self) == /\ pc[self] = "computeVelocity"
                         /\ TRUE
                         /\ barrierNumber' = barrierNumber + 1
                         /\ pc' = [pc EXCEPT ![self] = "waitBarrier"]
                         /\ UNCHANGED << iteration, positions, creation, 
                                         latchNumber, myIteration >>

waitBarrier(self) == /\ pc[self] = "waitBarrier"
                     /\ barrierNumber = NUMBER_OF_WORKERS
                     /\ pc' = [pc EXCEPT ![self] = "computePositionCollision"]
                     /\ UNCHANGED << iteration, positions, creation, 
                                     barrierNumber, latchNumber, myIteration >>

computePositionCollision(self) == /\ pc[self] = "computePositionCollision"
                                  /\ positions' = [positions EXCEPT ![self] = 1]
                                  /\ pc' = [pc EXCEPT ![self] = "updateMyIteration"]
                                  /\ UNCHANGED << iteration, creation, 
                                                  barrierNumber, latchNumber, 
                                                  myIteration >>

updateMyIteration(self) == /\ pc[self] = "updateMyIteration"
                           /\ myIteration' = [myIteration EXCEPT ![self] = myIteration[self] + 1]
                           /\ pc' = [pc EXCEPT ![self] = "signalLatch"]
                           /\ UNCHANGED << iteration, positions, creation, 
                                           barrierNumber, latchNumber >>

signalLatch(self) == /\ pc[self] = "signalLatch"
                     /\ latchNumber' = latchNumber - 1
                     /\ pc' = [pc EXCEPT ![self] = "awitFinishWork"]
                     /\ UNCHANGED << iteration, positions, creation, 
                                     barrierNumber, myIteration >>

awitFinishWork(self) == /\ pc[self] = "awitFinishWork"
                        /\ myIteration[self] = iteration
                        /\ pc' = [pc EXCEPT ![self] = "evaluateWhileWorker"]
                        /\ UNCHANGED << iteration, positions, creation, 
                                        barrierNumber, latchNumber, 
                                        myIteration >>

worker(self) == evaluateWhileWorker(self) \/ waitCreationWorkers(self)
                   \/ computeVelocity(self) \/ waitBarrier(self)
                   \/ computePositionCollision(self)
                   \/ updateMyIteration(self) \/ signalLatch(self)
                   \/ awitFinishWork(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == master
           \/ (\E self \in 1..NUMBER_OF_WORKERS: worker(self))
           \/ Terminating

Spec == /\ Init /\ [][Next]_vars
        /\ SF_vars(master)
        /\ \A self \in 1..NUMBER_OF_WORKERS : SF_vars(worker(self))

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION 


=============================================================================
\* Modification History
\* Last modified Wed Apr 06 14:21:05 CEST 2022 by angel
\* Created Wed Apr 06 10:24:20 CEST 2022 by angel
