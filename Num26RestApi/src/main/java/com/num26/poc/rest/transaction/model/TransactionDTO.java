package com.num26.poc.rest.transaction.model;

/**
 */

public class TransactionDTO {
    private double amount;
    private String type;
    private long parentId;

    @Deprecated
    TransactionDTO() {
    }

    public TransactionDTO(final double _amount, final String _type, final long _parentId) {
        amount = _amount;
        type = _type;
        parentId = _parentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public long getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "amount=" + amount +
                ", type='" + type + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
