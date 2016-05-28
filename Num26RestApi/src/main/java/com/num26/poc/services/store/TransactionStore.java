package com.num26.poc.services.store;

import com.num26.poc.services.entities.Transaction;

import java.util.stream.Stream;

/**
 */
public interface TransactionStore {
    void save (Transaction _transaction);

    Transaction findById(long _id);

    Stream<Long> findByType(String _type);
}
