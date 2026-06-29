package br.edu.atitus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class BcbRateRecord {

    @JsonProperty("cotacaoVenda")
    private BigDecimal sellRate;

    @JsonProperty("dataHoraCotacao")
    private String quotationDateTime;

    public BigDecimal getSellRate() {
        return sellRate;
    }

    public void setSellRate(BigDecimal sellRate) {
        this.sellRate = sellRate;
    }

    public String getQuotationDateTime() {
        return quotationDateTime;
    }

    public void setQuotationDateTime(String quotationDateTime) {
        this.quotationDateTime = quotationDateTime;
    }
}
