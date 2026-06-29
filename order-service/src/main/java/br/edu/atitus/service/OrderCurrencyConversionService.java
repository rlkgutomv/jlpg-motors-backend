package br.edu.atitus.service;

import br.edu.atitus.client.CurrencyClient;
import br.edu.atitus.dto.CurrencyConversionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class OrderCurrencyConversionService {

    private final CurrencyClient currencyClient;

    public OrderCurrencyConversionService(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    @CircuitBreaker(name = "currencyService", fallbackMethod = "currencyFallback")
    public CurrencyConversionResponse convert(String source, String target, BigDecimal amount) {
        if (source.equalsIgnoreCase(target)) {
            return new CurrencyConversionResponse(
                    source.toUpperCase(),
                    target.toUpperCase(),
                    amount,
                    BigDecimal.ONE,
                    amount,
                    LocalDate.now(),
                    false
            );
        }

        return currencyClient.convert(source.toUpperCase(), target.toUpperCase(), amount);
    }

    public CurrencyConversionResponse currencyFallback(String source, String target, BigDecimal amount, Throwable ex) {
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
