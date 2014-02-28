package edu.hkust.leap.replayer;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2007-2008,
 * Koushik Sen    <ksen@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class ActiveChecker {

    static private Vector<ActiveChecker> blockedThreads = new Vector<ActiveChecker>();
    static private Map threadToSemaphoreAndTime = Collections.synchronizedMap(new WeakIdentityHashMap());
    final public static Object lock = new Object();
    public static MersenneTwisterFast rand = new MersenneTwisterFast(System.currentTimeMillis());
    public final static AtomicBoolean dirty = new AtomicBoolean(false);
    private static double probability = 1.0;
    private int index;
    
    public ActiveChecker(int index)
    {
    	this.index = index;
    }
    public ActiveChecker()
    {
    }
    static public int getBlockedThreadCount() {
        return blockedThreads.size();
    }

    public static void reduceProbability() {
        probability /= 2;
        //System.out.println("Probability "+probability);
    }

    class Pair {
        int waitTime;
        Semaphore sem;
        ActiveChecker checker;

        Pair(int waitTime, Semaphore sem, ActiveChecker checker) {
            this.waitTime = waitTime;
            this.sem = sem;
            this.checker = checker;
        }
    }


    private Semaphore sem;

    final protected void block(int milliSeconds) {
        if (milliSeconds > 0) {
            threadToSemaphoreAndTime.put(Thread.currentThread(), new Pair(milliSeconds, null, this));
        } else {
            sem = new Semaphore();
            //blockedThreads.add(this);
            //dirty.set(true);
            threadToSemaphoreAndTime.put(Thread.currentThread(), new Pair(0, sem, this));
        }
    }

    final protected void unblock(int milliSeconds) {
        //System.out.println("Before "+blockedThreads.size());
        blockedThreads.remove(this);
        dirty.set(true);
        //System.out.println("After "+blockedThreads.size());
        sem.release(milliSeconds);
    }

    final public static void blockIfRequired() {
        Pair p = (Pair) threadToSemaphoreAndTime.get(Thread.currentThread());
        if (p != null) 
        {
            if (p.waitTime > 0) {
                try {
                    Thread.sleep(p.waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //StallBreaker.breakAnyStall();
                try {
                	blockedThreads.add(p.checker);
                    p.sem.acquire();
                } catch (InterruptedException e) {
                    blockedThreads.remove(p.checker);
                    dirty.set(true);
                }
            }
        }
    }

    public void check(Collection<ActiveChecker> checkers) {
        block(0);
    }

    final public void check() {
        synchronized (lock) {
            if (true) {//rand.nextDouble()<=probability
                int activeThreadCount = Scheduler.getActiveThreadCount();
                //System.err.println("--- activeThreadCount: "+activeThreadCount);
                if (activeThreadCount > 1 && threadToSemaphoreAndTime.get(Thread.currentThread()) == null) {
                    check(blockedThreads);
                }
            }
        }
    }

    final public void check(int waitTime) {
        synchronized (lock) {
            int activeThreadCount = Scheduler.getActiveThreadCount();
            if (activeThreadCount > 1 && threadToSemaphoreAndTime.get(Thread.currentThread()) == null) {
                block(waitTime);
            }
        }
    }

    public static void Check() {
        (new ActiveChecker()).check();
        blockIfRequired();
    }


    public static boolean unblockAThread() {
        synchronized (lock) {
            //System.out.println("Blockedthreads size "+ blockedThreads.size());
            if (blockedThreads.size() == 0) return false;
            //int randNum = rand.nextInt(blockedThreads.size());
            //ActiveChecker ac = blockedThreads.get(0);
            blockedThreads.remove(0).unblock(0);
            //blockedThreads.get(0).unblock(0);//randNum
            return true;
        }
    }

    public boolean equals(Object o) {
        return this == o;
    }
}
