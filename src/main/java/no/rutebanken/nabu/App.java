package no.rutebanken.nabu;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.Event;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackageClasses = {SystemJobStatus.class, Jsr310JpaConverters.class})
@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
