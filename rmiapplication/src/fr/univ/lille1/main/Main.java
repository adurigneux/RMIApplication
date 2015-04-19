package fr.univ.lille1.main;

import fr.univ.lille1.site.SiteImpl;
import fr.univ.lille1.site.SiteItf;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


/**
 * This class is used in order to create a instance of a site node and push it to the registry
 *
 * @author Durigneux Antoine
 * @author Dupont Cl�ment
 */
public class Main {

    public static void main(String[] args) {

        int port = 1099;
        try {
            LocateRegistry.createRegistry(port);
        } catch (RemoteException e1) {
            try {
                LocateRegistry.getRegistry(port);
            } catch (RemoteException e) {
            	System.err.println("Unable to access to RMI Registry");
                return;
            }
        }
        //enregister une instance dans le registry

        if (args.length != 1) {
            System.out.println("Incorrect usage, need the id in args");
            return;
        }

        System.out.println("RMI launch for node number : " + args[0]);
        String adresse = "rmi://localhost:" + port + "/" + args[0];

        SiteItf site;
        try {
            site = new SiteImpl(Integer.parseInt(args[0]));
            Naming.bind(adresse, site);
            System.out.println("Running : " + adresse);
        } catch (NumberFormatException ignore) {
        } catch (RemoteException ignore) {
        } catch (MalformedURLException ignore) {
        } catch (AlreadyBoundException ignore) {
        }


    }

}
