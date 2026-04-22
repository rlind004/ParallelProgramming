Week 3 — Thread Cooperation



This week is about threads cooperating — one thread waits for something to occur, and the other thread notifies it when it does. All examples are using ReentrantLock with the Condition objects.



**Wait and Signal**—withdraw waits for deposit; deposit signals after adding money.

**Conditional Signaling**—signal only when the balance is indeed sufficient

**Bidirectional Cooperation**—deposit and withdrawal both can wait (with a balance limit at 100)

Producer-Consumer with a bounded buffer and notFull/notEmpty conditions
