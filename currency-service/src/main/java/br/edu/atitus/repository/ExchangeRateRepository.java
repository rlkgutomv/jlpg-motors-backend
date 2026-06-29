package br.edu.atitus.repository;

import br.edu.atitus.model.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, UUID> {

    Optional<ExchangeRateEntity> findBySourceCurrencyAndTargetCurrencyAndQuotationDate(
            String sourceCurrency,
            String targetCurrency,
            LocalDate quotationDate);

    Optional<ExchangeRateEntity> findTopBySourceCurrencyAndTargetCurrencyOrderByQuotationDateDesc(
            String sourceCurrency,
            String targetCurrency);
}
