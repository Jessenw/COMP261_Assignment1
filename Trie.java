import java.util.*;

public class Trie {
	TrieNode root;

	/**
	 * Initializes the Trie and creates an empty root which the rest of the
	 * nodes can build off
	 */
	public Trie() {
		root = new TrieNode();
	}

	/**
	 * Adds a word to the trie
	 * 
	 * @param word
	 */
	public void add(String word) {
		// Ensures tree traversal begins at the node
		// for each word
		TrieNode current = root;
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			/*
			 * If current node's children doesn't contain char then make a new
			 * char and add to children
			 */
			if (!current.children.containsKey(c)) {
				current.children.put(c, new TrieNode());
			}
			current = current.children.get(c); // Otherwise set pointer to
												// char(i)
		}
		current.value = word; // Sets the final node that matches the word to
								// have the value word. Useful for search
	}

	/**
	 * Searches the trie with a given prefix and returns a list of all the words
	 * which begin with the prefix
	 * 
	 * @param prefix
	 * @return
	 */
	public ArrayList<String> search(String prefix) {
		TrieNode current = root;
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < prefix.length(); i++) {

			char c = prefix.charAt(i);

			if (current.children.get(c) == null)
				return values; // returns empty list if prefix doesn't have any
								// related words
			current = current.children.get(c);

			if (i == prefix.length() - 1) { // At the last node get prefix
				values = getAllFromNode(current, values);
			}
		}
		return values;
	}

	/**
	 * searches the trie until it finds a value (A word) and adds it to the
	 * list. Otherwise, using recursion, search all of the pointers children.
	 * 
	 * @param pointer
	 * @param values
	 * @return
	 */
	public ArrayList<String> getAllFromNode(TrieNode pointer, ArrayList<String> values) {
		if (!(pointer.value == null)) {
			values.add(pointer.value);
		}

		for (char c : pointer.children.keySet()) {
			getAllFromNode(pointer.children.get(c), values);
		}
		return values;
	}
}