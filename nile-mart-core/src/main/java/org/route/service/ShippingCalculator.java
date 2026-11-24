package org.route.service;

import org.route.model.Governorate;

import java.math.BigDecimal;

public interface ShippingCalculator {
    public BigDecimal calculate(Governorate g);
}
