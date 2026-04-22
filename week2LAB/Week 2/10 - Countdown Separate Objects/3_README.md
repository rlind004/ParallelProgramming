# Countdown — Separate Objects

Completely different approach. Instead of a single, shared Countdown object, each thread has its own. There is no race condition, and there is no need for synchronization because nothing is shared.

Instead of one shared countdown, your output includes two independent counts. The bottom line: In some cases, the simplest solution to concurrency issues is simply not sharing data to begin with.
