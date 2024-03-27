package pro.sky.telegrambot.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;
import pro.sky.telegrambot.service.TelegramBotAnswerer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Component
public class NotificationSend {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

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

        if (!allByNotificationDateTime.isEmpty()) {
            for (NotificationTask notificationTask : allByNotificationDateTime) {
                Long chatId = notificationTask.getChatId();
                String message = "Напоминаю! " + notificationTask.getMessage();
                logger.info("Попытка отправки сообщения на чат {} с текстом: {}", chatId, message);

                telegramBotAnswerer.send(chatId, message);
            }
        } else {
            logger.info("Нет уведомлений для отправки в текущее время.");
        }
    }
}