package edu.isistan.spellchecker.benchmarks;

import edu.isistan.spellchecker.corrector.DictionaryHash;
import edu.isistan.spellchecker.corrector.DictionaryTrie;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(2)
public class DictionaryBenchmark {

    private DictionaryHash hashDict;
    private DictionaryTrie trieDict;

    private String existingWord;
    private String nonExistingWord;

    @Setup(Level.Iteration)
    public void setup() throws IOException {
        String dictPath = "dictionary.txt";
        hashDict = new DictionaryHash(new TokenScanner(new FileReader(dictPath)));
        trieDict = new DictionaryTrie(new TokenScanner(new FileReader(dictPath)));
        existingWord = "banana";
        nonExistingWord = "unicornio";
    }

    // ---- Construcción ----
    @Benchmark
    public void buildHashDict(Blackhole bh) throws IOException {
        DictionaryHash dh = new DictionaryHash(new TokenScanner(new FileReader("smallDictionary.txt")));
        bh.consume(dh);
    }

    @Benchmark
    public void buildTrieDict(Blackhole bh) throws IOException {
        DictionaryTrie dt = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        bh.consume(dt);
    }

    // ---- isWord ----
    @Benchmark
    public void testHashHit(Blackhole bh) {
        bh.consume(hashDict.isWord(existingWord));
    }

    @Benchmark
    public void testHashMiss(Blackhole bh) {
        bh.consume(hashDict.isWord(nonExistingWord));
    }

    @Benchmark
    public void testTrieHit(Blackhole bh) {
        bh.consume(trieDict.isWord(existingWord));
    }

    @Benchmark
    public void testTrieMiss(Blackhole bh) {
        bh.consume(trieDict.isWord(nonExistingWord));
    }

    // ---- getNumWords ----
    @Benchmark
    public void testHashNumWords(Blackhole bh) {
        bh.consume(hashDict.getNumWords());
    }

    @Benchmark
    public void testTrieNumWords(Blackhole bh) {
        bh.consume(trieDict.getNumWords());
    }

    // ---- getWords ----
    @Benchmark
    public void testHashGetWords(Blackhole bh) {
        Set<String> words = hashDict.getWords();
        bh.consume(words.size()); // evitar copiar todo el set
    }

    @Benchmark
    public void testTrieGetWords(Blackhole bh) {
        Set<String> words = trieDict.getWords();
        bh.consume(words.size());
    }

    // ---- Memoria usada ----
    @Benchmark
    public void testHashMemory(Blackhole bh) {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long used = rt.totalMemory() - rt.freeMemory();
        bh.consume(used);
    }

    @Benchmark
    public void testTrieMemory(Blackhole bh) {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long used = rt.totalMemory() - rt.freeMemory();
        bh.consume(used);
    }
}
