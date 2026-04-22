# ReentrantLock with Thread Names


Same benevolence, but provide each thread a name ("1" through "100") and print out who did the deposit. The print is inside the locked section itself, so the output is consistent—you know exactly which thread deposited and what the balance was right after.

The ordering of operations is still not guaranteed (say, thread 47 could be ahead of thread 3), but each operation has been done correctly and reflected in the output.
