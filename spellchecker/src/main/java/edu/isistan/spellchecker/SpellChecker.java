package edu.isistan.spellchecker;
import java.io.*;
import java.util.*;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class SpellChecker {
	private Corrector corr;
	private Dictionary dict;
    private TokenScanner tokenScanner;

	public SpellChecker(Corrector c, Dictionary d) {
		corr = c;
		dict = d;
	}

	private int getNextInt(int min, int max, Scanner sc) {
		while (true) {
			try {
				int choice = Integer.parseInt(sc.next());
				if (choice >= min && choice <= max) {
					return choice;
				}
			} catch (NumberFormatException ex) {
				// Was not a number. Ignore and prompt again.
			}
			System.out.println("Entrada invalida. Pruebe de nuevo!");
		}
	}

	private String getNextString(Scanner sc) {
		return sc.next();
	}

	public void checkDocument(Reader in, InputStream input, Writer out) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
		Scanner sc = new Scanner(input);
        tokenScanner = new TokenScanner(in);
		ArrayList<String> palabras = new ArrayList<>();
        while(tokenScanner.hasNext()){
            String aux = tokenScanner.next();
			palabras.add(aux);
			if (tokenScanner.isWord(aux)) {
				if (dict.isWord(aux)) {
					System.out.println("Palabra correcta: " + aux);
				} else {
                    System.out.println("Palabra incorrecta: " + aux);
                    Set<String> correciones = corr.getCorrections(aux);
                    if (correciones == null)
                        correciones = Collections.emptySet();

                    List<String> lista = new ArrayList<>(correciones); // para indexado predecible
                    System.out.println("0) Mantener la palabra original: " + aux);
                    System.out.println("1) Ingresar por teclado la palabra correcta");
                    for (int i = 0; i < lista.size(); i++) {
                        System.out.println(i + 2 + ") " + lista.get(i));
                    }

                    while (true) {
                        System.out.print("Elige una opción (0-" + (lista.size() + 1) + "): ");
                        int opcion = -1;
                        boolean valido = true;
                        try {
                            opcion = Integer.parseInt(br.readLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida, no es un número. Se ignora.");
                            valido = false;
                        }

                        if(valido) {
                            System.out.println("opcion : " + opcion);
                            try {
                                if (opcion < 0 || opcion > lista.size() + 1) {
                                    System.out.println("Opción fuera de rango. Intenta nuevamente.");
                                    continue;
                                }
                                if (opcion == 0) {
                                    System.out.println("Se mantuvo: " + aux);
                                } else {
                                    if (opcion == 1) {
                                        System.out.println("Ingrese la palabra correcta: ");
                                        String palabra = br.readLine();
                                        aux = palabra;
                                    } else {
										aux = corr.matchCase(aux, Collections.singleton(lista.get(opcion - 2))).iterator().next();
										System.out.println("Se reemplazó por: " + aux);
                                        System.out.println();
                                    }
                                }

                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Entrada inválida. Ingresa un número válido.");
                            }
                        }
                    }
				}
                out.write(aux);
			}
            else
                out.write(aux);
		}
	}
}