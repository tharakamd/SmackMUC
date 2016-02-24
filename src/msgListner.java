import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class msgListner implements MessageListener{

	@Override
	public void processMessage(Message msg) {
		if (msg.getBody() != null)
			System.out.println(msg.getFrom() + ": " + msg.getBody());
	}

}
