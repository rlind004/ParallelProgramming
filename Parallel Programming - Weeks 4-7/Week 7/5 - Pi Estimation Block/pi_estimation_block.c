/*
 * First pi estimator. Uses the Leibniz series:
 *
 *     pi = 4 * (1 - 1/3 + 1/5 - 1/7 + 1/9 - ...)
 *
 * The series is famously slow to converge: you need millions of
 * terms to get a handful of correct digits. That sounds like a bug,
 * but for teaching purposes it's perfect. We need a lot of
 * arithmetic to split across threads, so the parallel speedup is
 * actually visible on a clock.
 *
 * The parallel pattern is the one we practised in script 4, the
 * little "parallel sum" warm-up. Each thread is given a BLOCK of
 * consecutive term indices, keeps a private running total, and
 * only touches the global sum once at the end, inside a short
 * critical section.
 *
 * What's new compared to script 4:
 *   - the real Leibniz formula with alternating signs,
 *   - math.h for atan, which gives us a "true" pi to compare to,
 *   - a timer, so we can see the parallel version vs the serial one,
 *   - argc/argv, so you pick the number of threads and the number
 *     of terms from the command line instead of hard-coding them.
 *
 * One rough edge: n has to be an exact multiple of thread_count, or
 * the tail of the series gets silently dropped. Script 6 fixes
 * that.
 *
 * Compile: gcc -g -Wall -o pi_estimation pi_estimation_block.c -lpthread -lm
 *          (timer.h needs to be reachable from the include path)
 * Run:     ./pi_estimation <threads> <n>
 *          Example: ./pi_estimation 4 1000000
 *
 * IPP section 4.6, pp. 168 and following.
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>                  /* atan, for the reference pi */
#include <pthread.h>
#include "../timer.h"             /* GET_TIME macro around gettimeofday */

/* Safety cap so the user can't ask for something ridiculous. */
const int MAX_THREADS = 1024;

long      thread_count;
long long n;
double    sum;            /* global sum, protected by mutex */
pthread_mutex_t mutex;

void* Thread_sum(void* rank);
void Get_args(int argc, char* argv[]);
void Usage(char* prog_name);
double Serial_pi(long long n);

int main(int argc, char* argv[]) {
   long       thread;        /* long to match the void* width */
   pthread_t* thread_handles;
   double start, finish, elapsed;

   /* Parse thread_count and n from argv. */
   Get_args(argc, argv);

   thread_handles = (pthread_t*) malloc (thread_count*sizeof(pthread_t));
   pthread_mutex_init(&mutex, NULL);
   sum = 0.0;

   /* --- Parallel run, timed ---------------------------------------- */
   GET_TIME(start);
   for (thread = 0; thread < thread_count; thread++)
      pthread_create(&thread_handles[thread], NULL,
          Thread_sum, (void*)thread);

   for (thread = 0; thread < thread_count; thread++)
      pthread_join(thread_handles[thread], NULL);
   GET_TIME(finish);
   elapsed = finish - start;

   /* The series adds up to pi/4, so multiply by 4 to get pi. */
   sum = 4.0*sum;
   printf("With n = %lld terms,\n", n);
   printf("   Our estimate of pi = %.15f\n", sum);
   printf("The elapsed time is %e seconds\n", elapsed);

   /* --- Serial run, timed, for comparison -------------------------- */
   GET_TIME(start);
   sum = Serial_pi(n);
   GET_TIME(finish);
   elapsed = finish - start;
   printf("   Single thread est  = %.15f\n", sum);
   printf("The elapsed time is %e seconds\n", elapsed);

   /* "Reference" pi via the math library. 4*atan(1) is pi by
      definition of the arctangent. */
   printf("                   pi = %.15f\n", 4.0*atan(1.0));

   pthread_mutex_destroy(&mutex);
   free(thread_handles);
   return 0;
}

/*
 * One worker. Computes its block and folds the private total into
 * the global sum with a single lock at the end.
 */
void* Thread_sum(void* rank) {
   long my_rank = (long) rank;
   double factor;
   long long i;
   long long my_n       = n/thread_count;
   long long my_first_i = my_n*my_rank;
   long long my_last_i  = my_first_i + my_n;
   double    my_sum     = 0.0;

   printf("Thread %ld is calculating sum of the terms from %lld to %lld \n", my_rank, my_first_i, my_last_i-1);

   /* Even-indexed terms are positive, odd ones negative. We start
      with the right sign for my_first_i and then just flip. */
   if (my_first_i % 2 == 0)
      factor = 1.0;
   else
      factor = -1.0;

   for (i = my_first_i; i < my_last_i; i++, factor = -factor) {
      my_sum += factor/(2*i+1);
   }

   /* One mutex grab per thread, not one per iteration. That's the
      whole reason for keeping my_sum local. */
   pthread_mutex_lock(&mutex);
   sum += my_sum;
   pthread_mutex_unlock(&mutex);

   return NULL;
}

/* Same calculation, no threads. Only here so we can compare wall
   times against the parallel version. */
double Serial_pi(long long n) {
   double sum = 0.0;
   long long i;
   double factor = 1.0;

   for (i = 0; i < n; i++, factor = -factor) {
      sum += factor/(2*i+1);
   }
   return 4.0*sum;

}

/* Read thread_count and n out of argv. If anything looks wrong we
   print a usage message and bail out. */
void Get_args(int argc, char* argv[]) {
   if (argc != 3) Usage(argv[0]);
   thread_count = strtol(argv[1], NULL, 10);
   if (thread_count <= 0 || thread_count > MAX_THREADS) Usage(argv[0]);
   n = strtoll(argv[2], NULL, 10);
   if (n <= 0) Usage(argv[0]);
}

void Usage(char* prog_name) {
   fprintf(stderr, "usage: %s <number of threads> <n>\n", prog_name);
   fprintf(stderr, "   n is the number of terms and should be >= 1\n");
   fprintf(stderr, "   n should be evenly divisible by the number of threads\n");
   exit(0);
}
