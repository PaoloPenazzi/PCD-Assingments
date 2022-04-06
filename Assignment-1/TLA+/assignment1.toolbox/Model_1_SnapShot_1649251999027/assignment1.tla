---------------------------- MODULE assignment1 ----------------------------
EXTENDS TLC, Integers, Sequences
CONSTANTS NUMBER_OF_WORKERS, STEPS
ASSUME NUMBER_OF_WORKERS > 0
ASSUME STEPS > 0

(*--algorithm assignment1

variable iteration = 0,
workerIteration = [myIteration \in 1..NUMBER_OF_WORKERS |-> 0 ],
velocities = [velocity \in 1..NUMBER_OF_WORKERS |-> 0 ], 
positions = [position \in 1..NUMBER_OF_WORKERS |-> 0 ], 
creation = 0,
barrier = 0,
latch = NUMBER_OF_WORKERS;
define
    WorkerIterationGreaterThanOrEqualIterationInvariant == []( \A n \in 1..NUMBER_OF_WORKERS: workerIteration[n] >= iteration)
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
begin

evaluateWhileWorker:
    while workerIteration[self] < STEPS do
    
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
             workerIteration[self] := workerIteration[self] + 1;
        
        signalLatch:
            signalLatch(latch);
        
        awaitWorkFinished:
            await workerIteration[self] = iteration;
                
    end while;
end process;

end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "c9f3c19a" /\ chksum(tla) = "7c6411a7")
VARIABLES iteration, workerIteration, velocities, positions, creation, 
          barrier, latch, pc

(* define statement *)
WorkerIterationGreaterThanOrEqualIterationInvariant == []( \A n \in 1..NUMBER_OF_WORKERS: workerIteration[n] >= iteration)
PositionAfterVelocityComputation == []( (\A n \in 1..NUMBER_OF_WORKERS: positions[n] = 1) => (\A n \in 1..NUMBER_OF_WORKERS: velocities[n] = 1) )
PositionComputation ==  <>(\A n \in 1..NUMBER_OF_WORKERS: positions[n] = 1)
SimTermination == <>(iteration = STEPS)
LatchTermination == <>(latch = 0)
BarrierTermination == <>(barrier = 0)


vars == << iteration, workerIteration, velocities, positions, creation, 
           barrier, latch, pc >>

ProcSet == {0} \cup (1..NUMBER_OF_WORKERS)

Init == (* Global variables *)
        /\ iteration = 0
        /\ workerIteration = [myIteration \in 1..NUMBER_OF_WORKERS |-> 0 ]
        /\ velocities = [velocity \in 1..NUMBER_OF_WORKERS |-> 0 ]
        /\ positions = [position \in 1..NUMBER_OF_WORKERS |-> 0 ]
        /\ creation = 0
        /\ barrier = 0
        /\ latch = NUMBER_OF_WORKERS
        /\ pc = [self \in ProcSet |-> CASE self = 0 -> "evaluateWhile"
                                        [] self \in 1..NUMBER_OF_WORKERS -> "evaluateWhileWorker"]

evaluateWhile == /\ pc[0] = "evaluateWhile"
                 /\ IF iteration < STEPS
                       THEN /\ pc' = [pc EXCEPT ![0] = "startLatchBarrier"]
                       ELSE /\ pc' = [pc EXCEPT ![0] = "Done"]
                 /\ UNCHANGED << iteration, workerIteration, velocities, 
                                 positions, creation, barrier, latch >>

startLatchBarrier == /\ pc[0] = "startLatchBarrier"
                     /\ latch' = NUMBER_OF_WORKERS
                     /\ barrier' = 0
                     /\ pc' = [pc EXCEPT ![0] = "startWorkers"]
                     /\ UNCHANGED << iteration, workerIteration, velocities, 
                                     positions, creation >>

startWorkers == /\ pc[0] = "startWorkers"
                /\ creation' = 1
                /\ pc' = [pc EXCEPT ![0] = "waitLatchReady"]
                /\ UNCHANGED << iteration, workerIteration, velocities, 
                                positions, barrier, latch >>

waitLatchReady == /\ pc[0] = "waitLatchReady"
                  /\ latch = 0
                  /\ positions' = [position \in 1..NUMBER_OF_WORKERS |-> 0 ]
                  /\ velocities' = [velocity \in 1..NUMBER_OF_WORKERS |-> 0 ]
                  /\ creation' = 0
                  /\ pc' = [pc EXCEPT ![0] = "updateIteration"]
                  /\ UNCHANGED << iteration, workerIteration, barrier, latch >>

updateIteration == /\ pc[0] = "updateIteration"
                   /\ iteration' = iteration + 1
                   /\ pc' = [pc EXCEPT ![0] = "evaluateWhile"]
                   /\ UNCHANGED << workerIteration, velocities, positions, 
                                   creation, barrier, latch >>

master == evaluateWhile \/ startLatchBarrier \/ startWorkers
             \/ waitLatchReady \/ updateIteration

evaluateWhileWorker(self) == /\ pc[self] = "evaluateWhileWorker"
                             /\ IF workerIteration[self] < STEPS
                                   THEN /\ pc' = [pc EXCEPT ![self] = "waitCreationWorkers"]
                                   ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                             /\ UNCHANGED << iteration, workerIteration, 
                                             velocities, positions, creation, 
                                             barrier, latch >>

waitCreationWorkers(self) == /\ pc[self] = "waitCreationWorkers"
                             /\ creation = 1
                             /\ pc' = [pc EXCEPT ![self] = "computeVelocity"]
                             /\ UNCHANGED << iteration, workerIteration, 
                                             velocities, positions, creation, 
                                             barrier, latch >>

computeVelocity(self) == /\ pc[self] = "computeVelocity"
                         /\ velocities' = [velocities EXCEPT ![self] = 1]
                         /\ barrier' = barrier + 1
                         /\ pc' = [pc EXCEPT ![self] = "waitBarrier"]
                         /\ UNCHANGED << iteration, workerIteration, positions, 
                                         creation, latch >>

waitBarrier(self) == /\ pc[self] = "waitBarrier"
                     /\ barrier = NUMBER_OF_WORKERS
                     /\ pc' = [pc EXCEPT ![self] = "computePositionCollision"]
                     /\ UNCHANGED << iteration, workerIteration, velocities, 
                                     positions, creation, barrier, latch >>

computePositionCollision(self) == /\ pc[self] = "computePositionCollision"
                                  /\ positions' = [positions EXCEPT ![self] = 1]
                                  /\ pc' = [pc EXCEPT ![self] = "updateMyIteration"]
                                  /\ UNCHANGED << iteration, workerIteration, 
                                                  velocities, creation, 
                                                  barrier, latch >>

updateMyIteration(self) == /\ pc[self] = "updateMyIteration"
                           /\ workerIteration' = [workerIteration EXCEPT ![self] = workerIteration[self] + 1]
                           /\ pc' = [pc EXCEPT ![self] = "signalLatch"]
                           /\ UNCHANGED << iteration, velocities, positions, 
                                           creation, barrier, latch >>

signalLatch(self) == /\ pc[self] = "signalLatch"
                     /\ latch' = latch - 1
                     /\ pc' = [pc EXCEPT ![self] = "awaitWorkFinished"]
                     /\ UNCHANGED << iteration, workerIteration, velocities, 
                                     positions, creation, barrier >>

awaitWorkFinished(self) == /\ pc[self] = "awaitWorkFinished"
                           /\ workerIteration[self] = iteration
                           /\ pc' = [pc EXCEPT ![self] = "evaluateWhileWorker"]
                           /\ UNCHANGED << iteration, workerIteration, 
                                           velocities, positions, creation, 
                                           barrier, latch >>

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
\* Last modified Wed Apr 06 15:32:53 CEST 2022 by angel
\* Created Wed Apr 06 10:24:20 CEST 2022 by angel
