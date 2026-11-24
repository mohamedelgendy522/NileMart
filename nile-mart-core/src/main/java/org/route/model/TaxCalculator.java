package org.route.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TaxCalculator {
    private final double vatRate;
    public TaxCalculator(@Value("${store.vat-rate}") double vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal calculate(BigDecimal subtotal){
        return subtotal.multiply(BigDecimal.valueOf(vatRate));
    }
}
