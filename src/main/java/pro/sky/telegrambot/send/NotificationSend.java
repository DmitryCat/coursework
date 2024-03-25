package pro.sky.telegrambot.send;

import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;
import pro.sky.telegrambot.service.TelegramBotAnswerer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationSend {
    private final NotificationRepository notificationRepository;
    private final TelegramBotAnswerer telegramBotAnswerer;

    public NotificationSend(NotificationRepository notificationRepository, TelegramBotAnswerer telegramBotAnswerer) {
        this.notificationRepository = notificationRepository;
        this.telegramBotAnswerer = telegramBotAnswerer;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void send() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> allByNotificationDateTime = notificationRepository.findAllByNotificationDateTime(currentDateTime);

        for (NotificationTask notificationTask : allByNotificationDateTime) {
            telegramBotAnswerer.send(notificationTask.getChatId(),
                    "Напоминаю!" + notificationTask.getMessage());
        }
    }
}
