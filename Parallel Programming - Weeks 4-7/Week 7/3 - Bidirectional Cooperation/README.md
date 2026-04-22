# Bidirectional Cooperation

In the previous versions, only the withdraw thread could wait. The deposit thread always succeeded. This version adds a rule: **the balance cannot exceed 100** (`MAX_INSURANCE_LIMIT`).

Now both threads can be forced to wait:
- **Withdraw** waits if balance is too low (same as before)
- **Deposit** waits if balance + amount would exceed the limit (new)

This requires **two condition variables**:

```c
pthread_cond_t cond_var;    // "a deposit happened" — withdraw waits on this
pthread_cond_t cond_var2;   // "a withdrawal happened" — deposit waits on this
```

The deposit thread signals `cond_var` after depositing (wakes withdraw). The withdraw thread signals `cond_var2` after withdrawing (wakes deposit).

Compare with Java's `ThreadCooperationV3`, which uses two `Condition` objects from the same `ReentrantLock`.

This is the **producer-consumer pattern**: the deposit thread is the producer, the withdraw thread is the consumer, and the balance is a bounded buffer that can't go below 0 or above 100.

**Compile and run:**
```bash
gcc -o cooperation3 thread_cooperation_v3.c -lpthread
./cooperation3
```
