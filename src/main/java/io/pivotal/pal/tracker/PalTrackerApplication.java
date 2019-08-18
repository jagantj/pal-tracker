package io.pivotal.pal.tracker;

import java.util.TimeZone;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PalTrackerApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(PalTrackerApplication.class);
    }

    @Bean
    JdbcTimeEntryRepository timeEntryRepository(DataSource dataSource) {
        return new JdbcTimeEntryRepository(dataSource);
    }

}
