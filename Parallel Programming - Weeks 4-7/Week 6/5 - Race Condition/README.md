# Race Condition

This is the classic race condition example, now in C. 100 threads each try to add 1 to a shared `balance` variable. If everything worked perfectly, the final balance should be 100. But it's often less.

The problem is that `deposit()` is not atomic. It does three things:
1. **Read** the current balance
2. **Compute** the new balance (old + 1)
3. **Write** the new balance back

If two threads both read the balance as 50 before either writes back, they both write 51. One deposit is lost. This is the same problem demonstrated in Java's `AccountWithoutSynchronization` from Week 2.

The fix is synchronization — which is shown in the next exercise using `pthread_mutex`.

Run this several times. You'll see the final balance vary. That non-determinism is the race condition at work.

**Compile and run:**
```bash
gcc -o race_condition race_condition_no_lock.c -lpthread
./race_condition
```
