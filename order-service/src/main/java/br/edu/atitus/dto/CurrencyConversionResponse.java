package br.edu.atitus.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrencyConversionResponse(
        String source,
        String target,
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal convertedAmount,
        LocalDate rateDate,
        boolean fallback
) {
}
