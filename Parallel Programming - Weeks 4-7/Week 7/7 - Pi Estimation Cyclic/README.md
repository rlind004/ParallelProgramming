# Pi Estimation — Cyclic Distribution

Same goal as versions 1 and 2, but uses a completely different work distribution strategy: **cyclic (round-robin)** instead of block.

**Block** (versions 1 & 2) — each thread gets a contiguous chunk:
```
Thread 0: terms 0, 1, 2, 3
Thread 1: terms 4, 5, 6, 7
Thread 2: terms 8, 9, 10, 11
```

**Cyclic** (this version) — threads take turns, like dealing cards:
```
Thread 0: terms 0, 3, 6, 9
Thread 1: terms 1, 4, 7, 10
Thread 2: terms 2, 5, 8, 11
```

The loop is simpler — no need to compute ranges or handle remainders:

```c
for (i = my_rank; i <= n; i += thread_count) {
    my_sum += factor / (2 * i + 1);
}
```

Load balancing is nearly perfect automatically. If n=1000 and threads=3, the difference between threads is at most 1 term.

**Comparison of all three versions:**

| | Block (v1) | Remainder (v2) | Cyclic (v3) |
|---|---|---|---|
| Handles n not divisible? | No | Yes | Yes |
| Load balance | Equal (if divisible) | Last thread does more | Near-perfect |
| Code complexity | Simple | Small fix | Simplest loop |
| Cache behavior | Better (contiguous) | Better (contiguous) | Worse (scattered) |

For this computation (no arrays, just math), cache doesn't matter. Cyclic is the cleanest solution.

**Compile and run:**
```bash
gcc -g -Wall -o pi3 pi_estimation_cyclic.c -lpthread -lm
./pi3 4 1000000
```
