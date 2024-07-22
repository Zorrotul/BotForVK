package vk.bot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class BotSchedulerConfig {

    public static final String SCHEDULER_NAME = "bot-scheduler";

    @Autowired
    private final AppConfig appConfig;

    @Bean(name = SCHEDULER_NAME)
    public ThreadPoolTaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        log.info("appConfig<- threadNamePrefix: {}, poolSize: {} , AwaitTerminationMillis: {}, period: {}",
                appConfig.getThreadNamePrefix(),
                appConfig.getPoolSize(),
                appConfig.getAwaitTerminationMillis(),
                appConfig.getPeriod());
        scheduler.setThreadNamePrefix(appConfig.getThreadNamePrefix());
        scheduler.setPoolSize(appConfig.getPoolSize());
        scheduler.setAwaitTerminationMillis(appConfig.getAwaitTerminationMillis());

        scheduler.initialize();
        return scheduler;
    }

}
