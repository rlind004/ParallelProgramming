/*
 * Same idea as script 0, but this time the user decides how many
 * threads we create.
 *
 * Two things are new here. First, we don't know the thread count at
 * compile time, so we use malloc to ask the OS for the right amount
 * of memory at runtime. Second, each thread needs its OWN copy of
 * the rank, or they'll stomp on each other.
 *
 * Why each thread needs its own copy: if we passed &i (the loop
 * variable) directly, by the time a thread actually read it, i may
 * already have moved on to the next iteration. The first thread
 * would see rank 3 even though it was meant to be rank 0. That's
 * one of the sneakier race conditions you run into. The fix is to
 * give each thread a pointer to its own slot in a small int array.
 *
 * Java does both of these things automatically for you. Arrays know
 * their own length; the garbage collector cleans up; and passing an
 * int into a Runnable's constructor copies it. In C, all of that is
 * manual.
 */

#include <stdio.h>      /* printf, scanf */
#include <stdlib.h>     /* malloc, free */
#include <pthread.h>    /* pthread_create, pthread_join */

/*
 * Thread body. Each thread reads its own rank through the pointer
 * it was given, then prints it.
 */
void* say_hello(void* arg) {
    int my_rank = *((int*) arg);
    printf("Hello from thread %d!\n", my_rank);
    return NULL;
}

int main() {
    int thread_count;

    printf("How many threads? ");
    scanf("%d", &thread_count);

    /*
     * Two dynamic arrays: one for the thread handles and one for
     * the per-thread ranks. malloc gives us a pointer to a block
     * of uninitialised memory big enough to hold thread_count
     * elements, which we can then use like a regular C array.
     *
     * In Java this would just be:
     *     Thread[] threads = new Thread[thread_count];
     */
    pthread_t* threads = malloc(thread_count * sizeof(pthread_t));
    int*       ranks   = malloc(thread_count * sizeof(int));

    /* Create the threads. Write the rank into its slot BEFORE the
       pthread_create call so the child always sees a valid value. */
    for (int i = 0; i < thread_count; i++) {
        ranks[i] = i;
        pthread_create(&threads[i], NULL, say_hello, &ranks[i]);
    }

    printf("Hello from the main thread!\n");

    /* Wait for every thread to finish. */
    for (int i = 0; i < thread_count; i++) {
        pthread_join(threads[i], NULL);
    }

    /*
     * No garbage collector, so we free what we malloc'd. Forgetting
     * this wouldn't break the program right now but it would leak
     * memory. In a long-running program that piles up quickly.
     */
    free(threads);
    free(ranks);

    printf("All %d threads are done. Goodbye!\n", thread_count);

    return 0;
}
