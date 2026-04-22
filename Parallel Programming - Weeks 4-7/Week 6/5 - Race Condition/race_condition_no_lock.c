/*
 * A hundred threads all trying to add 1 to the same variable.
 *
 * The expected answer is 100. On most runs you'll get something
 * lower, maybe 97, maybe 84, maybe 62. This is the C version of
 * the Java AccountWithoutSynchronization exercise.
 *
 * Why does it break? Look at deposit(). The update isn't a single
 * atomic step, it's three:
 *
 *   1. read  the current balance into newbalance
 *   2. add 1 to get the new value
 *   3. write newbalance back into balance
 *
 * Nothing stops the OS from pausing a thread between steps 1 and 3
 * and letting another thread run. When that happens:
 *
 *     Thread A reads balance = 50
 *     Thread B reads balance = 50    <- before A has written
 *     Thread A writes balance = 51
 *     Thread B writes balance = 51   <- should have been 52
 *
 * One deposit is gone. This is what a race condition looks like:
 * the final answer depends on which thread happens to win the
 * timing race, and nothing in the code guarantees correctness.
 *
 * Script 6 will fix this with a mutex.
 */

#include <stdio.h>    /* printf */
#include <stdlib.h>   /* exit */
#include <pthread.h>  /* POSIX threads */

/* The shared "account balance". Everyone is going to read and
   write this. In Java this would be:
       private static int balance = 0; */
int balance = 0;

/*
 * The intentionally broken deposit. The three steps (read, compute,
 * write) are visible, and there's nothing stopping another thread
 * from sneaking in between any of them.
 */
void deposit() {
    int newbalance;
    newbalance = balance + 1;               /* step 1: read + compute */
    printf("The balance now is %d.\n",newbalance);
    balance = newbalance;                    /* step 2: write back    */
}


int main()
{
    int i =0;
    pthread_t threads[100];


    /* Create 100 threads, each calls deposit() exactly once. */
    for( i =0; i<100; i++){
        pthread_create (&threads[i], NULL, (void *) &deposit, NULL);
    }

    /* Wait for all of them. */
    for( i =0; i<100; i++){
        pthread_join(threads[i], NULL);
    }

    /*
     * Expected: 100. Actual: usually a bit less. Run the program
     * a few times and you'll likely see a different final number
     * on every run. That unreliability is the whole point: "works
     * most of the time" is not the same as "works".
     */
    printf("Balance at the end is: %d\n", balance );

    exit(0);
}
