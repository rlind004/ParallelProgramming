# Producer Consumer

The classic producer-consumer problem. This part is a buffer; it holds no more than 4 items as a linked list. As the producer adds items, the consumer takes them out.

— Buffer full? The producer waits on notFull.

— Buffer empty? Consumer waits on notEmpty.

— Producer adds an item → signals not empty.

— Consumer removes item → notFull signaled

The same concept as the bidirectional cooperation example above, but applied to a buffer rather than a bank account. This pattern appears everywhere—message queues, task pools, and data pipelines.
