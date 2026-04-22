# Thread Creation

This is our starting point. We run three tasks in parallel:

Task 1: prints the letter 'a' 100 times.

Task 2: prints the letter 'b' 100 times.

Task 3: prints numbers from 1 to 100.

We wrap each task in a thread and call start() to get them going. Because all three threads run concurrently, their output is intermixed. If you run the program twice, you’re likely going to get a different output each time — that’s the whole point. Threads are not nice; the OS schedules them however it feels like.

Both PrintChar and PrintNum implement Runnable, which means they have a method called run) that specifies what the thread should do.

Go ahead and run it a couple of times to see the non-determinism for yourself by comparing outputs.
