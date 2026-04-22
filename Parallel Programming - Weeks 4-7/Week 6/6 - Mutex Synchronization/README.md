# Mutex Synchronization

This fixes the race condition from the previous exercise using a **pthread_mutex** — C's equivalent of Java's `synchronized` keyword or `ReentrantLock`.

A mutex (short for "mutual exclusion") is a lock that ensures only one thread at a time can execute a critical section of code. The pattern is:

```c
pthread_mutex_lock(&mutex);    // acquire the lock (blocks if taken)
// ... critical section ...
balance = balance + 1;         // safe: only one thread here at a time
pthread_mutex_unlock(&mutex);  // release the lock
```

Compare with Java:
```java
synchronized(lock) {           // acquire + auto-release
    balance = balance + 1;
}
```

The big difference: in Java, `synchronized` automatically releases the lock when the block ends (even on exceptions). In C, **you must manually call `pthread_mutex_unlock()`**. Forget it, and every other thread will wait forever — that's a deadlock.

Run this and compare with the unsynchronized version. The balance is now always exactly 100.

**Compile and run:**
```bash
gcc -o mutex_sync mutex_synchronization.c -lpthread
./mutex_sync
```
