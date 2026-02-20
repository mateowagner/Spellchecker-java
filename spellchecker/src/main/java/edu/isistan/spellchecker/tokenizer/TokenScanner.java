package edu.isistan.spellchecker.tokenizer;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Iterator;
import java.io.IOException;
import java.util.NoSuchElementException;

public class TokenScanner implements Iterator<String> {
    private final Reader in;

  public TokenScanner(Reader in) throws IOException {
      if (!in.markSupported()) {
          in = new BufferedReader(in);
      }
      this.in = in;
  }

  public static boolean isWordCharacter(int c) {
      return (Character.isLetter(c) || c == '\'');
  }

  public static boolean isWord(String s) {
      if (s == null || s.isEmpty()) {
          return false; // No se acepta null o cadena vacía como palabra
      }

      for (int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          if (!(Character.isLetter(c) || c == '\'')) {
              return false; // Encontró un carácter inválido
          }
      }
      return true; // Todos los caracteres son válidos
  }

    public boolean isComa(String s){
        if (s == null || s.isEmpty()) {
            return false; // No se acepta null o cadena vacía como palabra
        }
        int cantComas = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ( c == ',')
                cantComas++;
            else{
                if (!(c == ' '))
                    return false;
            }
        }
        if(cantComas != 1)
            return false;
        return true; // Todos los caracteres son válidos
    }

    public boolean isSalto(String s){
        if (s == null || s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(c == '\n' || c == '\r')) {
                return false;
            }
        }
        return true;
    }

  public boolean hasNext() {
      int c;
      try {
          in.mark(1); // marcamos la posición actual, podemos leer 1 caracter sin perder el mark
          c = 0; // leemos un caracter
          c = in.read();
          in.reset(); // volvemos a la posición marcada
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      return c != -1; // si c != -1, hay algo por leer
  }

  public String next()  {
      int aux = 0;
      String palabra = "";
      try {
          aux = in.read();
          if (isWordCharacter(aux)) {
              palabra = leerSecuenciaDeLetras(aux);
          }
          else {
              palabra = leerSecuenciaDeCaracteres(aux);
          }
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      return palabra;
  }

  private String leerSecuenciaDeCaracteres(int c) throws IOException{
      String palabra = "";
      while(!isWordCharacter(c) && c != -1){
          palabra = palabra + String.valueOf((char) c);
          in.mark(200);
          c = in.read();
      }
      if (c == -1){
          return palabra;
      }
      in.reset();
      return palabra;
  }

  private String leerSecuenciaDeLetras(int c) throws IOException{
      String palabra = "";
      while (isWordCharacter(c)){
          palabra = palabra + String.valueOf((char) c);
          in.mark(200);
          c = in.read();
      }
      in.reset();
      return palabra;
  }
}
