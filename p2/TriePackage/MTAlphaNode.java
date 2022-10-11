/*
Sameera Boppana - ssb40@pitt.edu
4254417
Ramirez 1501 - section 1020
Recitation - 1320
*/

package TriePackage;

import java.util.*;

public class MTAlphaNode<V> implements TrieNodeInt<V> {

	private static final int R = 26; // 26 letters in alphabet

	protected V val;
	protected TrieNodeInt<V>[] next;
	protected int degree;

	// You must supply the methods for this class. See TrieNodeInt.java
	// for the specifications. See also handout MTNode.java for a
	// partial implementation. Don't forget to include the conversion
	// constructor (passing a DLBNode<V> as an argument).

	@SuppressWarnings("unchecked")
	public MTAlphaNode() {
		this.val = null;
		this.next = (TrieNodeInt<V>[]) new TrieNodeInt<?>[R];
		this.degree = 0;
	}

	@SuppressWarnings("unchecked")
	public MTAlphaNode(V data) {
		this.val = data;
		this.next = (TrieNodeInt<V>[]) new TrieNodeInt<?>[R];
		this.degree = 0;
	}
	// loop through nodlete children of DLBnode d and put into next
	// nodelet children have pointer to cval - tells what index to put in next
	@SuppressWarnings("unchecked")
	public MTAlphaNode( DLBNode<V> node){
		this.val = null;
		this.next = (TrieNodeInt<V>[]) new TrieNodeInt<?>[R];
		this.degree = 0;

		while(node.front != null) {

			if(node.front.child.getData() != null) {

				//this.next[node.front.cval - 'a'] = new MTAlphaNode<V>(node.front.child.getData());
				this.next[node.front.cval - 'a'] = node.getNextNode(node.front.cval);
				//this.val = node.front.child.getData();
				this.degree ++;
			}else {
				//this.next[node.front.cval - 'a'] = new MTAlphaNode<V>();
				this.next[node.front.cval - 'a'] = node.getNextNode(node.front.cval);
				this.degree ++;
			}
			if(node.front.child.getDegree() > 0) {
				this.setNextNode(node.front.cval, node.getNextNode(node.front.cval));
			}
			node.front = node.front.rightSib;

		}

	}


	@SuppressWarnings("unchecked")
	private void traverseDLB( DLBNode<V> node) {

	//	MTAlphaNode<V> temp = null;

	}

	// Return the next node in the trie corresponding to character
	// c in the current node, or null if there is not next node for
	// that character.
	public TrieNodeInt<V> getNextNode(char c) {
		int index = (c - 'a');

		return next[index];
	}

	// Set the next node in the trie corresponding to character char
	// to the argument node. If the node at that position was previously
	// null, increase the degree of this node by one (since it is now
	// branching by one more link).
	public void setNextNode(char c, TrieNodeInt<V> node) {
		if (next[c - 'a'] == null) {
			this.degree++;
		}
		next[c - 'a'] = node;
	}

	// Return the data at the current node (or null if there is no data)
	public V getData() {
		if (val != null) {
			return val;
		}
		return null;
	}

	// Set the data at the current node to the data argument
	public void setData(V data) {
		this.val = data;
	}

	// Return the degree of the current node. This corresponds to the
	// number of children that this node has.
	public int getDegree() {
		return this.degree;
	}

	// Return the approximate size in bytes of the current node. This is
	// a very rough approximation based on the following:
	// 1) Assume each reference in a node will use 4 bytes (whether it is
	// used or it is null)
	// 2) Assume any primitive type is its specified size (see Java reference
	// for primitive type sizes in bytes)
	// Note that the actual size of the node is implementation dependent and
	// is not specified in the Java language. There are tools to give a better
	// approximation of this value but for our purposes, this approximation is
	// fine.
	public int getSize() {
		// 4 bytes for this.val, 4 bytes for this.degree, 26*4 bytes to instantiate
		// this.next
		// 4 + 4 + (26*4) = 112
		// if next[i] != null then there is a child

		int charBytes = 4;
		int intBytes = 4;
		int refBytes = 4;
		int size = 0;

		size += charBytes + intBytes + refBytes + (R * refBytes);

		for (int i = 0; i < next.length; i++) {
			if (next[i] != null) {
				char character = (char) (i + 'a');
				//checking for DLBNode with no children - increment size by DLB references
				if(next[i] instanceof DLBNode<?> && next[i].getDegree() ==0) {
					size += 12;
				}else{
					size += next[i].getSize();
				}
			}
		}
		return size;
	}

	// Return an Iterable collection of the references to all of the children
	// of this node. Do not put any null references into this result. The
	// order of the children as stored in the TrieNodeInt<V> node must be
	// maintained in the returned Iterable. The easiest way to do this is to
	// put all of the references into a Queue and to return the Queue (since a
	// Queue implements Iterable and maintains the order of the children).
	// This method will allow us to access all of the children of a node without
	// having to know how the node is actually implemented.

	@SuppressWarnings("unchecked")
	public Queue<TrieNodeInt<V>> children() {

		Queue<TrieNodeInt<V>> q = new LinkedList<>();

		return children(this, q);
	}

	public Queue<TrieNodeInt<V>> children(TrieNodeInt<V> t, Queue<TrieNodeInt<V>> q){
		if (t.getData() != null) {
		//	System.out.println(t.getData());
			q.add(t);
		}
		if (t.getDegree() > 0) {
			for (int i = 0; i < R; i++) {
				char character = (char) (i + 'a');
				if (t.getNextNode(character) != null) {
					TrieNodeInt<V> temp = t.getNextNode(character);
					children(temp, q);
				}
			}
		}
		return q;


	}

}
