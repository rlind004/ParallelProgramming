/*
 * Same broken program as AccountWithoutSync.java, written with
 * pthreads. Start 100 threads, each one adds 1 to a shared
 * balance, expect 100, get something lower.
 *
 * The bug is exactly the same as in the Java version: the
 * two-step "read newBalance, write newBalance" leaves a window
 * where another thread can sneak in and cause a lost update.
 *
 * The fix is in account_with_sync.c in the next folder.
 */

#include <stdio.h>
#include <pthread.h>

int balance = 0;

/* Intentionally broken. No lock. Same shape as the Java version. */
void* deposit(void* arg) {
    int newBalance = balance + 1;
    balance = newBalance;
    return NULL;
}

int main() {
    pthread_t threads[100];

    for (int i = 0; i < 100; i++) {
        pthread_create(&threads[i], NULL, deposit, NULL);
    }

    for (int i = 0; i < 100; i++) {
        pthread_join(threads[i], NULL);
    }

    /* Expected: 100. Actual: usually a bit less. Run it several
       times and watch the number change. */
    printf("Final balance: %d\n", balance);
    return 0;
}
