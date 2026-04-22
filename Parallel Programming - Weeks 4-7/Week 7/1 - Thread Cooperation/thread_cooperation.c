/*
 * First thread cooperation example.
 *
 * Week 6 showed how to keep two threads from corrupting a shared
 * balance using a mutex. That handles race conditions. But what
 * should a thread do when it simply CAN'T make progress yet?
 * Imagine trying to withdraw 30 from an account that currently
 * holds 5. You don't want to fail with an error; you want to wait
 * until some money comes in.
 *
 * A condition variable is the tool for that. Think of it as a
 * waiting spot. Threads park themselves on it with
 * pthread_cond_wait, and other threads wake them up with
 * pthread_cond_broadcast.
 *
 * Real-life picture: a cash register with two clerks working it.
 * A deposit clerk who keeps adding money, and a withdraw clerk who
 * keeps trying to take some out. When there isn't enough money the
 * withdraw clerk doesn't argue, they just stand by the register
 * and come back when the deposit clerk rings the bell.
 *
 * In this program the deposit thread adds 1 to 10 each second.
 * The withdraw thread tries to take out 1 to 20, and blocks if the
 * balance is too small. Whenever a deposit happens, the deposit
 * thread rings the "bell" (broadcast) so the withdraw thread can
 * try again.
 *
 * Worth reading alongside the Java version of the same idea.
 */

#include <unistd.h>     /* sleep */
#include <sys/types.h>  /* boilerplate, not actually used */
#include <errno.h>      /* boilerplate, not actually used */
#include <stdio.h>      /* printf */
#include <stdlib.h>     /* rand, srand, exit */
#include <pthread.h>    /* threads, mutex, condition variables */
#include <string.h>     /* boilerplate, not actually used */


void deposit();
void withdraw();

/* The shared account balance. Only ever read or written while the
   mutex is held. */
int balance=0;
pthread_mutex_t mutex;

/*
 * The waiting spot. Threads can sleep on this variable and other
 * threads can wake them up. A condition variable has no value of
 * its own; it's literally just a place to wait. Always used
 * together with a mutex.
 *
 * In Java terms, this is Condition.await() and Condition.signalAll()
 * on a Condition you'd create from a ReentrantLock.
 */
pthread_cond_t cond_var;

int main()
{
    pthread_t threads[2];
     int i;

     /* Seed the random number generator so we get different
        amounts on every run. Without this, rand() would produce
        the same sequence each time. */
     srand(time(NULL));

     pthread_create (&threads[0], NULL, (void *) &deposit, NULL);
     pthread_create (&threads[1], NULL, (void *) &withdraw, NULL);

     /* These two threads loop forever so the joins never actually
        return. The program runs until you stop it. */
     for( i =0; i<2; i++){
        pthread_join(threads[i], NULL);
     }
     printf("Balance is %d\n", balance);

    exit(0);
}

/*
 * Deposit clerk. Locks the mutex, adds a small random amount,
 * rings the bell so any waiting withdraw thread gets a chance to
 * re-check, unlocks, and then sleeps a second before doing it
 * again.
 */
void deposit(){
	while(1){
	    pthread_mutex_lock(&mutex);
	    int amount = rand()%10 +1;
	    int newBalance=balance+amount;
	    printf("Add %d. Balance now is %d\n",amount, newBalance);
	    balance=newBalance;

	    /* Ring the bell. broadcast wakes up every thread currently
	       waiting on cond_var. We don't care which one takes the
	       money; we just want them to have a shot. */
	    pthread_cond_broadcast(&cond_var);
	    pthread_mutex_unlock(&mutex);
	    sleep(1);
	}
}

/*
 * Withdraw clerk. Tries to pull out a random amount. If the
 * balance is too small, parks on cond_var and waits for the
 * deposit thread to ring the bell.
 */
void withdraw(){
    while(1) {
	  pthread_mutex_lock(&mutex);
	  int withdrawAmount = rand()%20 +1;

	  /*
	   * "while" and not "if", on purpose. When we wake up there's
	   * no guarantee the balance is now big enough. The latest
	   * deposit might have been tiny, or some other thread could
	   * have beaten us to the money. So we check again.
	   *
	   * pthread_cond_wait does three things in one atomic step:
	   *   1. release the mutex, so other threads can run,
	   *   2. put this thread to sleep,
	   *   3. once woken up, reacquire the mutex before returning.
	   */
	  while(balance < withdrawAmount){
	      printf("\t\t\tCannot withdraw %d, waiting ...\n",withdrawAmount);
	      pthread_cond_wait(&cond_var, &mutex);
	  }

	  /* By the time we get here the balance is definitely big
	     enough, because we only leave the while-loop when the
	     condition is true and we hold the mutex. */
	  int newBalance = balance-withdrawAmount;
          printf("\t\t\tWithdrew %d. Balance now is %d\n", withdrawAmount, newBalance);
          balance = newBalance;
          pthread_mutex_unlock(&mutex);
      }
}
