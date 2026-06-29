package br.edu.atitus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BcbPtaxResponse {

    @JsonProperty("value")
    private List<BcbRateRecord> value;

    public List<BcbRateRecord> getValue() {
        return value;
    }

    public void setValue(List<BcbRateRecord> value) {
        this.value = value;
    }
}
