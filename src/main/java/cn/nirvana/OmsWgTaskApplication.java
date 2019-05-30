package cn.nirvana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
@ComponentScan({"cn.nirvana.*"})
public class OmsWgTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmsWgTaskApplication.class, args);
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}
