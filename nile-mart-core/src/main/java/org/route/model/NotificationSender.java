package org.route.model;

public interface NotificationSender {
    void send(Customer customer, String message);
    default boolean supports(Customer customer) {
        return true;
    }
}
