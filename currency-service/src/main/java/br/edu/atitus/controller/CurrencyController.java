package br.edu.atitus.controller;

import br.edu.atitus.dto.CurrencyConversionResponse;
import br.edu.atitus.service.CurrencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/convert")
    public CurrencyConversionResponse convert(
            @RequestParam String source,
            @RequestParam String target,
            @RequestParam(defaultValue = "1") BigDecimal amount) {
        return currencyService.convert(source, target, amount);
    }
}
