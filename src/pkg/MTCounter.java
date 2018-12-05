package pkg;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MTCounter {
	
	int iThreadCounter;
    private static int iMaxVal = 1000000;	
	private static AtomicInteger iCounter = new AtomicInteger(0);


	MTCounter(int count){
		/*
		 * Not much chance to show off Dependency Injection in this example, but 
		 * we can set up one operational parameter with a constructor.
		 */
		this.iThreadCounter = count;
	}
    

	void fnc01() {
		
		/*
		 * What's going on here?
		 * To make a list of threads, we need three operations
		 * all chained together.
		 * i. IntStream.range(0, iThreadCounter)
		 * Nothing too radical here.  Run up an intstream between 0 and the value of iThreadCounter
		 * 
		 * ii. mapToObj(j->new Thread( MTCounter::doCount ) )
		 * Brand-new Java 8 pretend-Lisp stuff.
		 * The j-> part is a lambda (or Anonymous Function), which is to say, for each Whatever we've had passed on
		 * (in this case, a unit of intstream), assign to it the return value of "make a new thread, of type doCount", passed to (iii)
		 * (the 'oneIntStreamEntry', btw, is completely arbitrary, and just represents 'the instance of the Thing being mapped-to-object')
		 * 
		 * iii. collect(Collectors.toList())
		 * The lambda in part (ii) gives us a Collection (of what?  Who cares), which we have to make to be a List, so
		 * that it can be handled by our List of Threads from all the way over of the LHS.
		 */
        List<Thread> threads = IntStream.range(0, iThreadCounter).mapToObj(oneIntStreamEntry->new Thread( MTCounter::doCount ) ).collect(Collectors.toList());	
        
        /*
         * what it says.  For each thread in all-the-threads, start
         * running it. 
         */
        threads.forEach( Thread::start );
        
        /*
         * For each thread, get it to join in. 
         * Items on a List can be iterated over with the .forEach iterator, which 
         * takes EITHER a callback function name, or a lambda.  For brevity, we'll 
         * use the latter here.
		 *	
    	 * There is no good reason for exception handling here except that 
    	 * thread.join wants to be surrounded by a try-catch.  But thread
    	 * is never going to be an unpredictable something-or-other in _this 
    	 * use case_, the exception won't ever be thrown, and there's no reason
    	 * to write handling code for a circumstance that won't ever happen.
    	 */
        threads.forEach(oneThreadListItem -> {
			try {
				oneThreadListItem.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        
        /*
         * Don't forget: for each thread we have running, we must deduct 1 
         * from the final value.
         */
        int finalResult = iCounter.get() - this.iThreadCounter;
        
        System.out.print( "FINAL RESULT: " );
        System.out.print( finalResult );	        
        System.out.print( "    USING : " );
        System.out.print( this.iThreadCounter );
        System.out.println( " THREADS " );
	}

	
	private static void doCount(){
		
        for ( ;; ) {
        	/*
        	 * No biggie here.  The atomicInt offers a bunch of convenience functions
        	 * including getAndIncrement and incrementAndGet.
        	 * We are using incrementAndGet here for the same reason we used to
        	 * use ++i instead of i++.  Preincrement operators let us look at the value 
        	 * we want instead of the one we have, and since atomicInt is immutable, that's important.
        	 */
            int current_counter_value = iCounter.incrementAndGet();
            
            /*
             * Now what?
             * We're incrementing before we examine the value.  Which means, by the time we know the value 
             * has overshot, it's already overshot.  THIS IS NOT A PROBLEM, but we have to remember
             * to deduct the NUMBER OF THREADS from the FINAL iCounter value.  Which probably breaks 
             * encapsulation, but it's all inside the same class...
             */
           
            if (current_counter_value > iMaxVal) {
                break;
            }

            /*
             * TODO:
             * Stop print overrun.
             * Because the threads run out of order, after the *process* has concluded, 
             * the other threads will continue to dump values here.  So for instance, the final 
             * five entries on the lest, if 5 threads are used, might be something like:
             * 1000000
             * 999832
             * 999700
             * 999555
             * 999991
             */
            
            System.out.println(current_counter_value);

        }		
	}
}
