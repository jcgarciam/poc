/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.num26.poc.rest.transaction;

import com.num26.poc.rest.model.OkResult;
import com.num26.poc.rest.model.SumResponse;
import com.num26.poc.rest.transaction.model.TransactionDTO;
import com.num26.poc.services.TransactionService;
import com.num26.poc.services.entities.Transaction;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;

/**
 *
 */
@Path("/transactionservice")
public class TransactionsResources {
    private TransactionService transactionService;

    @Deprecated
    public TransactionsResources(){}

    @Inject
    public TransactionsResources(TransactionService _transactionService){
        transactionService = _transactionService;
    }

    @Path("/transaction/{transaction_id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response transaction(@Context UriInfo _uriInfo, TransactionDTO  _transactionDTO){
        Objects.nonNull(_uriInfo);
        Objects.nonNull(_transactionDTO);

        long transactionId = Long.parseLong(_uriInfo
                .getPathParameters()
                .get("transaction_id")
                .get(0));

        transactionService.save(transactionId, _transactionDTO);

        return Response.status(200).entity(new OkResult()).build();
    }

    @Path("/transaction/{transaction_id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response transaction(@PathParam("transaction_id") long transactionId){

        Transaction found = transactionService.findById(transactionId);
        if(found != null) {
            long parentId = -1;
            if(found.getParent() != null){
                parentId = found.getParent().getId();
            }
            return Response.status(200).entity(new TransactionDTO(
                      found.getAmount()
                    , found.getType()
                    , parentId))
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("/types/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response types(@PathParam("type") String type){

        final long[] transactionsByTypes
                = transactionService.findTransactionsByTypes(type);

        return Response.status(200).entity(transactionsByTypes).build();
    }

    @Path("/sum/{transaction_id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response sum(@PathParam("transaction_id") long transaction_id){
        double sum = transactionService.sumTransaction(transaction_id);
        return Response.status(200).entity(new SumResponse(sum)).build();
    }
}
