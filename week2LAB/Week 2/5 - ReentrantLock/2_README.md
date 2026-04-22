# ReentrantLock

This example completely drops the synchronized keyword in favor of using ReentrantLock. You must explicitly call lock.lock() before entering the critical section and lock.unlock() after exiting it.

It does what "synchronized" does—mutual exclusion—but more flexibly. You can do things with ReentrantLock that synchronized cannot, like tryLock (attempt to acquire without blocking) or creating Condition objects for thread cooperation (which we'll use heavily in Week 3).

The total still comes out to 100. It is simply a more verbose but also more powerful approach.
