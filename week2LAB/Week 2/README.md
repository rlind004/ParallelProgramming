# Week 2 — Synchronization

This week is about what goes wrong when multiple threads access shared data, and the different ways to fix it.

Examples 1–6 use a bank account scenario (100 threads depositing 1 each). Examples 7–10 use a countdown counter.

1. **Race Condition** — no protection, broken result
2. **Synchronized Method** — lock the entire method
3. **Synchronized Block** — lock just the critical section
4. **Print Outside Lock** — what happens when the print isn't protected
5. **ReentrantLock** — explicit lock/unlock (more flexible alternative)
6. **ReentrantLock with Thread Names** — same but shows which thread did what
7. **Countdown No Sync** — race condition on a shared counter
8. **Countdown Synchronized** — fixed with synchronized block
9. **Countdown Three Threads** — synchronization scales to any number of threads
10. **Countdown Separate Objects** — avoid sharing altogether
