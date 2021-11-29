package ticketingsystem;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class MyTicket extends Ticket {
	MyTicket(long tid, String passenger, int route, int coach, int seat, int departure, int arrival) {
		this.tid = tid;
		this.passenger = passenger;
		this.route = route;
		this.coach = coach;
		this.seat = seat;
		this.departure = departure;
		this.arrival = arrival;
	}
	@Override
	public boolean equals(Object obj) {
		MyTicket ticket = (MyTicket) obj;
		return this.tid==ticket.tid&&this.passenger==ticket.passenger&&
		this.route==ticket.route&&this.coach==ticket.coach&&
		this.seat==ticket.seat&&this.departure==ticket.departure&&this.arrival==ticket.arrival;
	}
}

class RouteDs {
	int size;
	AtomicInteger[] atomic;
	int[] nonAtomic;
	Ticket[][] tickets;

	RouteDs(int size,int stationNum) {
		this.size = size;
		atomic = new AtomicInteger[size];
		nonAtomic = new int[size];
		for (int i = 0; i < size; i++) {
			atomic[i] = new AtomicInteger(0);
			nonAtomic[i] = 0;
		}
		this.tickets=new Ticket[size][];
		for(int i=0;i<size;i++){
			this.tickets[i]=new Ticket[stationNum];
		}
	}
}

public class TicketingDS implements TicketingSystem {

	// ToDo
	ArrayList<RouteDs> routes;
	AtomicLong ticketId;
	int coachesPerRoute;
	int seatsPerCoach;
	final int seatMask;

	TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
		routes = new ArrayList<>();
		for (int i = 0; i < routenum; i++) {
			routes.add(new RouteDs(coachnum * seatnum,stationnum));
		}
		this.coachesPerRoute = coachnum;
		this.seatsPerCoach = seatnum;
		// 这个原子变量是一个竞争的节点，但我觉得冲突访问的机率不大。
		ticketId = new AtomicLong(0);
		seatMask = (1 << this.seatsPerCoach * this.coachesPerRoute) - 1;
	}

	public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
		RouteDs routeDs = routes.get(route - 1);
		int nonAtomic;
		// 00000110000
		int target = (1 << (arrival - 1)) - (1 << (departure - 1));
		int newValue;
		for (int i = 0; i < routeDs.size; i++) {
			nonAtomic = routeDs.nonAtomic[i];
			if ((nonAtomic & target) == 0) {
				newValue = nonAtomic | target;
				if (routeDs.atomic[i].compareAndSet(nonAtomic, newValue)) {
					routeDs.nonAtomic[i] = newValue;
					Ticket ticket = new MyTicket(ticketId.getAndIncrement(), passenger, route, (i / this.seatsPerCoach) + 1,
					(i % this.seatsPerCoach) + 1, departure, arrival);
					routeDs.tickets[i][departure-1]=ticket;
					return ticket;
				}
			}
		}
		return null;
	}

	public int inquiry(int route, int departure, int arrival) {
		int result = 0;
		RouteDs routeDs = routes.get(route - 1);
		int nonAtomic;
		int target = (1 << (arrival - 1)) - (1 << (departure - 1));
		for (int i = 0; i < routeDs.size; i++) {
			nonAtomic = routeDs.nonAtomic[i];
			if ((nonAtomic & target) == 0) {
				result ++;
			}
		}
		return result;
	}

	public boolean refundTicket(Ticket ticket) {
		boolean result = false;
		// try {
			int route = ticket.route;
			int arrival = ticket.arrival;
			int departure = ticket.departure;
			int seqSeat = (ticket.coach - 1) * this.seatsPerCoach + ticket.seat - 1;
			RouteDs routeDs = routes.get(route - 1);
			int nonAtomic;
			// 00000110000
			int target = (1 << (arrival - 1)) - (1 << (departure - 1));
			int newValue;
			nonAtomic = routeDs.nonAtomic[seqSeat];
			if ((nonAtomic & target) == target) {
				newValue = nonAtomic & ((~target) & seatMask);
				if (routeDs.atomic[seqSeat].compareAndSet(nonAtomic, newValue)) {
					if(!routeDs.tickets[seqSeat][departure-1].equals(ticket)){
						return false;
					}
					routeDs.nonAtomic[seqSeat] = newValue;
					return true;
				}
			}
		// } catch (Exception e) {
			// result = false;
		// }
		return result;
	}
	// public boolean buyTicketReplay(Ticket ticket);
	// public boolean refundTicketReplay(Ticket ticket);
}
