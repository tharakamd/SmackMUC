import java.io.IOException;
import java.util.Scanner;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class MUCBot {

	String HOST_NAME = "localhost";
	String SERVER_NAME = "dilan-pc";
	String CHAT_SERVICE = "conference.dilan-pc";
	int PORT = 5222;
	Scanner scanner;
	String userName;
	

	AbstractXMPPConnection con;
	Chat newChat;
	MultiUserChat muc;
	
	
	public MUCBot() {
		scanner = new Scanner(System.in);
	}

	public static void main(String[] args) throws InterruptedException {
		MUCBot bot = new MUCBot();
		bot.startBot();
	}

	public void startBot() throws InterruptedException {
		System.out.println("Enter username: ");
		userName = scanner.nextLine();
		System.out.println("Enter password: ");
		String pw = scanner.nextLine();
		connectAndLogin(userName, pw);
		System.out.println("Enter 1 to create chat room or 2 to enter to existing chat room: ");
		int option = Integer.parseInt(scanner.nextLine());
		String roomName = null;
		String nickName = null;
		if(option==1){ // create new chat room
			System.out.println("Enter room name:");
			roomName= scanner.nextLine();
			System.out.println("Enter nick name:");
			nickName = scanner.nextLine();
			createChatRoom(roomName, nickName);
		}else if(option ==2){ // join chat room
			System.out.println("Enter room name:");
			roomName = scanner.nextLine();
			System.out.println("Enter nick name:");
			nickName = scanner.nextLine();
			joinChatRoom(roomName, nickName);
		}else{
			System.out.println("wrong option !!!");
			System.exit(1);
		}
		addListners();
		while(true){
			System.out.print("You: ");
			try {
				muc.sendMessage(scanner.nextLine());
			} catch (NotConnectedException e) {
				System.out.println("Error sending message !!!");
				e.printStackTrace();
			}
		}
		
	}
	

	/*
	 * join to a chat room
	 */
	public void joinChatRoom(String roomName, String nickName) {
		MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(con);
		muc = mucManager.getMultiUserChat(roomName + "@" + CHAT_SERVICE);
		try {
			muc.join(nickName);
			System.out.println("joined to chat room: "+ muc.getRoom());
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			System.out.println("Error joining chat room !!!");
			e.printStackTrace();
		}
	}
	
	/*
	 * add listners to the created muc instance
	 */
	public void addListners(){
		muc.addMessageListener(new msgListner());
		
	}

	/*
	 * create chat room instance
	 */
	public void createChatRoom(String roomName, String nickName) {
		MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(con);
		muc = manager.getMultiUserChat(roomName + "@" + CHAT_SERVICE);
		try {
			muc.create(nickName);
			muc.sendConfigurationForm(new Form(DataForm.Type.submit));
			System.out.println("chat room created: " + muc.getRoom());
		} catch (XMPPErrorException | SmackException e) {
			System.out.println("Error creating the chat room !!!");
			e.printStackTrace();
		}
	}

	/*
	 * connect and log in to the server
	 */
	public void connectAndLogin(String userName, String pw) {
		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
				.setUsernameAndPassword(userName, pw).setServiceName(SERVER_NAME).setSecurityMode(SecurityMode.disabled)
				.setHost(HOST_NAME).setPort(PORT).build();
		con = new XMPPTCPConnection(config);
		try {
			con.connect();
			con.login();
			System.out.println("logged in");
		} catch (SmackException | IOException | XMPPException e) {
			System.out.println("Error in connect and login !!!");
			e.printStackTrace();
		}
	}
}
