package edu.isistan.spellchecker.corrector;
import java.util.TreeSet;
import java.util.Set;

public abstract class Corrector {

	public Set<String> matchCase(String incorrectWord, Set<String> corrections) {
		if (incorrectWord == null || corrections == null) {
			throw new IllegalArgumentException("null input given");
		}
		Set<String> revisedSet = new TreeSet<String>();
		boolean capitalizeFirst = Character.isUpperCase(incorrectWord.charAt(0));
		for (String s : corrections) {
			if (capitalizeFirst) {
				String ucfirst =
						s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
				revisedSet.add(ucfirst);
			} else {
				revisedSet.add(s.toLowerCase());
			}
		}
		return revisedSet;
	}

	public abstract Set<String> getCorrections(String wrong);
}
