package br.edu.atitus.service;

import br.edu.atitus.client.CurrencyClient;
import br.edu.atitus.dto.CurrencyConversionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CurrencyConversionService {

    private final CurrencyClient currencyClient;

    public CurrencyConversionService(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    @Cacheable(cacheNames = "productCurrencyConversions", key = "#source + '-' + #target + '-' + #amount")
    @CircuitBreaker(name = "currencyService", fallbackMethod = "fallbackConvert")
    public CurrencyConversionResponse convert(String source, String target, BigDecimal amount) {
        String normalizedSource = source.toUpperCase();
        String normalizedTarget = target.toUpperCase();

        if (normalizedSource.equals(normalizedTarget)) {
            return new CurrencyConversionResponse(
                    normalizedSource,
                    normalizedTarget,
                    amount,
                    BigDecimal.ONE,
                    amount,
                    LocalDate.now(),
                    false
            );
        }

        return currencyClient.convert(normalizedSource, normalizedTarget, amount);
    }

    public CurrencyConversionResponse fallbackConvert(String source, String target, BigDecimal amount, Throwable ex) {
        return new CurrencyConversionResponse(
                source.toUpperCase(),
                target.toUpperCase(),
                amount,
                BigDecimal.ONE,
                amount,
                LocalDate.now(),
                true
        );
    }
}
