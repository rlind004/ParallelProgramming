# PrintChar and PrintNum

This is the C version of the classic Week 1 Java exercise where two threads print characters and one thread prints numbers, all running at the same time.

Three threads run concurrently:
- Thread 1 prints 'A' twenty times
- Thread 2 prints 'B' twenty times
- Thread 3 prints numbers 1 through 50

Each thread sleeps periodically (`sleep(1)` = 1 second pause) to simulate real work and make the interleaving visible. Without the sleep, modern CPUs are so fast that one thread could finish entirely before the others get a chance to run.

Notice two things:
1. **Same function, different data**: thread1 and thread2 both call `print_chars_function` but receive different structs. This is like creating two instances of the same Runnable in Java with different constructor arguments.
2. **Different casting patterns**: the char threads receive a struct pointer, while the number thread receives a plain `int*`. Both go through `void*` — you just cast to whatever type you actually passed.

Run it a few times and compare the output. The order of A's, B's, and numbers will differ each time.

**Compile and run:**
```bash
gcc -o concurrent_printing concurrent_char_and_number_printing.c -lpthread
./concurrent_printing
```
