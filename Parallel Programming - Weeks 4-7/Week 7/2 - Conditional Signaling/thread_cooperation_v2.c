/*
 * Same idea as script 1, but the deposit clerk is smarter about
 * when to ring the bell.
 *
 * In script 1, every single deposit triggered a broadcast, even if
 * the amount was tiny and the withdraw clerk was waiting for much
 * more. The withdraw clerk would wake up, check, find nothing
 * interesting, and go back to sleep. A wasted round trip through
 * the scheduler.
 *
 * This version adds one shared number, requestedAmount, that the
 * withdraw clerk writes down before going to sleep: "I'm waiting
 * for X". The deposit clerk reads that number and only rings the
 * bell when the balance has actually reached X. No more pointless
 * wake-ups.
 *
 * Small change, nicer behaviour. The Java version of the same
 * idea is in Week 3 if you want to compare.
 */

#include <unistd.h>
#include <sys/types.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>


void deposit();
void withdraw();

int balance=0;

/*
 * New compared to script 1. The withdraw clerk uses this as a
 * sticky note: "I'm waiting for this much". The deposit clerk
 * reads it to decide whether to wake them up. Both threads only
 * touch this variable while holding the mutex.
 */
int requestedAmount=0;

pthread_mutex_t mutex;
pthread_cond_t cond_var;

int main()
{
    pthread_t threads[2];
     int i;
     srand(time(NULL));

     pthread_create (&threads[0], NULL, (void *) &deposit, NULL);
     pthread_create (&threads[1], NULL, (void *) &withdraw, NULL);

     for( i =0; i<2; i++){
        pthread_join(threads[i], NULL);
     }
     printf("Balance is %d\n", balance);

    exit(0);
}

void deposit(){
	while(1){
	    pthread_mutex_lock(&mutex);
	    int amount = rand()%10 +1;
	    int newBalance=balance+amount;
	    printf("Add %d. Balance now is %d\n",amount, newBalance);
	    balance=newBalance;

	    /* Ring the bell only if the balance has actually caught
	       up to the withdraw clerk's target. Otherwise there's
	       no point waking them. */
	    if(balance >= requestedAmount) {
	    	pthread_cond_broadcast(&cond_var);
	    }
	    pthread_mutex_unlock(&mutex);
	    sleep(1);
	}
}

void withdraw(){
       while(1) {
	  pthread_mutex_lock(&mutex);
	  int withdrawAmount = rand()%20 +1;
	  while(balance<=withdrawAmount){
	      /* Write the target down before going to sleep, so the
	         deposit clerk's smarter check has something to
	         compare against. */
	      requestedAmount = withdrawAmount;
	      printf("\t\t\tCannot withdraw %d, waiting ...\n",withdrawAmount);
	      pthread_cond_wait(&cond_var, &mutex);
	  }
	  int newBalance = balance-withdrawAmount;
          printf("\t\t\tWithdrew %d. Balance now is %d\n", withdrawAmount, newBalance);
          balance = newBalance;
          pthread_mutex_unlock(&mutex);
      }
}
