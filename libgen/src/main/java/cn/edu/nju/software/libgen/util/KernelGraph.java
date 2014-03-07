/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libgen.util;

import cn.edu.nju.software.libevent.SwanEvent;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author qingkaishi
 */
public class KernelGraph {

    private static KernelGraph kg = null;
    private Vector<SwanEvent> allEvents = null;

    boolean noSCCwith(PMAP pairMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private enum OrderType {

        ForkOrder, JoinOrder, WaitOrder, NotifyOrder, ProgramOrder, TemporalOrder, OtherOrder, NoOrder;
    }

    private KernelGraph(Vector<SwanEvent> trace) {
        allEvents = trace;

        Set<Integer> currentThreads = new HashSet<Integer>();
        for (int i = 0; i < trace.size(); i++) {
            SwanEvent ise = trace.get(i);
            ise.happensBefore = new HashSet<SwanEvent>();
            currentThreads.add(ise.threadId);

            boolean findFirstSameThread = false;
            if (ise.accessType == SwanEvent.AccessType.FORK) {
                boolean findFirstFork = false;
                for (int j = i + 1; j < trace.size(); j++) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(ise, i, jse, j);

                    if (ot == OrderType.ProgramOrder && !findFirstSameThread) {
                        ise.happensBefore.add(jse);
                        findFirstSameThread = true;
                    } else if (ot == OrderType.ForkOrder && !findFirstFork) {
                        ise.happensBefore.add(jse);
                        findFirstFork = true;
                    }

                    if (findFirstSameThread && findFirstFork) {
                        break;
                    }
                }
            } else if (ise.accessType == SwanEvent.AccessType.JOIN) {
                for (int j = i + 1; j < trace.size(); j++) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(ise, i, jse, j);

                    if (ot == OrderType.ProgramOrder && !findFirstSameThread) {
                        ise.happensBefore.add(jse);
                        break;
                    }
                }

                for (int j = i - 1; j >= 0; j--) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(jse, j, ise, i);
                    if (ot == OrderType.JoinOrder) {
                        jse.happensBefore.add(ise);
                        break;
                    }
                }
            } else if (ise.accessType == SwanEvent.AccessType.WAIT_RELEASE) {
                boolean findFirstWait = false;
                for (int j = i + 1; j < trace.size(); j++) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(ise, i, jse, j);

                    if (ot == OrderType.ProgramOrder && !findFirstSameThread) {
                        ise.happensBefore.add(jse);
                        findFirstSameThread = true;
                    } else if (ot == OrderType.WaitOrder && !findFirstWait) {
                        ise.happensBefore.add(jse);
                        findFirstWait = true;
                    }

                    if (findFirstSameThread && findFirstWait) {
                        break;
                    }
                }
            } else if (ise.accessType == SwanEvent.AccessType.NOTIFY) {
                boolean findFirstNotify = false;
                for (int j = i + 1; j < trace.size(); j++) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(ise, i, jse, j);

                    if (ot == OrderType.ProgramOrder && !findFirstSameThread) {
                        ise.happensBefore.add(jse);
                        findFirstSameThread = true;
                    } else if (ot == OrderType.NotifyOrder && !findFirstNotify) {
                        ise.happensBefore.add(jse);
                        findFirstNotify = true;
                    }

                    if (findFirstSameThread && findFirstNotify) {
                        break;
                    }
                }
            } else if (ise.accessType == SwanEvent.AccessType.NOTIFYALL) {
                Set<Integer> temp = new HashSet<Integer>(currentThreads);
                temp.remove(ise.threadId);
                for (int j = i + 1; j < trace.size(); j++) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(ise, i, jse, j);

                    if (ot == OrderType.ProgramOrder && !findFirstSameThread) {
                        ise.happensBefore.add(jse);
                        findFirstSameThread = true;
                    } else if (ot == OrderType.NotifyOrder && temp.contains(jse.threadId)) {
                        ise.happensBefore.add(jse);
                        temp.remove(jse.threadId);
                    }

                    if (findFirstSameThread && temp.isEmpty()) {
                        break;
                    }
                }
            } else {
                for (int j = i + 1; j < trace.size(); j++) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(ise, i, jse, j);

                    if (ot == OrderType.ProgramOrder && !findFirstSameThread) {
                        ise.happensBefore.add(jse);
                        break;
                    }
                }
            }
        }
    }

    private OrderType getOrderType(SwanEvent ei, int i, SwanEvent ej, int j) {
        if (i < j) {
            if (ei.accessType == SwanEvent.AccessType.FORK && ei.sharedMemId == ej.threadId) {
                return OrderType.ForkOrder;
            }

            if (ej.accessType == SwanEvent.AccessType.JOIN && ei.threadId == ej.sharedMemId) {
                return OrderType.JoinOrder;
            }

            if (ei.threadId == ej.threadId) {
                return OrderType.ProgramOrder;
            }

            if (ei.accessType == SwanEvent.AccessType.WAIT_RELEASE
                    && ej.accessType == SwanEvent.AccessType.ACQUIRE
                    && !ej.patchedEvent
                    && ei.threadId != ej.threadId) {
                return OrderType.WaitOrder;
            }

            if ((ei.accessType == SwanEvent.AccessType.NOTIFY || ei.accessType == SwanEvent.AccessType.NOTIFYALL)
                    && ej.accessType == SwanEvent.AccessType.WAIT_ACQUIRE
                    && ei.threadId != ej.threadId) {
                return OrderType.NotifyOrder;
            }

            if (ei.sharedMemId == ej.sharedMemId) {
                return OrderType.TemporalOrder;
            }

            return OrderType.OtherOrder;
        }
        return OrderType.NoOrder;
    }

    public static KernelGraph v(Vector<SwanEvent> trace) {
        if (kg == null) {
            kg = new KernelGraph(trace);
        }

        return kg;
    }

    public void test() {
        try {
            FileWriter fw = new FileWriter("testhbg.dot");
            fw.append("digraph hbg{ \n");
            for (int i = 0; i < allEvents.size(); i++) {
                SwanEvent ei = allEvents.get(i);

                if (ei.patchedEvent) {
                    fw.append("a" + i + "[label=\"NEW " + ei.accessType.name() + "\"];\n");
                } else {
                    fw.append("a" + i + "[label=\"" + ei.accessType.name() + "\"];\n");
                }

                Set<SwanEvent> hb = ei.happensBefore;
                Iterator<SwanEvent> it = hb.iterator();
                while (it.hasNext()) {
                    SwanEvent se = it.next();
                    fw.append("a" + i + "->a" + allEvents.indexOf(se) + ";\n");
                }
            }
            fw.append("}\n");
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
