package bearmaps.proj2c.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MyTrieSet implements TrieSet61B {
  private Node root;

  private class Node {
    private char ch;
    private boolean isKey;
    private Map<Character, Node> next;

    private Node(char c) {
      ch = c;
      next = new HashMap<>();
    }

    private Node() {
      next = new HashMap<>();
    }
  }

  public MyTrieSet() {
    root = new Node();
  }

  @Override
  public void clear() {
    root = null;
  }

  @Override
  public boolean contains(String key) {
    return get(root, key, 0) != null && get(root, key, 0).isKey;
  }

  private Node get(Node node, String key, int cursor) {
    if (node == null || key == null) return null;
    if (cursor == key.length()) return node;

    char c = key.charAt(cursor);
    return get(node.next.get(c), key, cursor + 1);
  }

  @Override
  public void add(String key) {
    if (key == null || key.length() < 1) return;

    Node tmp = root;
    for (int i = 0; i < key.length(); i++) {
      char c = key.charAt(i);
      if (!tmp.next.containsKey(c))
        tmp.next.put(c, new Node(c));

      tmp = tmp.next.get(c);
    }
    tmp.isKey = true;
  }

  @Override
  public List<String> keysWithPrefix(String prefix) {
    if (prefix == null || prefix.length() < 1) return null;

    Node node = get(root, prefix, 0);
    Queue<String> results = new ArrayDeque<>();
    collect(node, new StringBuilder(prefix), results);
    return new ArrayList<>(results);
  }

  private void collect(Node node, StringBuilder prefix, Queue<String> results) {
    if (node == null) return;
    if (node.isKey) results.add(prefix.toString());

    for (char c = 0; c < 256; c++) { // 256 for extended ASCII
      prefix.append(c);
      collect(node.next.get(c), prefix, results);
      prefix.deleteCharAt(prefix.length() - 1);
    }
  }

  @Override
  public String longestPrefixOf(String key) {
    if (key == null) return null;
    int length = longestPrefixHelper(root, key, 0, -1);
    if (length == -1) return null;

    return key.substring(0, length);
  }

  private int longestPrefixHelper(Node node, String key, int cursor, int length) {
    if (node == null || cursor == key.length()) return length;
    if (node.isKey) length = cursor;

    char c = key.charAt(cursor);
    return longestPrefixHelper(node.next.get(c), key, cursor + 1, length);
  }
}
