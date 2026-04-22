# Passing Structs to Threads

In Java, when you want a thread to work with multiple pieces of data, you just put fields in your Runnable class and set them through a constructor. Easy.

In C, there are no classes. The equivalent pattern is:

1. **Define a struct** with all the data a thread needs
2. **Create one struct instance per thread** and fill it with data
3. **Pass a pointer** to that struct via `pthread_create`
4. **Cast it back** inside the thread function (`void*` → `your_struct*`)

The `void*` parameter in `pthread_create` is C's way of saying "you can pass anything here, but you're responsible for knowing what it actually is." There's no type safety — if you cast it to the wrong type, you'll get garbage or a crash.

Each thread in this example receives its own separate struct, so there's no sharing problem. If you accidentally passed the same struct to multiple threads, they'd step on each other's data.

**Compile and run:**
```bash
gcc -o passing_structs passing_structs_to_threads.c -lpthread
./passing_structs
```
