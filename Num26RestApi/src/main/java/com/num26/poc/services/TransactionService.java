package com.num26.poc.services;

import com.num26.poc.rest.transaction.model.TransactionDTO;
import com.num26.poc.services.entities.Transaction;
import com.num26.poc.services.store.TransactionStore;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;

/**
 */
@ApplicationScoped
public class TransactionService {
    private TransactionStore transactionStore;

    @Deprecated
    TransactionService(){}

    @Inject
    public TransactionService(TransactionStore __transactionStore) {
        transactionStore = __transactionStore;
    }

    public void save(long _id, TransactionDTO _transaction){
        Objects.nonNull(_transaction);
        Transaction parent = findById(_transaction.getParentId());

        Transaction newTrx = new Transaction(_id
                , _transaction.getAmount()
                , _transaction.getType()
                , parent);

        if(parent != null) {
            parent.addChild(newTrx);
        }
        transactionStore.save(newTrx);
    }

    public Transaction findById(long _id){
        return transactionStore.findById(_id);
    }

    public long[] findTransactionsByTypes(String _type){

        return transactionStore
                .findByType(_type)
                .mapToLong(m-> m)
                .toArray();
    }

    public double sumTransaction(long _id){
        Transaction root = transactionStore.findById(_id);
        if(root == null){
            return 0;
        }
        return recursiveFlat(root).mapToDouble(f -> f.getAmount()).sum();
    }

    private Stream<Transaction> recursiveFlat(final Transaction _transaction) {
        return Stream.concat(
                Stream.of(_transaction),
                _transaction.getChildren().flatMap(this::recursiveFlat));
    }
}
