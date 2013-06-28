package com.IBConnector.Orders;

import java.util.HashMap;

public enum OrderStatus {

    UNKNOWN("UNKNOWN"), PENDING_SUBMIT("PendingSubmit"), PENDING_CANCEL("PendingCancel"),
    PRE_SUBMITTED("PreSubmitted"), SUBMITTED("Submitted"), CANCELLED("Cancelled"), FILLED("Filled"), INACTIVE(
            "Inactive"), EMPTY("");

    private final String label;
    private static final HashMap<String, OrderStatus> MAP;

    static {
        MAP = new HashMap<String, OrderStatus>();
        for (final OrderStatus orderStatus : values()) {
            MAP.put(orderStatus.getLabel().toUpperCase(), orderStatus);
        }
    }

    private OrderStatus(final String label) {
        this.label = label;
    }

    public final String getLabel() {
        return label;
    }

    public static final OrderStatus fromLabel(final String label) {
        if (label.equals("")) {
            return EMPTY;
        }
        final String labelUpperCase = label.toUpperCase();
        if (MAP.containsKey(labelUpperCase)) {
            return MAP.get(labelUpperCase);
        }
        return UNKNOWN;
    }
}
