/*
 * Cyclic (round-robin) version of the pi estimator.
 *
 * Scripts 5 and 6 handed out contiguous BLOCKS of term indices.
 * This one deals them out like playing cards: thread k takes
 * terms k, k+T, k+2T, and so on, where T is the thread count.
 *
 *     thread 0 -> terms 0, 4,  8,  12, ...
 *     thread 1 -> terms 1, 5,  9,  13, ...
 *     thread 2 -> terms 2, 6,  10, 14, ...
 *     thread 3 -> terms 3, 7,  11, 15, ...
 *
 * Each thread ends up with either floor(n/T) or ceil(n/T) terms,
 * so the workload is balanced to within a single iteration.
 * There's no special case for leftovers: the "last thread handles
 * the remainder" trick from script 6 is gone.
 *
 * The small price: the sign of each term can't be obtained by
 * flipping a local factor on every iteration, because the indices
 * visited by one thread are no longer consecutive. We recompute
 * it from i's parity inside the loop instead. For a bigger
 * computation that touched arrays we would also worry about cache
 * behaviour, but for pure scalar arithmetic like this it doesn't
 * matter.
 *
 * Compile: gcc -g -Wall -o pi_estimation pi_estimation_cyclic.c -lpthread -lm
 * Run:     ./pi_estimation <threads> <n>
 *
 * IPP section 4.6, pp. 168 and following.
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <pthread.h>
#include "../timer.h"

const int MAX_THREADS = 1024;

long thread_count;
long long n;
double sum;
pthread_mutex_t mutex;

void* Thread_sum(void* rank);
void Get_args(int argc, char* argv[]);
void Usage(char* prog_name);
double Serial_pi(long long n);

int main(int argc, char* argv[]) {
   long       thread;
   pthread_t* thread_handles;
   double start, finish, elapsed;

   Get_args(argc, argv);

   thread_handles = (pthread_t*) malloc (thread_count*sizeof(pthread_t));
   pthread_mutex_init(&mutex, NULL);
   sum = 0.0;

   /* Parallel run, timed. */
   GET_TIME(start);
   for (thread = 0; thread < thread_count; thread++)
      pthread_create(&thread_handles[thread], NULL,
          Thread_sum, (void*)thread);

   for (thread = 0; thread < thread_count; thread++)
      pthread_join(thread_handles[thread], NULL);
   GET_TIME(finish);
   elapsed = finish - start;

   sum = 4.0*sum;
   printf("With n = %lld terms,\n", n);
   printf("   Our estimate of pi = %.15f\n", sum);
   printf("The elapsed time is %e seconds\n", elapsed);

   /* Serial run, timed. */
   GET_TIME(start);
   sum = Serial_pi(n);
   GET_TIME(finish);
   elapsed = finish - start;
   printf("   Single thread est  = %.15f\n", sum);
   printf("The elapsed time is %e seconds\n", elapsed);
   printf("                   pi = %.15f\n", 4.0*atan(1.0));

   pthread_mutex_destroy(&mutex);
   free(thread_handles);
   return 0;
}

/*
 * Worker. Structurally simpler than the block versions: there's
 * no block size and no last-index calculation. The thread just
 * starts at its own rank and jumps by thread_count every step.
 */
void* Thread_sum(void* rank) {
   long my_rank = (long) rank;
   double factor;
   long long i;
   long long my_first_i = my_rank;
   double my_sum = 0.0;

   printf("Thread %ld is calculating sum of the terms: %lld, %lld, %lld ...\n", my_rank, my_first_i, my_first_i+thread_count, my_first_i+2*thread_count);


   /* Cyclic step: jump by thread_count each iteration. */
   for (i = my_first_i; i <= n; i += thread_count) {

   	/* The sign has to come from i's parity because consecutive
   	   iterations visit non-consecutive terms. In the block
   	   version we could just flip factor; here we can't. */
   	if (i % 2 == 0)
      		factor = 1.0;
   	else
      		factor = -1.0;

       my_sum += factor/(2*i+1);
   }
   pthread_mutex_lock(&mutex);
   sum += my_sum;
   pthread_mutex_unlock(&mutex);

   return NULL;
}

double Serial_pi(long long n) {
   double sum = 0.0;
   long long i;
   double factor = 1.0;

   for (i = 0; i < n; i++, factor = -factor) {
      sum += factor/(2*i+1);
   }
   return 4.0*sum;

}

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
   exit(0);
}
