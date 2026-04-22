# Print Outside Lock

This one is a bit tricky. The balance update occurs in the synchronized block, so the final result is also always correct — 100. However, the println showing the current balance is outside the block.

What happens: thread A updates the balance from 50 to 51, releases the lock, thread B grabs it and updates it to 52, releases it, and then thread A prints—but at this point the balance is now 52, not 51. So the printed output may show duplicates, miss values, or appear out-of-order.

The data is fine. Only the display is deceptive. Lesson here: if you want the print to match what just transpired, it needs to be on the inside of the lock as well.
