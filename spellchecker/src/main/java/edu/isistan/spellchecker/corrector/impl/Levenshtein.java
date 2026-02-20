package edu.isistan.spellchecker.corrector.impl;
import java.util.*;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;

public class Levenshtein extends Corrector {
	private Dictionary dictionary;
	private Map<Integer, ArrayList<String>> palabras;

	public Levenshtein(Dictionary dict) {
		if (dict == null) {
			throw new IllegalArgumentException("El Diccionario no puede ser null");
		}
		else {
            this.dictionary = dict;
            palabras = new HashMap<Integer, ArrayList<String>>();
            ArrayList<String> lista = new ArrayList<>(dictionary.getWords());
            for( String palabra : lista) {
                palabras.computeIfAbsent(palabra.length(), k -> new ArrayList<>()).add(palabra);
            }
        }
	}

    public Set<String> getDeletions(String s) {
        Set<String> correcciones = new HashSet<>();
        ArrayList<String> menosLenght = palabras.get(s.length() - 1);

        if (menosLenght != null && !menosLenght.isEmpty()) {
            for (String palabra : menosLenght) {
                int i = 0; // índice de s (palabra incorrecta)
                int j = 0; // índice de palabra correcta (más corta)
                int diferencias = 0;

                while (i < s.length() && j < palabra.length()) {
                    if (s.charAt(i) == palabra.charAt(j)) {
                        i++;
                        j++;
                    } else {
                        diferencias++;
                        i++; // se “salta” un carácter de s (delete)
                        if (diferencias > 1) {
                            break;
                        }
                    }
                }
                // Si quedó un carácter extra en s
                if (i < s.length()) {
                    diferencias++;
                }
                if (diferencias == 1) {
                    correcciones.add(palabra);
                }
            }
        }
        return correcciones;
    }

    public Set<String> getSubstitutions(String s) {
        Set<String> correcciones = new HashSet<>();
        ArrayList<String> mismoLenght = palabras.get(s.length());
        if(mismoLenght != null && !mismoLenght.isEmpty()) {
            for (String palabra : mismoLenght) {
                int diferencias = 0;
                if(!s.equals(palabra.toLowerCase())) {
                    for (int i = 0; i < s.length(); i++) {
                        if (s.charAt(i) != palabra.charAt(i))
                            diferencias++;
                        if (diferencias > 1)
                            i = s.length();
                    }
                    if (diferencias <= 1) {
                        correcciones.add(palabra);
                    }
                }
            }
        }
        return correcciones;
	}

	public Set<String> getInsertions(String s) {
        Set<String> correcciones = new HashSet<>();
        ArrayList<String> masLenght = palabras.get(s.length() + 1);
        if (masLenght != null &&!masLenght.isEmpty()) {
            for (String palabra : masLenght) {
                int i = 0;  //indice wrong
                int j = 0;    //indice right
                int diferencias = 0;

                while (i < s.length() && j < palabra.length()) {
                    if (s.charAt(i) == palabra.charAt(j)) {
                        i++;
                        j++;
                    } else {
                        diferencias++;
                        j++;
                        if (diferencias > 1)
                            i = s.length();
                    }
                }
                if (diferencias <= 1) {
                    correcciones.add(palabra);
                }
            }
        }
            return correcciones;

    }

	public Set<String> getCorrections(String s) {
        if(s == null)
            throw new IllegalArgumentException("La palabra no puede ser null");
        else {
            String wrong = s.toLowerCase();
            Set<String> correcciones = new HashSet<>();
            correcciones = getInsertions(wrong);
            correcciones.addAll(getSubstitutions(wrong));
            correcciones.addAll(getDeletions(wrong));
            if(correcciones.isEmpty())
                return Collections.emptySet();
            return super.matchCase(s, correcciones);
        }
	}
}