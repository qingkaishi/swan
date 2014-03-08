swan
======
Synchronization is the most commonly-used fix method for atomicity violations, 
but it is usually error-prone. Besides introducing deadlocks, programmers also 
easily synchronize codes insufficiently. Since almost all fix examinations in 
industry still depend on expensive labour force, and concurrent programs are 
notoriously nondeterministic, it is difficult to find such bad fixes.

Swan is a prototype tool that can be used to help programmers examine fixes of 
atomicity violations. Among many advantages of S WAN , three of them stand out. 
(1) It accords with programmers’ conventional fix examination method, which 
examines a fix by repeat executing the patched program to observe whether a bug 
will be reactivated, thereby no false warnings; (2) Our approach can effectively 
examine fixes without any knowledge on the bug-triggering atomicity violations; 
(3) Using Swan , we only need to repeat executing a patched program 3 times on 
average to examine the fixes of atomicity violations, which is far more effective 
than the state-of-the-arts.

What is insufficient synchronization?
-------------------------------------

### Using equivalent lock, but excluding some critical statements
        if (membership
                .memberAlive(m)) {
                                         synchronize(...){
                                         McastMember[] expired
                                                   = membership.expire(...);
                                         for ( int i=0; i<expired.length; i++) {
                                             ...
                                             service.
                                                  memberDisappeared(expired[i]);
                                         }
                                         }
            ...
            synchronize(...){
            service
                .memberAdded(m);
            }
        }

The example is a multi-variable atomicity violation about _membership_ and 
_service_. The synchronization uses equivalent lock, but exclude the first if 
statement from the critical section.

### Synchronizing all codes, but using non-equivalent locks
        // keep a copy of them for unregisteration later
        synchronized(handler) {
            handlerMap.put(ref.getPropoerty(Constants.SERVICE_ID), holders);
        }

The variable _handler_ is an object field. Therefore, it is always different 
for different objects, which will make the global object _handlerMap_ broken.

Usage
-------
See the example in directory demo/READM.md.

Bugs
------

Please help us look for bugs. Please feel free to contact Qingkai.
Email: qingkaishi@gmail.com
