package com.num26.poc.services;

import com.num26.poc.rest.transaction.model.TransactionDTO;
import com.num26.poc.services.entities.Transaction;
import com.num26.poc.services.store.InMemoryTransactionStore;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.LongStream;

/**
 */
public class TransactionServiceTest {
    TransactionService transactionService;

    @Before
    public void tearup(){
        transactionService = new TransactionService(new InMemoryTransactionStore());
    }

    @Test
    public void shouldSaveTransaction(){
        //given
        TransactionDTO transaction = new TransactionDTO(2.0, "car", 0);

        //when
        transactionService.save(1, transaction);

        //then
        Transaction found = transactionService.findById(1);
        Assert.assertThat(found, CoreMatchers.notNullValue());
        Assert.assertThat(found.getType(), Is.is("car"));
        Assert.assertThat(found.getAmount(), Is.is(2.0));
        Assert.assertThat(found.getParent(), CoreMatchers.nullValue());
    }

    @Test
    public void shouldLinkTransaction(){
        //given
        transactionService.save(1, new TransactionDTO(2.0, "car", 0));
        transactionService.save(2, new TransactionDTO(2.0, "headphone", 1));
        transactionService.save(3, new TransactionDTO(2.0, "hammer", 1));
        transactionService.save(4, new TransactionDTO(2.0, "nail", 3));

        //when
        Transaction t1 = transactionService.findById(1);
        Transaction t2 = transactionService.findById(2);
        Transaction t3 = transactionService.findById(3);
        Transaction t4 = transactionService.findById(4);

        //then
        Assert.assertThat(t1.getParent(), CoreMatchers.nullValue());
        Assert.assertThat(t2.getParent(), CoreMatchers.notNullValue());
        Assert.assertThat(t2.getParent(), Is.is(t1));
        Assert.assertThat(t1.getChildren().count(), Is.is(2L));
        Assert.assertThat(t3.getParent(), Is.is(t1));
        Assert.assertThat(t3.getChildren().count(), Is.is(1L));
        Assert.assertThat(t4.getParent(), Is.is(t3));
        Assert.assertThat(t4.getChildren().count(), Is.is(0L));
    }


    @Test
    public void shouldSumRelatedTransaction(){
        //given / when
        transactionService.save(1, new TransactionDTO(2.0, "car", 0));
        transactionService.save(2, new TransactionDTO(2.0, "headphone", 1));
        transactionService.save(3, new TransactionDTO(2.0, "hammer", 1));
        transactionService.save(4, new TransactionDTO(2.0, "nail", 3));
        transactionService.save(5, new TransactionDTO(2.0, "shoes", 0));
        transactionService.save(6, new TransactionDTO(2.0, "nike", 5));
        transactionService.save(7, new TransactionDTO(2.0, "rebook", 6));


        double sum1 = transactionService.sumTransaction(1);
        double sum2 = transactionService.sumTransaction(2);
        double sum3 = transactionService.sumTransaction(3);
        double sum5 = transactionService.sumTransaction(5);

        //then
        Assert.assertThat(sum1, Is.is(8.0));
        Assert.assertThat(sum2, Is.is(2.0));
        Assert.assertThat(sum3, Is.is(4.0));
        Assert.assertThat(sum5, Is.is(6.0));
    }

    @Test
    public void shouldListRelatedTransactionByType(){
        //given
        transactionService.save(1, new TransactionDTO(2.0, "car", 0));
        transactionService.save(2, new TransactionDTO(2.0, "headphone", 1));
        transactionService.save(3, new TransactionDTO(2.0, "hammer", 1));
        transactionService.save(4, new TransactionDTO(2.0, "nail", 3));
        transactionService.save(5, new TransactionDTO(2.0, "car", 0));

        //when
        long[] tids = transactionService.findTransactionsByTypes("car");
        long[] tids2 = transactionService.findTransactionsByTypes("headphone");
        long[] tids3 = transactionService.findTransactionsByTypes("ABCABC");

        //then
        Assert.assertThat(tids.length, Is.is(2));
        Assert.assertThat(tids2.length, Is.is(1));
        Assert.assertThat(tids3.length, Is.is(0));

    }

    @Test
    public void shouldTestSumWithLargeRelatedTransactions(){
        //given
        long max = 999999;
        LongStream
                .range(1, max)
                .forEach(i -> transactionService.save(i, new TransactionDTO(1.0, "car", i - 1)));
        //when
        double sum1 = transactionService.sumTransaction(1);

        //then
        Assert.assertThat(sum1, Is.is((double)max - 1));
    }
}
