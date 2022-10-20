package com.kharlanov.game_entities;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Сущность с информацией клана
 */
@Builder
@Data
public class Clan {
    private final UUID id;     // id клана
    private final String name; // имя клана
    private AtomicInteger gold;    // текущее количество золота в казне клана
    private boolean stat; // true ставится если начат процесс обслуживания статистики по клану, изменение gold приостанавливается
}