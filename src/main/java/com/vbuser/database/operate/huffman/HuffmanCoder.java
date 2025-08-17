package com.vbuser.database.operate.huffman;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 哈夫曼编码压缩与解压缩实现<br>
 * 提供文件级压缩/解压缩功能，包含位级I/O操作
 */
public class HuffmanCoder {

    /**
     * 压缩文件
     * @param inputFile 原始文件路径
     * @param outputFile 压缩输出文件路径
     */
    public static void compress(String inputFile, String outputFile) throws IOException {
        // 1. 读取文件内容
        String content = readFile(inputFile);
        // 2. 构建字符频率映射表
        Map<Character, Integer> frequencyMap = buildFrequencyMap(content);
        // 3. 构建哈夫曼树
        HuffmanNode root = buildHuffmanTree(frequencyMap);
        // 4. 生成哈夫曼编码表
        Map<Character, String> huffmanCodes = new HashMap<>();
        buildCodeTable(root, "", huffmanCodes);
        // 5. 写入压缩文件
        writeCompressedFile(content, huffmanCodes, outputFile);
        // 6. 保存哈夫曼树结构（用于解压）
        saveHuffmanTree(root, outputFile + ".tree");
    }

    /**
     * 解压缩文件
     * @param inputFile 压缩文件路径
     * @param outputFile 解压输出文件路径
     * @param treeFile 哈夫曼树结构文件路径
     * @throws ClassNotFoundException 序列化异常
     */
    public static void decompress(String inputFile, String outputFile, String treeFile) throws IOException, ClassNotFoundException {
        // 1. 从文件加载哈夫曼树
        HuffmanNode root = readHuffmanTree(treeFile);
        // 2. 解码文件内容
        String decompressedContent = decodeFile(inputFile, root);
        // 3. 写入解压文件
        writeFile(outputFile, decompressedContent);
    }

    /**
     * 读取文件内容到字符串
     */
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

    /**
     * 构建字符频率统计表
     * @param content 输入文本内容
     * @return 字符->频率映射表
     */
    private static Map<Character, Integer> buildFrequencyMap(String content) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : content.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    /**
     * 构建哈夫曼树
     * @param frequencyMap 字符频率表
     * @return 哈夫曼树根节点
     */
    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        // 使用优先队列（最小堆）构建哈夫曼树
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();

        // 初始化叶子节点
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        // 合并节点直到只剩根节点
        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            assert right != null;
            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        }

        return priorityQueue.poll();
    }

    /**
     * 递归生成哈夫曼编码表
     * @param node 当前节点
     * @param code 当前路径编码
     * @param huffmanCodes 编码表
     */
    private static void buildCodeTable(HuffmanNode node, String code, Map<Character, String> huffmanCodes) {
        if (node.isLeaf()) {
            huffmanCodes.put(node.character, code);
            return;
        }

        // 左子树添加"0"，右子树添加"1"
        buildCodeTable(node.left, code + "0", huffmanCodes);
        buildCodeTable(node.right, code + "1", huffmanCodes);
    }

    /**
     * 写入压缩文件（按位写入）
     * @param content 原始内容
     * @param huffmanCodes 哈夫曼编码表
     * @param outputFile 输出文件路径
     */
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

    /**
     * 序列化哈夫曼树到文件
     * @param root 哈夫曼树根节点
     * @param treeFile 树结构文件路径
     */
    private static void saveHuffmanTree(HuffmanNode root, String treeFile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(treeFile)))) {
            oos.writeObject(serializeTree(root));
        }
    }

    /**
     * 递归序列化哈夫曼树（前序遍历）<br>
     * 叶子节点格式: "1"+字符<br>
     * 内部节点格式: "0"
     * @param node 当前节点
     * @return 序列化字符串
     */
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

    /**
     * 从文件加载哈夫曼树
     * @param treeFile 树结构文件路径
     * @return 哈夫曼树根节点
     */
    private static HuffmanNode readHuffmanTree(String treeFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(treeFile)))) {
            String serializedTree = (String) ois.readObject();
            return deserializeTree(new StringBuilder(serializedTree));
        }
    }

    /**
     * 反序列化哈夫曼树
     * @param data 序列化字符串
     * @return 重建的哈夫曼树
     */
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

    /**
     * 解码压缩文件
     * @param inputFile 压缩文件路径
     * @param root 哈夫曼树根节点
     * @return 解码后的原始内容
     */
    private static String decodeFile(String inputFile, HuffmanNode root) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BitInputStream bitIn = new BitInputStream(Files.newInputStream(Paths.get(inputFile)))) {
            HuffmanNode current = root;

            while (true) {
                int bit = bitIn.readBit();
                if (bit == -1) break;  // 文件结束

                // 根据比特位遍历树：0向左，1向右
                if (bit == 0) {
                    current = current.left;
                } else {
                    current = current.right;
                }

                // 到达叶子节点时输出字符
                if (current.isLeaf()) {
                    result.append(current.character);
                    current = root;  // 重置到根节点
                }
            }
        }
        return result.toString();
    }

    /**
     * 将内容写入文件
     * @param filePath 输出文件路径
     * @param content 要写入的内容
     */
    private static void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    // =============== 位输出流实现 ===============

    /**
     * 比特级输出流（按位写入）
     */
    static class BitOutputStream implements Closeable {
        private final OutputStream out;
        private int currentByte;          // 当前组装的字节
        private int numBitsInCurrentByte; // 当前字节中已写入的比特数

        public BitOutputStream(OutputStream out) {
            this.out = out;
            this.currentByte = 0;
            this.numBitsInCurrentByte = 0;
        }

        /**
         * 写入单个比特位
         * @param bit 要写入的比特（0或1）
         */
        public void writeBit(int bit) throws IOException {
            if (bit != 0 && bit != 1) {
                throw new IllegalArgumentException("Bit must be 0 or 1");
            }

            // 左移后写入新比特
            currentByte = (currentByte << 1) | bit;
            numBitsInCurrentByte++;

            // 满8比特时写入输出流
            if (numBitsInCurrentByte == 8) {
                out.write(currentByte);
                currentByte = 0;
                numBitsInCurrentByte = 0;
            }
        }

        /**
         * 关闭流（自动填充未满字节）
         */
        @Override
        public void close() throws IOException {
            // 填充剩余位为0
            while (numBitsInCurrentByte != 0) {
                writeBit(0);
            }
            out.close();
        }
    }

    // =============== 位输入流实现 ===============

    /**
     * 比特级输入流（按位读取）
     */
    static class BitInputStream implements Closeable {
        private final InputStream in;
        private int currentByte;          // 当前读取的字节
        private int numBitsRemaining;     // 当前字节中剩余的比特数

        public BitInputStream(InputStream in) {
            this.in = in;
            this.currentByte = 0;
            this.numBitsRemaining = 0;
        }

        /**
         * 读取单个比特位
         * @return 读取的比特（0或1），-1表示结束
         */
        public int readBit() throws IOException {
            // 需要新字节时从流中读取
            if (numBitsRemaining == 0) {
                currentByte = in.read();
                if (currentByte == -1) {
                    return -1;  // 文件结束
                }
                numBitsRemaining = 8;
            }

            // 从高位开始提取比特
            numBitsRemaining--;
            return (currentByte >> numBitsRemaining) & 1;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}