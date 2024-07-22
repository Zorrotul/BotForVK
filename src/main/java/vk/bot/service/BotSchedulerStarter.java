package vk.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import vk.bot.config.AppConfig;
import vk.bot.config.BotSchedulerConfig;

@Slf4j
@Service
public class BotSchedulerStarter {

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final AppConfig appConfig;

    private final BotHandler botHandler;


    public BotSchedulerStarter(@Qualifier(BotSchedulerConfig.SCHEDULER_NAME) ThreadPoolTaskScheduler threadPoolTaskScheduler, AppConfig appConfig, BotHandler botHandler) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.appConfig = appConfig;
        this.botHandler = botHandler;
    }

    @PostConstruct
    public void init() {
        log.info("Scheduler started");
        threadPoolTaskScheduler.scheduleWithFixedDelay(
                botHandler::handle,
                appConfig.getPeriod()
        );
    }
}
