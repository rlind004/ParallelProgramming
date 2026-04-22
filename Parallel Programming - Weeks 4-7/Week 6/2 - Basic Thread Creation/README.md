# Basic Thread Creation

This is the "Hello World" of pthreads. We create N threads, each one prints a message with its rank, and the main thread waits for all of them to finish.

The core pattern in C is always:

1. **Allocate** thread handles (`malloc`)
2. **Create** threads (`pthread_create`)
3. **Join** threads (`pthread_join`) — wait for them to finish
4. **Free** the handles (`free`)

Compare this with Java, where you just do `new Thread(r).start()` and `thread.join()`. In C, you deal with memory manually, pass data through `void*` pointers, and write standalone functions instead of `run()` methods.

Notice that the output order is non-deterministic — just like in Java. Run it a few times and you'll see the threads print in different orders each time. The OS scheduler decides who runs when.

**Compile and run:**
```bash
gcc -o basic_thread_creation basic_thread_creation.c -lpthread
./basic_thread_creation 4
```
