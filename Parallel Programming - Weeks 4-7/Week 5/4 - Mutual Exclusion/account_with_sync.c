/*
 * C version of AccountWithSync.java. The bug from the previous
 * folder is fixed here with a pthread mutex.
 *
 * A couple of things to compare with the Java side:
 *
 *   - Java's `synchronized` releases the lock automatically when
 *     the method returns (or when an exception propagates out).
 *     In C we have to call pthread_mutex_unlock ourselves.
 *     Forgetting the unlock does not give wrong answers; it
 *     silently freezes the whole program.
 *
 *   - The mutex in C is a separate object. You declare it, you
 *     initialise it, and you destroy it at the end. In Java the
 *     lock is implicit in the `synchronized` keyword and the
 *     target object.
 */

#include <stdio.h>
#include <pthread.h>

int balance = 0;
pthread_mutex_t mutex;

void* deposit(void* arg) {
    pthread_mutex_lock(&mutex);          /* enter the room */

    int newBalance = balance + 1;
    balance = newBalance;

    pthread_mutex_unlock(&mutex);        /* leave the room */
    return NULL;
}

int main() {
    pthread_t threads[100];

    /* Initialise the mutex before any thread tries to use it. */
    pthread_mutex_init(&mutex, NULL);

    for (int i = 0; i < 100; i++) {
        pthread_create(&threads[i], NULL, deposit, NULL);
    }

    for (int i = 0; i < 100; i++) {
        pthread_join(threads[i], NULL);
    }

    /* Always 100, no matter how many times you run it. */
    printf("Final balance: %d\n", balance);

    pthread_mutex_destroy(&mutex);
    return 0;
}
