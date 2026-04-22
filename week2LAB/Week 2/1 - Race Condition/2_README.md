# Race Condition

This is where we see the problem that the rest of Week 2 is about fixing.

This scenario is where we see the problem that the rest of Week 2 will try to fix.

We create 100 threads, where each one deposits 1 in the same bank account. So we should end up with a final balance of 100. But it’s not—it's probably less and different each time you run it.

deposit(), the issue is that it reads the balance, adds 1, and then writes it back. But with both read and write, there is another thread waiting to take the same action. Both threads read the same stale balance, both add 1, and both write back the same final value—and we lost a deposit.

This scenario is a race condition. The correctness of the program depends on what thread happens to run first, and that changes every time.
