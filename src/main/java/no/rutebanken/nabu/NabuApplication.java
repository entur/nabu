package no.rutebanken.nabu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
//@EntityScan(basePackageClasses={StopPlace.class})

@ComponentScan
public class NabuApplication {

    public static void main(String[] args) {
        // Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort","9876").start();
        // Server server = Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","9875").start();

        SpringApplication.run(NabuApplication.class, args);
    }
}
