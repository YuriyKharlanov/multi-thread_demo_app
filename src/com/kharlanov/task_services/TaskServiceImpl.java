package com.kharlanov.task_services;

import com.kharlanov.game_entities.Clan;
import com.kharlanov.repos.ClansRepoEnum;
import com.kharlanov.game_entities.GoldChange;
import com.kharlanov.repos.TaskQueueEnum;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.*;

/**
 * Предположим что у нас сервис изменения и учета золота для клана
 * Некие внешние силы наполняют очередь с заданиями для пополнения или вычитания золота у клана
 * Тут создается пул из 100 потоков которые с задержкой от 1 до 100 миллисекунд генерируют
 * сущность GoldChange и помещают в очередь blockingQueue, где другой пул будет брать эти задания и вносить изменения
 * в сущности Clan, где хранятся данные каждого клана
 */
public class TaskServiceImpl implements TasksService {

    /**
     * Очередь, куда работающие внешние потоки помещают задания на изменение количества голды
     */
    private final ArrayBlockingQueue<GoldChange> blockingQueue = TaskQueueEnum.INSTANCE.getBlockingQueue();

    /**
     * Пул потоков который со случайной задержкой генерирует задачи для изменения количества голды
     */
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * Список id имеющихся кланов берется из мапы содержащей предсозданные кланы
     */
    private final List<UUID> clanIdsList = new ArrayList<>();
    /**
     * Список ID возможных пользователей (генерируется рандомно)
     */
    private final List<UUID> userIds = new ArrayList<>();

    /**
     * Подготавливаются необходимые коллекции и запускается пул из 100 потоков
     */
    public TaskServiceImpl() {
        for (int i = 0; i < 100; i++) {
            userIds.add(UUID.randomUUID());
        }
        Map<String, Clan> clanMap = ClansRepoEnum.INSTANCE.getClanMap();
        clanMap.forEach((s, clan) -> clanIdsList.add(clan.getId()));
        ExecutorService executor = Executors.newFixedThreadPool(100);
        threadPoolExecutor = (ThreadPoolExecutor) executor;
        createTaskThreadPool();
    }

    /**
     * Запускаем 100 потоков
     */
    private void createTaskThreadPool() {
        for (int i = 0; i < 100; i++) {
            threadPoolExecutor.submit(getRunnable());
        }
        System.out.println("Core threads: " + threadPoolExecutor.getCorePoolSize());
        System.out.println("Largest executions: " + threadPoolExecutor.getLargestPoolSize());
        System.out.println("Maximum allowed threads: " + threadPoolExecutor.getMaximumPoolSize());
        System.out.println("Current threads in pool: " + threadPoolExecutor.getPoolSize());
        System.out.println("Currently executing threads: " + threadPoolExecutor.getActiveCount());
        System.out.println("Total number of threads(ever scheduled): " + threadPoolExecutor.getTaskCount());

    }

    /**
     * Тут в цикле со случайной задержкой генерим задания
     */
    private Runnable getRunnable() {
        Runnable runnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                while (blockingQueue.size() < 10000) {
                    putChangeToQueue();
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1, 101));
                }
            }
        };
        return runnable;
    }

    /**
     * Кладем задания в очередь
     */
    private void putChangeToQueue() {
        try {
            blockingQueue.put(createNewChange());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Генерируем задание на изменение
     */
    private GoldChange createNewChange() {
        return GoldChange.builder()
                .clanId(clanIdsList.get(ThreadLocalRandom.current().nextInt(0, clanIdsList.size())))
                .userId(userIds.get(ThreadLocalRandom.current().nextInt(0, userIds.size())))
                .change(ThreadLocalRandom.current().nextInt(-10000, 10001))
                .build();
    }
}
