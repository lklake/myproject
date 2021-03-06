package ticketingsystem;

import java.util.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class ThreadId {
    // Atomic integer containing the next thread ID to be assigned
    private static final AtomicInteger nextId = new AtomicInteger(0);

    // Thread local variable containing each thread's ID
    private static final ThreadLocal<Integer> threadId =
        new ThreadLocal<Integer>() {
            @Override protected Integer initialValue() {
                return nextId.getAndIncrement();
        }
    };

    // Returns the current thread's unique ID, assigning it if necessary
    public static int get() {
        return threadId.get();
    }
}

public class Test {
	final static int threadnum = 64;
	final static int routenum = 5; // route is designed from 1 to 3
	final static int coachnum = 8; // coach is arranged from 1 to 5
	final static int seatnum = 100; // seat is allocated from 1 to 20
	final static int stationnum = 10; // station is designed from 1 to 5

	final static int testnum = 20000;
	final static int retpc = 10; // return ticket operation is 10% percent
	final static int buypc = 40; // buy ticket operation is 30% percent
	final static int inqpc = 100; //inquiry ticket operation is 60% percent
	
	static String passengerName() {
		Random rand = new Random();
		long uid = rand.nextInt(testnum);
		return "passenger" + uid; 
	}

	public static void main(String[] args) throws InterruptedException {
		Thread[] threads = new Thread[threadnum];
		AtomicLong time = new AtomicLong(0);
		
		final TicketingDS tds = new TicketingDS(routenum, coachnum, seatnum, stationnum, threadnum);

		for (int i = 0; i< threadnum; i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					Random rand = new Random();
					Ticket ticket = new Ticket();
					ArrayList<Ticket> soldTicket = new ArrayList<Ticket>();
				
					for (int i = 0; i < testnum; i++) {
						int sel = rand.nextInt(inqpc);
						if (0 <= sel && sel < retpc && soldTicket.size() > 0) { // return ticket
							int select = rand.nextInt(soldTicket.size());
							if ((ticket = soldTicket.remove(select)) != null) {
								// long startTime = System.nanoTime();
								tds.refundTicket(ticket);
								// long endTime = System.nanoTime();
								// time.addAndGet(endTime-startTime);
							}
						} else if (retpc <= sel && sel < buypc) { // buy ticket
							String passenger = passengerName();
							int route = rand.nextInt(routenum) + 1;
							int departure = rand.nextInt(stationnum - 1) + 1;
							int arrival = departure + rand.nextInt(stationnum - departure) + 1; // arrival is always greater than departure
							// long startTime = System.nanoTime();
							ticket = tds.buyTicket(passenger, route, departure, arrival);
							// long endTime = System.nanoTime();
							// time.addAndGet(endTime-startTime);
							if ((ticket) != null) {
								
								soldTicket.add(ticket);
							}
						} else if (buypc <= sel && sel < inqpc) { // inquiry ticket
							int route = rand.nextInt(routenum) + 1;
							int departure = rand.nextInt(stationnum - 1) + 1;
							int arrival = departure + rand.nextInt(stationnum - departure) + 1; // arrival is always greater than departure
							// long startTime = System.nanoTime();
							tds.inquiry(route, departure, arrival);
							// long endTime = System.nanoTime();
							// time.addAndGet(endTime-startTime);
						}
					}
				}
			});
		}

		long startTime = System.nanoTime();
		for (int i = 0; i< threadnum; i++) {
			threads[i].start();
	    }
	    for (int i = 0; i< threadnum; i++) {
	    	threads[i].join();
	    }
		long endTime = System.nanoTime();
		System.out.println("time"+((endTime-startTime)/1000000.0)+" ops"+threadnum*testnum/((endTime-startTime)/1000000.0)+" thread"+threadnum);
		// System.out.println("time"+time.get()+" ops"+threadnum*testnum/(time.get()/1000000.0)+" thread"+threadnum);
	}
}
