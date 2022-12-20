package com.xdroid.spring.codedesign.launchstarter.sort;



import android.util.ArraySet;

import androidx.annotation.NonNull;

import com.xdroid.spring.codedesign.launchstarter.task.XDTask;
import com.xdroid.spring.codedesign.launchstarter.utils.X_DispatcherLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class X_TaskSortUtil {

    private static List<XDTask> sNewTasksHigh = new ArrayList<>();// 高优先级的Task

    /**
     * 任务的有向无环图的拓扑排序
     *
     * @return
     */
    public static synchronized List<XDTask> getSortResult(List<XDTask> originTasks,
                                                          List<Class<? extends XDTask>> clsLaunchTasks) {
        long makeTime = System.currentTimeMillis();

        Set<Integer> dependSet = new ArraySet<>();
        X_Graph graph = new X_Graph(originTasks.size());
        for (int i = 0; i < originTasks.size(); i++) {
            XDTask task = originTasks.get(i);
            if (task.isSend() || task.dependsOn() == null || task.dependsOn().size() == 0) {
                continue;
            }
            for (Class cls : task.dependsOn()) {
                int indexOfDepend = getIndexOfTask(originTasks, clsLaunchTasks, cls);
                if (indexOfDepend < 0) {
                    throw new IllegalStateException(task.getClass().getSimpleName() +
                            " depends on " + cls.getSimpleName() + " can not be found in task list ");
                }
                dependSet.add(indexOfDepend);
                graph.addEdge(indexOfDepend, i);
            }
        }
        List<Integer> indexList = graph.topologicalSort();
        List<XDTask> newTasksAll = getResultTasks(originTasks, dependSet, indexList);

        X_DispatcherLog.i("task analyse cost makeTime " + (System.currentTimeMillis() - makeTime));
        printAllTaskName(newTasksAll);
        return newTasksAll;
    }

    @NonNull
    private static List<XDTask> getResultTasks(List<XDTask> originTasks,
                                               Set<Integer> dependSet, List<Integer> indexList) {
        List<XDTask> newTasksAll = new ArrayList<>(originTasks.size());
        List<XDTask> newTasksDepended = new ArrayList<>();// 被别人依赖的
        List<XDTask> newTasksWithOutDepend = new ArrayList<>();// 没有依赖的
        List<XDTask> newTasksRunAsSoon = new ArrayList<>();// 需要提升自己优先级的，先执行（这个先是相对于没有依赖的先）
        for (int index : indexList) {
            if (dependSet.contains(index)) {
                newTasksDepended.add(originTasks.get(index));
            } else {
                XDTask task = originTasks.get(index);
                if (task.needRunAsSoon()) {
                    newTasksRunAsSoon.add(task);
                } else {
                    newTasksWithOutDepend.add(task);
                }
            }
        }
        // 顺序：被别人依赖的————》需要提升自己优先级的————》需要被等待的————》没有依赖的
        sNewTasksHigh.addAll(newTasksDepended);
        sNewTasksHigh.addAll(newTasksRunAsSoon);
        newTasksAll.addAll(sNewTasksHigh);
        newTasksAll.addAll(newTasksWithOutDepend);
        return newTasksAll;
    }

    private static void printAllTaskName(List<XDTask> newTasksAll) {
        if (true) {
            return;
        }
        for (XDTask task : newTasksAll) {
            X_DispatcherLog.i(task.getClass().getSimpleName());
        }
    }

    public static List<XDTask> getTasksHigh() {
        return sNewTasksHigh;
    }

    /**
     * 获取任务在任务列表中的index
     *
     * @param originTasks
     * @param taskName
     * @return
     */
    private static int getIndexOfTask(List<XDTask> originTasks,
                                      List<Class<? extends XDTask>> clsLaunchTasks, Class cls) {
        int index = clsLaunchTasks.indexOf(cls);
        if (index >= 0) {
            return index;
        }

        // 仅仅是保护性代码
        final int size = originTasks.size();
        for (int i = 0; i < size; i++) {
            if (cls.getSimpleName().equals(originTasks.get(i).getClass().getSimpleName())) {
                return i;
            }
        }
        return index;
    }

}
