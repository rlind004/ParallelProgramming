/*
 * What if a thread needs more than one piece of data?
 *
 * Scripts 0 and 1 passed a single int. If you want to pass two, or
 * ten, or a mix of numbers and strings, one integer isn't enough.
 * The fix is to bundle everything the thread needs into a struct
 * and pass a pointer to that struct.
 *
 * In Java you'd write a class with fields and hand it to a Runnable
 * through the constructor:
 *
 *     class Task implements Runnable {
 *         int threadNo;
 *         String message;
 *     }
 *
 * In C the idea is the same, there's just less syntax to hide
 * behind: you declare a struct, fill it in, and pass its address.
 *
 * This program starts ten threads. Each one receives a struct
 * containing a number and a short greeting: "Hello!" for the even
 * threads, "Hi!" for the odd ones. The thread prints them and
 * exits.
 */

#include <unistd.h>     /* sleep and friends, not actually used here */
#include <sys/types.h>  /* primitive system types, boilerplate      */
#include <errno.h>      /* errno, boilerplate                       */
#include <stdio.h>      /* printf, fprintf                          */
#include <stdlib.h>     /* exit, malloc, free                       */
#include <pthread.h>    /* POSIX threads                            */
#include <string.h>     /* strcpy — C has no String class, strings
                           are char arrays and you need functions
                           like strcpy to copy them. */

#define THREAD_NO 10

/* Thread function prototype. One per thread, all the same shape. */
void print_message_function(void *ptr);

/*
 * Data bundle. In Java you'd write:
 *     class Task { int threadNo; String message; }
 *
 * In C we write a struct and keep the string as a fixed-size char
 * array so we don't need an extra malloc just for the message.
 */
typedef struct str_thdata
{
    int  thread_no;
    char message[100];
} thdata;

int main()
{
    pthread_t threads[THREAD_NO];  /* one handle per thread       */
    thdata    data[THREAD_NO];     /* one struct per thread       */

    /*
     * Fill in every struct BEFORE we start any thread. Even
     * threads say "Hello!", odd ones say "Hi!". The thread_no is
     * just a decorative number that goes 11, 22, 33, ... 110.
     */
    for(int i = 0; i < THREAD_NO; i++){
     data[i].thread_no = (i + 1) * 11;
     if (i % 2 == 0){
	     strcpy(data[i].message, "Hello!");
     }else{
           strcpy(data[i].message, "Hi!");
      }

   }

    /*
     * Each thread gets the address of ITS OWN struct. If we passed
     * the same struct to every thread they'd all read the same
     * value at roughly the same time and stomp on each other.
     */
    for(int i = 0; i < THREAD_NO; i++){
            pthread_create(&threads[i], NULL, (void *) &print_message_function, (void *) &data[i]);
    }

    printf("Main thread says hello everyone! \n");

    /* Wait for every thread. If main exited without waiting, all
       of the children would be killed instantly, even if they
       hadn't printed yet. */
    for(int i = 0; i < THREAD_NO; i++){
        pthread_join(threads[i], NULL);
    }

    exit(0);
}

/*
 * Thread body. Unwrap the void* into a struct pointer and print
 * the two fields. The cast is your responsibility; the compiler
 * won't check it.
 */
void print_message_function ( void *ptr )
{
    thdata *data;
    data = (thdata *) ptr;

    printf("Thread %d says %s \n", data->thread_no, data->message);

    pthread_exit(0);
}
