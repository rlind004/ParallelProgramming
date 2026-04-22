# Thread Sleep

Next, we see what happens when a thread stops itself. The PrintNum task calls Thread.sleep(5000) (meaning the script waits for 5 seconds) when that row reaches 80. And the other two threads keep printing 'a' and 'b' respectively without interruption.

You must wrap the sleep() method in a try-catch block because Java can throw an InterruptedException, as specified by the API. The key thing to remember is that sleep affects only the thread that calls it—everyone else continues unabated.

When you run this, numbers will print up to 80, then after a noticeable pause, only 'a' and 'b' characters appear, and finally the numbers resume from 81 to 100.
