package br.edu.atitus.client;

import br.edu.atitus.dto.CurrencyConversionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(name = "currency-service")
public interface CurrencyClient {

    @GetMapping("/convert")
    CurrencyConversionResponse convert(
            @RequestParam String source,
            @RequestParam String target,
            @RequestParam BigDecimal amount);
}
