import java.io.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFileChooser;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import java.util.*;

/**
 * HuffmanEncoder.java
 * 
 * Class that compresses and decompresses input file using Huffman Coding. Makes use of maps and priority queues
 * to assign each character a code whose length inversely reflects its frequency. Uses each character's code to
 * compress and decompress the file.
 * 
 * @author Robert Livaudais, consulted with Teddy Ni
 */

public class HuffmanEncoder {
	/**
	 * HuffmanEncoder's main method. Makes use of other methods to compress and decompress
	 * the chosen file using Huffman Coding.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		String pathName = getFilePath();		//saves file's name as pathName
		HashMap<Character, Integer> freqTable = makeFrequencyMap(pathName);				//creates frequency table
		if (freqTable.keySet().size() != 0){		//if file is not empty
			//instantiates priority queue and assigns it to PQ of singleton trees representing the frequency map
			PriorityQueue<BinaryTree<Element>> huff = makeSingletonTrees(freqTable);  
			condenseHuffTree(huff);		//condenses the singleton trees into one single Huffman code tree
			HashMap<Character, String> codeMap = getCodeMap(huff); 	//creates map holding each character's code
			compress(codeMap, pathName);	//compresses the file
			decompress(huff.peek(), pathName);		//decompresses the file
		}
		else{														//if empty file
			compress(new HashMap<Character, String>(), pathName);	//compress the empty file
			decompress(null, pathName);								//decompress the empty file
		}
	}
	
	/**
	 * Creates and returns a frequency map holding each character's frequency in the file
	 * @param pathName the file's name
	 * @return the frequency map
	 * @throws IOException
	 */
	public static HashMap<Character, Integer> makeFrequencyMap(String pathName) throws IOException{
		BufferedReader inputFile = new BufferedReader(new FileReader(pathName));  	//BufferedReader object to read file 
		HashMap<Character, Integer> freqTable = new HashMap<Character, Integer>();	//map to hold frequency table
		try {
		  int cInt = inputFile.read();   // read the next character's integer representation
		  while (cInt != -1) {
		    // Code to process the character whose Unicode value is cInt goes here.
			char c = (char) cInt;
			if (freqTable.containsKey(c))					//if character is already in table
				freqTable.put(c, freqTable.get(c) + 1);		//increments frequency
			else freqTable.put(c, 1);						//else adds character to table
		    cInt = inputFile.read();    // read the next character's integer representation
		  }
		return freqTable;
		}
		finally {
		  inputFile.close();
		}
	}
	
	/**
	 * Creates singleton trees for each character and its frequency
	 * @param freqTable the frequency table
	 * @return a priority queue holding the singleton trees
	 */
	public static PriorityQueue<BinaryTree<Element>> makeSingletonTrees(HashMap<Character, Integer> freqTable){
		//Creates priority queue to hold singleton trees. Passes TreeComparator object in constructor
		PriorityQueue<BinaryTree<Element>> huff = new PriorityQueue<BinaryTree<Element>>(freqTable.keySet().size(), new TreeComparator());
		for(Map.Entry<Character,Integer> entry : freqTable.entrySet()){		//for each entry in the frequency table
			Element x = new Element(entry.getKey(), entry.getValue());		//makes element object holding character and frequency
			BinaryTree<Element> y = new BinaryTree<Element>(x);				//makes singleton tree with element object
			huff.add(y);	//adds singleton tree to PQ
		}
		return huff;
	}
	
	/**
	 * Condenses the singleton trees into one Huffman code tree
	 * @param huff the priority queue of singleton trees
	 */
	public static void condenseHuffTree(PriorityQueue<BinaryTree<Element>> huff){
		while (huff.size() > 1) {						//while there is more than one tree in Huffman tree
			BinaryTree<Element> t1 = huff.remove();		//removes smallest tree
			BinaryTree<Element> t2 = huff.remove();		//removes second smallest tree
			int f1 = t1.getValue().getFreq();
			int f2 = t2.getValue().getFreq();
			BinaryTree<Element> r = new BinaryTree<Element>(new Element('\0', f1+f2), t1, t2); //consolidates two trees, adding frequencies in root node
			huff.add(r);		//adds consolidated tree back into Huffman tree
		}
		if(huff.peek().size() == 1){						//if there is only one character in file
			BinaryTree<Element> single = huff.remove();		//gets singleton tree
			BinaryTree<Element> temp = new BinaryTree<Element>(new Element('\0', single.getValue().getKey())); //creates dummy tree
			temp.setRight(single); 		//sets singleton tree as child of dummy tree, thus giving it a path from the root
			huff.add(temp);				//adds dummy tree to Huffman tree
		}
	}
	
	/**
	 * Traverses PQ using helper method to recursively create a code map for each character
	 * @param pq
	 * @return the code map
	 */
	public static HashMap<Character, String> getCodeMap(PriorityQueue<BinaryTree<Element>> pq) {
		BinaryTree<Element> huffTree = pq.peek(); 		//gets huffman tree to be traversed
		HashMap<Character, String> codes = new HashMap<Character, String>(); 		//creates code map
		String path = new String("");	//empty initial path
		codes = makeCodeMap(huffTree, codes, path);		//calls helper method
		return codes;
	}
	
	/**
	 * Recursively traverses Huffman tree, assigning each leaf node a path. Add's character's path to code map
	 * @param hT Huffman tree to be traversed
	 * @param c code map
	 * @param p path
	 * @return
	 */
	private static HashMap<Character, String> makeCodeMap(BinaryTree<Element> hT, HashMap<Character, String> c, String p) {
		if (hT.hasLeft())
			makeCodeMap(hT.getLeft(), c, p + "0"); //calls on left tree adding 0 to code
		if (hT.hasRight())
			makeCodeMap(hT.getRight(), c, p + "1"); //calls on right tree adding 0 to code
		if (hT.isLeaf())							//makes sure that internal (sum) nodes don't get path put in codeMap
			c.put(hT.getValue().getKey(), p);	//adds character and path to code map
		return c;
	}
	
	/**
	 * Compresses the file using the code map
	 * @param cMap code map
	 * @param pName path name
	 * @throws IOException
	 */
	private static void compress(HashMap<Character, String> cMap, String pName) throws IOException{
		String outputPathName = pName.replace(".txt", "_compressed.txt"); 		//creates output file
		BufferedReader i = new BufferedReader(new FileReader(pName)); 			//creates buffered reader object
		BufferedBitWriter outputFile =  new BufferedBitWriter(outputPathName);	//creates bit writer object
		try {
			int cInt = i.read();				//reads next character
			while (cInt != -1) {				//while not at end of file
				char c = (char) cInt;
				String code = cMap.get(c);				//gets path code
				for (int z = 0; z < code.length(); z++){		//for each digit in path code
					char digit = code.charAt(z);
					if (digit == '1')
						outputFile.writeBit(1); 		//writes 1 if digit is 1
					if (digit == '0')
						outputFile.writeBit(0);			//writes 0 if digit is 0
				}
				cInt = i.read();		//reads next character
			}
		}
		finally {
			i.close();
			outputFile.close();
		}
		System.out.println("Compression Complete");
	}
	
	/**
	 * Decompresses the compressed file
	 * @param bT Huffman binary tree
	 * @param pName path name
	 * @throws IOException
	 */
	public static void decompress(BinaryTree<Element> bT, String pName) throws IOException{
		String inputPathName = pName.replace(".txt", "_compressed.txt");		//gets compressed file using name
		String outputPathName = pName.replace(".txt", "_decompressed.txt");		//creates decompressed file
		BufferedBitReader i = new BufferedBitReader(inputPathName); 				//bit reader object
		BufferedWriter outputFile =  new BufferedWriter(new FileWriter(outputPathName)); 	//buffered writer object
		BinaryTree<Element> cursor = bT;		//will traverse tree
		try {
			int cInt = i.readBit(); 	
			while (cInt != -1) {
				if (cInt == 0) 
					cursor = cursor.getLeft(); 		//if bit is 0, go left
				else
					cursor = cursor.getRight(); 	//if bit is 1, go right
				if (cursor.isLeaf()){				//if at leaf,
					outputFile.write(cursor.getValue().getKey()); 		//write the code's character
					cursor = bT;		//sets cursor back to root
				}
				cInt = i.readBit();
			}
		}
		finally {
			i.close();
			outputFile.close();
		}
		System.out.println("Decompression Complete");
	}
	
	/**
	 * Method to get a file's path name
	 * @return the path name
	 */
	public static String getFilePath() {
	    final AtomicReference<String> result = new AtomicReference<>();

	    try {
	      SwingUtilities.invokeAndWait(new Runnable() {
	          public void run() {
	            JFileChooser fc = new JFileChooser();

	          int returnVal = fc.showOpenDialog(null);
	          if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            String pathName = file.getAbsolutePath();
	            result.set(pathName);
	          }
	          else
	            result.set("");
	          }
	      });
	    } catch (InvocationTargetException | InterruptedException e) {
	      e.printStackTrace();
	    }

	    // Create a file chooser.
	    return result.get();
	  }
}