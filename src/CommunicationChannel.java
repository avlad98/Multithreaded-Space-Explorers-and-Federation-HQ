import java.util.concurrent.*;

public class CommunicationChannel {
	private ArrayBlockingQueue<Message> fromExplorers;
	private ArrayBlockingQueue<Message> fromHQs;
	private ConcurrentMap<Long, Message> map;
	private int size = 1000;


	public CommunicationChannel() {
		fromExplorers = new ArrayBlockingQueue<Message>(size);
		fromHQs = new ArrayBlockingQueue<Message>(size);
		map = new ConcurrentHashMap<>();
	}

	public void putMessageSpaceExplorerChannel(Message message) {
		fromExplorers.add(message);
	}

	public Message getMessageSpaceExplorerChannel() {
		Message message = null;

		try {
			message = fromExplorers.take();
		} catch (InterruptedException e) {

		}

		return message;
	}

	public void putMessageHeadQuarterChannel(Message message) {
		// Verifica ce tip de mesaj e si insereaza-l in array
		if(message.getData().equals(HeadQuarter.EXIT) || message.getData().equals(HeadQuarter.END)) {
			fromHQs.add(message);
			return;
		}

		// aflu threadul care vrea sa puna mesajul
		Long tid = Thread.currentThread().getId();

		// verific daca thread-ul are mapare la mesaj1
		if(map.containsKey(tid)){
			Message old = map.remove(tid);
			Message toSend = new Message(old.getCurrentSolarSystem(), message.getCurrentSolarSystem(), message.getData());
			fromHQs.add(toSend);
		} else {
			// thread-ul curent nu are asociat un mesaj
			map.put(tid, message);
		}
	}

	public Message getMessageHeadQuarterChannel() {
		Message message = null;

		try {
			message = fromHQs.take();
		} catch (InterruptedException e) {
		}

		return message;
	}
}
