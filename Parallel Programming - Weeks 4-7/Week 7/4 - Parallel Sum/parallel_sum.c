/*
 * Parallel sum of 1..N, a quiet warm-up before the pi estimators.
 *
 * Scripts 1..3 of this week were about cooperation: two threads
 * blocking each other on a shared bank balance. The pi estimator that
 * comes next looks very different at first, because it drops argv
 * parsing, math.h, a timer and the Leibniz series on you all at the
 * same time. This file peels off the only thing in that pile that
 * really matters, which is the parallel pattern itself. Everything
 * else can wait for the next script.
 *
 * What's actually new here? Four things, and nothing else. A worker
 * function that receives its rank as a void*, using the classic "cast
 * a long through void*" trick. A private running total inside each
 * worker that only touches the global sum once, at the very end,
 * under the mutex. Block distribution, where thread k takes the
 * indices k*n/T up to (k+1)*n/T. And a forward declaration of
 * Thread_sum at the top of the file so main can refer to it before
 * its definition.
 *
 * N and THREAD_COUNT are #define'd constants to keep the main function
 * tiny and to keep command-line parsing out of the way for now.
 *
 * The expected result for N = 1000000 is 500000500000. You can check
 * it against the closed form n*(n+1)/2.
 */

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#define THREAD_COUNT 4
#define N 1000000

/* sum is shared. The mutex below guards it, but only for one line
   of code per thread, at the end of Thread_sum. Everywhere else each
   thread accumulates into its own local my_sum and doesn't need to
   lock anything. */
long long       sum = 0;
pthread_mutex_t mutex;

/* Forward declaration so main() can see Thread_sum before its body. */
void* Thread_sum(void* rank);

int main(void) {
    pthread_t threads[THREAD_COUNT];
    long      t;

    pthread_mutex_init(&mutex, NULL);

    /* Start the workers. We squeeze the rank into a void* by casting
       it through long. This is the usual pthreads shortcut when you
       only need to pass a small integer and you don't want to malloc
       a whole struct per thread. */
    for (t = 0; t < THREAD_COUNT; t++) {
        pthread_create(&threads[t], NULL, Thread_sum, (void*) t);
    }

    for (t = 0; t < THREAD_COUNT; t++) {
        pthread_join(threads[t], NULL);
    }

    printf("Sum of 1..%d with %d threads = %lld\n", N, THREAD_COUNT, sum);
    printf("Expected                     = %lld\n",
           (long long) N * (N + 1) / 2);

    pthread_mutex_destroy(&mutex);
    return 0;
}

void* Thread_sum(void* rank) {
    /* Undo the cast trick from main. my_rank is now 0, 1, 2 or 3. */
    long my_rank = (long) rank;

    /* Block distribution. Each thread gets a contiguous slice of the
       range 1..N. For this warm-up we assume N is a multiple of
       THREAD_COUNT, so there is no remainder to handle. Script 5 of
       Week 7 will show two ways of dealing with the leftover. */
    long long my_n       = N / THREAD_COUNT;
    long long my_first_i = my_n * my_rank + 1;   // +1 because we start at 1
    long long my_last_i  = my_first_i + my_n;    // one past the last index
    long long my_sum     = 0;
    long long i;

    for (i = my_first_i; i < my_last_i; i++) {
        my_sum += i;
    }

    /* The whole point of keeping my_sum local is this next block:
       one lock, one add, one unlock, per thread. If instead we'd
       added into the global sum inside the loop, we'd pay for a mutex
       on every single iteration. Do that once and the parallel
       version is easily slower than the single-threaded one. */
    pthread_mutex_lock(&mutex);
    sum += my_sum;
    pthread_mutex_unlock(&mutex);

    printf("Thread %ld summed %lld..%lld -> %lld\n",
           my_rank, my_first_i, my_last_i - 1, my_sum);

    return NULL;
}
