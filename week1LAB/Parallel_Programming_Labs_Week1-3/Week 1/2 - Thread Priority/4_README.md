# Thread Priority

Now we have the same program as in the first example, but before starting the threads we assign priorities to them:



Thread prints 'a' has MIN_PRIORITY (lowest).

The thread prints 'b' and gets MAX_PRIORITY (the highest).

– Thread prints numbers and gets NORM_PRIORITY (default)



The implication here is that the scheduler will be able to assign more CPU time to higher-priority threads. In practice, however, this is merely a hint—the JVM and the OS are free to ignore it. Don’t rely on priorities for anything important; they are more like a soft suggestion.



Run it a few times. You may see that 'b' seems to appear sooner in the output, but it's far from precise.
