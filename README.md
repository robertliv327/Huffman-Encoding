# Huffman-Encoder

This program compresses and decompresses files according to Huffman Encoding. Unlike ASCII which uses 7 bits to encode each letter, Huffman Encoding uses variable-length codes; the words that have the highest frequency in the file use fewer bits. This implementation makes use of trees, maps, and priority queues.

## Files

#### HuffmanEncoder.java

Includes the following methods:

```java
/**
 * HuffmanEncoder's main method. Makes use of other methods to compress and decompress
 * the chosen file using Huffman Coding.
 * @throws IOException
 */
public static void main(String[] args) throws IOException;

/**
 * Creates and returns a frequency map holding each character's frequency in the file
 * @param pathName the file's name
 * @return the frequency map
 * @throws IOException
 */
public static HashMap<Character, Integer> makeFrequencyMap(String pathName) throws IOException;

/**
 * Creates singleton trees for each character and its frequency
 * @param freqTable the frequency table
 * @return a priority queue holding the singleton trees
 */
public static PriorityQueue<BinaryTree<Element>> makeSingletonTrees(HashMap<Character, Integer> freqTable);

/**
 * Condenses the singleton trees into one Huffman code tree
 * @param huff the priority queue of singleton trees
 */
public static void condenseHuffTree(PriorityQueue<BinaryTree<Element>> huff);

/**
 * Traverses PQ using helper method to recursively create a code map for each character
 * @param pq
 * @return the code map
 */
public static HashMap<Character, String> getCodeMap(PriorityQueue<BinaryTree<Element>> pq);

/**
 * Recursively traverses Huffman tree, assigning each leaf node a path. Add's character's path to code map
 * @param hT Huffman tree to be traversed
 * @param c code map
 * @param p path
 * @return
 */
private static HashMap<Character, String> makeCodeMap(BinaryTree<Element> hT, HashMap<Character, String> c, String p);

/**
 * Compresses the file using the code map
 * @param cMap code map
 * @param pName path name
 * @throws IOException
 */
private static void compress(HashMap<Character, String> cMap, String pName) throws IOException;

/**
 * Decompresses the compressed file
 * @param bT Huffman binary tree
 * @param pName path name
 * @throws IOException
 */
public static void decompress(BinaryTree<Element> bT, String pName) throws IOException;

/**
 * Method to get a file's path name
 * @return the path name
 */
public static String getFilePath();
```

#### BinaryTree.java

Generic binary tree, storing data of a parametric type in each node

#### BufferedBitReader.java

Reads bits from a file, one at a time. Assumes that the last byte of the file contains the number of valid bits in the previous byte.

#### BufferedBitWriter.java

Writes bits to a file.  Accumulates bits until gets a byte, then writes it.  On closing writes an additional byte holding the number of valid bits in the final byte written.

#### Element.java

Class to be data type for binary tree. Holds a character and its frequency.

#### TreeComparator.java

Class used to compare two binary trees using frequency counts in the root nodes of the two trees.