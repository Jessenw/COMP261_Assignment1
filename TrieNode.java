import java.util.*;

public class TrieNode {
	char c; // The character is used to traverse the tree
	HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
	String value;
	
	/**
	 * Constructor
	 * For root
	 */
	public TrieNode(){}
	
	/**
	 * For nodes which aren't the root
	 * 
	 * @param c
	 */
	public TrieNode(char c){
		this.c = c;
	}
}
