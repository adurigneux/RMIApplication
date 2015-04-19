package fr.univ.lille1.main;

import fr.univ.lille1.site.ColorTerm;
import fr.univ.lille1.site.SiteItf;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used in order to create the tree example of hierarchy
 *
 * @author Durigneux Antoine
 * @author Dupont Cl�ment
 */
public class MainTree {

    public static void main(String[] args) {

        List<SiteItf> nodesList = new ArrayList<SiteItf>();

        try {
        	System.out.println("Launch Client Maint Tree");

            buildNodes(nodesList);
            setColorForNodes(nodesList);
            sendFirstMessageFrom1(nodesList.get(1));
            sendMessageFrom5to6(nodesList.get(5));
            sendMessageNotLinkedNode(nodesList.get(5));


        } catch (MalformedURLException ignore) {
        } catch (RemoteException ignore) {
        } catch (NotBoundException ignore) {
        }


    }

    /**
     * build the tree with every nodes
     *
     * @param nodesList
     * @return
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    private static List<SiteItf> buildNodes(List<SiteItf> nodesList) throws RemoteException, MalformedURLException, NotBoundException {
        //just add a null pointer in the first value, in order to use 1, for node 1, etc, not 0 for node 1
        nodesList.add(null);

        int port = 1099;
        LocateRegistry.getRegistry(port);
        System.out.println("Try to recover every nodes");
        //get every object in the registry
        for (int i = 1; i <= 6; i++) {
            String addresse = "rmi://localhost:" + port + "/" + i;
            SiteItf site = (SiteItf) Naming.lookup(addresse);
            nodesList.add(i, site);
        }

        System.out.println("Try to link every nodes");
        //construct every nodes
        nodesList.get(1).ConstructSite(null, new SiteItf[]{nodesList.get(2), nodesList.get(5)});
        nodesList.get(2).ConstructSite(nodesList.get(1), new SiteItf[]{nodesList.get(3), nodesList.get(4)});
        nodesList.get(3).ConstructSite(nodesList.get(2), null);
        nodesList.get(4).ConstructSite(nodesList.get(2), null);
        nodesList.get(5).ConstructSite(nodesList.get(1), new SiteItf[]{nodesList.get(6)});
        nodesList.get(6).ConstructSite(nodesList.get(5), null);
        return nodesList;
    }


    /**
     * Send a message from the node 5 to the node 3, they are not linked
     *
     * @param launcher
     * @throws RemoteException
     */
    private static void sendMessageNotLinkedNode(SiteItf launcher) throws RemoteException {
        System.out.println("Try to send a message from 5 to 3 (not a son or father)");
        //send message from 1
        String father5to3 = "From 5 for 3, i m just a friend !";
        father5to3 = getMessageWithId(father5to3, 5 + "");
        launcher.sendMessageToSite("3", father5to3.getBytes());
    }

    /**
     * Send a message from the node 5 to the node 6, they are linked
     *
     * @param launcher
     * @throws RemoteException
     */
    private static void sendMessageFrom5to6(SiteItf launcher) throws RemoteException {
        System.out.println("Try to send a message from 5 to his sons");
        //send message from 1
        String father5to6 = "From 5 for 6, i m your father !";
        father5to6 = getMessageWithId(father5to6, 5 + "");
        launcher.propagate(father5to6.getBytes());
    }

    /**
     * Send a message from the root node
     *
     * @param launcher
     * @throws RemoteException
     */
    private static void sendFirstMessageFrom1(SiteItf launcher) throws RemoteException {
        System.out.println("Try to send a message from 1 to his sons, and every sons will send after tho their own sons, etc..");
        //send message from 1
        String hello = "Hello World !";
        hello = getMessageWithId(hello, 1 + "");
        launcher.propagate(hello.getBytes());
    }


    /**
     * Define a message with a unique id for a message + # for delimeter and the message
     *
     * @param message the message
     * @param siteId  The sender site id
     * @return id#message
     */
    private static String getMessageWithId(String message, String siteId) {
        String result = siteId + message;
        result = System.currentTimeMillis() + result;
        result = result.hashCode() + "#" + message;
        return result;
    }
    
	/**
	 * Define a color for each node to will be used when a log is printed
	 *
	 * @param nodesList
	 */
	private static void setColorForNodes(List<SiteItf> nodesList) throws RemoteException {

		nodesList.get(1).setColor(ColorTerm.BLEU);
		nodesList.get(2).setColor(ColorTerm.JAUNE);
		nodesList.get(3).setColor(ColorTerm.CYAN);
		nodesList.get(4).setColor(ColorTerm.MAUVE);
		nodesList.get(5).setColor(ColorTerm.ROUGE);
		nodesList.get(6).setColor(ColorTerm.VERT);

	}


}
