# Week 7 — Thread Cooperation & Parallel Computation

This week covers two topics: thread cooperation using condition variables, and a real-world parallel computation example (estimating pi).

Examples 1–3 use a bank account scenario where a deposit thread and a withdraw thread must cooperate. Example 4 is a quiet warm-up that introduces the parallel work pattern on a trivial sum, so the pi estimators in 5–7 only have to teach the math side of things.

1. **Thread Cooperation** — deposit/withdraw with mutex + condition variable
2. **Conditional Signaling** — signal only when balance is sufficient
3. **Bidirectional Cooperation** — both threads can wait (deposit limit + insufficient funds)
4. **Parallel Sum** — parallel sum of 1..N with block distribution and a private accumulator
5. **Pi Estimation (Block)** — each thread computes a contiguous block of terms
6. **Pi Estimation (Remainder)** — last thread handles leftover terms
7. **Pi Estimation (Cyclic)** — round-robin distribution for better load balancing
