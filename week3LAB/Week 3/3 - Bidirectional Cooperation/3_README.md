# Bidirectional Cooperation

Now both sides can block. The withdrawal thread waits if the balance is too low (same as before), but now the deposit thread will also wait if adding money would push us over 100.

We use two Condition objects on the same lock here:

– newDeposit—the withdraw thread waits on this, and the deposit thread signals it.

– newWithdraw—the deposit thread waits on this; the withdraw thread signals it.

This is a bounded-resources pattern. You will see a similar structure in any scenario where both producer and consumer can block each other.
