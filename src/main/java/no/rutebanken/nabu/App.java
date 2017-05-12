package no.rutebanken.nabu;

import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.organisation.model.CodeSpace;
import no.rutebanken.nabu.organisation.repository.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"no.rutebanken.nabu.organisation.repository"},
        repositoryBaseClass = BaseRepositoryImpl.class)
@EntityScan(basePackageClasses = {Provider.class, CodeSpace.class, Jsr310JpaConverters.class})
@EnableCaching
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
