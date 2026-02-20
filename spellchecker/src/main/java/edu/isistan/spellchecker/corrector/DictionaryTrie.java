package edu.isistan.spellchecker.corrector;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

public class DictionaryTrie implements Dictionary{
    private TrieNode root;
    private int numWords;

    // Nodo interno del Trie
    private static class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;

        TrieNode() {
            children = new TrieNode[27]; // 26 letras + apostrofe suponiendo idioma ingles
            isEndOfWord = false;
        }
    }

    public DictionaryTrie(TokenScanner ts) throws IOException {
        if (ts == null) {
            throw new IllegalArgumentException("El TokenScanner no puede ser null");
        }
        root = new TrieNode();
        numWords = 0;

        while (ts.hasNext()) {
            String token = ts.next();
            if (ts.isWord(token)) {
                insert(token.toLowerCase());
            }
        }
    }

    public static DictionaryTrie make(String filename) throws IOException {
        Reader r = new java.io.FileReader(filename);
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(r));
        r.close();
        return d;
    }

    // Inserta una palabra en el Trie
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int idx = getIndex(c);
            if (idx == -1) continue; // ignorar caracteres no válidos
            if (current.children[idx] == null) {
                current.children[idx] = new TrieNode();
            }
            current = current.children[idx];
        }
        if (!current.isEndOfWord) {
            current.isEndOfWord = true;
            numWords++;
        }
    }

    // Verifica si la palabra existe en el diccionario
    public boolean isWord(String word) {
        if (word == null) return false;
        TrieNode current = root;
        for (char c : word.toLowerCase().toCharArray()) {
            int idx = getIndex(c);
            if (idx == -1 || current.children[idx] == null) {
                return false;
            }
            current = current.children[idx];
        }
        return current.isEndOfWord;
    }

    public int getNumWords() {
        return numWords;
    }

    // Convierte un carácter en índice (a–z = 0–25, apostrofe = 26)
    private int getIndex(char c) {
        if (c == '\'') return 26;
        if (c >= 'a' && c <= 'z') return c - 'a';
        return -1;
    }

    public Set<String> getWords() {
        Set<String> result = new java.util.HashSet<>();
        collectWords(root, new StringBuilder(), result);
        return result;
    }

    private void collectWords(TrieNode node, StringBuilder prefix, Set<String> result) {
        if (node == null) return;

        if (node.isEndOfWord) {
            result.add(prefix.toString());
        }

        for (int i = 0; i < node.children.length; i++) {
            if (node.children[i] != null) {
                char c = (i == 26) ? '\'' : (char) ('a' + i);
                prefix.append(c);
                collectWords(node.children[i], prefix, result);
                prefix.deleteCharAt(prefix.length() - 1); // backtrack
            }
        }
    }
}
