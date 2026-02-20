package edu.isistan.spellchecker;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.DictionaryHash;
import edu.isistan.spellchecker.corrector.DictionaryTrie;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.impl.Levenshtein;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;

public class SpellCheckerRunner {

	private static Corrector makeCorrector(String type, Dictionary dict)
			throws IOException, FileCorrector.FormatException {
		if (type.equals("SWAP")) {
			return new SwapCorrector(dict);
		}
		if (type.equals("LEV")) {
			return new Levenshtein(dict);
		}

		return FileCorrector.make(type);
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("uso: java SpellCheckRunner <in> <out> <dictionary> <corrector>");
			System.out.println("<corrector> es SWAP, LEV, or el path para instanciar el FileCorrector.");
			return;
		}
		try {
			Reader in = new BufferedReader(new FileReader(args[0]));
			Writer out = new BufferedWriter(new FileWriter(args[1]));
			Dictionary dict = DictionaryHash.make(args[2]);

			SpellChecker sp = new SpellChecker(makeCorrector(args[3], dict), dict);
			sp.checkDocument(in, System.in, out);

			in.close();
			out.flush();
			out.close();

			// Mostrar el contenido del archivo de entrada
			String inputText = new String(Files.readAllBytes(Paths.get(args[0])));
			System.out.println("===== TEXTO DE ENTRADA =====");
			System.out.println(inputText);
			// Mostrar el contenido del archivo de salida
			String outputText = new String(Files.readAllBytes(Paths.get(args[1])));
			System.out.println("===== TEXTO CORREGIDO =====");
			System.out.println(outputText);

		} catch (IOException e) {
			System.out.println("error procesando el document: " + e.getMessage());
		} catch (FileCorrector.FormatException e) {
			System.out.println("error de formato: " + e.getMessage());
		}
	}
}
