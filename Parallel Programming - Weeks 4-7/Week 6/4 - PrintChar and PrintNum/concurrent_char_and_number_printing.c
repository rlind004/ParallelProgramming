/*
 * Two different thread functions running side by side.
 *
 * This is the C version of the PrintChar/PrintNum exercise from
 * Week 1 in Java. Three threads run at the same time:
 *
 *   thread 1: print 'A' twenty times
 *   thread 2: print 'B' twenty times
 *   thread 3: print the numbers 1..50
 *
 * To make the interleaving obvious on screen, each thread sleeps
 * for a second every few prints. Without the sleeps one thread
 * would usually finish before the others even got scheduled and
 * you wouldn't see much mixing in the output.
 *
 * The new idea here, compared to script 3, is that we now have
 * TWO different thread functions in the same program, AND we're
 * passing two different argument shapes. The character threads
 * take a struct; the number thread takes a plain int*. pthread
 * doesn't care what shape the argument is as long as you cast it
 * correctly on both sides.
 */

#include <unistd.h>     /* sleep — pauses a thread for N seconds. In
                           Java this is Thread.sleep, except Java
                           uses milliseconds and sleep uses whole
                           seconds. For sub-second precision use
                           usleep (microseconds) instead. */
#include <sys/types.h>  /* boilerplate */
#include <errno.h>      /* boilerplate */
#include <stdio.h>      /* printf */
#include <stdlib.h>     /* exit */
#include <pthread.h>    /* POSIX threads */
#include <string.h>     /* boilerplate, not used here */

/* One thread function per print style. */
void print_chars_function(void *ptr);
void print_numbers_function(void *ptr);

/*
 * Bundle for the character threads: which character to print, and
 * how many times to print it.
 */
typedef struct str_thdata
{
    char c;
    int  number;
} thdata;

int main()
{
    pthread_t thread1, thread2, thread3;
    thdata    data1, data2;

    /* First character thread: 20 copies of 'A' */
    data1.c = 'A';
    data1.number = 20;

    /* Second character thread: 20 copies of 'B' */
    data2.c = 'B';
    data2.number = 20;

    /*
     * The number thread only needs a single int, so we pass the
     * address of a local variable. This is safe here because main
     * won't return until AFTER pthread_join, which means `number`
     * is guaranteed to stay alive the whole time the thread is
     * reading it. Pass a pointer to a soon-to-vanish local and
     * you get a dangling pointer.
     */
    int number = 50;

    /* Three threads, three different arguments. Two of them run
       the same function; the third runs a different one. */
    pthread_create(&thread1, NULL, (void *) &print_chars_function,   (void *) &data1);
    pthread_create(&thread2, NULL, (void *) &print_chars_function,   (void *) &data2);
    pthread_create(&thread3, NULL, (void *) &print_numbers_function, (void *) &number);

    /* Wait for all three. The output you'll see is a mix of A's,
       B's and numbers; the exact order depends on the scheduler
       and the sleep calls below. */
    pthread_join(thread1, NULL);
    pthread_join(thread2, NULL);
    pthread_join(thread3, NULL);

    exit(0);
}

/*
 * Print a character over and over. The sleep every five
 * iterations nudges the scheduler to switch to another thread so
 * the output looks interleaved on screen instead of "all A's then
 * all B's".
 */
void print_chars_function ( void *ptr )
{
    thdata *data;
    data = (thdata *) ptr;

    for(int i = 0; i<data->number; i++){
        printf("%c\n", data->c);
       if(i%5==0) sleep(1);
    }

    pthread_exit(0);
}

/*
 * Print 1..n. Dereferences a plain int*, no struct involved. Same
 * idea as the character version, just a different argument shape.
 */
void print_numbers_function ( void *ptr )
{
    int n = *((int *) ptr);

    for(int i = 1; i<=n; i++){
        printf("%d\n", i);
        if(i % 20 == 0)
            sleep(1);

    }

    pthread_exit(0);
}
