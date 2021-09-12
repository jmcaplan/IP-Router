package edu.yu.cs.com1320.Final;

import java.util.HashMap;
import java.util.Map;
/**
 * This is a bounded cache that maintains only the most recently accessed IP Addresses
 * and their routes.  Only the least recently accessed route will be purged from the
 * cache when the cache exceeds capacity.  There are 2 closely coupled data structures:
 *   -  a Map keyed to IP Address, used for quick lookup
 *   -  a Queue of the N most recently accessed IP Addresses
 * All operations must be O(1).  A big hint how to make that happen is contained
 * in the type signature of the Map on line 38.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RouteCache
{
    // instance variables - add others if you need them
    // do not change the names of any fields as the test code depends on them
    
    // Cache total capacity and current fill count.
    private final int capacity;
    private int nodeCount;
    
    // private class for nodes in a doubly-linked queue
    // used to keep most-recently-used data
    private class Node {
        private Node prev, next;
        private final IPAddress elem; 
        private final int route;

        Node(IPAddress elem, int route) {
            prev = next = null;
            this.elem = elem;
            this.route = route;
        }  
    }
    
    private Node head = null; // least-recently used, get's chopped off
    private Node tail = null; // most-recently used, newest get's added here
    private Map<IPAddress, Node> nodeMap; // the cache itself

    /**
     * Constructor for objects of class RouteCache
     */
    public RouteCache(int cacheCapacity)
    {
    	this.capacity = cacheCapacity;
    	this.nodeMap = new HashMap<>();
    	this.nodeCount = 0;
    }

    /**
     * Lookup the output port for an IP Address in the cache
     * 
     * @param  addr   a possibly cached IP Address
     * @return     the cached route for this address, or null if not found 
     */
    public Integer lookupRoute(IPAddress addr)
    {
    	Node node = nodeMap.get(addr);
    	if (node == null) return null;
    	// if we made it here, the given IP is in cache
    	return node.route;
     }
     
    /**
     * Update the cache each time an element's route is looked up.
     * Make sure the element and its route is in the Map.
     * Enqueue the element at the tail of the queue if it is not already in the queue.  
     * Otherwise, move it from its current position to the tail of the queue.  If the queue
     * was already at capacity, remove and return the element at the head of the queue.
     * 
     * @param  elem  an element to be added to the queue, which may already be in the queue. 
     *               If it is, don't add it redundantly, but move it to the back of the queue
     * @return       the expired least recently used element, if any, or null
     */
    public IPAddress updateCache(IPAddress elem, int route)
    {
    	IPAddress previousHeadIP = null;
    	Node currentNode = nodeMap.get(elem);
    	if (currentNode == null) { // this is a new addition to cache
    		Node newNode = new Node(elem, route);
    		nodeMap.put(elem, newNode);
    		enqueue(newNode);
    		if (this.nodeCount > this.capacity) { // if over capacity, dequeue, return the previous head's IP
    			Node previousHead = dequeue();
    			previousHeadIP = previousHead.elem;
    			nodeMap.remove(previousHeadIP);
    		}
    	}
    	else {             // this IPAddress was already in cache
    		this.deleteFromQueue(currentNode); // surgically remove from current position
    		enqueue(currentNode); // place at the tail
    		Node newNode = new Node(elem, route);
    		nodeMap.put(elem, newNode);
    	}
    	return previousHeadIP; // this will only have been set as non-null if a dequeue was called
    }

    /*
     * adds the node to the tail, increments nodeCount
     */
    private void enqueue(Node node) {
    	if (node == null) throw new IllegalArgumentException("Cannot enqueue a null");
    	Node oldTail = this.tail;
    	if (oldTail == null) { // an empty queue
    		this.tail = node;
    		this.head = node;
    		node.prev = node.next = null;
    		this.nodeCount = 1;
    		return;
    	}
    	node.next = oldTail;
    	node.prev = null;
    	oldTail.prev = node;
    	this.tail = node;
    	this.nodeCount++;
    }
    
    /*
     * removes the current head, sets head to the next-to-last, decrements nodeCount
     * @return the old head
     */
    private Node dequeue() {
    	Node oldHead = this.head;
    	if (oldHead == null) throw new IllegalStateException("Cannot dequeue on an empty queue");
    	this.head = (oldHead.prev);
    	if (this.head != null) this.head.next = null; // prepare oldHead for GC
    	this.nodeCount--;
    	return oldHead;
    }
    
    /*
     * If this is the only node in the queue, effectively redefines a new queue
     * If tail is input, it sets the next node as new tail
     * If head is input, it dequeues
     * Otherwise it just has node.prev skip over this node to node.next
     * Always decrements nodeCount (indirectly in the case where it calls dequeue)
     */
    private void deleteFromQueue(Node node) {
    	if (node == null) throw new IllegalArgumentException("Cannot delete null from queue");
    	if (this.nodeCount == 1) {
    		this.tail = this.head = null;
    		this.nodeCount = 0;
    		return;
    	}
    	if (node == this.tail) {
    		this.tail = node.next;
    		this.tail.prev = null;
    		this.nodeCount--;
    		return;
    	}
    	if (node == this.head) {
    		dequeue(); // this will take care of decrementing nodeCount
    		return;
    	}
		(node.prev).next = node.next;
		this.nodeCount--; // this reflects the "skipping over" of node
    }
      
    /**
     * For testing and debugging, return the contents of the LRU queue in most-recent-first order,
     * as an array of IP Addresses in CIDR format. Return a zero length array if the cache is empty
     * 
     */
    String[] dumpQueue()
    {
    	String[] result = new String[this.nodeCount];
    	int index = 0;
    	Node current = this.tail;
    	while (current != null) {
    		result[index] = current.elem.toCIDR();
    		current = current.next;
    		index++;	
    	}
    	return result;
    }
    
    
}

