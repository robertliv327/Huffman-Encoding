/**
 * Element.java
 * 
 * Class to be data type for binary tree. Holds a character and its frequency.
 * 
 * @author Robert Livaudais
 */
public class Element {
	private char key;  //holds the character
	private int freq;  //holds the frequency
	
	/**
	 * Constructor for Element object
	 * @param k the character
	 * @param f the frequency of the character
	 */
	public Element(char k, int f) {
		key = k;
		freq = f;
	}
	
	/**
	 * @return the element object's character key
	 */
	public char getKey() {
		return this.key;
	}
	
	/**
	 * @return the element object's frequency
	 */
	public int getFreq() {
		return this.freq;
	}
}
