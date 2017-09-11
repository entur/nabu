package no.rutebanken.nabu;


import no.rutebanken.nabu.config.NabuSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = {
                                        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NabuSecurityConfiguration.class),
                                        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = App.class),
})
public class NabuTestApp {

    public static void main(String[] args) {
        SpringApplication.run(NabuTestApp.class, args);
    }
}
