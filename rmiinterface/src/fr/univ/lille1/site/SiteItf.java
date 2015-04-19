package fr.univ.lille1.site;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * This interface define a site node. A site node is defind by an id, his father and sons.
 * A node can send and receive message from different node in the hierarchy
 *
 * @author Durigneux Antoine
 * @author Dupont Cl�ment
 */
public interface SiteItf extends Remote {
    /**
     * builder of a site node, used in order to create the hierarchie
     * Here we set the father and define all sons
     *
     * @param father The father of a site node
     * @param child  An array of site node, all sons
     * @throws RemoteException
     */
    public void ConstructSite(SiteItf father, SiteItf[] child)
            throws RemoteException;

    /**
     * @return The site node ID
     * @throws RemoteException
     */
    public int getId() throws RemoteException;

    /**
     * This method sends a message to all different sons of this site node
     *
     * @param message The message to send
     * @throws RemoteException
     */
    public void propagate(byte[] message) throws RemoteException;

    /**
     * This method is used in order to receive message from another site node
     *
     * @param initiator The site node that create the message and send it to this site node
     * @param message   The message that we receive
     * @throws RemoteException
     */
    public void receiveMessage(SiteItf initiator, byte[] message)
            throws RemoteException;

    /**
     * This method is used in order to send a message to a node that is not a father / son
     * of this node. It looks for the particlar node in the registry
     *
     * @param siteId  The site id that we want to send a message to
     * @param message The message that we send
     * @throws RemoteException
     */
    public void sendMessageToSite(String siteId, byte[] message)
            throws RemoteException;
    
    public Map<String, String> getHistoryMessages() throws RemoteException;

    public void setColor(ColorTerm color) throws RemoteException;

    public SiteItf getFather() throws RemoteException;

    public List<SiteItf> getChildren() throws RemoteException;
    
}

