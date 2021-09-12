package edu.yu.cs.com1320.Final;

import static org.junit.Assert.*;

import org.junit.Test;

public class CacheTest {

	@Test
	public void simpleLookUpTest() {
		RouteCache cache = new RouteCache(3);
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		cache.updateCache(ip1,1);
		cache.updateCache(ip2,2);
		cache.updateCache(ip3,3);
		assertEquals((Integer)3, cache.lookupRoute(ip3));
		assertEquals((Integer)2, cache.lookupRoute(ip2));
		assertEquals((Integer)1, cache.lookupRoute(ip1));
	}
	
	@Test
	public void simpleQueueStateTest() {
		RouteCache cache = new RouteCache(3);
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		cache.updateCache(ip1,1);
		cache.updateCache(ip2,2);
		cache.updateCache(ip3,3);
		String[] q = cache.dumpQueue();
		assertEquals(ip3.toCIDR(), q[0]);
		assertEquals(ip2.toCIDR(), q[1]);
		assertEquals(ip1.toCIDR(), q[2]);
	}
	
	@Test
	public void simpleLRURemovedTest() {
		RouteCache cache = new RouteCache(3);
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		IPAddress ip4 = new IPAddress("4.0.0.0");
		cache.updateCache(ip1,1);
		cache.updateCache(ip2,2);
		cache.updateCache(ip3,3);
		// right now the queue is: 3,2,1
		// we add ip4, should boot 1
		cache.updateCache(ip4,4);
		// the queue should now be 4,3,2
		String[] q = cache.dumpQueue();
		assertEquals(ip4.toCIDR(), q[0]);
		assertEquals(ip3.toCIDR(), q[1]);
		assertEquals(ip2.toCIDR(), q[2]);
		// make sure 1 is gone
		assertEquals(3, q.length);
		assertNull(cache.lookupRoute(ip1));
		// make sure ip4 was input properly
		assertEquals((Integer)4, cache.lookupRoute(ip4));
	}
	
	@Test
	public void simpleUpdateCacheAlreadyPresentTest() {
		RouteCache cache = new RouteCache(3);
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		cache.updateCache(ip1,1);
		cache.updateCache(ip2,2);
		cache.updateCache(ip3,3);
		// right now the queue is: 3,2,1
		// when we update 2, it should be 2,3,1
		cache.updateCache(ip2,4);
		String[] q = cache.dumpQueue();
		assertEquals(ip2.toCIDR(), q[0]);
		assertEquals(ip3.toCIDR(), q[1]);
		assertEquals(ip1.toCIDR(), q[2]);
		assertEquals(3, q.length);
		assertEquals((Integer)4, cache.lookupRoute(ip2));
	}
	
	@Test
	public void updateTailTest() {
		RouteCache cache = new RouteCache(3);
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		cache.updateCache(ip1,1);
		cache.updateCache(ip2,2);
		cache.updateCache(ip3,3);
		// right now the queue is: 3,2,1
		// when we update 3, the state should stay 3,2,1
		cache.updateCache(ip3,4);
		String[] q = cache.dumpQueue();
		assertEquals(ip3.toCIDR(), q[0]);
		assertEquals(ip2.toCIDR(), q[1]);
		assertEquals(ip1.toCIDR(), q[2]);
		assertEquals(3, q.length);
		assertEquals((Integer)4, cache.lookupRoute(ip3));
	}
	
	@Test
	public void updateHeadTest() {
		RouteCache cache = new RouteCache(3);
		IPAddress ip1 = new IPAddress("1.0.0.0");
		IPAddress ip2 = new IPAddress("2.0.0.0");
		IPAddress ip3 = new IPAddress("3.0.0.0");
		cache.updateCache(ip1,1);
		cache.updateCache(ip2,2);
		cache.updateCache(ip3,3);
		// right now the queue is: 3,2,1
		// when we update 1, the queue should be 1,3,2
		cache.updateCache(ip1,4);
		String[] q = cache.dumpQueue();
		assertEquals(ip1.toCIDR(), q[0]);
		assertEquals(ip3.toCIDR(), q[1]);
		assertEquals(ip2.toCIDR(), q[2]);
		assertEquals(3, q.length);
		assertEquals((Integer)4, cache.lookupRoute(ip1));
	}
	
	@Test
	public void updateSingletonTest() {
		RouteCache cache = new RouteCache(3);
		IPAddress ip1 = new IPAddress("1.0.0.0");
		cache.updateCache(ip1,1);
		assertEquals((Integer)1, cache.lookupRoute(ip1));
		cache.updateCache(ip1,2);
		String[] q = cache.dumpQueue();
		assertEquals(ip1.toCIDR(), q[0]);
		assertEquals(1, q.length);
		assertEquals((Integer)2, cache.lookupRoute(ip1));
	}

}
