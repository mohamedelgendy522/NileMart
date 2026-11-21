package org.route.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    ELECTRONICS("Electronics"),
    FASHION("Fashion"),
    HOME_KITCHEN("Home & Kitchen"),
    BEAUTY("Beauty"),
    BOOKS("Books");

    private final String label;
}