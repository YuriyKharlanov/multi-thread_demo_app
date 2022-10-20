package com.kharlanov.game_entities;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class LogEntity {
    /**
     * Время изменения
     */
    private long timeStamp;
    /**
     * UUID агента кто дает или забирает ГОЛД
     */
    private UUID participant;
    /**
     * UUID текущего клана
     */
    private UUID clanId;
    /**
     * На какую величину меняется ГОЛД
     */
    private long modifier;
    /**
     * новое значение
     */
    private long newValue;
}
