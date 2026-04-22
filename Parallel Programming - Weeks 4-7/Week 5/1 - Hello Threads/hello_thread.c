/*
 * Same program as HelloThread.java, rewritten with pthreads.
 *
 * A few things worth noticing next to the Java version:
 *
 *   - The thread body has to be a function with the exact shape
 *         void* fn(void* arg)
 *     There are no lambdas and no anonymous Runnables.
 *
 *   - There is no separate "start" step. pthread_create both
 *     creates AND starts the thread in a single call.
 *
 *   - pthread_join takes a pointer for the thread's return value.
 *     We don't care about a return value here, so we pass NULL.
 */

#include <stdio.h>
#include <pthread.h>

/* The thread body. */
void* say_hello(void* arg) {
    printf("Hello from the child thread!\n");
    return NULL;
}

int main() {
    pthread_t t;

    /* Create AND start the thread in one call. No .start() step. */
    pthread_create(&t, NULL, say_hello, NULL);

    printf("Hello from the main thread!\n");

    /* Wait for the child thread to finish. Same role as t.join() in Java. */
    pthread_join(t, NULL);

    printf("Both threads are done. Goodbye!\n");
    return 0;
}
