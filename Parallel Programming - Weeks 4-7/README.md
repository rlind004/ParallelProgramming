# Parallel Programming — Weeks 4 to 7

Welcome. This folder has everything you need for the second half of the course: higher-level Java concurrency, a bridge from Java to C, and two weeks of pthreads in C.

## What's in each folder

- **Week 4 — Higher-level Java concurrency.**
  Semaphores, CyclicBarrier, BlockingQueue, ExecutorService. Five worked examples that show the tools in `java.util.concurrent` and what they replace from Weeks 1–3.

- **Week 5 — From Java threads to pthreads.**
  A bridge week. No new concepts, just the Java tools you already know lined up next to their C equivalents. Start with the top-level README in this folder; it has a translation table, four gotchas, and five small programs written in both languages side by side.

- **Week 6 — Pthreads basics in C.**
  Thread creation, passing arguments, sleeping, two threads printing in parallel, a race condition on a shared counter, and the mutex that fixes it. Everything you saw in Weeks 1–2 but now in C.

- **Week 7 — Cooperation and parallel computation.**
  Condition variables with deposit / withdraw (scripts 1–3), a small warm-up that introduces the parallel-work pattern (script 4 — Parallel Sum), and three pi estimators that put that pattern to real use with block, remainder, and cyclic distribution (scripts 5–7).

Every week has its own README inside the folder, and every example has detailed comments explaining what is happening.

## How to read the `.md` files with proper formatting

The course materials use `.md` (markdown). To read them rendered instead of seeing raw `#` and `*` characters:

- **VS Code** (easiest). Open the file and press `Cmd+Shift+V` (Mac) or `Ctrl+Shift+V` (Windows/Linux). A rendered preview opens in a new tab. `Cmd+K V` opens it side-by-side.
- **Obsidian** (free). Point it at this folder as a vault and browse any file.
- **Browser extension** "Markdown Viewer" for Chrome/Edge/Firefox lets you open local `.md` files as rendered pages.
- If you only want to read and not edit, any of these will do. VS Code is the most common choice because you probably already have it installed for the rest of the course.

## How to compile and run

### Java files

```
javac SomeFile.java
java  SomeFile
```

### C files

All the C files use pthreads, so you need to link with `-lpthread`. The pi estimators also use the math library, so they need `-lm`:

```
gcc your_file.c -o your_program -lpthread
./your_program

# For the pi estimators in Week 7 (scripts 5, 6, 7):
gcc pi_estimation_block.c -o pi -lpthread -lm
./pi 4 1000000
```

`timer.h` is shared between the three pi scripts. It sits one level up, at `Week 7/timer.h`, and the pi scripts include it as `"../timer.h"`. If you move a pi script out of its folder, remember to move `timer.h` with it or fix the include path.

## Suggested order

Read and run the weeks in order. Within each week, read the files in the order the numbers suggest (0, 1, 2, ... or 1, 2, 3, ...). Every script builds on the one before it, and jumping around will make some things harder than they need to be.

## When something doesn't work

- Java won't compile: most likely a class name / file name mismatch. The file must be named exactly the same as the public class inside it.
- C won't compile: did you pass `-lpthread`? For the pi scripts you also need `-lm`.
- C compiles but does nothing visible: some examples loop forever on purpose. Press `Ctrl+C` to stop.
- The race-condition script gives 100 every time: your machine is probably too fast for the race to show up on this tiny example. Increase the number of threads or add a short sleep inside deposit() to make the window wider.
