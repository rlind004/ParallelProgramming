# Synchronized Method

First fix for the race condition. The deposit() method is marked with the synchronized keyword so that only a single thread can be inside it at any point. If a second thread attempts to call deposit() when someone already is in there, it's going to have to wait.

The lock itself is on the Account object (this). The end balance will now always be 100.

This is the easiest way—put "synchronized" on your method and call it done. The tradeoff is that the whole method body is locked, even parts of it that never touch shared data.
