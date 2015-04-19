package test.fr.univ.lille1;

import fr.univ.lille1.site.SiteImpl;
import fr.univ.lille1.site.SiteItf;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.Map;

import static org.junit.Assert.*;

public class SiteImplTest {
	
	
	@Test
	public void testConstructSite() throws RemoteException {
		SiteImpl site1 = new SiteImpl(0);
		SiteImpl site2 = new SiteImpl(1);
		SiteImpl site3 = new SiteImpl(2);
		
		site1.ConstructSite(null, new SiteImpl[]{site2});
		site2.ConstructSite(site1, new SiteImpl[]{site3});
		site3.ConstructSite(site2, null);
		
		assertNull(site1.getFather());
		assertEquals(site1, site2.getFather());
		assertEquals(site2, site3.getFather());
		
		assertTrue(site1.getChildren().contains(site2));
		assertTrue(site2.getChildren().contains(site3));
		assertTrue(site3.getChildren().isEmpty());
	}
	
	@Test
	public void testSendMessageTree() throws RemoteException, InterruptedException {
		SiteImpl site1 = new SiteImpl(0);
		SiteImpl site2 = new SiteImpl(1);
		SiteImpl site3 = new SiteImpl(2);
		
		site1.ConstructSite(null, new SiteImpl[]{site2});
		site2.ConstructSite(site1, new SiteImpl[]{site3});
		site3.ConstructSite(site2, null);
		
		String id = "idMessage";
		String message = "Ceci est un test";
		String all = id +"#" + message;
		site1.propagate(all.getBytes());
		
		Thread.sleep(500);
		
		Map<String,String> history = site2.getHistoryMessages();
		String histMessage = history.get(id);
		assertEquals("Ceci est un test", histMessage);
		
		history = site3.getHistoryMessages();
		histMessage = history.get(id);
		assertEquals("Ceci est un test", histMessage);
	}
	
	@Test
	public void testSendMessageGraph() throws RemoteException, InterruptedException {
		SiteImpl site1 = new SiteImpl(0);
		SiteImpl site2 = new SiteImpl(1);
		SiteImpl site3 = new SiteImpl(2);
		SiteImpl site4 = new SiteImpl(3);
		SiteImpl site5 = new SiteImpl(4);
		SiteImpl site6 = new SiteImpl(5);
		
		site1.ConstructSite(null, new SiteItf[]{site2, site5});
		site2.ConstructSite(null, new SiteItf[]{site3, site4, site1});
		site3.ConstructSite(null, new SiteItf[]{site2});
		site4.ConstructSite(null, new SiteItf[]{site2});
		site5.ConstructSite(null, new SiteItf[]{site6, site1});
		site6.ConstructSite(null, new SiteItf[]{site5});
		
		String id = "idMessage";
		String message = "Ceci est un test";
		String all = id + "#" + message;
		site1.propagate(all.getBytes());
		
		Thread.sleep(500);
		
		Map<String,String> history = site2.getHistoryMessages();
		String histMessage = history.get(id);
		assertEquals("Ceci est un test", histMessage);
		
		history = site3.getHistoryMessages();
		histMessage = history.get(id);
		assertEquals("Ceci est un test", histMessage);
		
		history = site4.getHistoryMessages();
		histMessage = history.get(id);
		assertEquals("Ceci est un test", histMessage);
		
		history = site5.getHistoryMessages();
		histMessage = history.get(id);
		assertEquals("Ceci est un test", histMessage);
		
		history = site6.getHistoryMessages();
		histMessage = history.get(id);
		assertEquals("Ceci est un test", histMessage);
	}

}
