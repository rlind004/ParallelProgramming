# Conditional Signaling

This extends the previous example. In version 1, the deposit thread broadcasts **every time** it deposits, even when the balance still isn't enough for the pending withdrawal. That causes the withdraw thread to wake up, re-check, and go back to sleep for nothing.

This version adds a shared variable `requestedAmount`. The withdraw thread writes how much it needs, and the deposit thread only signals when the balance meets that requirement:

```c
// Withdraw thread:
requestedAmount = withdrawAmount;           // "I need this much"
pthread_cond_wait(&cond_var, &mutex);       // "wake me when it's ready"

// Deposit thread:
if(balance >= requestedAmount) {            // "is it enough now?"
    pthread_cond_broadcast(&cond_var);      // "yes, wake up"
}
```

Compare with Java's `ThreadCooperationV2`: same optimization — avoid unnecessary `signalAll()` calls.

The trade-off: fewer wasted wake-ups, but the deposit thread now needs to know about withdrawal requests. This couples the two threads more tightly.

**Compile and run:**
```bash
gcc -o cooperation2 thread_cooperation_v2.c -lpthread
./cooperation2
```
