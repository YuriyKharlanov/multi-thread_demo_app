Тестовое приложение для демонстрации возможностей работы в многопоточной среде.

Приложение написано на чистой Java 11 и тестировалось на Liberica JDK 11. Только добавлен Lombook, ибо так код нагляднее. Кто-то не любит его, но везде есть плюсы и минусы.

Порядок работы:
1. Сначала генерируется мапа, содержащая кланы 100 штук. У каждого есть UUID id и случайное имя из цифр (можно убрать имена, в коде не используются).
2. Затем запускается пул из 100 потоков которые со случайной задержкой от 1 до 100 миллисекунд генерят задания за изменения количества (рандомно от -10000 до 10000) золота у клана выбранного так же случайным образом из имеющегося списка.
3. Далее есть 1 поток, который проверяет очередь и если не нашел заданий ждет (100 наносекунд) и снова проверяет очередь и если нашел, то берет задание на изменение количества золота у клана и обращается к сервису ClanService и проверет, если не стоит флаг stat, что означает что сервис статистики проверяет корректность подсчетов, вносит изменения и создает запись об изменении.
4. Далее есть StatService. Там отдельный поток раз в 10 секунд перебирает мапу, где хранятся списки с сущностями, отражающими внесенные изменения. Он же производит сверку суммы значений, имеющихся в логах с текущей записью в сущности клана. На этот период приостанавливается изменение данных в клане.
5. Так же StatService проверяет, чтобы число записей было не более тысячи (можно хоть миллион сделать, вопрос в оперативной памяти и производительности). Если число записей тысяча, то очищает статистику и создает запись о последнем числе. Если бы была БД, то лист так же бы чистился, только перед этим копия листа отдавалась бы отдельному потоку для записи в БД.


Что не сделано и я это понимаю:
1. Не подключен логгер, поэтому существенная информация о работе программы выводится в консоль.
2. Не выведено сохранение в базу данных. Понимаю что базу можно раздувать сильно (и ротировать при необходимости) и хранить там логи долго и сделать рест-методы или вывод в консоль, например, статистики по каждому клану. На самом деле конечно бэк игры должен заниматься игрой, а если нужна статистика для администратора, лучше повесить отдельный микросервис который будет обеспечивать работой консоль администратора и вносить изменения в конфиг игры для запуска и отдавать необходимую информацию и статистику для поддержки.
3. Вопрос о выборе ExecutorService для запуска (и метод запуска) каждого пула открыт, как и выбор той или иной очереди (варианты имеют плюсы и минусы).
4. Так же вопрос об использовании очередей (следовало ли довериться автоматике на создание необходимого количества потоков или лучше держать под ручным контролем). В текущей реализации сервис статистики и внесений изменений легко обходится одним потоком, но если бы это был продакшен, а не демо, конечно следовало бы мониторить этот поток и в случае стопора (или если не справляется) сделать схему перезапуска или поднятия еще одного потока например.
5. Вопрос по организации структуры программы, нейминга сервисов и методов, раскладки по пакетам и прочая логика обычно в растущих проекта претерпевает несколько изменений по мере роста сложности бэка.
6. Покрытие тестами

Следует оттестировать и, возможно, оптимизировать алгоритм, если число кланов будет не 100 и не 1000 (как я тестировал), а 10000 или 100000 например.
При обходе статистики более 10000 кланов следует увеличить интервал обходов с 10 секунд (интервал выбран для наглядности статистики) до, например, 1 минуты и сделать ряд оптимизаций (обходить, например, только те кланы, которые получали изменения с момента предыдущего посещения, так же делать это в два потока и прочие оптимизации)