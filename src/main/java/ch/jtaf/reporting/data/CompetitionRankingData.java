package ch.jtaf.reporting.data;

import java.time.LocalDate;
import java.util.List;

public record CompetitionRankingData(String name, LocalDate competitionDate, boolean alwaysFirstThreeMedals, int medalPercentage,
                                     List<CompetitionRankingCategory> categories) {
}
