package no.rutebanken.nabu;


import no.rutebanken.nabu.config.NabuSecurityConfiguration;
import no.rutebanken.nabu.organisation.repository.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"no.rutebanken.nabu.organisation.repository"},
        repositoryBaseClass = BaseRepositoryImpl.class)
@ComponentScan(excludeFilters = {
                                        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NabuSecurityConfiguration.class),
                                        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = App.class),
})
public class NabuTestApp {

    public static void main(String[] args) {
        SpringApplication.run(NabuTestApp.class, args);
    }
}
