package vk.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Slf4j
@Service
public class BotSchedulerStarter {

    private final BotHandler botHandler;


    public BotSchedulerStarter(BotHandler botHandler) {

        this.botHandler = botHandler;
    }

    @PostConstruct
    public void init() {
        log.info("Scheduler started");
        botHandler.init();
        startSchedule();
    }

    @Scheduled(fixedDelayString = "${app.config.scheduler.period}")
    public void startSchedule() {
        try {
            botHandler.handle();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
