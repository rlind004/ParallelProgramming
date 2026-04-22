/*
 * The previous script, with a fix.
 *
 * The problem in script 5 was that deposit() had three steps (read,
 * compute, write) with no guard around them. The fix is to wrap
 * those steps in a LOCK so that only one thread at a time can be
 * inside that section of code.
 *
 * A pthread mutex is the C version of Java's synchronized block.
 * Two operations:
 *
 *     pthread_mutex_lock(&mutex)    is "enter the room"
 *     pthread_mutex_unlock(&mutex)  is "leave the room"
 *
 * Only one thread can be in the room at a time. Everybody else
 * waits at the door until the current occupant walks out.
 *
 * The big difference from Java: in Java a synchronized block
 * releases its lock automatically when you leave it, even on an
 * exception. In C, you release the lock manually. If you forget,
 * every other thread piles up at the door forever and your program
 * freezes. No warning, no error, just silence. So you have to be
 * careful with the pairing.
 */

#include <stdio.h>    /* printf */
#include <stdlib.h>   /* exit */
#include <pthread.h>  /* POSIX threads */


/* Shared state, same as in script 5. */
int balance = 0;

/*
 * The mutex. Global, so every thread can grab it. It doesn't
 * matter who created it; what matters is that every thread that
 * wants to deposit goes through this same one.
 */
pthread_mutex_t mutex;

/*
 * Same deposit as script 5, but now the three steps live inside a
 * locked region. Only one thread at a time is between the lock
 * and unlock call, so there's no window for anyone to sneak in.
 */
void deposit() {
    int newbalance;
    pthread_mutex_lock(&mutex);               /* enter the room    */
    newbalance = balance + 1;
    balance = newbalance;
    printf("The balance now is %d.\n",newbalance);
    pthread_mutex_unlock(&mutex);             /* leave the room    */
}


int main()
{
    int i =0;
    pthread_t threads[100];


    for( i =0; i<100; i++){
        pthread_create (&threads[i], NULL, (void *) &deposit, NULL);
    }

    for( i =0; i<100; i++){
        pthread_join(threads[i], NULL);
    }

    /*
     * Run it as often as you like; the answer is always 100. The
     * mutex turns the three-step update into "only one thread at a
     * time does this", and the race disappears.
     */
    printf("Balance at the end is: %d\n", balance );

    exit(0);
}
