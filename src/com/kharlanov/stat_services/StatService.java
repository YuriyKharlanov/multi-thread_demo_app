package com.kharlanov.stat_services;

import com.kharlanov.game_entities.Clan;
import com.kharlanov.game_entities.GoldChange;
import com.kharlanov.repos.ClansRepoEnum;
import com.kharlanov.game_entities.LogEntity;
import com.kharlanov.repos.LogEnum;
import com.kharlanov.repos.TaskQueueEnum;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Сервис статистики
 */
public class StatService {

    Map<String, List<LogEntity>> logMap = LogEnum.INSTANCE.getLogMap();

    Map<String, Clan> repoEnum = ClansRepoEnum.INSTANCE.getClanMap();

    ArrayBlockingQueue<GoldChange> taskQueueEnum = TaskQueueEnum.INSTANCE.getBlockingQueue();

    public static void runStatService() {
        new StatService();
    }

    public StatService() {
        ScheduledExecutorService scheduledES = new ScheduledThreadPoolExecutor(2);
        scheduledES.scheduleWithFixedDelay(getRunable(), 1, 10, TimeUnit.SECONDS);
        scheduledES.execute(getRunable());
    }

    private Runnable getRunable() {
        return new Runnable() {
            @Override
            public void run() {
                getWithStat();
            }
        };
    }

    /**
     * тут периодически печатаем размер очереди заданий
     * Останавливаем процедуру обработки клана и чуток ждем (вдруг кто-то не внес еще изменения)
     * получаем список логов, считаем сумму
     * если более 1000 то чистим список логов (передаем в БД - не реализовано еще)
     * проверяем корректность подсчета логов сверкой суммы с записью в сущности
     * если что-то не сойдется, можем уведомить администратора или сделать что-то еще
     * разрешаем работать с сущностью клана
     * Тут есть большой простор для оптимизаций
     */
    private void getWithStat() {
        System.out.println("taskQueueEnum.size(): " + taskQueueEnum.size());
        logMap.forEach((k,v) -> {
            repoEnum.get(k).setStat(true); // останавливаем процедуру обработки этого клана
            sleep(0, 10); // немного ждем на всякий случай вдруг какой-то поток не записал изменения
            List<LogEntity> logEntities = logMap.get(k); // получаем список

            long sum = logEntities.stream().mapToLong(LogEntity::getModifier).sum(); // считаем сумму

            if (logEntities.size() > 1000) { //работаем с размером
                long newValue = logEntities.get(logEntities.size() - 1).getNewValue();
                UUID clanId = logEntities.get(logEntities.size() - 1).getClanId();
                logEntities.clear();
                logEntities.add(LogEntity.builder().clanId(clanId).modifier(newValue).newValue(0L).build());
                System.out.println("Queue for " + k + " was cleared");
            }
            if (sum != repoEnum.get(k).getGold().get()) {
                System.out.println("Clan with ID has problem with stat");
            }
            repoEnum.get(k).setStat(false);
        });
    }

    private void sleep(int mills, int nanos) {
        try {
            Thread.sleep(mills, nanos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
