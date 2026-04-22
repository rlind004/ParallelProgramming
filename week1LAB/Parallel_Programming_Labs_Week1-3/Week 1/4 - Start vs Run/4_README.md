# Start vs Run

This one contains a very common beginner error. Instead of calling thread.start(), we call thread.run(). The difference matters a lot:


− start() actually creates a new thread and runs the task on it.

− run() is just a regular method call—it runs in the current thread, blocking

So in this implementation, all code runs on the main thread, one after another. The output is always the same: all of the 'a's, then all of the 'b's, and then the numbers. No parallelism at all.

The tasks also print Thread.currentThread().getName(), so you can see they all say "main"—this is evidence that new threads were NOT created.

You just need to replace run() with start(), and you'll notice the difference right away.
