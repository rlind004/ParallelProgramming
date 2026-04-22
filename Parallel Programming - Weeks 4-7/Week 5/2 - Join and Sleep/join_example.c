/*
 * Same as JoinExample.java, rewritten with pthreads.
 *
 * A few things worth comparing with the Java side:
 *
 *   - Java's Thread.sleep takes milliseconds. C's sleep takes
 *     whole seconds. For sub-second pauses in C you'd use
 *     usleep (microseconds) or nanosleep.
 *
 *   - The work of each thread lives in a named function rather
 *     than an inline lambda. There are no lambdas in plain C.
 *
 *   - We can use one function for both threads and pass the name
 *     through the void* argument. Java's two lambdas could also
 *     have been collapsed into one Runnable with a constructor
 *     argument, but usually you just write two.
 */

#include <stdio.h>
#include <unistd.h>   /* sleep */
#include <pthread.h>

void* worker(void* arg) {
    const char* name = (const char*) arg;
    for (int i = 1; i <= 3; i++) {
        printf("Thread %s step %d\n", name, i);
        sleep(1);
    }
    return NULL;
}

int main() {
    pthread_t a, b;

    /* Pass a string literal through the void* slot. Inside the
       worker we cast it back to const char*. */
    pthread_create(&a, NULL, worker, "A");
    pthread_create(&b, NULL, worker, "B");

    pthread_join(a, NULL);
    pthread_join(b, NULL);

    printf("Both threads are done.\n");
    return 0;
}
