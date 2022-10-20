package com.kharlanov;

import com.kharlanov.task_services.TasksService;
import com.kharlanov.game_entities.Clan;
import com.kharlanov.repos.ClansRepoEnum;
import com.kharlanov.gold_services.ClanService;
import com.kharlanov.stat_services.StatService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SkytecGamesTestProject {

    public static void main(String[] args) {
        // Initially
        Map<String, Clan> clanMap = ClansRepoEnum.INSTANCE.getClanMap();
        // Генерим кланы
        for (int i = 0; i < 1000; i++) {
            long i1 = (long) ( Math.random() * Long.MAX_VALUE);
            Clan clan = Clan.builder()
                    .gold(new AtomicInteger(0))
                    .id(UUID.randomUUID())
                    .name(String.valueOf(i1))
                    .build();
            clanMap.put(clan.getId().toString(), clan);
        }
        // Запускаем все сервисы не мудрствуя
        TasksService.runService(); // Тут происходит генерация в 100 потоков со случайной задержкой заданий на изменение золота
        ClanService.runClanExecutor(); // тут запускаются потоки, которые берут из очереди задания и меняют у кланов золото и создают логи.
        StatService.runStatService(); // тут процесс получает статистику и выводит промежуточные результаты
    }
}
