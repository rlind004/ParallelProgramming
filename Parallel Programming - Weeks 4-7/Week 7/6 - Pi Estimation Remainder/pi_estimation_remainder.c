/*
 * Same pi estimator as script 5, minus the "n must divide
 * thread_count exactly" limitation.
 *
 * In script 5, if n was 1000 and you asked for 3 threads, each
 * thread got floor(1000/3) = 333 terms, and the three of them
 * together only computed 999 terms. Term 999 was just dropped on
 * the floor.
 *
 * The fix here is trivial: the LAST thread stretches its upper
 * bound all the way to n and mops up whatever remainder is left:
 *
 *     if (my_rank == thread_count - 1) {
 *         my_last_i = n;
 *     }
 *
 * The last thread ends up doing at most thread_count-1 extra
 * iterations, which is noise next to the total work. Script 7
 * gets rid of even that small imbalance.
 *
 * Compile: gcc -g -Wall -o pi_estimation pi_estimation_remainder.c -lpthread -lm
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

void* Thread_sum(void* rank) {
   long my_rank = (long) rank;
   double factor;
   long long i;
   long long my_n = n/thread_count;
   long long my_first_i = my_n*my_rank;
   long long my_last_i = my_first_i + my_n;
   double my_sum = 0.0;

   /* The remainder fix: if this is the last thread, extend
      my_last_i all the way to n so no term is left out. Load
      balance is slightly off because the last thread does a
      few more iterations than the others, but for any
      reasonable n the difference is unmeasurable. */
   if( my_rank == thread_count - 1){
   	my_last_i = n;
   }

   printf("Thread %ld is calculating sum of the terms from %lld to %lld \n", my_rank, my_first_i, my_last_i-1);

   if (my_first_i % 2 == 0)
      factor = 1.0;
   else
      factor = -1.0;

   for (i = my_first_i; i < my_last_i; i++, factor = -factor) {
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
