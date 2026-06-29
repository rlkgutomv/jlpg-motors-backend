package br.edu.atitus.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CurrencyConversionResponse {

    private String source;
    private String target;
    private BigDecimal amount;
    private BigDecimal rate;
    private BigDecimal convertedAmount;
    private LocalDate rateDate;
    private boolean fallback;

    public CurrencyConversionResponse() {
    }

    public CurrencyConversionResponse(String source, String target, BigDecimal amount, BigDecimal rate,
                                      BigDecimal convertedAmount, LocalDate rateDate, boolean fallback) {
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
        this.rateDate = rateDate;
        this.fallback = fallback;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public LocalDate getRateDate() {
        return rateDate;
    }

    public void setRateDate(LocalDate rateDate) {
        this.rateDate = rateDate;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }
}
