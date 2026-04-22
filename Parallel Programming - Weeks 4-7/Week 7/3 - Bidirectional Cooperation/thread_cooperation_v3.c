/*
 * Now both clerks can be forced to wait.
 *
 * Scripts 1 and 2 only ever made the withdraw clerk wait. The
 * deposit clerk could always do their job. This version adds an
 * upper limit on the balance: MAX_INSURANCE_LIMIT. If a deposit
 * would push the balance above that limit, the deposit clerk has
 * to wait too, until the withdraw clerk has taken some money out.
 *
 * It's the classic producer/consumer pattern with a shelf that
 * has limited space. The producer waits when the shelf is full,
 * the consumer waits when the shelf is empty.
 *
 * We now need TWO waiting spots, because the two clerks are
 * waiting for different things:
 *
 *     cond_var  — "there's money to take"
 *     cond_var2 — "there's room to add"
 *
 * The matching Java version lives in Week 3 if you want to see
 * them side by side.
 */

#include <unistd.h>
#include <sys/types.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

/* Upper bound on the balance. New in this version. */
#define MAX_INSURANCE_LIMIT 100

void deposit();
void withdraw();

int balance=0;
pthread_mutex_t mutex;

/*
 * Two separate waiting spots now. Think of them as two bells on
 * two different walls of the same room.
 *
 *   cond_var  — the "new deposit happened" bell.
 *               The withdraw clerk listens here.
 *   cond_var2 — the "room freed up" bell.
 *               The deposit clerk listens here.
 */
pthread_cond_t cond_var, cond_var2;

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


/*
 * Deposit clerk. Now has their own reason to wait: if the balance
 * is close to the limit and the next deposit would push past it,
 * park on cond_var2 and wait for some money to leave first.
 */
void deposit(){
	while(1){
	    pthread_mutex_lock(&mutex);

	    /* Wider random range so the limit gets hit often enough
	       that you can actually see the deposit clerk blocking. */
	    int amount = rand()%40 +1;

	    /* Same while-around-wait rule as the withdraw side. After
	       being woken up someone else might have already taken
	       the room, so we check again. */
	    while(balance+amount > MAX_INSURANCE_LIMIT){
		printf("Cannot deposit %d, waiting ...\n",amount);
	        pthread_cond_wait(&cond_var2, &mutex);
	    }
	    int newBalance=balance+amount;
	    printf("Add %d. Balance now is %d\n",amount, newBalance);
	    balance=newBalance;

	    /* Ring the "money is here" bell for the withdraw clerk. */
	    pthread_cond_broadcast(&cond_var);
	    pthread_mutex_unlock(&mutex);
	    sleep(1);
	}
}

/*
 * Withdraw clerk. Same waiting logic as before for "not enough
 * money". What's new is that after each withdrawal it also rings
 * the "room freed up" bell, so a deposit clerk parked on cond_var2
 * has a chance to come back to work.
 */
void withdraw(){
       while(1) {
	  pthread_mutex_lock(&mutex);
	  int withdrawAmount = rand()%20 +1;
	  while(balance<withdrawAmount){
	      printf("\t\t\tCannot withdraw %d, waiting ...\n",withdrawAmount);
	      pthread_cond_wait(&cond_var, &mutex);
	  }
	  int newBalance = balance-withdrawAmount;
          printf("\t\t\tWithdrew %d. Balance now is %d\n", withdrawAmount, newBalance);
          balance = newBalance;

          /* Ring the "room freed up" bell for the deposit clerk. */
          pthread_cond_broadcast(&cond_var2);
          pthread_mutex_unlock(&mutex);
          sleep(1);
      }
}
