package com.kharlanov.repos;

import com.kharlanov.game_entities.LogEntity;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Здесь хранятся логи
 * Enum используется вместо реализации многопоточного singlton-шаблона для хранения общих коллекций
 */
public enum LogEnum {
    INSTANCE;

    @Getter
    private final Map<String, List<LogEntity>> logMap = new ConcurrentHashMap();
}
