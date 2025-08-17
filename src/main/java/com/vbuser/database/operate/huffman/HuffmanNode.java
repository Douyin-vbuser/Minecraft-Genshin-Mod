package com.vbuser.database.operate.huffman;

/**
 * 表示哈夫曼树中的节点<br>
 * 实现Comparable接口以支持节点间的频率比较
 */
public class HuffmanNode implements Comparable<HuffmanNode> {
    // 节点存储的字符（叶子节点有效）
    char character;
    // 字符出现频率（权重）
    int frequency;
    // 左右子节点
    HuffmanNode left, right;

    /**
     * 构造叶子节点
     * @param frequency 字符出现频率
     */
    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    /**
     * 构造内部节点（非叶子节点）
     */
    public HuffmanNode(char character, int frequency, HuffmanNode left, HuffmanNode right) {
        this.character = character;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    /**
     * 判断是否为叶子节点
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * 节点比较方法（基于频率）
     * @param other 待比较的节点
     * @return 频率差值（用于优先队列排序）
     */
    @Override
    public int compareTo(HuffmanNode other) {
        return this.frequency - other.frequency;
    }
}