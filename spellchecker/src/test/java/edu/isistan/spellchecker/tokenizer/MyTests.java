package edu.isistan.spellchecker.tokenizer;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;
import org.junit.Test;
import edu.isistan.spellchecker.corrector.*;


/** Cree sus propios tests. */
public class MyTests {
    //TOKENSCANNER
    // Caso 1: La entrada es vacía
    @Test(timeout=500) public void testEntradaVacia() throws IOException {
        TokenScanner ts = new TokenScanner(new StringReader(""));
        assertFalse("No debería haber tokens", ts.hasNext());
    }
    // Caso 2: La entrada tiene un solo token palabra
    @Test(timeout=500) public void testUnSoloTokenPalabra() throws IOException {
        TokenScanner ts = new TokenScanner(new StringReader("hola"));
        assertTrue(ts.hasNext());
        assertEquals("hola", ts.next());
        assertFalse(ts.hasNext());
    }
    // Caso 3: La entrada tiene un solo token no-palabra
    @Test(timeout=500) public void testUnSoloTokenNoPalabra() throws IOException {
        TokenScanner ts = new TokenScanner(new StringReader("   "));
        assertTrue(ts.hasNext());
        assertEquals("   ", ts.next()); // espacios
        assertFalse(ts.hasNext());
    }
    // Caso 4: Tiene palabra + no-palabra, termina en palabra
    @Test(timeout=500) public void testDosTiposTerminaPalabra() throws IOException {
        TokenScanner ts = new TokenScanner(new StringReader(" hola"));
        assertTrue(ts.hasNext());
        assertEquals(" ", ts.next());
        assertTrue(ts.hasNext());
        assertEquals("hola", ts.next());
        assertFalse(ts.hasNext());
    }
    // Caso 5: Tiene palabra + no-palabra, termina en no-palabra
    @Test(timeout=500) public void testDosTiposTerminaNoPalabra() throws IOException {
        TokenScanner ts = new TokenScanner(new StringReader("hola!!!"));
        assertTrue(ts.hasNext());
        assertEquals("hola", ts.next());
        assertTrue(ts.hasNext());
        assertEquals("!!!", ts.next());
        assertFalse(ts.hasNext());
    }


    //DICTIONARY
    // Caso 1: palabra que está en el diccionario
    @Test
    public void testPalabraEnDiccionario() throws IOException {
        DictionaryHash d = new DictionaryHash(new TokenScanner(new FileReader("data/Dictionary.txt")));
        assertTrue("La palabra 'virtuosity' debería estar en el diccionario", d.isWord("virtuosity"));
    }
    // Caso 2: palabra que NO está en el diccionario
    @Test(timeout=500) public void testPalabraNoEnDiccionario() throws IOException {
        DictionaryHash d = new DictionaryHash(new TokenScanner(new FileReader("data/smallDictionary.txt")));
        assertFalse("La palabra 'hola' no debería estar en el diccionario", d.isWord("hola"));
    }
    // Caso 3: número de palabras en el diccionario
    @Test(timeout=500) public void testNumeroDePalabras() throws IOException {
        DictionaryHash d = new DictionaryHash(new TokenScanner(new FileReader("data/smallDictionary.txt")));
        assertEquals("El diccionario debería tener 32 palabras", 32, d.getNumWords());
    }
    // Caso 4: Verifica que el String vacío no es palabra
    @Test(timeout=500) public void testStringVacio() throws IOException {
        DictionaryHash d = new DictionaryHash(new TokenScanner(new FileReader("data/smallDictionary.txt")));
        assertFalse("El string vacío no debería ser considerado palabra", d.isWord(""));
    }
    // Caso 5: misma palabra con distintas capitalizaciones
    @Test(timeout=500) public void testPalabraCaseInsensitive() throws IOException {
        DictionaryHash d = new DictionaryHash(new TokenScanner(new FileReader("data/smallDictionary.txt")));
        assertTrue(d.isWord("carrot"));
        assertTrue(d.isWord("Carrot"));
        assertTrue(d.isWord("CARROT"));
        assertTrue(d.isWord("CarrOt"));
    }

    //FileCorrector
    // Caso 1: Con espacios extras en alrededor de las líneas o alrededor de las comas.
    @Test(expected = FileCorrector.FormatException.class)
    public void testFormatoInvalidoConEspacios() throws IOException, FileCorrector.FormatException {
        File archivo = File.createTempFile("extraSpacesMisspellings", ".txt");
        archivo.deleteOnExit();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("   cebola  ,  cebolla  \n");
            bw.write("glovo,  globo\n");
            bw.write("  denada , de nada  \n");
        }
        FileCorrector.make(archivo.getAbsolutePath());
    }
    // Caso 2: Pedir correcciones para una palabra sin correcciones.
    @Test(timeout=500)  public void testGetCorrectionVacia() throws IOException, FileCorrector.FormatException  {
        Corrector c = FileCorrector.make("data/smallMisspellings.txt");
        assertTrue("la palabra no tiene correcciones", c.getCorrections("carrot").isEmpty());
    }
    // Caso 3: Pedir correcciones para una palabra con múltiples correcciones.
    @Test(timeout=500)  public void testMultipleCorrections() throws IOException, FileCorrector.FormatException {
        Corrector c = FileCorrector.make("data/theFoxMisspellings.txt");
        Set<String> esperado = new HashSet<>(Arrays.asList("the", "tehe"));
        assertEquals("teh -> {the, tehe}", esperado, c.getCorrections("teh"));
    }

    // Caso 4: Probar correcciones para palabras con distintas capitalizaciones.
    @Test(timeout=500)
    public void testCapitalizacionesFC() throws IOException, FileCorrector.FormatException {
        Corrector c = FileCorrector.make("data/smallMisspellings.txt");
        Set<String> esperadoMinuscula = new HashSet<>(Arrays.asList("lion"));
        Set<String> esperadoMayuscula = new HashSet<>(Arrays.asList("Lion"));
        assertEquals("lyon -> {lion}", esperadoMinuscula, c.getCorrections("lYOn"));
        assertEquals("Lyon -> {Lion}", esperadoMayuscula, c.getCorrections("Lyon"));
        assertEquals("LYON -> {Lion}", esperadoMayuscula, c.getCorrections("LYON"));
        assertEquals("LyOn -> {Lion}", esperadoMayuscula, c.getCorrections("LyOn"));
    }

    // SwapCorrector
    // Caso 1: Proveer un diccionario null.
    @Test(expected = IllegalArgumentException.class) public void testNullDictionary() {
        Corrector c = new SwapCorrector(null);
    }
    // Caso 2: Pedir correcciones para una palabra que está en el diccionario.
    @Test(timeout=500) public void testEnDiccionarioSC() throws IOException {
        DictionaryHash d = new DictionaryHash(new TokenScanner(new FileReader("data/smallDictionary.txt")));
        SwapCorrector sc = new SwapCorrector(d);
        assertTrue("La palabra ya está en el diccionario, no debe sugerir nada", sc.getCorrections("hello").isEmpty());
    }

    // Caso 3: Pedir correcciones para una palabra con distintas capitalizaciones.
    @Test (timeout=500) public void testCapitalizacionesSC() throws IOException {
            DictionaryHash d = new DictionaryHash(new TokenScanner(new FileReader("data/smallDictionary.txt")));
            Corrector sc = new SwapCorrector(d);
            Set<String> esperadoMayuscula = new HashSet<>(Arrays.asList("You"));
            Set<String> esperadoMinuscula = new HashSet<>(Arrays.asList("you"));
            assertEquals("YUO -> {You}", esperadoMayuscula, sc.getCorrections("YUO"));
            assertEquals("YuO -> {You}", esperadoMayuscula, sc.getCorrections("YuO"));
            assertEquals("yOu -> {you}", esperadoMinuscula, sc.getCorrections("yUO"));
    }

}