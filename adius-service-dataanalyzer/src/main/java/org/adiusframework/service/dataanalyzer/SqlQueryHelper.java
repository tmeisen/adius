package org.adiusframework.service.dataanalyzer;

import java.util.Collection;

import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

/**
 * Static helper class for building queries. It contains functions to convert
 * comparison operators to strings and to identify SWRL builtins.
 * 
 * @author Alexander Tenbrock
 */
public class SqlQueryHelper {
	static public String getComparator(boolean negate) {
		if (!negate)
			return "=";
		return "<>";
	}

	static public String getComparator(OWLFacet facet, boolean negate) {
		if (!negate) {
			switch (facet) {
			case MIN_INCLUSIVE:
				return ">=";
			case MIN_EXCLUSIVE:
				return ">";
			case MAX_INCLUSIVE:
				return "<=";
			case MAX_EXCLUSIVE:
				return "<";
			default:
				return null;
			}
		}
		switch (facet) {
		case MIN_INCLUSIVE:
			return "<";
		case MIN_EXCLUSIVE:
			return "<=";
		case MAX_INCLUSIVE:
			return ">";
		case MAX_EXCLUSIVE:
			return ">=";
		default:
			return null;
		}
	}

	static public String getComparator(SWRLBuiltInsVocabulary builtIn, boolean negate) {
		if (!negate) {
			switch (builtIn) {
			case EQUAL:
				return "=";
			case NOT_EQUAL:
				return " <> ";
			case LESS_THAN:
				return " < ";
			case LESS_THAN_OR_EQUAL:
				return " <= ";
			case GREATER_THAN:
				return " > ";
			case GREATER_THAN_OR_EQUAL:
				return " >= ";
			default:
				return null;
			}
		}
		switch (builtIn) {
		case EQUAL:
			return " <> ";
		case NOT_EQUAL:
			return " = ";
		case LESS_THAN:
			return " >= ";
		case LESS_THAN_OR_EQUAL:
			return " > ";
		case GREATER_THAN:
			return " <= ";
		case GREATER_THAN_OR_EQUAL:
			return " < ";
		default:
			return null;
		}
	}

	public static SWRLBuiltInsVocabulary getSWRLMathFunction(SWRLBuiltInAtom atom) {
		if (atom.getPredicate().equals(SWRLBuiltInsVocabulary.SUBTRACT.getIRI()))
			return SWRLBuiltInsVocabulary.SUBTRACT;
		else if (atom.getPredicate().equals(SWRLBuiltInsVocabulary.ADD.getIRI()))
			return SWRLBuiltInsVocabulary.ADD;
		else if (atom.getPredicate().equals(SWRLBuiltInsVocabulary.MULTIPLY.getIRI()))
			return SWRLBuiltInsVocabulary.MULTIPLY;
		else if (atom.getPredicate().equals(SWRLBuiltInsVocabulary.DIVIDE.getIRI()))
			return SWRLBuiltInsVocabulary.DIVIDE;
		else if (atom.getPredicate().equals(SWRLBuiltInsVocabulary.MOD.getIRI()))
			return SWRLBuiltInsVocabulary.MOD;
		return null;
	}

	public static String connectStrings(Collection<String> list, String connector) {
		String result = null;
		for (String str : list) {
			if (result == null)
				result = str;
			else
				result += connector + str;
		}
		return result;
	}
}
