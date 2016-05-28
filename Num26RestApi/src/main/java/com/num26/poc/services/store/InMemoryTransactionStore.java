package com.num26.poc.services.store;

import com.num26.poc.services.entities.Transaction;

import javax.enterprise.context.ApplicationScoped;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 */
@ApplicationScoped
public class InMemoryTransactionStore implements TransactionStore {
    private Map<Long, Transaction> transactions;
    private Map<String, List<Transaction>> typeRelation;

    public InMemoryTransactionStore() {
        transactions = new ConcurrentHashMap<>();
        typeRelation = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Transaction _transaction){
        transactions.put(_transaction.getId(), _transaction);
        typeRelation
                .computeIfAbsent(_transaction.getType(), f -> new LinkedList<>())
                .add(_transaction);
    }

    @Override
    public Transaction findById(long _id){
        return transactions.get(Long.valueOf(_id));
    }

    @Override
    public Stream<Long> findByType(String _type){
        return typeRelation
                .computeIfAbsent(_type, m -> new LinkedList<>())
                .stream().mapToLong(m->m.getId())
                .boxed();
    }
}
