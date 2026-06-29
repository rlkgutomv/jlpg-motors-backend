package br.edu.atitus.service;

import br.edu.atitus.dto.BcbPtaxResponse;
import br.edu.atitus.dto.BcbRateRecord;
import br.edu.atitus.dto.CurrencyConversionResponse;
import br.edu.atitus.model.ExchangeRateEntity;
import br.edu.atitus.repository.ExchangeRateRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CurrencyService {

    private static final String BRL = "BRL";
    private static final int SCALE = 6;
    private static final DateTimeFormatter BCB_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final ExchangeRateRepository exchangeRateRepository;
    private final RestClient bcbRestClient;

    public CurrencyService(ExchangeRateRepository exchangeRateRepository, RestClient bcbRestClient) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.bcbRestClient = bcbRestClient;
    }

    @Cacheable(cacheNames = "currencyConversions", key = "#source + '-' + #target + '-' + #amount")
    @CircuitBreaker(name = "bcbService", fallbackMethod = "convertFallback")
    public CurrencyConversionResponse convert(String source, String target, BigDecimal amount) {
        String normalizedSource = normalizeCurrency(source);
        String normalizedTarget = normalizeCurrency(target);
        BigDecimal safeAmount = amount == null ? BigDecimal.ONE : amount;

        if (normalizedSource.equals(normalizedTarget)) {
            return new CurrencyConversionResponse(
                    normalizedSource,
                    normalizedTarget,
                    safeAmount,
                    BigDecimal.ONE,
                    safeAmount,
                    LocalDate.now(),
                    false
            );
        }

        RateResult result = resolveRate(normalizedSource, normalizedTarget);
        return response(normalizedSource, normalizedTarget, safeAmount, result.rate(), result.date(), result.fallback());
    }

    public CurrencyConversionResponse convertFallback(String source, String target, BigDecimal amount, Throwable ex) {
        String normalizedSource = normalizeCurrency(source);
        String normalizedTarget = normalizeCurrency(target);
        BigDecimal safeAmount = amount == null ? BigDecimal.ONE : amount;

        if (normalizedSource.equals(normalizedTarget)) {
            return response(normalizedSource, normalizedTarget, safeAmount, BigDecimal.ONE, LocalDate.now(), true);
        }

        Optional<RateResult> databaseRate = resolveRateFromDatabase(normalizedSource, normalizedTarget);
        if (databaseRate.isPresent()) {
            RateResult result = databaseRate.get();
            return response(normalizedSource, normalizedTarget, safeAmount, result.rate(), result.date(), true);
        }

        return response(normalizedSource, normalizedTarget, safeAmount, BigDecimal.ONE, LocalDate.now(), true);
    }

    private RateResult resolveRate(String source, String target) {
        if (target.equals(BRL)) {
            return getRateToBrl(source);
        }

        if (source.equals(BRL)) {
            RateResult targetToBrl = getRateToBrl(target);
            return new RateResult(BigDecimal.ONE.divide(targetToBrl.rate(), SCALE, RoundingMode.HALF_UP),
                    targetToBrl.date(), targetToBrl.fallback());
        }

        RateResult sourceToBrl = getRateToBrl(source);
        RateResult targetToBrl = getRateToBrl(target);
        BigDecimal rate = sourceToBrl.rate().divide(targetToBrl.rate(), SCALE, RoundingMode.HALF_UP);
        return new RateResult(rate, sourceToBrl.date(), sourceToBrl.fallback() || targetToBrl.fallback());
    }

    private Optional<RateResult> resolveRateFromDatabase(String source, String target) {
        if (target.equals(BRL)) {
            return getLatestRateToBrlFromDatabase(source);
        }

        if (source.equals(BRL)) {
            return getLatestRateToBrlFromDatabase(target)
                    .map(rate -> new RateResult(
                            BigDecimal.ONE.divide(rate.rate(), SCALE, RoundingMode.HALF_UP),
                            rate.date(),
                            true
                    ));
        }

        Optional<RateResult> sourceToBrl = getLatestRateToBrlFromDatabase(source);
        Optional<RateResult> targetToBrl = getLatestRateToBrlFromDatabase(target);
        if (sourceToBrl.isPresent() && targetToBrl.isPresent()) {
            BigDecimal rate = sourceToBrl.get().rate().divide(targetToBrl.get().rate(), SCALE, RoundingMode.HALF_UP);
            return Optional.of(new RateResult(rate, sourceToBrl.get().date(), true));
        }

        return Optional.empty();
    }

    private RateResult getRateToBrl(String currency) {
        if (currency.equals(BRL)) {
            return new RateResult(BigDecimal.ONE, LocalDate.now(), false);
        }

        Optional<RateResult> databaseRate = getLatestRateToBrlFromDatabase(currency);

        try {
            return fetchRateToBrlFromBcb(currency);
        } catch (RuntimeException ex) {
            return databaseRate.orElseThrow(() -> ex);
        }
    }

    private Optional<RateResult> getLatestRateToBrlFromDatabase(String currency) {
        return exchangeRateRepository
                .findTopBySourceCurrencyAndTargetCurrencyOrderByQuotationDateDesc(currency, BRL)
                .map(rate -> new RateResult(rate.getRate(), rate.getQuotationDate(), true));
    }

    private RateResult fetchRateToBrlFromBcb(String currency) {
        LocalDate currentDate = LocalDate.now();

        for (int daysBack = 0; daysBack < 7; daysBack++) {
            LocalDate quotationDate = currentDate.minusDays(daysBack);
            Optional<BcbRateRecord> record = requestBcbRate(currency, quotationDate);

            if (record.isPresent()) {
                BigDecimal rate = record.get().getSellRate();
                saveRate(currency, BRL, rate, quotationDate);
                return new RateResult(rate, quotationDate, false);
            }
        }

        throw new RuntimeException("Cotacao nao encontrada no Banco Central para " + currency + ".");
    }

    private Optional<BcbRateRecord> requestBcbRate(String currency, LocalDate quotationDate) {
        String formattedDate = quotationDate.format(BCB_DATE_FORMATTER);
        String uri = "/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)"
                + "?@moeda='" + currency + "'"
                + "&@dataCotacao='" + formattedDate + "'"
                + "&$top=1"
                + "&$format=json";

        BcbPtaxResponse response = bcbRestClient.get()
                .uri(uri)
                .retrieve()
                .body(BcbPtaxResponse.class);

        if (response == null || response.getValue() == null || response.getValue().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(response.getValue().get(0));
    }

    private void saveRate(String source, String target, BigDecimal rate, LocalDate quotationDate) {
        ExchangeRateEntity entity = exchangeRateRepository
                .findBySourceCurrencyAndTargetCurrencyAndQuotationDate(source, target, quotationDate)
                .orElseGet(ExchangeRateEntity::new);

        entity.setSourceCurrency(source);
        entity.setTargetCurrency(target);
        entity.setRate(rate);
        entity.setQuotationDate(quotationDate);
        exchangeRateRepository.save(entity);
    }

    private CurrencyConversionResponse response(String source, String target, BigDecimal amount, BigDecimal rate,
                                                LocalDate rateDate, boolean fallback) {
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return new CurrencyConversionResponse(source, target, amount, rate, convertedAmount, rateDate, fallback);
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new RuntimeException("Moeda e obrigatoria.");
        }

        String normalized = currency.trim().toUpperCase();
        if (normalized.length() != 3) {
            throw new RuntimeException("Moeda deve seguir o padrao ISO de 3 letras.");
        }

        return normalized;
    }

    private record RateResult(BigDecimal rate, LocalDate date, boolean fallback) {
    }
}
