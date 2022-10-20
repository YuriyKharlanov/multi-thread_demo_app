package com.kharlanov.gold_services;

import com.kharlanov.game_entities.GoldChange;
import com.kharlanov.repos.TaskQueueEnum;
import lombok.SneakyThrows;

import java.util.concurrent.*;

/**
 * Тут происходит запуск пула для обслуживания кланов
 */
class RunClanExecutor {

    /**
     * Потокобезопасная очередь, куда работающие внешние потоки помещают задания на изменение количества золота
     */
    ArrayBlockingQueue<GoldChange> blockingQueue = TaskQueueEnum.INSTANCE.getBlockingQueue();

    SynchronousQueue<Runnable> runnables = new SynchronousQueue<>();

    @SneakyThrows
    public RunClanExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 100,
                60L, TimeUnit.SECONDS, runnables);
        threadPoolExecutor.execute(Task());
    }

    private Runnable Task() {
        return new Runnable() {
            @Override
            public void run() {
                while (true) {
                    GoldChange task = getTask();
                    while (task == null) {
                        try {
                            Thread.sleep(0, 100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        task = getTask();
                    }
                    ClanService.changeGoldToClan(task.getUserId(), task.getClanId(), task.getChange());
                }

            }
        };
    }

    private GoldChange getTask() {
        GoldChange poll = null;
        try {
            poll = blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return poll;
    }
}
