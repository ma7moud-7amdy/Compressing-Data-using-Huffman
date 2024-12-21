package huffmanproject;

import java.util.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {

    private String unCompressedData;
    private String compressedData;
    private TreeNode huffmanTree;
    private Map< Character, String> codesMap; // store each character and it's code

    public Huffman() {
    }

    public Huffman(String unCompressedData) {
        this.unCompressedData = unCompressedData;
        compress(unCompressedData);
    }

    public void compress(String unCompressedData) {
        Map< Character, Integer> frequencyMap = generateFrequencyMap(unCompressedData);
        huffmanTree = generateHuffmanTree(frequencyMap);
        codesMap = generateCodes();
        compressedData = generateCompressedMessage();
    }

    public LinkedHashMap< Character, Integer> generateFrequencyMap(String messege) {
        // Linked Hash Map to count occurnace of each character and keep them in insertion order
        LinkedHashMap< Character, Integer> freqMap = new LinkedHashMap();
        int length = messege.length();
        // count each char and store it 
        for (int i = 0; i < length; i++) {
            char ch = messege.charAt(i);
            if (freqMap.containsKey(ch)) {
                freqMap.put(ch, freqMap.get(ch) + 1); // increase freq by 1 
            } else {
                freqMap.put(ch, 1);
            }
        }
        return freqMap;
    }

    private TreeNode generateHuffmanTree(Map< Character, Integer> charMap) {

        // Queue to store each char and frequancy as Node
        Queue<TreeNode> dataQueue = new PriorityQueue<>();
        for (char key : charMap.keySet()) {
            TreeNode node = new TreeNode(key, charMap.get(key));
            dataQueue.offer(node);
        }

        // deQueue each node and generate Tree
        while (dataQueue.size() > 1) {

            TreeNode temp = new TreeNode();
            TreeNode left = dataQueue.poll();
            temp.left = left;

            TreeNode right = dataQueue.poll();
            temp.right = right;

            temp.freq = left.freq + right.freq;
            dataQueue.offer(temp);
        }
        return dataQueue.poll();
    }

    private HashMap< Character, String> generateCodes() {
        HashMap< Character, String> codes;
        if (huffmanTree == null) {
            codes = null;
        } else {
            codes = new HashMap();
            setCodes(huffmanTree, codes);
        }
        return codes;

    }

    // method take tree and Array and store each char code at char position in Array  
    private void setCodes(TreeNode root, Map< Character, String> codes) {
        if (root.left == null) { // char found
            codes.put(root.character, root.code); //store code of this ascii
        } else {
            root.left.code = root.code + "0";
            setCodes(root.left, codes);

            root.right.code = root.code + "1";
            setCodes(root.right, codes);
        }

    }

    private String generateCompressedMessage() {
        if (codesMap == null || unCompressedData == null || unCompressedData.isEmpty()) {
            return null;
        }

        StringBuilder compressedMessage = new StringBuilder();

        // Iterate through each character in the uncompressed message
        for (char ch : unCompressedData.toCharArray()) {
            String code = codesMap.get(ch); // Retrieve the Huffman code for the character
            if (code != null) {
                compressedMessage.append(code); // Append the code to the compressed message
            } else {
                throw new IllegalStateException("Character code not found in the Huffman codes.");
            }
        }
        return compressedMessage.toString();
    }

    public float getCompressionPercentage() {
        int unCompBits = unCompressedData.length() * 8;
        int compBits = compressedData.length();
        return ((float) (unCompBits - compBits) / unCompBits) * 100;
    }

    public String getUnCompressedData() {
        return unCompressedData;
    }

    public String getCompressedData() {
        return compressedData;
    }

    public TreeNode getHuffmanTree() {
        return huffmanTree;
    }

    public Map< Character, String> getCodesMap() {
        return codesMap;
    }

}

class TreeNode implements Comparable<TreeNode> {

    public static int globalTime = 0;

    protected char character;
    protected int freq;
    protected TreeNode right;
    protected TreeNode left;
    protected String code = "";
    protected int creationTime;

    public TreeNode() {
        creationTime = globalTime++;
    }

    public TreeNode(char character, int freq) {
        creationTime = globalTime++;
        this.character = character;
        this.freq = freq;
    }

    public char getCharacter() {
        return character;
    }

    public int getFreq() {
        return freq;
    }

    public TreeNode getRight() {
        return right;
    }

    public TreeNode getLeft() {
        return left;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int compareTo(TreeNode node) {
        if (this.freq > node.freq) {
            return 1;
        } else if (this.freq < node.freq) {
            return -1;
        } else { // if nodes has same frequency dequeue the node that entered queue first 
            return Integer.compare(this.creationTime, node.creationTime);
        }
    }
}
