/*
Sameera Boppana - ssb40@pitt.edu
4254417
Ramirez 1501 - section 1020
Recitation - 1320
*/

package TriePackage;

import java.util.*;
import java.io.*;

public class HybridTrieST<V> {

	private TrieNodeInt<V> root;

	int treeType = 0;
	private static int THRESHOLD = 10;

	// treeType = 0 --> multiway trie
	// treeType = 1 --> DLB
	// treeType = 2 --> hybrid

	// You must supply the methods for this class. See test program
	// HybridTrieTest.java for details on the methods and their
	// functionality. Also see handout TrieSTMT.java for a partial
	// implementation.
	// if type == 0 MTAlphaNode; else root is DLBNode
	public HybridTrieST(int type) {
		this.treeType = type;
		if (type == 0) {
			root = new MTAlphaNode<V>();
		} else {
			root = new DLBNode<V>();
		}
	}

	public void put(String key, V val) {
		root = put(root, key, val, 0);
	}

	//method to add words into the trie
	@SuppressWarnings("unchecked")
	private TrieNodeInt<V> put(TrieNodeInt<V> node, String key, V val, int depth) {
		// MTAlphaNode
		boolean isMTAlpha = false;
		boolean isDLB = false;
		//determine the type of node to form
		// nodetype is MTAlpha
		if (treeType == 0) {
			isMTAlpha = true;
		} else if (treeType == 1) {
			isDLB = true;
		} else {
			// forming a new node, checking if new node should be MTAlpha or DLB
			// if the node is null, create DLB; else check to see the type of the node
			if (node == null) {
				isDLB = true;
			} else {
				if (node instanceof MTAlphaNode<?>) {
					isMTAlpha = true;
				} else {
					isDLB = true;
				}
			}

		}
		//current node is an MTAlphaNode
		if (isMTAlpha) {
			//create a new node
			if (node == null) {
				node = new MTAlphaNode<V>();
			}
			//check to see if at the end of the key
			if (depth == key.length()) {
				node.setData(val);
				return node;
			}
			char c = key.charAt(depth);
			//get the next node and add it to the trie; then set the next node as the child of the current node
			node.setNextNode(c, put(node.getNextNode(c), key, val, depth + 1));
			return node;

			// if DLB or Hybrid
		} else {
			//create new node
			if (node == null) {
				node = new DLBNode<V>();
			}
			//check if at the end of the key
			if (depth == key.length()) {
				node.setData(val);
				return node;
			}
			char c = key.charAt(depth);

			TrieNodeInt<V> newNode = node.getNextNode(c);

			TrieNodeInt<V> temp = put(newNode, key, val, depth + 1);
			node.setNextNode(c, temp);

			//check to see if the current nodes needs to be converted to MTAlphaNode
			if (treeType == 2 && node.getDegree() > THRESHOLD) {
				node = new MTAlphaNode<V>((DLBNode<V>) node);
			}
			return node;
		}

	}

	//method to return if the key is in the Trie
	// if the key is present - return the key
	// else return null
	public V get(String key) {
		TrieNodeInt<V> node = get(root, key, 0);
		if (node == null) {
			return null;
		}
		return node.getData();
	}
	//helper method to traverse through trie to see if the key is present
	private TrieNodeInt<V> get(TrieNodeInt<V> node, String key, int d) {
		//if the tree is an MTAlpha
		if (treeType == 0) {
			//did not find the node
			if (node == null) {
				return null;
			}
			//at the end of the key
			if (d == key.length()) {
				return node;
			}

			char c = key.charAt(d);
			//recurse with the next node
			return get(node.getNextNode(c), key, d + 1);
		} else {
			TrieNodeInt<V> curr = root;
			//loop through the key and get the next node
			// if the next node is null - key not present, return null
			// if at the end of the for loop, then key is present - return node
			for (int i = 0; i < key.length(); i++) {
				char character = key.charAt(i);
				curr = curr.getNextNode(character);
				if (curr == null) {
					return null;
				}
			}
			return curr;
		}

	}
	//search for prefix of a key
	public int searchPrefix(String key) {
		int res = 0;
		TrieNodeInt<V> curr = root;
		// treetype is MTAlpha
		if (treeType == 0) {
			boolean done = false;
			int loc = 0;

			while (curr != null && !done) {
				//at the end of the word
				if (loc == key.length()) {
					//if value != null - then word
					if (curr.getData() != null) {
						res += 2;
					}
					// at the end of the word and still has children - prefix
					if (curr.getDegree() > 0) {
						res += 1;
					}
					done = true;
					//get next node
				} else {
					curr = curr.getNextNode(key.charAt(loc));
					loc++;
				}
			}
		//DLBNode or Hybrid
		} else {
			//loop through key
			for (int i = 0; i < key.length(); i++) {
				char character = key.charAt(i);
				//get next node
				curr = curr.getNextNode(character);
				//if next node is null - not found
				if (curr == null) {
					break;
				}
			}
			// at the end of the word
			if (curr == null) {
				res = 0;
			} else {
				// if value != null - word
				if (curr.getData() != null) {
					res += 2;
				}
				// if degree > 0 - prefix
				if (curr.getDegree() > 0) {
					res += 1;
				}
			}

		}
		return res;
	}

	// The degreeDistribution() method should traverse the trie and return an
	// int [] of size K+1 (where K is the maximum possible degree of a node in
	// the trie), indexed from 0 to K. The value of each location dist[i] will
	// be equal to the number of nodes with degree i in the trie. Note that in
	// our trie, the value K should be 26 since we are limiting it to lower
	// case letters, but for an arbitrary trie K could be as large as 256.
	public int[] degreeDistribution() {
		int[] dist = new int[27];
		return degreeDistribution(root, dist, false);
	}
	//helper method to return an array where the value of each location is the number of nodes with degree if
	// takes in the current node, the int[] and a boolean value
	// boolean value added to flag dist[i] already incremnted - when recursing with rightSiblings for DLB
	// does not incrmeent index again
	private int[] degreeDistribution(TrieNodeInt<V> curr, int[] dist, boolean added) {
		//current node is MTAlpha
		if(curr instanceof MTAlphaNode<?>) {
			//get the degree of the current node and increment appropriate index
			int degree = curr.getDegree();
			dist[degree] += 1;
			// loop through alphabet and getNextNode of each character
			// if the nextNode is not null - recurse with nextNode
			for(int i=0; i<26; i++) {
				char character = (char) (i +'a');
				TrieNodeInt<V> nextNode = curr.getNextNode(character);
				if(nextNode != null) {
					degreeDistribution(nextNode, dist, added);
				}
			}
			//DLBNode
		}else if(curr instanceof DLBNode<?>) {
			int degree = curr.getDegree();
			// increment index of degree by 1
			if(!added) {
				dist[degree] += 1;
			}
			//get the childNode - if childNode is not null then recurse with childNode
			if (((DLBNode<V>) curr).front != null) {
				TrieNodeInt<V> childNode = ((DLBNode<V>) curr).front.child;
				if(childNode != null) {
					added = false;
					degreeDistribution(childNode, dist, added);
				}
				//check if nodelet has rightSibling
				// if rightSibling != null then recurse with rightSibling
				if (((DLBNode<V>) curr).front.rightSib != null) {
					TrieNodeInt<V> rightSib = new DLBNode<V> ();
					((DLBNode<V>) rightSib).front = ((DLBNode<V>) curr).front.rightSib;
					//flag so that dist[index] is not incremented again
					added = true;
					degreeDistribution(rightSib, dist, added);
				}
			}
		}
		return dist;
	}

	// Count the number of nodes of a given type. For this method we are using the
	// value 1 to indicate MTAlphaNode<?> nodes and the value 2 to indicated
	// DLBNode<?>
	// nodes. The actual method will traverse all of the nodes of the trie and use
	// the instanceof operator to test the types of the nodes. A way to test the
	// correctness of this method and of the degreeDistribution method above is as
	// follows: Your HybridTrieST should convert any nodes with a degree of 11 or
	// above to MTAlphaNode<?> nodes, while those with degree 10 or below should
	// remain
	// as DLBNode<?> nodes. Thus, if the hybrid version of the trie is being used,
	// the number of MTAlphaNode<?> nodes below should be equal to the sum of the
	// distribution value from 11 to 26, while the number of DLBNode<?> nodes below
	// should be equal to the sum of the distribution value from 0 to 10.
	public int countNodes(int type) {
		boolean added = false;
		return countNodes(type, root, added);
	}
	//helper mthod to recursively count nodes of the current node
	//boolean parameter to flag if a node has already been counted
	@SuppressWarnings("unchecked")
	private int countNodes(int type, TrieNodeInt<V> curr, boolean added) {
		int num_nodes = 0;
		//current node is MTAlphaNode
		if (curr instanceof MTAlphaNode<?>) {
			//looking for MTAlphaNodes and not added - increment num_nodes
			if (type == 1 && !added) {
				num_nodes++;
			}
			//if the current node has children, loop through alphabet and check if
			// a character has children - if it does recurse with the nextNode
			if (curr.getDegree() != 0) {
				for (int i = 0; i < 26; i++) {
					char character = (char) (i + 'a');
					TrieNodeInt<V> nextNode = curr.getNextNode(character);
					if (nextNode != null) {
						num_nodes += countNodes(type, nextNode, added);
						added = false;
					}
				}
			}
			//current node is DLBNode
		} else if (curr instanceof DLBNode<?>) {
			//looking for DLBNodes and not added - increment num_nodes
			if (type == 2 && !added) {
				num_nodes++;
			}

			if (((DLBNode<V>) curr).front != null) {
				//get childNode
				TrieNodeInt<V> childNode = ((DLBNode<V>) curr).front.child;
				// if childNode != null and degree == 0 and looking for DLB
				// increment num_nodes - null node
				// if degree != 0 then recurse with childNode
				if (childNode != null) {
					if (childNode.getDegree() == 0 && type == 2) {
						num_nodes++;
					} else if (childNode.getDegree() != 0) {
						added = false;
						num_nodes += countNodes(type, childNode, added);
					}

				}
				//traverse through siblings of current node
				if (((DLBNode<V>) curr).front.rightSib != null) {

					TrieNodeInt<V> rightSib = new DLBNode<V> ();
					((DLBNode<V>) rightSib).front = ((DLBNode<V>) curr).front.rightSib;
					// added = true - does not increment num_nodes again
					added = true;

					num_nodes += countNodes(type, rightSib, added);
				}

			}
		}

		return num_nodes;
	}


 // Output the approximate size of the trie structure (not counting
 // the sizes of the actual string values that are stored in the trie).
 // This method will traverse through all of the nodes in the trie,
 // utilizing the getSize() method for each node in order to get the overall
 // size of the trie.

	//calls appropriate getSize() method depending on the type of root
	public int getSize() {
		return root.getSize();
	}

	// Save the trie in order back to args[1]. This method will traverse
	// through all of the values in the trie in alpha order, saving all of them
	// to the file name provided in args[1].

  public void save(String fName) throws IOException{

	  BufferedWriter writer = new BufferedWriter(new FileWriter(fName));
	  for(TrieNodeInt<V> child : root.children()) {
		  writer.write(child.getData().toString() + "\n");
	  }
		writer.close();
 }

}
