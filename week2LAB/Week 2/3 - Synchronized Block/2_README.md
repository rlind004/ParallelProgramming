# Synchronized Block

We apply a different style for the same fix. Rather than making the entire method synchronized, we encapsulate just the critical part in a block synchronized(this) {...}.

The outcome is identical — balance always totals 100. But this way, you get to decide which lines are exactly protected. Any work above or below the block executes without holding the lock (this approach matters for performance, especially when the method does more than just the critical section).
