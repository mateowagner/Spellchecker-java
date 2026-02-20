package edu.isistan.spellchecker.corrector;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class DictionaryHash implements Dictionary {

	private Set<String> words;

	public DictionaryHash(TokenScanner ts) throws IOException {
		if (ts == null) {
			throw new IllegalArgumentException("El TokenScanner no puede ser null");
		}

		words = new HashSet<>();
		while (ts.hasNext()) {
			String token = ts.next();
			if (ts.isWord(token)) {
				words.add(token.toLowerCase()); // guardamos en minúsculas
			}
		}
	}

	public static DictionaryHash make(String filename) throws IOException {
		Reader r = new FileReader(filename);
		DictionaryHash d = new DictionaryHash(new TokenScanner(r));
		r.close();
		return d;
	}

	public int getNumWords() {
		return words.size();
	}

	public boolean isWord(String word) {
        return words.contains(word.toLowerCase());
    }

	public Set<String> getWords(){
		return words;
	}
}