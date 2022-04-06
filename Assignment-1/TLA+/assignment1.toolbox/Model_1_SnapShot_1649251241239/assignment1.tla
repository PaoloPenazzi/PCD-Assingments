---------------------------- MODULE assignment1 ----------------------------
EXTENDS TLC, Integers, Sequences
CONSTANTS NUMBER_OF_WORKERS, STEPS

(*--algorithm assignment1

variable iteration = 0,
velocities = [velocity \in 1..NUMBER_OF_WORKERS |-> 0 ], 
positions = [position \in 1..NUMBER_OF_WORKERS |-> 0 ], 
creation = 0,
barrier = 0,
latch = NUMBER_OF_WORKERS;

define
    PositionAfterVelocityComputation == []( (\A n \in 1..NUMBER_OF_WORKERS: positions[n] = 1) => (\A n \in 1..NUMBER_OF_WORKERS: velocities[n] = 1) )
    PositionComputation ==  <>(\A n \in 1..NUMBER_OF_WORKERS: positions[n] = 1)
    SimTermination == <>(iteration = STEPS)
    LatchTermination == <>(latch = 0)
    BarrierTermination == <>(barrier = 0)
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

fair+ process master = 0
begin

evaluateWhile:
    while iteration < STEPS do
        
        startLatchBarrier:
            startLatch(latch);
            startBarrier(barrier);
    
        startWorkers:
            creation := 1;
        
        waitLatchReady:
            waitLatch(latch);
            positions := [position \in 1..NUMBER_OF_WORKERS |-> 0 ];
            velocities :=  [velocity \in 1..NUMBER_OF_WORKERS |-> 0 ];
            creation := 0;
            
        updateIteration:
            iteration := iteration + 1;
            
    end while;
end process;

fair+ process worker \in 1..NUMBER_OF_WORKERS
variable myIteration = 0;
begin

evaluateWhileWorker:
    while myIteration < STEPS do
    
        waitCreationWorkers:
            await creation = 1;
            
        computeVelocity:
            velocities[self] := 1;
            signalBarrier(barrier);
        
        waitBarrier:
            waitBarrier(barrier);
        
        computePositionCollision:
            positions[self] := 1;
            
        updateMyIteration:
             myIteration := myIteration + 1;
        
        signalLatch:
            signalLatch(latch);
        
        awaitWorkFinished:
            await myIteration = iteration;
                
    end while;
end process;

end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "d65f817f" /\ chksum(tla) = "561792ee")
VARIABLES iteration, velocities, positions, creation, barrier, latch, pc

(* define statement *)
PositionAfterVelocityComputation == []( (\A n \in 1..NUMBER_OF_WORKERS: positions[n] = 1) => (\A n \in 1..NUMBER_OF_WORKERS: velocities[n] = 1) )
PositionComputation ==  <>(\A n \in 1..NUMBER_OF_WORKERS: positions[n] = 1)
SimTermination == <>(iteration = STEPS)
LatchTermination == <>(latch = 0)
BarrierTermination == <>(barrier = 0)

VARIABLE myIteration

vars == << iteration, velocities, positions, creation, barrier, latch, pc, 
           myIteration >>

ProcSet == {0} \cup (1..NUMBER_OF_WORKERS)

Init == (* Global variables *)
        /\ iteration = 0
        /\ velocities = [velocity \in 1..NUMBER_OF_WORKERS |-> 0 ]
        /\ positions = [position \in 1..NUMBER_OF_WORKERS |-> 0 ]
        /\ creation = 0
        /\ barrier = 0
        /\ latch = NUMBER_OF_WORKERS
        (* Process worker *)
        /\ myIteration = [self \in 1..NUMBER_OF_WORKERS |-> 0]
        /\ pc = [self \in ProcSet |-> CASE self = 0 -> "evaluateWhile"
                                        [] self \in 1..NUMBER_OF_WORKERS -> "evaluateWhileWorker"]

evaluateWhile == /\ pc[0] = "evaluateWhile"
                 /\ IF iteration < STEPS
                       THEN /\ pc' = [pc EXCEPT ![0] = "startLatchBarrier"]
                       ELSE /\ pc' = [pc EXCEPT ![0] = "Done"]
                 /\ UNCHANGED << iteration, velocities, positions, creation, 
                                 barrier, latch, myIteration >>

startLatchBarrier == /\ pc[0] = "startLatchBarrier"
                     /\ latch' = NUMBER_OF_WORKERS
                     /\ barrier' = 0
                     /\ pc' = [pc EXCEPT ![0] = "startWorkers"]
                     /\ UNCHANGED << iteration, velocities, positions, 
                                     creation, myIteration >>

startWorkers == /\ pc[0] = "startWorkers"
                /\ creation' = 1
                /\ pc' = [pc EXCEPT ![0] = "waitLatchReady"]
                /\ UNCHANGED << iteration, velocities, positions, barrier, 
                                latch, myIteration >>

waitLatchReady == /\ pc[0] = "waitLatchReady"
                  /\ latch = 0
                  /\ positions' = [position \in 1..NUMBER_OF_WORKERS |-> 0 ]
                  /\ velocities' = [velocity \in 1..NUMBER_OF_WORKERS |-> 0 ]
                  /\ creation' = 0
                  /\ pc' = [pc EXCEPT ![0] = "updateIteration"]
                  /\ UNCHANGED << iteration, barrier, latch, myIteration >>

updateIteration == /\ pc[0] = "updateIteration"
                   /\ iteration' = iteration + 1
                   /\ pc' = [pc EXCEPT ![0] = "evaluateWhile"]
                   /\ UNCHANGED << velocities, positions, creation, barrier, 
                                   latch, myIteration >>

master == evaluateWhile \/ startLatchBarrier \/ startWorkers
             \/ waitLatchReady \/ updateIteration

evaluateWhileWorker(self) == /\ pc[self] = "evaluateWhileWorker"
                             /\ IF myIteration[self] < STEPS
                                   THEN /\ pc' = [pc EXCEPT ![self] = "waitCreationWorkers"]
                                   ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                             /\ UNCHANGED << iteration, velocities, positions, 
                                             creation, barrier, latch, 
                                             myIteration >>

waitCreationWorkers(self) == /\ pc[self] = "waitCreationWorkers"
                             /\ creation = 1
                             /\ pc' = [pc EXCEPT ![self] = "computeVelocity"]
                             /\ UNCHANGED << iteration, velocities, positions, 
                                             creation, barrier, latch, 
                                             myIteration >>

computeVelocity(self) == /\ pc[self] = "computeVelocity"
                         /\ velocities' = [velocities EXCEPT ![self] = 1]
                         /\ barrier' = barrier + 1
                         /\ pc' = [pc EXCEPT ![self] = "waitBarrier"]
                         /\ UNCHANGED << iteration, positions, creation, latch, 
                                         myIteration >>

waitBarrier(self) == /\ pc[self] = "waitBarrier"
                     /\ barrier = NUMBER_OF_WORKERS
                     /\ pc' = [pc EXCEPT ![self] = "computePositionCollision"]
                     /\ UNCHANGED << iteration, velocities, positions, 
                                     creation, barrier, latch, myIteration >>

computePositionCollision(self) == /\ pc[self] = "computePositionCollision"
                                  /\ positions' = [positions EXCEPT ![self] = 1]
                                  /\ pc' = [pc EXCEPT ![self] = "updateMyIteration"]
                                  /\ UNCHANGED << iteration, velocities, 
                                                  creation, barrier, latch, 
                                                  myIteration >>

updateMyIteration(self) == /\ pc[self] = "updateMyIteration"
                           /\ myIteration' = [myIteration EXCEPT ![self] = myIteration[self] + 1]
                           /\ pc' = [pc EXCEPT ![self] = "signalLatch"]
                           /\ UNCHANGED << iteration, velocities, positions, 
                                           creation, barrier, latch >>

signalLatch(self) == /\ pc[self] = "signalLatch"
                     /\ latch' = latch - 1
                     /\ pc' = [pc EXCEPT ![self] = "awaitWorkFinished"]
                     /\ UNCHANGED << iteration, velocities, positions, 
                                     creation, barrier, myIteration >>

awaitWorkFinished(self) == /\ pc[self] = "awaitWorkFinished"
                           /\ myIteration[self] = iteration
                           /\ pc' = [pc EXCEPT ![self] = "evaluateWhileWorker"]
                           /\ UNCHANGED << iteration, velocities, positions, 
                                           creation, barrier, latch, 
                                           myIteration >>

worker(self) == evaluateWhileWorker(self) \/ waitCreationWorkers(self)
                   \/ computeVelocity(self) \/ waitBarrier(self)
                   \/ computePositionCollision(self)
                   \/ updateMyIteration(self) \/ signalLatch(self)
                   \/ awaitWorkFinished(self)

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
\* Last modified Wed Apr 06 15:20:31 CEST 2022 by angel
\* Created Wed Apr 06 10:24:20 CEST 2022 by angel
