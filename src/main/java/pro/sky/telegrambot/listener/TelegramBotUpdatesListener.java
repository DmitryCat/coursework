package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;
import pro.sky.telegrambot.service.TelegramBotAnswerer;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

      @Service
      public class TelegramBotUpdatesListener implements UpdatesListener {

          private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
          private final Pattern PATTERN_MESSAGE = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
          private final DateTimeFormatter NOTIFICATION_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
          private TelegramBot telegramBot;
          private final TelegramBotAnswerer telegramBotAnswerer;
          private final NotificationRepository notificationRepository;

          public TelegramBotUpdatesListener(TelegramBot telegramBot, TelegramBotAnswerer telegramBotAnswerer, NotificationRepository notificationRepository) {
              this.telegramBot = telegramBot;
              this.telegramBotAnswerer = telegramBotAnswerer;
              this.notificationRepository = notificationRepository;
          }

          @PostConstruct
          public void init() {
              telegramBot.setUpdatesListener(this);
          }

          @Override
          public int process(List<Update> updates) {
              updates.forEach(update -> {
                  logger.info("Processing update: {}", update);
                  String message = update.message().text();
                  Long chatId = update.message().chat().id();
                  if (message.equals("/start")) {
                      logger.info("Received message: {}", message);
                      telegramBotAnswerer.send(chatId, "Добрый день!");
                  } else {
                      Matcher matcher = PATTERN_MESSAGE.matcher(message);
                      if (matcher.matches()) {
                          logger.info("Received new message: {}", message);
                          String date = matcher.group(1);
                          String item = matcher.group(3);
                          NotificationTask notificationTask = new NotificationTask(chatId, item,
                                  LocalDateTime.parse(date, NOTIFICATION_DATE_TIME_FORMAT)
                          );
                          logger.info("Saving notification task: {}", notificationTask);
                          notificationRepository.save(notificationTask);
                          telegramBotAnswerer.send(chatId, "Я тебя услышал");

                      }
                  }
              });
              return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
      }










