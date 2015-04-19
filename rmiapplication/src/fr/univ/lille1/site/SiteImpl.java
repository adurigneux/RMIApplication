package fr.univ.lille1.site;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the implementation for a site node, defined by a unique ID It
 * can receive and send message to his sons and it knows his father
 * 
 * @author Durigneux Antoine
 * @author Dupont Cl�ment
 */
public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	private static final long serialVersionUID = -725073737349988139L;

	private SiteItf father;
	private List<SiteItf> child;
	private int id;
	private ColorTerm color = ColorTerm.BLEU;
	private Map<String, String> mAlreadyReceived = new HashMap<String, String>();

	public SiteImpl(int id) throws RemoteException {
		super();
		this.id = id;
	}

	public void ConstructSite(SiteItf father, SiteItf[] child)
			throws RemoteException {
		this.father = father;
		this.child = new ArrayList<SiteItf>();
		if (child != null) {
			for (SiteItf node : child) {
				this.child.add(node);
			}
		}
	}

	public void setColor(ColorTerm color) throws RemoteException {
		this.color = color;
	}

	public SiteItf getFather() throws RemoteException {
		return father;
	}

	public List<SiteItf> getChildren() throws RemoteException {
		return child;
	}

	public int getId() throws RemoteException {
		return id;
	}

	private void sendMessage(final SiteItf son, final byte[] message)
			throws RemoteException {

		final Message mess = new Message(message);

		System.out.println(this.color.toString() + this.getId()
				+ ") I Send a message to : " + son.getId() + " || message : "
				+ mess.toString());
		final SiteItf initiator = this;

		// i send the message, i dont need to received it again
		this.mAlreadyReceived.put(mess.getId(), mess.getMessage());

		new Thread() {
			public void run() {
				try {
					son.receiveMessage(initiator, mess.getOriginalMessage());
				} catch (RemoteException ignore) {
				}
			}
		}.start();
	}

	public void sendMessageToSite(String siteId, byte[] message)
			throws RemoteException {
		int port = 1099;
		LocateRegistry.getRegistry(port);
		String addresse = "rmi://localhost:" + port + "/" + siteId;
		SiteItf site;

		try {
			site = (SiteItf) Naming.lookup(addresse);
			sendMessage(site, message);
		} catch (MalformedURLException ignore) {
		} catch (NotBoundException ignore) {
		}
	}

	@Override
	public void propagate(byte[] message) throws RemoteException {
		for (SiteItf son : child) {
			sendMessage(son, message);
		}
	}

	@Override
	public void receiveMessage(SiteItf initiator, byte[] message)
			throws RemoteException {
		Message messageReceived = new Message(message);

		// dont send the message if we already received it or if we are the one
		// that send the message
		if (mAlreadyReceived.get(messageReceived.getId()) == null) {
			mAlreadyReceived.put(messageReceived.getId(),
					messageReceived.getMessage());
			System.out.println(this.color.toString() + this.getId()
					+ ") I receive a message from " + initiator.getId()
					+ " || message : " + messageReceived.toString());
			// now i have to send to my sons the same message
			propagate(message);
		}
	}

	@Override
	public Map<String, String> getHistoryMessages() throws RemoteException {
		return mAlreadyReceived;
	}

	/**
	 * Internal class that represant a message, its only use in order to be
	 * easier to use message
	 */
	private class Message {
		String message;
		String id;
		byte[] originalMessage;

		Message(byte[] mes) {
			this.originalMessage = mes;
			String messageReceived = "";
			for (byte b : mes) {
				messageReceived += (char) b;
			}
			String[] messageArray = messageReceived.split("#");
			this.id = messageArray[0];
			this.message = messageArray[1];
		}

		public String getMessage() {
			return message;
		}

		public String getId() {
			return id;
		}

		public byte[] getOriginalMessage() {
			return originalMessage;
		}

		@Override
		public String toString() {
			return "(" + "message='" + message + '\'' + ", id='" + id + '\''
					+ ')';
		}
	}

}
