package no.rutebanken.nabu.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
@Service
public class DbStatusChecker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityManager entityManager;

    public boolean isDbUp() {
        try {
            if ((Integer) entityManager.createNativeQuery("SELECT 1").getSingleResult() == 1) {
                return true;
            }
        } catch (RuntimeException e) {
            logger.warn("Failed while testing DB connection", e);
        }
        return false;
    }
}
