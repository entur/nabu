package no.rutebanken.nabu;

import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.repository.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"no.rutebanken.nabu.organization.repository"},
		repositoryBaseClass = BaseRepositoryImpl.class)
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
