package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeriesRankingResult(Long athleteId, Long competitionId, String competitionName, LocalDate competitionDate,
                                  BigDecimal points) {
}
