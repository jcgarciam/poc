# Explanation
*The rest-endpoint of this poc was tested under WildFly 10.*

This implementation relies of an InMemory [storage](https://github.com/jcgarciam/poc/blob/master/Num26RestApi/src/main/java/com/num26/poc/services/TransactionService.java) which distribute the Transaction objects across two groups.

 1. ID    -> Transaction objects
     ```Get Element by Id, with access time of 'O(1)'```

 2. TYPES -> List of Transaction objects
     ```List of Ids, with access time 'O(N)' for all transactions under a given type partition.```

 3. For the third requirement (A sum of all transactions that are transitively linked by their parentId):

We rely on the following [*Transation*](https://github.com/jcgarciam/poc/blob/master/Num26RestApi/src/main/java/com/num26/poc/services/entities/Transaction.java) object

```java
public class Transaction {
    private long id;
    private double amount;
    private String type;
    private Transaction parent;
    private List<Transaction> children;
}
```

Having said that, every time we request to save a new Transaction object we make the following links, in our [TransactionService](https://github.com/jcgarciam/poc/blob/master/Num26RestApi/src/main/java/com/num26/poc/services/TransactionService.java#L26):

```java
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
```
And given that we have a Tree like structure for our Transaction object, we can flatten our structure of related children see [TransactionService](https://github.com/jcgarciam/poc/blob/master/Num26RestApi/src/main/java/com/num26/poc/services/TransactionService.java#L53):
using DFS Algorithm 
```java
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
```

See [TransactionServiceTest.java](https://github.com/jcgarciam/poc/blob/master/Num26RestApi/src/test/java/com/num26/poc/services/TransactionServiceTest.java) to check each
requirement.
