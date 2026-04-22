/*
 * The C version of ThreadCooperation.java. Same idea: one
 * deposit thread, one withdraw thread, one shared balance. The
 * withdraw thread waits when the balance is too small.
 *
 * Where Java used
 *     synchronized + lock.wait() + lock.notifyAll()
 * on a single lock object, C splits the job between two things:
 *     pthread_mutex_t mutex    — the lock
 *     pthread_cond_t  cv       — the waiting room
 *
 * They are two separate objects, but they always travel
 * together. Every pthread_cond_wait takes the matching mutex as
 * its second argument.
 *
 * Compile: gcc thread_cooperation.c -o thread_cooperation -lpthread
 * Run:     ./thread_cooperation
 * Stop:    Ctrl+C (the loops never end on their own)
 */

#include <stdio.h>
#include <stdlib.h>    /* rand, srand */
#include <time.h>      /* time */
#include <unistd.h>    /* sleep */
#include <pthread.h>

int balance = 0;
pthread_mutex_t mutex;
pthread_cond_t  cv;

void* deposit_loop(void* arg) {
    while (1) {
        pthread_mutex_lock(&mutex);

        int amount = rand() % 10 + 1;
        balance += amount;
        printf("Add %d. Balance now is %d\n", amount, balance);

        /* Ring the bell. Same role as lock.notifyAll() in Java. */
        pthread_cond_broadcast(&cv);

        pthread_mutex_unlock(&mutex);
        sleep(1);
    }
    return NULL;
}

void* withdraw_loop(void* arg) {
    while (1) {
        pthread_mutex_lock(&mutex);

        int amount = rand() % 20 + 1;

        /* while, not if. Same reason as in the Java version:
           after waking up, the condition may still be false. */
        while (balance < amount) {
            printf("\t\t\tCannot withdraw %d, waiting...\n", amount);

            /* Same role as lock.wait() in Java. Releases the
               mutex, sleeps, and reacquires the mutex on wakeup. */
            pthread_cond_wait(&cv, &mutex);
        }

        balance -= amount;
        printf("\t\t\tWithdrew %d. Balance now is %d\n", amount, balance);

        pthread_mutex_unlock(&mutex);
    }
    return NULL;
}

int main() {
    pthread_t d, w;

    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cv, NULL);
    srand(time(NULL));

    pthread_create(&d, NULL, deposit_loop, NULL);
    pthread_create(&w, NULL, withdraw_loop, NULL);

    /* These never actually return because the workers loop
       forever. They're here for completeness; in real code you
       would join threads that have a finite lifetime. */
    pthread_join(d, NULL);
    pthread_join(w, NULL);

    pthread_mutex_destroy(&mutex);
    pthread_cond_destroy(&cv);
    return 0;
}
