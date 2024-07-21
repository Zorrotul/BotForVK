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
        scheduler.setThreadNamePrefix("bot-sch");
        scheduler.setPoolSize(1);
        log.info("appConfig<- AwaitTerminationMillis: {}, period: {}", appConfig.getAwaitTerminationMillis(), appConfig.getPeriod());
        scheduler.setAwaitTerminationMillis(appConfig.getAwaitTerminationMillis());//TODO: параметризовать через yml

        scheduler.initialize();
        return scheduler;
    }

}
