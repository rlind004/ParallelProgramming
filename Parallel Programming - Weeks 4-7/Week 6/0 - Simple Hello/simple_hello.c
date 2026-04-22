/*
 * The smallest possible pthreads program.
 *
 * One thread, one number, one printf. The main thread kicks off a
 * child thread, waits for it to finish, and then prints a goodbye
 * line. If you've written Java threads, the shape is the same but
 * with more moving parts:
 *
 *   Java:  new Thread(() -> System.out.println("hi")).start();
 *          t.join();
 *
 *   C:     pthread_create(&t, NULL, say_hello, &number);
 *          pthread_join(t, NULL);
 *
 * In C there are no classes and no Runnable. A thread is a plain
 * function that has to look exactly like:
 *
 *     void* function_name(void* arg)
 *
 * You pass data to it by handing over a pointer and casting it to
 * the right type on the other side.
 */

#include <stdio.h>      /* printf */
#include <pthread.h>    /* pthread_create, pthread_join, pthread_t */

/*
 * The thread body. void* in, void* out. We accepted "a pointer to
 * something" and we promised to return "a pointer to something".
 *
 * Since we are the ones who passed an int address from main, we
 * know it's really an int* here and we can safely cast it back.
 * If we got the cast wrong, C would not tell us; it would just
 * misread whatever bytes were in memory.
 */
void* say_hello(void* arg) {
    int my_number = *((int*) arg);

    printf("Hello from the child thread! My number is %d\n", my_number);

    /* Nothing meaningful to return. We still have to return a
       pointer, so we return NULL. */
    return NULL;
}

int main() {
    /* pthread_t is a handle. Think of it as a little ticket with
       the thread's ID on it. It doesn't do anything by itself;
       we use it later to join (or cancel) the thread. */
    pthread_t my_thread;

    /* The value we're going to hand to the thread. */
    int number = 42;

    /*
     * pthread_create is "new Thread(...).start()" compressed into a
     * single call. The four arguments:
     *   &my_thread   where to write the thread handle
     *   NULL         default thread options
     *   say_hello    the function to run
     *   &number      the data, passed through as a void*
     */
    pthread_create(&my_thread, NULL, say_hello, &number);

    /* This line runs on the main thread, at the same time as
       say_hello is running on the child. Which message prints
       first is up to the OS scheduler; there is no guaranteed
       order. */
    printf("Hello from the main thread!\n");

    /*
     * pthread_join is "t.join()". Wait here until my_thread has
     * finished. Without this, main could reach the end and exit,
     * and the whole process would die before the child thread
     * had a chance to run.
     */
    pthread_join(my_thread, NULL);

    printf("Both threads are done. Goodbye!\n");

    return 0;
}
