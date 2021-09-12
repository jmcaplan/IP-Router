package edu.yu.cs.com1320.Final;

import java.util.LinkedList;

public class BitVectorTrie<Value> {
    
    private static final int R = 2;      
    private Node root;

    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

   /****************************************************
    * Is the key in the symbol table?
    ****************************************************/
    public boolean isRoutable(BitVector key) {
    	if (key == null) throw new IllegalArgumentException("argument to isRoutable() is null");
    	return get(key) != null;
    }

   /****************************************************
    * get needs the most changes since its result depends
    * not on the entire key but on its longest matching 
    * prefix
    ****************************************************/
    public Value get(BitVector key) {
    	if (key == null) throw new IllegalArgumentException("argument to get() is null");
    	BitVector longestPrefix = longestPrefixOf(key);
    	if (longestPrefix == null) return null;
    	return getExactMatch(longestPrefix);
    }
    /*
    // original version
    public Value get(BitVector key) {
        return get(root, key, 0, null);
    }
    */

    private Value get(Node x, BitVector key, int d, Value bestSoFar) {
    	
    	return null;
    }
    
    /**
     * Returns the string in the symbol table that is the longest prefix of {@code query},
     * or {@code null}, if no such string.
     * @param query the query string
     * @return the string in the symbol table that is the longest prefix of {@code query},
     *     or {@code null} if no such string
     * @throws IllegalArgumentException if {@code query} is {@code null}
     */
    protected BitVector longestPrefixOf(BitVector query) {
        if (query == null) throw new IllegalArgumentException("argument to longestPrefixOf() is null");
        int length = longestPrefixOf(root, query, 0, -1);
        if (length == -1) return null;
        else { // using the length, we build a new BitVector representing the prefix
        	BitVector prefix = new BitVector(length);
        	for (int i = 0; i < length; i++) {
        		prefix.set(i, query.get(i));
        	}
        	return prefix;
        }
    }

    // returns the length of the longest string key in the subtrie
    // rooted at x that is a prefix of the query string,
    // assuming the first d character match and we have already
    // found a prefix match of given length (-1 if no such match)
    private int longestPrefixOf(Node x, BitVector query, int d, int length) {
        if (x == null) return length;
        if (x.val != null) length = d;
        if (d == query.size()) return length;
        int c = query.get(d);
        return longestPrefixOf(x.next[c], query, d+1, length);
    }
    
    /*
     * This is used to get by exact match, NOT by prefix match
     */
    protected Value getExactMatch(BitVector key) {
    	if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }
	private Node get(Node x, BitVector key, int d) {
	    if (x == null) return null;
	    if (d == key.size()) return x;
	    int c = key.get(d);
	    return get(x.next[c], key, d+1);
	}

   /****************************************************
    * Insert Value value into the prefix Trie.
    * If a different value exists for the same key
    * throw an IllegalArgumentException
    ****************************************************/
    public void put(BitVector key, Value port) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (port == null) throw new IllegalArgumentException("this trie does not accept put(key,null)");
        else {
        	root = put(root, key, port, 0);
        }
    }

    private Node put(Node x, BitVector key, Value port, int d) {
        if (x == null) x = new Node();
        if (d == key.size()) {
            if (x.val != null && !(x.val.equals(port)) ) throw new IllegalArgumentException("Could not add rule, a different value already exists at this key");
            x.val = port;
            return x;
        }
        int c = key.get(d);
        x.next[c] = put(x.next[c], key, port, d+1);
        return x;
    }

   /****************************************************
    * Delete the value for a key.
    * If no value exists for this key
    * throw and IllegalArgumentException
    ****************************************************/
    public void delete(BitVector key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        if (getExactMatch(key) == null) throw new IllegalArgumentException("no value exists at the given key to delete");
    	root = delete(root, key, 0);
    }

    private Node delete(Node x, BitVector key, int d) {
    	 if (x == null) return null;
         if (d == key.size()) {
             //if (x.val != null) n--;
             x.val = null;
         }
         else {
             int c = key.get(d);
             x.next[c] = delete(x.next[c], key, d+1);
         }

         // remove subtrie rooted at x if it is completely empty
         if (x.val != null) return x;
         for (int c = 0; c < R; c++)
             if (x.next[c] != null)
                 return x;
         return null;

    }

}

