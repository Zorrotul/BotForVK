package vk.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import vk.bot.config.SchedulerConfig;
import vk.bot.config.BotSchedulerConfig;

@Slf4j
@Service
public class BotSchedulerStarter {

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final SchedulerConfig schedulerConfig;

    private final BotHandler botHandler;


    public BotSchedulerStarter(@Qualifier(BotSchedulerConfig.SCHEDULER_NAME) ThreadPoolTaskScheduler threadPoolTaskScheduler, SchedulerConfig schedulerConfig, BotHandler botHandler) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.schedulerConfig = schedulerConfig;
        this.botHandler = botHandler;
    }

    @PostConstruct
    public void init() {
        log.info("Scheduler started");
        botHandler.init();
        threadPoolTaskScheduler.scheduleWithFixedDelay(
                botHandler::handle,
                schedulerConfig.getPeriod()
        );
    }
}
