package edu.isistan.spellchecker.corrector.impl;

import java.util.*;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

import java.io.*;


public class FileCorrector extends Corrector {
	public TokenScanner token;
	public HashMap<String, Set<String>> correcciones;

	public static class FormatException extends Exception {
		public FormatException(String msg) {
			super(msg);
		}
	}

	public FileCorrector(Reader r) throws IOException, FormatException {
		if (r == null) {
			throw new IllegalArgumentException("El Reader no puede ser null");
		}
		boolean valido;
		correcciones = new HashMap<>();
		token = new TokenScanner(r);
			while (token.hasNext()) {
				valido = false;
				String wrong = token.next();
				if (token.isWord(wrong)) {  //si el primer token es una palabra esta acorde al formato
					String aux;
					if (token.hasNext()) {
						aux = token.next();
						if (token.isComa(aux)) {  //si el segundo token es una unica coma (que puede estar rodeada de espacios en blanco) es acorde al formato
							if (token.hasNext()) {
								String right = token.next();
								if (token.isWord(right)) { //Si el tercer token es una palabra esta acorde al formato
									if (token.hasNext()) {
										aux = token.next();
										if (token.isSalto(aux)) { //si tiene siguiente y es un salto de linea esta acorde al formato
											valido = true;
                                            correcciones.computeIfAbsent(wrong.toLowerCase(), k -> new HashSet<>()).add(right);
                                        }
									}
                                    else { //Si es el fin del archivo es valido tambien
                                    valido = true;
                                    correcciones.computeIfAbsent(wrong.toLowerCase(), k -> new HashSet<>()).add(right);
                                    }
								}
							}
						}
					}
				}
				if (!valido) {
					throw new FormatException("Formato incorrecto en el texto: ");
				}
			}
	}

	public static FileCorrector make(String filename) throws IOException, FormatException {
		Reader r = new FileReader(filename);
		FileCorrector fc;
		try {
			fc = new FileCorrector(r);

		} finally {
			if (r != null) { r.close(); }
		}
		return fc;
	}

	@Override
	public Set<String> getCorrections(String wrong) {
		Set<String> Corrections = correcciones.get(wrong.toLowerCase());
		if (Corrections == null) {
			return Collections.emptySet();
		}
		return super.matchCase(wrong, Corrections);
	}
}
