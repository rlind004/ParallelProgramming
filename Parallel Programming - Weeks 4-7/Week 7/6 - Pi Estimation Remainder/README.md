# Pi Estimation — Remainder Handling

Same as version 1 (block distribution), but fixes the case where `n` is not evenly divisible by the number of threads.

The fix is simple — the **last thread** extends its range to cover the leftover terms:

```c
if (my_rank == thread_count - 1) {
    my_last_i = n;
}
```

Example with n=1000 and 3 threads:

| Thread | Version 1 (broken) | Version 2 (fixed) |
|--------|--------------------|--------------------|
| 0 | terms 0–332 | terms 0–332 |
| 1 | terms 333–665 | terms 333–665 |
| 2 | terms 666–998 | terms 666–**999** |
| **Total** | **999 terms (missing 1!)** | **1000 terms** |

The trade-off: the last thread does slightly more work than the others. This is a minor **load imbalance**. For small remainders it doesn't matter, but if the imbalance is large, the cyclic distribution in version 3 is better.

**Compile and run:**
```bash
gcc -g -Wall -o pi2 pi_estimation_remainder.c -lpthread -lm
./pi2 3 1000
```
