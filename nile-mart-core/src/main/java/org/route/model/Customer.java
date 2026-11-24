package org.route.model;

import lombok.Getter;
import org.route.common.StoreException;

public record Customer(String fullName, String phone, String email) {
    @Override
    public String fullName() {
        return fullName;
    }

    @Override
    public String phone() {
        return phone;
    }

    @Override
    public String email() {
        return email;
    }

    public Customer {
        if (fullName == null || fullName.isBlank()) {
            throw new StoreException("Customer full name cannot be blank.");
        }
        if (phone == null || !phone.matches("01[0125][0-9]{8}")) {
            throw new StoreException("Phone must be a valid 11-digit Egyptian mobile number starting with 010, 011, 012, or 015.");
        }
        if (email != null && email.isBlank()) {
            email = null;
        }
        if (email != null && !email.contains("@")) {
            throw new StoreException("Customer email must contain '@'.");
        }

    }
}