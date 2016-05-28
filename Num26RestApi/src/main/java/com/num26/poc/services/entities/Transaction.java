package com.num26.poc.services.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 */
public class Transaction {
    private long id;
    private double amount;
    private String type;
    private Transaction parent;
    private List<Transaction> children;

    public Transaction(final long _id,
                       final double _amount,
                       final String _type,
                       final Transaction _parent) {
        id = _id;
        amount = _amount;
        type = _type;
        parent = _parent;
        children = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Transaction getParent() {
        return parent;
    }

    public void addChild(final Transaction _child) {
        children.add(_child);
    }

    public Stream<Transaction> getChildren(){
        return children.stream();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Transaction that = (Transaction) o;

        if (Double.compare(that.amount, amount) != 0) return false;
        if (id != that.id) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + type.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}
