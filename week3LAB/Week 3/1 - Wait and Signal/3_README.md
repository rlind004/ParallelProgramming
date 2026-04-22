# Wait and Signal



A thread for deposits, a thread for withdrawals—a bank account. The withdrawal thread requires a sufficient balance to perform its action, but the account may have zero. So it calls await() on a condition and sleeps. The deposit thread adds money and then calls signalAll() to wake up whoever's waiting.



The main pattern is there's a while loop around the await(). After waking up, we re-verify whether the balance is sufficient—because perhaps the deposit was not enough, or something else changed. Always use while (not if) when waiting on conditions.
