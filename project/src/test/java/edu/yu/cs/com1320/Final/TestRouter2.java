package edu.yu.cs.com1320.Final;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

/**
 * The test class TestRouter2.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class TestRouter2
{

    //private TrieST<Integer> router;
    private IPRouter r;
    private IPRouter r1;  // cache capacity = 1
    private IPRouter ncr; // no cache router
    /**
     * Default constructor for test class TestRouter
     */
    public TestRouter2()
    {
    }

    /**
     * This test class uses my new "routes3" file
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
       this.r = new IPRouter(10,3); 
       this.r1 = new IPRouter(10,1); 
       this.ncr = new IPRouter(10,0);
        try {
            r.loadRoutes(System.getProperty("user.dir")+File.separator+"routes3.txt");
            r1.loadRoutes(System.getProperty("user.dir")+File.separator+"routes3.txt");
            ncr.loadRoutes(System.getProperty("user.dir")+File.separator+"routes3.txt");
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Bad routes file name. Tests aborted");
        }
    }

    
    @Test
    public void lessSpecificRuleAddedAfterTest() {
    	IPAddress ip1 = new IPAddress("0.1.61.42"); // should map to 1, b/c of line 1 in routes3
    	assertEquals(1, r.getRoute(ip1));
    }
    
    @Test
    public void tripleOverlapDeleteMiddleRuleTest() { // for delete we need to use the no cache router so we don't get false positives, see @502
    	IPAddress ip1 = new IPAddress("9.57.13.42"); // should map to 9
    	IPAddress ip2 = new IPAddress("9.1.25.74"); // should map to 1
    	IPAddress ip3 = new IPAddress("9.1.2.36"); // should map to 2
    	assertEquals(9, ncr.getRoute(ip1));
    	assertEquals(1, ncr.getRoute(ip2));
    	assertEquals(2, ncr.getRoute(ip3));
    	ncr.deleteRule("9.1.203.75/16"); // now ip2 should map to 9
    	assertEquals(9, ncr.getRoute(ip1));
    	assertEquals(9, ncr.getRoute(ip2));
    	assertEquals(2, ncr.getRoute(ip3));
    }
    
    @Test
    public void simpleDeleteRuleTest() { // for delete we need to use the no cache router so we don't get false positives, see @502
    	IPAddress ip1 = new IPAddress("7.57.13.42"); // should map to 7
    	assertEquals(7, ncr.getRoute(ip1));
    	ncr.deleteRule("7.1.203.75/8"); // now ip2 should map to 9
    	assertEquals(-1, ncr.getRoute(ip1));
    }
    
    @Test
    public void tripleOverlapTest() {
    	IPAddress ip1 = new IPAddress("9.57.13.42"); // should map to 9
    	IPAddress ip2 = new IPAddress("9.1.25.74"); // should map to 1
    	IPAddress ip3 = new IPAddress("9.1.2.36"); // should map to 2
    	assertEquals(9, r.getRoute(ip1));
    	assertEquals(1, r.getRoute(ip2));
    	assertEquals(2, r.getRoute(ip3));
    }
    
    @Test
    public void simpleIsCachedTest() {
    	IPAddress ip1 = new IPAddress("9.57.13.42"); 
    	assertEquals(9, r.getRoute(ip1));
    	assertTrue(r.isCached(ip1));
    }     
    
    
    //******************* CACHE TESTING ***************************
    // NOTE: similar to the CacheTest but from the framework of the router
    
    @Test
	public void simpleLookUpTest() {
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		r.getRoute(ip1);
		r.getRoute(ip2);
		r.getRoute(ip3);
		assertTrue(r.isCached(ip3));
		assertTrue(r.isCached(ip2));
		assertTrue(r.isCached(ip1));
	}
    
    @Test
	public void simpleQueueStateTest() {
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		r.getRoute(ip1);
		r.getRoute(ip2);
		r.getRoute(ip3);
		// state of queue should be 3,2,1
		String[] q = r.dumpCache();
		assertEquals(ip3.toCIDR(), q[0]);
		assertEquals(ip2.toCIDR(), q[1]);
		assertEquals(ip1.toCIDR(), q[2]);
	}
    
    @Test
	public void simpleLRURemovedTest() {
		IPAddress ip1 = new IPAddress("1.0.47.0");
		IPAddress ip2 = new IPAddress("2.0.0.24");
		IPAddress ip3 = new IPAddress("3.98.0.0");
		IPAddress ip4 = new IPAddress("4.0.129.8");
		assertEquals(1, r.getRoute(ip1));
		assertEquals(2, r.getRoute(ip2));
		assertEquals(3, r.getRoute(ip3));
		// right now the queue is: 3,2,1
		// we add ip4, should boot 1
		r.getRoute(ip4);
		// the queue should now be 4,3,2
		String[] q = r.dumpCache();
		assertEquals(ip4.toCIDR(), q[0]);
		assertEquals(ip3.toCIDR(), q[1]);
		assertEquals(ip2.toCIDR(), q[2]);
		// make sure 1 is gone
		assertEquals(3, q.length);
		assertFalse(r.isCached(ip1));
		// make sure ip4 was input properly
		assertEquals(4, r.getRoute(ip4));
	}
    
    @Test
	public void simpleUpdateCacheAlreadyPresentTest() {
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		assertEquals(1, r.getRoute(ip1));
		assertEquals(2, r.getRoute(ip2));
		assertEquals(3, r.getRoute(ip3));
		// right now the queue is: 3,2,1
		// when we query 2, it should be 2,3,1
		r.getRoute(ip2);
		String[] q = r.dumpCache();
		assertEquals(ip2.toCIDR(), q[0]);
		assertEquals(ip3.toCIDR(), q[1]);
		assertEquals(ip1.toCIDR(), q[2]);
		assertEquals(3, q.length);
	}
    
    @Test
	public void updateTailTest() {
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		assertEquals(1, r.getRoute(ip1));
		assertEquals(2, r.getRoute(ip2));
		assertEquals(3, r.getRoute(ip3));
		// right now the queue is: 3,2,1
		// when we query 3, the state should stay 3,2,1
		r.getRoute(ip3);
		String[] q = r.dumpCache();
		assertEquals(ip3.toCIDR(), q[0]);
		assertEquals(ip2.toCIDR(), q[1]);
		assertEquals(ip1.toCIDR(), q[2]);
		assertEquals(3, q.length);
	}
    
    @Test
	public void updateHeadTest() {
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		assertEquals(1, r.getRoute(ip1));
		assertEquals(2, r.getRoute(ip2));
		assertEquals(3, r.getRoute(ip3));
		// right now the queue is: 3,2,1
		// when we query 1, the queue should be 1,3,2
		r.getRoute(ip1);
		String[] q = r.dumpCache();
		assertEquals(ip1.toCIDR(), q[0]);
		assertEquals(ip3.toCIDR(), q[1]);
		assertEquals(ip2.toCIDR(), q[2]);
		assertEquals(3, q.length);
	} 

    @Test
	public void updateSingletonTest() {
		IPAddress ip1 = new IPAddress("1.0.0.0");
		assertEquals(1, r.getRoute(ip1));
		r.getRoute(ip1);
		String[] q = r.dumpCache();
		assertEquals(ip1.toCIDR(), q[0]);
		assertEquals(1, q.length);
	}
    
    @Test
	public void singletonCacheTest() {
		IPAddress ip1 = new IPAddress("1.0.0.0");
		assertEquals(1, r1.getRoute(ip1));
		// making sure ip1 is in cache, in the Map and Queue
		assertTrue(r1.isCached(ip1)); // checks map
		String[] q = r1.dumpCache(); // checks queue
		assertEquals(ip1.toCIDR(), q[0]);
		assertEquals(1, q.length);
		// now we check that any other query will boot ip1 from cache
		IPAddress ip2 = new IPAddress("2.0.0.0");
		assertEquals(2, r1.getRoute(ip2));
		// check that ip1 is not in map but ip2 is there
		assertTrue( (!r1.isCached(ip1)) && (r1.isCached(ip2))); 
		// check that ip2 is alone in the queue
		q = r1.dumpCache(); // checks queue
		assertEquals(ip2.toCIDR(), q[0]);
		assertEquals(1, q.length);
		// sanity check that ip1 is still routable, albeit through the trie
		assertEquals(1, r1.getRoute(ip1));
	}
    
    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }
}

