package keville.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableJdbcAuditing
@EnableJdbcRepositories( basePackages = { "keville.repository" })
@SpringBootApplication( scanBasePackages = { "keville" })
public class Proximity {
  static Logger LOG = LoggerFactory.getLogger(Proximity.class);

  public static void main(String[] args) {
    SpringApplication.run(Proximity.class,args);
  }

  @Bean
  TaskScheduler taskScheduler() {
    return new ThreadPoolTaskScheduler();
  }

}
