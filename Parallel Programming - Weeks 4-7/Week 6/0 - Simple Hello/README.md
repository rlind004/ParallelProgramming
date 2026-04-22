# Simple Hello

The absolute simplest pthreads example. One thread, one argument, one join.

This introduces the three fundamental pthreads operations:

```c
pthread_t my_thread;                                      // declare a thread handle
pthread_create(&my_thread, NULL, say_hello, &number);     // create and start
pthread_join(my_thread, NULL);                             // wait for it to finish
```

Compare with Java:
```java
Thread t = new Thread(myRunnable);   // create
t.start();                           // start (separate step in Java)
t.join();                            // wait
```

The key concept here is `void*` — the only way to pass data to a thread in C. Since C has no generics or constructors, everything goes through a single `void*` pointer:
- You pass `&number` (an address) to `pthread_create`
- Inside the thread function, you cast it back: `int my_number = *((int*)arg);`

Run it multiple times. Sometimes the main thread prints first, sometimes the child does.

**Compile and run:**
```bash
gcc -o simple simple_hello.c -lpthread
./simple
```
