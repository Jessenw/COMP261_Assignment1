import java.util.*;

public class Trie {
	TrieNode root;

	public Trie() { // Initializes the Trie and creates an empty root which the rest of the nodes can build off
		root = new TrieNode();
	}

	public void add(String word) {
		TrieNode current = root; // Ensures tree traversal begins at the node for each word
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (!current.children.containsKey(c)) { // If current node's children doesn't contain char then make a new char
													// and add to children
				current.children.put(c, new TrieNode());
			}
			current = current.children.get(c); // Otherwise set pointer to char(i)
		}
		current.value = word; // Sets the final node that matches the word to have the value word. Useful for search
	}

	// Returns up to 10 words 
	public ArrayList<String> search(String prefix) {
		TrieNode current = root;
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < prefix.length(); i++) {
			
			char c = prefix.charAt(i);
			
			if (current.children.get(c) == null)
				return values; // returns empty list if prefix doesn't have any related words
			current = current.children.get(c);
			
			if (i == prefix.length() - 1) { // At the last node get prefix
				values = getAllFromNode(current, values);
			}
		}
		return values;
	}

	public ArrayList<String> getAllFromNode(TrieNode pointer, ArrayList<String> values) {
		if (!(pointer.value == null)) {
			values.add(pointer.value);
		}
		
		for (char c : pointer.children.keySet()) {
			getAllFromNode(pointer.children.get(c), values);
		}
		return values;
	}

	public TrieNode searchNode(String str) {
		Map<Character, TrieNode> children = root.children;
		TrieNode t = null;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (children.containsKey(c)) {
				t = children.get(c);
				children = t.children;
			} else {
				return null;
			}
		}
		return t;
	}
}