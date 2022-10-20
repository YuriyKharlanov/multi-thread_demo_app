package com.kharlanov.repos;

import com.kharlanov.game_entities.GoldChange;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Самая главная очередь куда помещаются сгенерированные задания на изменение золота кланов,
 * емкость задана достаточно большая, но очередь практически всегда пустая
 * Enum используется вместо реализации многопоточного singlton-шаблона для хранения общих коллекций
 */
public enum TaskQueueEnum {

    INSTANCE;

    /**
     * Очередь куда помещаются сгенерированные задания на изменение золота кланов
     */
    @Getter
    @Setter
    private final ArrayBlockingQueue<GoldChange> blockingQueue = new ArrayBlockingQueue(100000);
}
