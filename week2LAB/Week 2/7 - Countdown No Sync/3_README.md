# Countdown — No Synchronization

We have two threads that share the same Countdown object and then try to count down from 30 to 0. And there’s no synchronization, so the threads stomp on each other. Some numbers are skipped or repeated, and numbers may appear out of sequence.

Same idea as the account race condition but more visible since the result is an output that should be a sequence and isn't. Using colored output makes it easier to tell which thread printed what lines. Try running it a few different times and compare.
