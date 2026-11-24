package org.route.model;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class EmailNotificationSender extends AbstractNotificationSender{

    @Override
    public boolean supports(Customer customer) {
        return customer != null
                && customer.email() != null
                && !customer.email().isBlank();
    }

    @Override
    public String channel() {
        return "Email";
    }

    @Override
    public String address(Customer customer) {
        return customer.email();
    }
}
