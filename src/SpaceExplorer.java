import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.nio.charset.StandardCharsets;

public class SpaceExplorer extends Thread {
	private Integer hashCount;
	private Set<Integer> discovered;
	private CommunicationChannel channel;

	public SpaceExplorer(Integer hashCount, Set<Integer> discovered, CommunicationChannel channel) {
		this.hashCount = hashCount;
		this.discovered = discovered;
		this.channel = channel;
	}

	@Override
	public void run() {
		Message message;

		while(true) {
			message = channel.getMessageHeadQuarterChannel();
			if(message == null) {continue;}
			if(message.getData().equals(HeadQuarter.EXIT)) {return;}
			if(message.getData().equals(HeadQuarter.END)) {continue;}

			Integer parentNode = message.getParentSolarSystem();
			Integer newNode = message.getCurrentSolarSystem();

			if(discovered.contains(newNode)) continue;
			else discovered.add(newNode);

			String data = message.getData();
			String encryption = encryptMultipleTimes(data, hashCount);
			Message toSend = new Message(parentNode, newNode, encryption);
			channel.putMessageSpaceExplorerChannel(toSend);
		}

	}

	private String encryptMultipleTimes(String input, Integer count) {
		String hashed = input;
		for (int i = 0; i < count; ++i) {
			hashed = encryptThisString(hashed);
		}

		return hashed;
	}

	private static String encryptThisString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

			// convert to string
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xff & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}