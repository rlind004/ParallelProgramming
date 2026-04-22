# Join Between Threads



In the earlier version, only the main thread called join(). And here we take it one step up—one worker thread is waiting for another worker thread.

We define a waitFor(Thread t) method for PrintChar. We call printB before starting the threads. waitFor(thread1), which instructs task B, "When you reach iteration 70, stop and wait for thread 1 to finish before continuing. ”.

The result is thread 2 prints 'b' normally for 70 times, then it pauses and waits until thread 1 (the one printing 'a') has already run. Then once thread 1 finishes, thread 2 resumes and completes the last 30 iterations.

The main thread still joins() all three threads before printing -- "MAIN-DONE" as in the previous example.

Try changing that number (70) to something else and see how it affects the output.
