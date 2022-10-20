package com.kharlanov.repos;

import com.kharlanov.game_entities.Clan;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Здесь хранятся кланы
 * Enum используется вместо реализации многопоточного singlton-шаблона для хранения общих коллекций
 */
public enum ClansRepoEnum {
    INSTANCE;

    @Getter
    @Setter
    private final Map<String, Clan> clanMap = new ConcurrentHashMap<>();
}
