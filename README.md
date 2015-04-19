Site node RMI
===================
Auteurs
: Antoine Durigneux
: Clement Dupont

02/04/2015



Introduction
-------------

Le but de ce TP est de construire une application RMI qui va enregistrer un ensemble d'objets organisés selon deux typologies : en arbre et en graphe. 
Chaque noeud va être éxecuté sur une machine virtuelle différente et propage des messages à ses fils (ou ses voisins pour les graphes).

Le principe est séparer chaque noeud pour que le transfert de données se fasse par le réseau, et non pas par le biais d'une machine virtuelle commune.
Ceci est possible en enregistrant chaque noeud à l'aide d'un registre RMI en spécifiant adresse IP et port.

Utilisation
----------

Afin de tester notre application, nous avons mis à disposition deux scripts codés en Shell :
-> multiple-Vms-Tree.sh (topologie en arbre)
-> multiple-Vms-Graph.sh (topologie en graphe)

Chaque script va créer 6 noeuds selon sa propre topologie. Ensuite, chaque noeud va être lancé (à l'aide d'un jar executable) depuis un nouveau terminal afin de bien distinguer les machines virtuelles.

Architecture
-------------



###configuration


Le projet a été constitué sous la forme de 3 projets distincts :
-> RMIInterface qui contient uniquement les interfaces utilisées dans les autres projets (principe de CDN)
-> RMIApplication qui contient les fichiers d'implémentation des interfaces
-> RMIClient qui contient les fichiers de la création des arbres et des graphes (client)

###Polymorphisme

Parmi le peu de classes présentes dans ce projet, il y a toutefois un certain nombres d'héritages et d'interfacing.

Interface SiteItf de RMIInterface
Cette interface va permettre au développeur de choisir sa propre implémentation d'un noeud pour les faire communiquer entre eux.
Il faut donc réimplémenter les méthodes suivantes :

	public interface SiteItf extends Remote {
		public void ConstructSite(SiteItf father, SiteItf[] child) throws RemoteException;
		
		public int getId() throws RemoteException;
		
		public void propagate(byte[] message) throws RemoteException;
		
		public void receiveMessage(SiteItf initiator, byte[] message) throws RemoteException;
				
		public void sendMessageToSite(String siteId, byte[] message) throws RemoteException;
		
		public Map<String, String> getHistoryMessages() throws RemoteException;
		
		public void setColor(ColorTerm color) throws RemoteException;
		
		public SiteItf getFather() throws RemoteException;
		
		public List<SiteItf> getChildren() throws RemoteException;
	
	}

Classe SiteImpl de RMIApplication
Dans notre cas ici, la classe SiteImpl doit être hériter par la classe UnicastRemoteObject afin qu'elle puisse être utilisé par le registre RMI.
Depuis dans le but d'avoir le même fonctionnement sur chaque noeud, la classe doit étendre SiteItf.

	public class SiteImpl extends UnicastRemoteObject implements SiteItf {
		
		...
		
		//Classe interne pour faciliter les 
		private class Message {
	
		}
	}	
	

###Erreurs

La majeure partie des try/catch ici vont être sur l'utilisation du concept RMI, c'est à dire que chaque fonction est complété par des "throws RemoteException" :

Methode sendMessage de la classe RMIApplication.SiteImpl.

	new Thread() {
		public void run() {
			try {
				son.receiveMessage(initiator, mess.getOriginalMessage());
			} catch (RemoteException ignore) {
			}
		}
	}.start();
		
Methode sendMessageToSite de la classe RMIApplication.SiteImpl.

	try {
		site = (SiteItf) Naming.lookup(addresse);
		sendMessage(site, message);
	} catch (MalformedURLException ignore) {
	} catch (NotBoundException ignore) {
	}
		
Methode main de la class RMIApplication.Main

	try {
		site = (SiteItf) Naming.lookup(addresse);
		sendMessage(site, message);
	} catch (MalformedURLException ignore) {
	} catch (NotBoundException ignore) {
	}
		
Methode main de la class RMIApplication.Main

	try {
		site = new SiteImpl(Integer.parseInt(args[0]));
		Naming.bind(adresse, site);
		System.out.println("Running : " + adresse);
	} catch (NumberFormatException ignore) {
	} catch (RemoteException ignore) {
	} catch (MalformedURLException ignore) {
	} catch (AlreadyBoundException ignore) {
	}
        
Méthode getFather de la classe SiteImpl.

	public SiteItf getFather() throws RemoteException {
		return father;
	}
	

Codes samples
-------------

###1. Reception d'un message

Méthode sendMessageToSite de la classe RMIApplication.SiteImpl
	
	public void sendMessageToSite(String siteId, byte[] message) throws RemoteException {
		int port = 1099;
		// Search local RMIRegistry
		LocateRegistry.getRegistry(port);
		// Recuperation of node adresse
		String addresse = "rmi://localhost:" + port + "/" + siteId;
		SiteItf site;

		try {
			// Search RMI node
			site = (SiteItf) Naming.lookup(addresse);
			// Send message to node
			sendMessage(site, message);
		} catch (MalformedURLException ignore) {
		} catch (NotBoundException ignore) {
		}
	}

###2. Envoie d'un message

Méthode receiveMessage de la classe RMIApplication.SiteImpl
	   	
	public void receiveMessage(SiteItf initiator, byte[] message) throws RemoteException {
		Message messageReceived = new Message(message);

		// dont send the message if we already received it or if we are the one
		// that send the message
		if (mAlreadyReceived.get(messageReceived.getId()) == null) {
			// Add the message to history
			mAlreadyReceived.put(messageReceived.getId(), messageReceived.getMessage());
			// Print received message with specific color of node
			System.out.println(this.color.toString() + this.getId()
					+ ") I receive a message from " + initiator.getId()
					+ " || message : " + messageReceived.toString());
			// now i have to send to my sons the same message
			propagate(message);
		}
	}
		
###3. Connexion / deconnexion au registry via du code java
Grâce à cette fonction "LocateRegistry.createRegistry(port)", nous n'avons pas besoin de créer et gérer manuellement le registry.

Méthode main de la classe RMIApplication.Main
		
	public static void main(String[] args) {
		int port = 1099;
		try {
			// Try to create registry with the specific port
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e1) {
			try {
				// If can't create, already create ?
				// So try to connected on registry on the specific port
				LocateRegistry.getRegistry(port);
			} catch (RemoteException e) {
				// Can't connect and create 
				// Display error and exit
				System.err.println("Unable to access to RMI Registry");
				return;
			}
		}
		//Save RMI instance into the registry
		....
		SiteItf site;
		try {
			// Node creation
			site = new SiteImpl(Integer.parseInt(args[0]));
			// Add node into registry
			Naming.bind(adresse, site);
			System.out.println("Running : " + adresse);
		} catch (...) {
		} 
	}
		
###4. Script d'execution des sites
Script multiple-Vms-Graph.sh
	
	#!/bin/bash

	echo "---------Starting Script Tree---------"
	# Loop between 1 and 6
	for i in `seq 1 6`
		# Create a new terminal to execute node creation with id i
		do xterm -e java -jar site-node.jar ${i} &
		# Recuperation of the last pid
		pids[$i]=$!
	done

	# Sleep 2 seconds to secure nodes creation
	sleep 2

	# graph client
	xterm -e java -jar client-graph.jar &
	pids[7]=$!

	# wait a key press
	echo -n "Press enter to quit..."
	read var_ok

	# kill all programms
	for i in `seq 1 7`
		do kill -9 ${pids[$i]}
	done 

	echo "---------Ending Script---------"

###5. Construct site
Fonction qui permet d'initialiser le parent et les enfants d'un site. Elle permet également de récuperer les noeuds dans le registry.
Méthode BuildNodes de la classe RMIApplication.MainTree
	
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
	
	
	

