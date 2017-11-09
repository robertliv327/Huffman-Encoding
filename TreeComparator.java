import java.util.Comparator;

/**
 * TreeComparator.java
 * 
 * Class used to compare two binary trees using frequency counts in the root nodes of the two trees
 * 
 * @author Robert Livaudais
 */
public class TreeComparator implements Comparator<BinaryTree<Element>>{
	/**
	 * Compares two binary trees' root frequency counts.
	 * 
	 * @return -1, 0, or 1 depending on whether first is smaller, equal to, or larger than the second
	 */
	public int compare(BinaryTree<Element> first, BinaryTree<Element> second) {
		int freq1 = first.getValue().getFreq();  //first tree's root frequency
		int freq2 = second.getValue().getFreq(); //second tree's root frequency
		if (freq1 < freq2)
			return -1;
		else if (freq1 > freq2)
			return 1;
		else
			return 0;
	}
}
