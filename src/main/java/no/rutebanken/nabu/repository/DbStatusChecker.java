package no.rutebanken.nabu.repository;

import org.slf4j.Logger;

import javax.persistence.EntityManager;

class DbStatusChecker {

    public static boolean isPostgresUp(EntityManager entityManager, Logger logger) {
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
