/*
 * N threads again, but with two differences from script 1.
 *
 * 1) The thread count can come from the command line. If the user
 *    runs "./program 8", we use 8. Otherwise we fall back to
 *    asking on stdin, just like script 1 did.
 *
 * 2) We pass the rank with a slightly different trick. Instead of
 *    keeping a small int array and passing &ranks[i], we cast the
 *    loop index itself straight through void*:
 *
 *        pthread_create(..., (void*) thread);
 *
 *    Inside the thread we undo the cast with:
 *
 *        long my_rank = (long) rank;
 *
 *    It looks weird but it's a standard pthreads shortcut. On any
 *    system you'll use in this course, a long and a void* are the
 *    same size, so we can squeeze the rank into one and pull it
 *    back out on the other side without ever allocating anything.
 *
 * Both techniques (the int array from script 1 and the cast from
 * here) are valid. You will see both out in the wild. The int
 * array is a little clearer; the cast is a little cheaper and
 * saves you a malloc.
 */

#include <stdio.h>      /* printf, fprintf, scanf */
#include <stdlib.h>     /* malloc, free, exit, strtol */
#include <pthread.h>    /* POSIX threads */

const int MAX_THREADS = 64;

/* Global so every thread can read it for the "of N" part of the
   printout. Shared read-only state is safe without a lock; it's
   only simultaneous WRITES that need protection. */
int thread_count;

void Usage(char* prog_name);
void *Hello(void* rank);  /* Thread function */

int main(int argc, char* argv[]) {
   long       thread;        /* long to match the void* width */
   pthread_t* thread_handles;

   /*
    * Two ways to supply the thread count. If the user passed
    * exactly one argument on the command line we use strtol to
    * parse it. Otherwise we ask on stdin.
    */
   if (argc != 2){
        printf("Please enter the number of threads: ");
        scanf("%d", &thread_count);
   }else{
        thread_count = strtol(argv[1], NULL, 10);
   }

   if (thread_count <= 0 || thread_count > MAX_THREADS){
       Usage(argv[0]);
       return -1;
   }

   thread_handles = malloc(thread_count * sizeof(pthread_t));

   /*
    * Cast the loop index through void*. Inside Hello we cast it
    * back to long. Both casts work because void* is wide enough
    * to hold a long on every platform you'll compile this on.
    */
   for (thread = 0; thread < thread_count; thread++)
      pthread_create(&thread_handles[thread], NULL, Hello, (void*) thread);

   printf("Hello from the main thread\n");

   for (thread = 0; thread < thread_count; thread++)
      pthread_join(thread_handles[thread], NULL);

   free(thread_handles);
   return 0;
}

/*
 * Thread body. The void* we were given is really just an integer
 * in disguise; cast it back to long to read the rank.
 */
void *Hello(void* rank) {
   long my_rank = (long) rank;

   printf("Hello from thread %ld of %d\n", my_rank, thread_count);

   return NULL;
}

/*
 * Print a usage message and bail out. Called when the user passed
 * a nonsense thread count (too small, too big, not a number, ...).
 */
void Usage(char* prog_name) {
   fprintf(stderr, "usage: %s <number of threads>\n", prog_name);
   fprintf(stderr, "0 < number of threads <= %d\n", MAX_THREADS);
   exit(0);
}
