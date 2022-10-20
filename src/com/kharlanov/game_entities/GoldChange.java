package com.kharlanov.game_entities;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Сущность создается для имитации заданий на изменение количества золота в игровом процессе
 */
@Builder
@Getter
public class GoldChange {
    private final UUID userId; // ID участника внесшего изменения
    private final UUID clanId; // клан в который вносятся изменения
    private final int change; // Число, содержащее изменение. Может быть отрицательным или положительным
}
