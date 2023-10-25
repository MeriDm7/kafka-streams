package com.epam.bd201.map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import static java.util.Objects.isNull;

public class InputMapper {

    enum DurationCategory {
        ERRONEOUS_DATA("Erroneous data"),
        SHORT_STAY("Short stay"),
        STANDARD_STAY("Standard stay"),
        STANDARD_EXTENDED_STAY("Standard extended stay"),
        LONG_STAY("Long stay");

        private final String categoryName;
        DurationCategory(String category) {
            this.categoryName = category;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(InputMapper.class);

    private static final String DURATION_CATEGORY = "duration_category";
    public static String calculateDuration(String message) {
        JSONObject json = new JSONObject(message);
        String srch_ci = json.optString("srch_ci", null);
        String srch_co = json.optString("srch_co", null);

        if (isNull(srch_ci) || isNull(srch_co)) {
            json.put(DURATION_CATEGORY, DurationCategory.ERRONEOUS_DATA.categoryName);
        } else if (dateDurationCalculator(srch_ci, srch_co) <= 0) {
            json.put(DURATION_CATEGORY, DurationCategory.ERRONEOUS_DATA.categoryName);
        }
        else if ((dateDurationCalculator(srch_ci, srch_co) > 0) && (dateDurationCalculator(srch_ci, srch_co) < 5)) {
            json.put(DURATION_CATEGORY, DurationCategory.SHORT_STAY.categoryName);
        }
        else if ((dateDurationCalculator(srch_ci, srch_co) >= 5) && (dateDurationCalculator(srch_ci, srch_co) < 11)) {
            json.put(DURATION_CATEGORY, DurationCategory.STANDARD_STAY.categoryName);
        }
        else if ((dateDurationCalculator(srch_ci, srch_co) >= 11) && (dateDurationCalculator(srch_ci, srch_co) < 14)) {
            json.put(DURATION_CATEGORY, DurationCategory.STANDARD_EXTENDED_STAY.categoryName);
        }
        else if (dateDurationCalculator(srch_ci, srch_co) >= 14) {
            json.put(DURATION_CATEGORY, DurationCategory.LONG_STAY.categoryName);
        }
        return json.toString();
    }

    private static long dateDurationCalculator(String srch_ci, String srch_o) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate startDate = LocalDate.parse(srch_ci, formatter);
            LocalDate endDate = LocalDate.parse(srch_o, formatter);

            return ChronoUnit.DAYS.between(startDate, endDate);
        } catch (DateTimeParseException e) {
            log.info("One or both of the provided strings are not in a valid date format: " + e.getMessage());
            return -1;
        }
    }
}
