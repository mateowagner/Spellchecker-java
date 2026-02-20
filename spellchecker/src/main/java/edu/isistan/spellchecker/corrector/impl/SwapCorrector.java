package edu.isistan.spellchecker.corrector.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;

public class SwapCorrector extends Corrector {
	private Dictionary dict;

	public SwapCorrector(Dictionary dict) {
        if (dict == null) {
            throw new IllegalArgumentException("El Diccionario no puede ser null");
        }
		this.dict = dict;
	}

	public Set<String> getCorrections(String wrong) {
		Set<String> Corrections = new HashSet<>();
		StringBuilder sb = new StringBuilder(wrong);
		int i = 0;
		while (i < sb.length() - 1) {
			char aux1 = sb.charAt(i);
			char aux2 = sb.charAt(i + 1);
			sb.setCharAt(i, aux2);
			sb.setCharAt(i + 1, aux1);
            if (dict.isWord(sb.toString().toLowerCase())) {
				Corrections.add(sb.toString().toLowerCase());
            }
			sb.setCharAt(i, aux1);
			sb.setCharAt(i + 1, aux2);
			i++;
		}

        if(Corrections.isEmpty())
		    return Collections.emptySet();
		return super.matchCase(wrong, Corrections);
	}
}
