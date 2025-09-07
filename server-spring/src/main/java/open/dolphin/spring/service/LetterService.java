package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spring Boot service for medical letter management.
 * Migrated from LetterServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class LetterService {

    private static final Logger logger = Logger.getLogger(LetterService.class.getName());

    private static final String KARTE_ID = "karteId";
    private static final String ID = "id";

    private static final String QUERY_LETTER_BY_KARTE_ID = "from LetterModule l where l.karte.id=:karteId";
    private static final String QUERY_LETTER_BY_ID = "from LetterModule l where l.id=:id";
    private static final String QUERY_ITEM_BY_ID = "from LetterItem l where l.module.id=:id";
    private static final String QUERY_TEXT_BY_ID = "from LetterText l where l.module.id=:id";
    private static final String QUERY_DATE_BY_ID = "from LetterDate l where l.module.id=:id";

    @PersistenceContext
    private EntityManager em;

    /**
     * Save or update a medical letter with all its components.
     * @param model the letter module to save
     * @return the saved letter ID
     */
    public long saveOrUpdateLetter(LetterModule model) {
        // Save main letter
        em.persist(model);

        // Save letter items
        List<LetterItem> items = model.getLetterItems();
        if (items != null) {
            for (LetterItem item : items) {
                item.setModule(model);
                em.persist(item);
            }
        }

        // Save letter texts
        List<LetterText> texts = model.getLetterTexts();
        if (texts != null) {
            for (LetterText txt : texts) {
                txt.setModule(model);
                em.persist(txt);
            }
        }

        // Save letter dates
        List<LetterDate> dates = model.getLetterDates();
        if (dates != null) {
            for (LetterDate date : dates) {
                date.setModule(model);
                em.persist(date);
            }
        }

        // Delete old version if linkId is set (update operation)
        if (model.getLinkId() != 0L) {
            deleteLetterComponents(model.getLinkId());
            deleteLetter(model.getLinkId());
        }

        return model.getId();
    }

    /**
     * Get all letters for a specific karte.
     * @param karteId the karte ID
     * @return list of letter modules
     */
    public List<LetterModule> getLetterList(long karteId) {
        List<LetterModule> list = em.createQuery(QUERY_LETTER_BY_KARTE_ID)
                                   .setParameter(KARTE_ID, karteId)
                                   .getResultList();
        return list;
    }

    /**
     * Get a single letter with all its components.
     * @param letterPk the letter primary key
     * @return the complete letter module
     */
    public LetterModule getLetter(long letterPk) {
        LetterModule letter = em.createQuery(QUERY_LETTER_BY_ID, LetterModule.class)
                               .setParameter(ID, letterPk)
                               .getSingleResult();

        // Load letter items
        List<LetterItem> items = em.createQuery(QUERY_ITEM_BY_ID)
                                  .setParameter(ID, letter.getId())
                                  .getResultList();
        letter.setLetterItems(items);

        // Load letter texts
        List<LetterText> texts = em.createQuery(QUERY_TEXT_BY_ID)
                                  .setParameter(ID, letter.getId())
                                  .getResultList();
        letter.setLetterTexts(texts);

        // Load letter dates
        List<LetterDate> dates = em.createQuery(QUERY_DATE_BY_ID)
                                  .setParameter(ID, letter.getId())
                                  .getResultList();
        letter.setLetterDates(dates);

        return letter;
    }

    /**
     * Delete a letter and all its components.
     * @param pk the letter primary key
     */
    public void delete(long pk) {
        deleteLetterComponents(pk);
        deleteLetter(pk);
    }

    /**
     * Delete letter components (items, texts, dates).
     * @param letterId the letter ID
     */
    private void deleteLetterComponents(long letterId) {
        // Delete letter items
        try {
            List<LetterItem> itemList = em.createQuery(QUERY_ITEM_BY_ID)
                                         .setParameter(ID, letterId)
                                         .getResultList();
            for (LetterItem item : itemList) {
                em.remove(item);
            }
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "No letter items found for ID: {0}", letterId);
        }

        // Delete letter texts
        try {
            List<LetterText> textList = em.createQuery(QUERY_TEXT_BY_ID)
                                         .setParameter(ID, letterId)
                                         .getResultList();
            for (LetterText txt : textList) {
                em.remove(txt);
            }
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "No letter texts found for ID: {0}", letterId);
        }

        // Delete letter dates
        try {
            List<LetterDate> dateList = em.createQuery(QUERY_DATE_BY_ID)
                                         .setParameter(ID, letterId)
                                         .getResultList();
            for (LetterDate date : dateList) {
                em.remove(date);
            }
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "No letter dates found for ID: {0}", letterId);
        }
    }

    /**
     * Delete the main letter module.
     * @param letterId the letter ID
     */
    private void deleteLetter(long letterId) {
        try {
            LetterModule letter = em.createQuery(QUERY_LETTER_BY_ID, LetterModule.class)
                                   .setParameter(ID, letterId)
                                   .getSingleResult();
            em.remove(letter);
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "No letter found for ID: {0}", letterId);
        }
    }

    /**
     * Get letter statistics for a karte.
     * @param karteId the karte ID
     * @return statistics map
     */
    public java.util.Map<String, Object> getLetterStats(long karteId) {
        List<LetterModule> letters = getLetterList(karteId);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalLetters", letters.size());
        stats.put("karteId", karteId);

        return stats;
    }
}
