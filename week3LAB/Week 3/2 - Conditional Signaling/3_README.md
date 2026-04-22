# Conditional Signaling

Better than the original but still bad. The account keeps a withdrawRequest field—how much does the withdraw thread need? However, the deposit method only invokes signalAll() when the balance is sufficiently large that it can actually fill the withdrawal.

This avoids spurious wakeups where the withdrawal thread gets woken, checks the balance, finds it still insufficient, and goes straight back to sleep.
