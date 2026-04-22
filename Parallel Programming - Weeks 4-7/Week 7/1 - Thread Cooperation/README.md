# Thread Cooperation

This is the C version of Java's `ThreadCooperation` from Week 3. Two threads share a bank account: one deposits, one withdraws. The withdraw thread must **wait** if there isn't enough money.

This introduces **condition variables** — a way for threads to cooperate, not just avoid collisions.

In Week 6, we used a mutex to prevent race conditions (threads stepping on each other). But a mutex alone can't express "wait until something happens." For that, we need:

```c
pthread_cond_wait(&cond_var, &mutex);      // "sleep until signaled, release the lock while I wait"
pthread_cond_broadcast(&cond_var);          // "wake up everyone waiting on this condition"
```

Compare with Java:
```java
newDeposit.await();       // same as pthread_cond_wait
newDeposit.signalAll();   // same as pthread_cond_broadcast
```

The critical pattern is the **while loop** around the wait:
```c
while(balance < withdrawAmount){
    pthread_cond_wait(&cond_var, &mutex);
}
```

Why `while` and not `if`? Because after being woken up, the condition might still be false (the deposit wasn't enough). Always re-check.

**Compile and run:**
```bash
gcc -o cooperation thread_cooperation.c -lpthread
./cooperation
```
