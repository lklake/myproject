package ticketingsystem;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) throws InterruptedException {
        ArrayList<Ticket> tickets = new ArrayList<>();
		final TicketingDS tds = new TicketingDS(1, 3, 1, 3, 1);
		tickets.add(tds.buyTicket("passenger1", 1, 2, 3));
		tickets.add(tds.buyTicket("passenger1", 1, 2, 3));
		tds.refundTicket(tickets.get(0));
		tickets.add(tds.buyTicket("passenger1", 1, 2, 3));
		return ;
		//ToDo
	    
	}
}
