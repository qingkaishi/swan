/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libgen.util;

import cn.edu.nju.software.libevent.SwanEvent;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

/**
 *
 * @author qingkaishi
 */
public class KernelGraph {

    private static KernelGraph kg = null;
    private Vector<SwanEvent> allEvents = null;

    private enum OrderType {

        ForkOrder, JoinOrder, WaitOrder, NotifyOrder, ProgramOrder, TemporalOrder, OtherOrder, NoOrder;
    }

    List<MAP> temporalOrders = new ArrayList<MAP>();

    private KernelGraph(Vector<SwanEvent> trace) {
        allEvents = trace;

        Set<Integer> currentThreads = new HashSet<Integer>();
        for (int i = 0; i < trace.size(); i++) {
            SwanEvent ise = trace.get(i);
            ise.idx = i;
            ise.happensBefore = new HashSet<SwanEvent>();
            currentThreads.add(ise.threadId);

            boolean findFirstSameThread = false;
            if (ise.accessType == SwanEvent.AccessType.FORK) {
                boolean findFirstFork = false;
                for (int j = i + 1; j < trace.size(); j++) {
                    SwanEvent jse = trace.get(j);
                    OrderType ot = getOrderType(ise, i, jse, j);

                    if (ot == OrderType.ProgramOrder && !findFirstSameThread) {
                        ise.sameThreadNext = jse;
                        jse.sameThreadLast = ise;
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
                        ise.sameThreadNext = jse;
                        jse.sameThreadLast = ise;
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
                        ise.sameThreadNext = jse;
                        jse.sameThreadLast = ise;
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
                        ise.sameThreadNext = jse;
                        jse.sameThreadLast = ise;
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
                        ise.sameThreadNext = jse;
                        jse.sameThreadLast = ise;
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
                        ise.sameThreadNext = jse;
                        jse.sameThreadLast = ise;
                        ise.happensBefore.add(jse);
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < trace.size(); i++) {
            SwanEvent ise = trace.get(i);
            for (int j = i + 1; j < trace.size(); j++) {
                SwanEvent jse = trace.get(j);
                OrderType ot = getOrderType(ise, i, jse, j);
                if (ot == OrderType.TemporalOrder
                        || ot == OrderType.OtherOrder) {
                    temporalOrders.add(new MAP(ise, jse));
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
                    fw.append("a" + i + "[label=\"NEW " + ei.accessType.name() + "(" + ei.idx + ")" + ei.sharedMemId + "\"];\n");
                } else {
                    fw.append("a" + i + "[label=\"" + ei.accessType.name() + "(" + ei.idx + ")" + ei.sharedMemId + "\"];\n");
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

    public void generateTestSchedules(List<PMAP> tocover) {
        int idx = 0;
        int k = 5;
        while (!tocover.isEmpty()) {
            // clear
            for (SwanEvent se : allEvents) {
                se.clearTemporalNext();
            }

            int startIdx = 0;
            while (startIdx < tocover.size()) {
                // cover
                int total = (startIdx + k <= tocover.size()) ? k : (tocover.size() - startIdx);
                List<PMAP> sub = tocover.subList(startIdx, startIdx + total);
                int coveredNum = checkSCCForCoverage(sub);

                startIdx = startIdx + total - coveredNum;
            }

            // add other orders
            List<MAP> backup = new ArrayList<MAP>(temporalOrders);
            while (!backup.isEmpty()) {
                List<MAP> list = backup.subList(0, backup.size() >= k ? k : backup.size());
                checkSCCwithTemporal(list);
                list.clear();
            }
            backup = null;

            // output trace
            Vector<SwanEvent> newTrace = topologicalSorting();

            for (SwanEvent se : newTrace) {
                System.out.print(se.accessType.name() + "(" + se.idx + ")" + se.sharedMemId + "--> ");
            }
            System.out.println();

            String filename = "new." + ++idx + ".trace.gz";
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
                oos.writeObject(newTrace);
                oos.close();
                System.out.println("[Swan] Ouput a new trace! ==> " + filename);
                newTrace = null;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            } finally {
                System.gc();
                System.gc();
                System.gc();
            }
        }
    }

    /**
     * *****************************************************************
     */
    private Vector<SwanEvent> topologicalSorting() {
        nodeindex.clear();
        for (int i = 0; i < allEvents.size(); i++) {
            nodeindex.add(-1); // borrow it for sorting; it is used for SCC originally.
        }

        Vector<SwanEvent> ret = new Vector<SwanEvent>();
        LinkedList<SwanEvent> linkedList = new LinkedList<SwanEvent>();

        while (nodeindex.contains(-1)) {
            int x = nodeindex.indexOf(-1);
            visit(x, linkedList);
        }
        ret.addAll(linkedList);
        linkedList = null;
        return ret;
    }

    private void visit(int n, LinkedList<SwanEvent> ret) {
        if (nodeindex.get(n) == 0) {
            throw new RuntimeException("Not a DAG!");
        }

        if (nodeindex.get(n) == -1) {
            nodeindex.set(n, 0);
            Iterator<SwanEvent> nIt = allEvents.get(n).getEdgeIteraror();
            while (nIt.hasNext()) {
                SwanEvent to = nIt.next();
                visit(to.idx, ret);
            }
            nodeindex.set(n, 1);
            ret.addFirst(allEvents.get(n));
        }
    }

    /**
     * *****************************************************************
     */
    private int checkSCCwithTemporal(List<MAP> list) {
        List<Integer> starts = new ArrayList<Integer>();
        List<MAP> added = new ArrayList<MAP>();
        for (MAP p : list) {
            // add cached closed edges
            SwanEvent from1 = p.first();
            SwanEvent to1 = p.second();

            if (!from1.containsTemporalNext(to1)) {
                from1.addTemporalNext(to1);
                starts.add(to1.idx);
                added.add(p);
            }
        }
        // check whether there are SCCs
        if (!containsSCC(starts)) {
            // if no SCCs, return
            return list.size();
        } else {
            // remove those make it cyclic, and try one by one
            for (MAP p : added) {
                if (!p.first().containsHappensBeforeNext(p.second())) {
                    p.first().removeTemporalNext(p.second());
                }
            }
            if (list.size() == 1) {
                return 0;
            } else {
                // else check each cached pmap, respectively, also using the method
                int num = 0;
                for (int i = 0; i < list.size(); i++) {
                    int ret = checkSCCwithTemporal(list.subList(i, i + 1)); // FIXME, test it
                    num = num + ret;
                }
                return num;
            }
        }
    }

    private int checkSCCForCoverage(List<PMAP> list) {
        List<Integer> starts = new ArrayList<Integer>();
        List<MAP> added = new ArrayList<MAP>();
        for (PMAP p : list) {
            // add cached closed edges
            PMAP edges = p.getEdgeClosure();
            SwanEvent from1 = edges.first().first();
            SwanEvent to1 = edges.first().second();

            SwanEvent from2 = edges.second().first();
            SwanEvent to2 = edges.second().second();

            if (!from1.containsTemporalNext(to1)) {
                from1.addTemporalNext(to1);
                starts.add(to1.idx);
                added.add(edges.first());
            }

            if (!from2.containsTemporalNext(to2)) {
                from2.addTemporalNext(to2);
                starts.add(to2.idx);
                added.add(edges.second());
            }
        }
        // check whether there are SCCs
        if (!containsSCC(starts)) {
            int ret = list.size();
            // if no SCCs, return
            list.clear(); // clear the covered ones
            return ret;
        } else {
            // remove those make it cyclic, and try one by one
            for (MAP p : added) {
                if (!p.first().containsHappensBeforeNext(p.second())) {
                    p.first().removeTemporalNext(p.second());
                }
            }
            if (list.size() == 1) {
                return 0;
            } else {
                // else check each cached pmap, respectively, also using the method
                int num = 0;
                for (int i = 0; i < list.size(); i++) {
                    int ret = checkSCCForCoverage(list.subList(i, i + 1)); // FIXME, test it
                    if (ret != 0) {
                        i--;
                    }
                    num = num + ret;
                }
                return num;
            }
        }
    }

    /**
     *
     * @param list the list of PMAPs try to cover, one by one
     * @return the number of covered PMAPs
     */
    int checkSCCwith(List<PMAP> list) {
        // clear all temporal edges
        for (SwanEvent se : allEvents) {
            se.clearTemporalNext();
        }

        List<Integer> starts = new ArrayList<Integer>();
        for (PMAP p : list) {
            // add cached closed edges
            PMAP edges = p.getEdgeClosure();
            edges.first().first().addTemporalNext(edges.first().second());
            edges.second().first().addTemporalNext(edges.second().second());
            starts.add(edges.first().second().idx);
            starts.add(edges.second().second().idx);
        }
        // check whether there are SCCs
        if (!containsSCC(starts)) {
            int ret = list.size();
            // if no SCCs, return
            return ret;
        } else {

            if (list.size() == 1) {
                // if there is SCC, but only one cached pmap
                list.clear();
                return 0;
            } else {
                // else check each cached pmap, respectively, also using the method
                int num = 0;
                for (int i = 0; i < list.size(); i++) {
                    int ret = checkSCCwith(list.subList(i, i + 1)); // FIXME, test it
                    if ((ret == 0)) {
                        i--;
                    }
                    num = num + ret;
                }
                return num;
            }
        }
    }

    /**
     * *****************************************************************
     */
    List<List<Integer>> SCCs = new ArrayList<List<Integer>>();
    List<Integer> nodeindex = new ArrayList<Integer>();
    List<Integer> nodelowlink = new ArrayList<Integer>();
    Stack<Integer> sstack = new Stack<Integer>();
    int iindex = 0;

    private boolean containsSCC(List<Integer> starts) {
        SCCs.clear();
        nodeindex.clear();
        nodelowlink.clear();
        sstack.clear();
        iindex = 0;

        for (int i = 0; i < allEvents.size(); i++) {
            nodeindex.add(-1);
            nodelowlink.add(-1);
        }

        for (int i : starts) {
            if (nodeindex.get(i) == -1) {
                strongConnect(i);
                if (!SCCs.isEmpty()) {
                    return true;
                }
            }
        }

        return !SCCs.isEmpty();
    }

    private void strongConnect(int v) {
        if (!SCCs.isEmpty()) {
            return;
        }

        iindex++;
        nodeindex.set(v, iindex);
        nodelowlink.set(v, iindex);
        sstack.push(v);

        Iterator<SwanEvent> edgeIt = allEvents.get(v).getEdgeIteraror();
        while (edgeIt.hasNext()) {
            int w = edgeIt.next().idx;
            if (nodeindex.get(w) == -1) {
                strongConnect(w);
                nodelowlink.set(v,
                        Math.min(nodelowlink.get(v), nodelowlink.get(w)));
            } else if (nodeindex.get(w) < nodeindex.get(v)) {
                if (sstack.contains(w)) {
                    nodelowlink.set(v,
                            Math.min(nodelowlink.get(v), nodeindex.get(w)));
                }
            }
        }

        if (nodeindex.get(v).equals(nodelowlink.get(v))) {
            List<Integer> SCC = new ArrayList<Integer>();
            int w = sstack.peek();
            while (nodeindex.get(w) >= nodeindex.get(v)) {
                sstack.pop();
                SCC.add(w);
                if (sstack.isEmpty()) {
                    break;
                }
                w = sstack.peek();
            }

            if (SCC.size() > 1) {
                SCCs.add(SCC);
            }
        }
    }

}
