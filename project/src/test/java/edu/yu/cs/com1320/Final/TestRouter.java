package edu.yu.cs.com1320.Final;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

/**
 * The test class TestRouter.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class TestRouter
{

    //private TrieST<Integer> router;
    private IPRouter router;
    /**
     * Default constructor for test class TestRouter
     */
    public TestRouter()
    {
    }

    /**
     * This test class uses the "routes2" file
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
       this.router = new IPRouter(8,4); 
        try {
            router.loadRoutes(System.getProperty("user.dir")+File.separator+"routes2.txt");
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Bad routes file name. Tests aborted");
        }
    }

    /**
     * Handle an unroutable address
     */
    @Test
    public void testBadRoute()
    {
        IPAddress address = new IPAddress("73.73.0.1");
        assertEquals(-1, this.router.getRoute(address));
    }
    
    /**
     * Delete an unroutable address throws IAE
     */
    @Test (expected = IllegalArgumentException.class)
    public void testBadDelete()
    {
        this.router.deleteRule("73.73.0.1");
    }
    
    /**
     * Add rule when rule already exists with different port throws IAE
     */
    @Test (expected = IllegalArgumentException.class)
    public void testAddDifferentAlreadyThere()
    {
    	this.router.addRule("73.73.0.1",1);
    	this.router.addRule("73.73.0.1",2);
    }
    
    /**
     * Add rule when rule already exists with same port doesn't throw IAE
     */
    @Test 
    public void testAddIdenticalAlreadyThere()
    {
    	try {
    		this.router.addRule("73.73.0.1",1);
        	this.router.addRule("73.73.0.1",1);
    		assertTrue(true);
    	} catch (IllegalArgumentException e) {
    		assertTrue(false);
    	}
    }
    
    @Test
    public void badRouteAddedToCache()
    {
        IPAddress address = new IPAddress("73.73.0.1");
        assertEquals(-1, this.router.getRoute(address));
        assertTrue(this.router.isCached(address));
    }
    
    /**
     * Add rule when port above range throws IAE
     */
    @Test (expected = IllegalArgumentException.class)
    public void testAddAboveRange()
    {
    	this.router.addRule("73.73.0.1",8);
    }
    
    /**
     * Add rule when port below range throws IAE
     */
    @Test (expected = IllegalArgumentException.class)
    public void testBadAddBelowRange()
    {
    	this.router.addRule("73.73.0.1",-1);
    }

    /**
     * Tests that if longer prefixes have defined rules, the parent will not be routed
     */
    @Test
    public void parentRuleUndefinedTest()
    {
        IPAddress ip1 = new IPAddress("1.1.7.23");
        IPAddress ip2 = new IPAddress("1.2.42.72");
        router.addRule("1.1.0.0/16", 1);
        router.addRule("1.2.0.0/16", 2);
        assertEquals(1, router.getRoute(ip1));
        assertEquals(2, router.getRoute(ip2));
        IPAddress parent = new IPAddress("1.0.0.0/8");
        assertEquals(-1, router.getRoute(parent));
    }
    
    /**
     * Deleting a rule changes where the IP will be routed
     * Note: we use empty cache so that it doesn't return
     * the deleted route from cache
     */
    @Test
    public void deleteRuleFallBackToShorterPrefixNoCacheTest()
    {
        IPRouter customRouter = new IPRouter(8,0); 
        customRouter.addRule("4.0.0.0/8", 1);
        customRouter.addRule("4.1.0.0/16", 2); // more specific
    	IPAddress address = new IPAddress("4.1.1.0");
        assertEquals(2, customRouter.getRoute(address));
        assertFalse(customRouter.isCached(address)); // making sure it's not in cache, so test will work
        customRouter.deleteRule("4.1.0.0/16");
        assertFalse(customRouter.isCached(address)); // making sure it's not in cache, so test will work
        assertEquals(1, customRouter.getRoute(address)); 
    }
    
    //************ CONFIRMING CORRECT ROUTING *********************
    // NOTE: uses routes2.txt and each test confirms that the corresponding line was routed as expected
    
    @Test
    public void line3Test()
    {
        IPAddress address = new IPAddress("24.200.0.0");
        int res = this.router.getRoute(address);
        assertEquals(3, res);
    }
    
    @Test
    public void line5Test()
    {
        IPAddress address = new IPAddress("24.0.0.0");
        int res = this.router.getRoute(address);
        assertEquals(4, res);
    }
        
    @Test
    public void line8Test()
    {
        IPAddress address = new IPAddress("24.30.13.249");
        int res = this.router.getRoute(address);
        assertEquals(5, res);
    }
    
    @Test
    public void line12Test()
    {
        IPAddress address = new IPAddress("24.91.73.22");
        int res = this.router.getRoute(address);
        assertEquals(7, res);
    }
    
    @Test
    public void line13Test()
    {
        IPAddress address = new IPAddress("24.99.0.249");
        int res = this.router.getRoute(address);
        assertEquals(6, res);
    }
    
    /**
     * Handle an address that only matches one prefix
     */
    @Test
    public void port2Test()
    {
        IPAddress address = new IPAddress("85.2.0.1");
        int res = this.router.getRoute(address);
        assertEquals(2, res);
    }

    /**
     * Handle an address that  matches multiple prefixes. Only the longest one counts
     */
    @Test
    public void port1Test()
    {
        IPAddress address = new IPAddress("85.85.85.85");
        int res = this.router.getRoute(address);
        assertEquals(1, res);
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

