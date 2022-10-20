package com.kharlanov.gold_services;

import com.kharlanov.game_entities.Clan;
import com.kharlanov.repos.ClansRepoEnum;
import com.kharlanov.game_entities.LogEntity;
import com.kharlanov.repos.LogEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Интерфейс, содержит методы для внесения изменений в записи кланов и после внесения изменений создает log запись
 * и помещает в соответствующий список.
 * Эти методы оставлены в интерфейсе для демонстрации возможностей интерфейсов java > 9 (реализация статических методов и прочее)
 */
public interface ClanService {

    static void runClanExecutor() {
        new RunClanExecutor();
    }

    /**
     * Содержит кланы
     */
    Map<String, Clan> clanMap = ClansRepoEnum.INSTANCE.getClanMap();

    /**
     * Содержит списки с LogEntity
     */
    Map<String, List<LogEntity>> logEnumList = LogEnum.INSTANCE.getLogMap();

    /**
     * Вносит изменения в записи клана и вызывает метод генерации лога
     * @param userId кто (потенциальный игрок) добавил или забрал золото
     * @param clanId к какому клану относятся изменения
     * @param gold размер изменений
     */
    static void changeGoldToClan(UUID userId, UUID clanId, int gold) {
        String s = String.valueOf(clanId);
        Clan clan = clanMap.get(s);
        while (clan.isStat()) {
            try {
                Thread.sleep(0, 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int i = clan.getGold().addAndGet(gold);

        logChange(userId, clanId, gold, s, i);
    }

    /**
     *
     * @param userId кто (потенциальный игрок) добавил или забрал золото
     * @param clanId к какому клану относятся изменения
     * @param gold размер изменений
     * @param s id клана в текстовом виде (техническое поле)
     * @param i результат после изменений
     */
    static void logChange(UUID userId, UUID clanId, int gold, String s, int i) {
        if (logEnumList.containsKey(s)) {
            logEnumList.get(s).add(createlogRecord(userId, clanId, gold, i));
        } else {
            ArrayList<LogEntity> logEntities = new ArrayList<>();
            logEntities.add(createlogRecord(userId, clanId, gold, i));
            logEnumList.put(s, logEntities);
        }
    }

    /**
     * Генерирует LogEntity
     * @param userId кто (потенциальный игрок) добавил или забрал золото
     * @param clanId к какому клану относятся изменения
     * @param gold размер изменений
     * @param i результат после изменений
     */
    static LogEntity createlogRecord(UUID userId, UUID clanId, int gold, int i) {
        return LogEntity.builder()
                .clanId(clanId)
                .modifier(gold)
                .participant(userId)
                .timeStamp(System.currentTimeMillis())
                .newValue(i)
                .build();
    }

    Clan get(UUID clanId);
}
