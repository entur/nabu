package no.rutebanken.nabu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = NabuTestApp.class)
@Transactional
public abstract class BaseIntegrationTest {


    protected String toJson(Object o) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, o);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
