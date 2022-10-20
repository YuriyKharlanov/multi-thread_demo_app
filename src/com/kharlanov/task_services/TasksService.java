package com.kharlanov.task_services;

/**
 * Тут создаем сущность которая генерирует задания имитируя активность других игроков
 */
public interface TasksService {

    static void runService() {
        new TaskServiceImpl();
    }
}
