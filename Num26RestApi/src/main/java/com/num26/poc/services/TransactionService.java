package com.num26.poc.services;

import com.num26.poc.rest.transaction.model.TransactionDTO;
import com.num26.poc.services.entities.Transaction;
import com.num26.poc.services.store.TransactionStore;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

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
        Set<Long> visited = new HashSet<>();
        Stack<Transaction> elements = new Stack<>();
        elements.push(root);
        double sum = 0;
        while(!elements.empty()){
            Transaction popped = elements.pop();
            visited.add(popped.getId());
            sum += popped.getAmount();

            popped.getChildren().forEach(m-> {
                if(!visited.contains(m.getId())){
                    elements.push(m);
                }
            });
        }
        return sum;
    }
}
