# N Threads

Scales up from script 0 (one thread) to a user-chosen number of threads. Each thread prints its own rank.

This introduces two new concepts:

**1. `malloc` (dynamic memory allocation)**

We don't know how many threads the user will request at compile time, so we can't declare a fixed-size array. `malloc` asks the OS for memory at runtime:

```c
pthread_t* threads = malloc(thread_count * sizeof(pthread_t));
```

In Java, `new Thread[n]` handles this automatically. In C, you allocate with `malloc` and must `free` when done.

**2. Separate rank variable per thread**

Why not just pass `&i` to each thread? Because `i` changes in the loop. By the time thread 2 reads it, `i` might already be 5. Each thread needs its own copy:

```c
int* ranks = malloc(thread_count * sizeof(int));
ranks[i] = i;
pthread_create(&threads[i], NULL, say_hello, &ranks[i]);
```

This is the bridge between script 0 (one hardcoded thread) and script 2 (N threads with command line args and validation).

**Compile and run:**
```bash
gcc -o n_threads n_threads.c -lpthread
./n_threads
```
