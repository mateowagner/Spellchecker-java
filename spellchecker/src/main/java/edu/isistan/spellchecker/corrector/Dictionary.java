package edu.isistan.spellchecker.corrector;

import java.io.IOException;
import java.util.Set;

public interface Dictionary {
    int getNumWords();
    boolean isWord(String word);
    Set<String> getWords();
}