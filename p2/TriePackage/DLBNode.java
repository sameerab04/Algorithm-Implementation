/*
Sameera Boppana - ssb40@pitt.edu
4254417
Ramirez 1501 - section 1020
Recitation - 1320
*/

package TriePackage;

import java.util.*;

public class DLBNode<V> implements TrieNodeInt<V> {
	protected Nodelet front; // 4 bytes
	protected int degree; // 4 bytes
	protected V val; // 4 bytes

	protected class Nodelet {
		protected char cval; // 2 bytes
		protected Nodelet rightSib; // 4 bytes
		protected TrieNodeInt<V> child; // 4 bytes

	}

	//constructs a DLBNode setting all instance variables to null
	public DLBNode() {
		front = new Nodelet();
		degree = 0;
		val = null;
	}

	//creates a DLB node setting the value of the node
	public DLBNode(V data) {
		val = data;
		degree = 0;
		front = new Nodelet();
	}


	// You must supply the methods for this class. See TrieNodeInt.java for the
	// interface specifications. You will also need a constructor or two.


	// Return the next node in the trie corresponding to character
	// c in the current node, or null if there is not next node for
	// that character.
	public TrieNodeInt<V> getNextNode(char c) {
		Nodelet n = front;
		// while not at the end of the list or character matches c
		while (n.rightSib != null || n.cval == c) {
			//found the correct nodelet, return child node
			if (n.cval == c) {
				return n.child;
			} else {
				//increment to the right sibiling
				n = n.rightSib;
			}
		}
		//node not found - return null
		return null;
	}


	// Set the next node in the trie corresponding to character char
	// to the argument node. If the node at that position was previously
	// null, increase the degree of this node by one (since it is now
	// branching by one more link).
	public void setNextNode(char c, TrieNodeInt<V> node) {

		Nodelet nodelet = new Nodelet();
		Nodelet curr = front;
		Nodelet prev = null;
		// creating new nodelet - empty node
		if (front.child == null) {
			front.child = node;
			this.degree++;
			front.cval = c;

		} else {
			boolean done = false;
			// traversing
			while (!done) {
				// found character - done
				if (curr.cval == c) {
					done = true;
					// linking parent to child to newly formed child
					if (node instanceof MTAlphaNode<?>) {
						curr.child = node;
					}
					break;
				}
				// if location is positive then keep traversing until end
				int location = c - curr.cval;
				if (location > 0) {
					prev = curr;
					curr = curr.rightSib;
					// at the end of the list, insert nodelet
					if (curr == null) {
						nodelet.cval = c;
						nodelet.rightSib = null;
						nodelet.child = node;
						this.degree++;
						prev.rightSib = nodelet;
						done = true;
					}
					// location is negative - found the correct location to insert node
				} else {
					// adding at middle
					nodelet.cval = c;
					nodelet.child = node;
					this.degree++;
					nodelet.rightSib = curr;
					if (prev != null) {
						// adding to middle
						prev.rightSib = nodelet;
					} else {
						// adding to front
						front = nodelet;
					}
					done = true;

				}

			}

		}

	}

 // Return the data at the current node (or null if there is no data)
	public V getData() {
		return this.val;
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
		// each node automatically has 12 bytes from pointers inherient of the class
		// go through all nodelets and calculate size

		//values for each references
		int refFront = 4;
		int degreeRef = 4;
		int valueRef = 4;
		int nodeletSize = 10;

		int size = 0;
		Nodelet nodelet = front;
		// at bottom of the trie
		if (nodelet.child == null) {
			return 0;
		}

		Nodelet temp = nodelet;
		int degree = 0;
		//calculate the degree of the current node
		while (temp != null) {
			temp = temp.rightSib;
			degree++;
		}
		//calculate size of the node
		size += refFront + degreeRef + valueRef + (nodeletSize * degree);
		//traverse down the trie
		while (nodelet != null) {
			if (nodelet.child.getDegree() > 0) {
				size += nodelet.child.getSize();

			} else {
				//add size of null node
				size += refFront + degreeRef + valueRef;
			}
			//traverse to the right
			nodelet = nodelet.rightSib;


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
	public Iterable<TrieNodeInt<V>> children() {
		Queue<TrieNodeInt<V>> q = new LinkedList<>();
		return this.children(q);
	}

	public Iterable<TrieNodeInt<V>> children(Queue<TrieNodeInt<V>> q) {

		Nodelet nodelet = new Nodelet();
		nodelet = this.front;
		//recursively get the children traversing down the trie
		while (nodelet != null) {
			if (nodelet.child.getData() != null) {
				q.add(nodelet.child);
			}
			if (nodelet.child.getDegree() > 0) {
				TrieNodeInt<V> tempNode = this.getNextNode(nodelet.cval);
				if (tempNode instanceof DLBNode<?>) {
					DLBNode<V> tempDLBNode = (DLBNode<V>) tempNode;
					tempDLBNode.children(q);
				} else {
					MTAlphaNode<V> tempMTNode = (MTAlphaNode<V>) tempNode;
					tempMTNode.children(tempMTNode, q);
				}

			}
			//traverse to the right
			nodelet = nodelet.rightSib;

		}
		return q;
	}


}
