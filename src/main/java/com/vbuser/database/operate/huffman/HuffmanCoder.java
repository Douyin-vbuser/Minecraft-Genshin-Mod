package com.vbuser.database.operate.huffman;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HuffmanCoder {
    public static void compress(String inputFile, String outputFile) throws IOException {
        String content = readFile(inputFile);
        Map<Character, Integer> frequencyMap = buildFrequencyMap(content);
        HuffmanNode root = buildHuffmanTree(frequencyMap);
        Map<Character, String> huffmanCodes = new HashMap<>();
        buildCodeTable(root, "", huffmanCodes);
        writeCompressedFile(content, huffmanCodes, outputFile);

        saveHuffmanTree(root, outputFile + ".tree");
    }

    public static void decompress(String inputFile, String outputFile, String treeFile) throws IOException, ClassNotFoundException {
        HuffmanNode root = readHuffmanTree(treeFile);
        String decompressedContent = decodeFile(inputFile, root);
        writeFile(outputFile, decompressedContent);
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static Map<Character, Integer> buildFrequencyMap(String content) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : content.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            assert right != null;
            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        }

        return priorityQueue.poll();
    }

    private static void buildCodeTable(HuffmanNode node, String code, Map<Character, String> huffmanCodes) {
        if (node.isLeaf()) {
            huffmanCodes.put(node.character, code);
            return;
        }

        buildCodeTable(node.left, code + "0", huffmanCodes);
        buildCodeTable(node.right, code + "1", huffmanCodes);
    }

    private static void writeCompressedFile(String content, Map<Character, String> huffmanCodes, String outputFile)
            throws IOException {
        try (BitOutputStream bitOut = new BitOutputStream(Files.newOutputStream(Paths.get(outputFile)))) {
            for (char c : content.toCharArray()) {
                String code = huffmanCodes.get(c);
                for (char bit : code.toCharArray()) {
                    bitOut.writeBit(bit == '1' ? 1 : 0);
                }
            }
        }
    }

    private static void saveHuffmanTree(HuffmanNode root, String treeFile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(treeFile)))) {
            oos.writeObject(serializeTree(root));
        }
    }

    private static String serializeTree(HuffmanNode node) {
        if (node == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (node.isLeaf()) {
            sb.append("1").append(node.character);
        } else {
            sb.append("0");
            sb.append(serializeTree(node.left));
            sb.append(serializeTree(node.right));
        }

        return sb.toString();
    }

    private static HuffmanNode readHuffmanTree(String treeFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(treeFile)))) {
            String serializedTree = (String) ois.readObject();
            return deserializeTree(new StringBuilder(serializedTree));
        }
    }

    private static HuffmanNode deserializeTree(StringBuilder data) {
        if (data.length() == 0) {
            return null;
        }

        char type = data.charAt(0);
        data.deleteCharAt(0);

        if (type == '1') {
            char character = data.charAt(0);
            data.deleteCharAt(0);
            return new HuffmanNode(character, 0); // 频率在解压时不重要
        } else {
            HuffmanNode left = deserializeTree(data);
            HuffmanNode right = deserializeTree(data);
            return new HuffmanNode('\0', 0, left, right);
        }
    }

    private static String decodeFile(String inputFile, HuffmanNode root) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BitInputStream bitIn = new BitInputStream(Files.newInputStream(Paths.get(inputFile)))) {
            HuffmanNode current = root;

            while (true) {
                int bit = bitIn.readBit();
                if (bit == -1) break;

                if (bit == 0) {
                    current = current.left;
                } else {
                    current = current.right;
                }

                if (current.isLeaf()) {
                    result.append(current.character);
                    current = root;
                }
            }
        }
        return result.toString();
    }

    private static void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    static class BitOutputStream implements Closeable {
        private final OutputStream out;
        private int currentByte;
        private int numBitsInCurrentByte;

        public BitOutputStream(OutputStream out) {
            this.out = out;
            this.currentByte = 0;
            this.numBitsInCurrentByte = 0;
        }

        public void writeBit(int bit) throws IOException {
            if (bit != 0 && bit != 1) {
                throw new IllegalArgumentException("Bit must be 0 or 1");
            }

            currentByte = (currentByte << 1) | bit;
            numBitsInCurrentByte++;

            if (numBitsInCurrentByte == 8) {
                out.write(currentByte);
                currentByte = 0;
                numBitsInCurrentByte = 0;
            }
        }

        @Override
        public void close() throws IOException {
            while (numBitsInCurrentByte != 0) {
                writeBit(0);
            }
            out.close();
        }
    }

    static class BitInputStream implements Closeable {
        private final InputStream in;
        private int currentByte;
        private int numBitsRemaining;

        public BitInputStream(InputStream in) {
            this.in = in;
            this.currentByte = 0;
            this.numBitsRemaining = 0;
        }

        public int readBit() throws IOException {
            if (numBitsRemaining == 0) {
                currentByte = in.read();
                if (currentByte == -1) {
                    return -1;
                }
                numBitsRemaining = 8;
            }

            numBitsRemaining--;
            return (currentByte >> numBitsRemaining) & 1;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}
