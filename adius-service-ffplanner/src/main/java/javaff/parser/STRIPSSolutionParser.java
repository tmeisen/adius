package javaff.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javaff.data.Action;
import javaff.data.Parameter;
import javaff.data.TotalOrderPlan;
import javaff.data.UngroundProblem;
import javaff.data.strips.Operator;
import javaff.data.strips.OperatorName;
import javaff.data.strips.PDDLObject;
import javaff.data.strips.UngroundInstantAction;

/**
 * Parses a .SOL file and produces the plan which created it.
 * 
 * @author David Pattison
 * 
 */
public class STRIPSSolutionParser {

	public static TotalOrderPlan parseSTRIPSPlan(UngroundProblem up, String solutionPath) throws IOException,
			ParseException {
		return STRIPSSolutionParser.parseSTRIPSPlan(up, new File(solutionPath));
	}

	public static TotalOrderPlan parseSTRIPSPlan(UngroundProblem up, File solutionFile) throws IOException,
			ParseException {
		if (solutionFile.exists() == false)
			throw new FileNotFoundException("Cannot find solution file \"" + solutionFile.getAbsolutePath() + "\"");
		// first, construct a map of the grounded parameters names to their
		// objects
		UngroundProblem ungroundProblem = up;// (UngroundProblem)gp.clone();
		// Map<String, Parameter> map = new HashMap<String, Parameter>();
		// for (Proposition p : groundProblem.groundedPropositions)
		// {
		// for (Object paro : p.getParameters())
		// {
		// Parameter param = (Parameter)paro;
		// // System.out.println("Adding "+param.getName().toLowerCase());
		// map.put(param.getName().toLowerCase(), param);
		// }
		// }

		FileReader fReader = new FileReader(solutionFile);

		BufferedReader bufReader = new BufferedReader(fReader);
		String line;

		TotalOrderPlan top = new TotalOrderPlan();
		StringTokenizer strTok;

		try {
			while (bufReader.ready()) {
				line = bufReader.readLine();
				strTok = new StringTokenizer(line, ":() \t\n");
				// System.out.println("line is "+line);
				if (strTok.hasMoreTokens() == false || line.charAt(0) == ';')
					continue;
				// scan = new Scanner(line);
				// scan.useDelimiter(":(.*)");
				// scan.skip(Pattern.compile(".+:("));

				// System.out.println("Next is "+scan.next());
				// int stepNumber = scan.nextInt();
				// int stepNumber = Integer.parseInt(strTok.nextToken());
				// STRIPSInstantAction action = new STRIPSInstantAction();
				if (line.startsWith("(") == false)
					strTok.nextToken(); // skip action number

				OperatorName actionName = new OperatorName(strTok.nextToken());
				ArrayList<PDDLObject> vars = new ArrayList<PDDLObject>();
				while (strTok.hasMoreTokens()) {
					String tok = strTok.nextToken().toLowerCase();
					if (tok.startsWith("["))
						continue;

					Parameter var = ungroundProblem.objectMap.get(tok);
					if (var == null)
						throw new NullPointerException("Cannot find parameter mapping: " + tok);

					vars.add((PDDLObject) var);
				}

				UngroundInstantAction ungroundAction = null;
				for (Operator a : ungroundProblem.actions) {
					if (a.name.toString().equalsIgnoreCase(actionName.toString()))
						ungroundAction = (UngroundInstantAction) a;
				}
				if (ungroundAction == null)
					throw new NullPointerException("Cannot find action with name \"" + actionName + "\" in domain file");

				Action groundAction = ungroundAction.ground(vars);

				top.addAction(groundAction);
			}
			while (bufReader.ready()) {
				line = bufReader.readLine();
				strTok = new StringTokenizer(line, ":() \t\n");
				// System.out.println("line is "+line);
				if (strTok.hasMoreTokens() == false || line.charAt(0) == ';')
					continue;
				// scan = new Scanner(line);
				// scan.useDelimiter(":(.*)");
				// scan.skip(Pattern.compile(".+:("));

				// System.out.println("Next is "+scan.next());
				// int stepNumber = scan.nextInt();
				// int stepNumber = Integer.parseInt(strTok.nextToken());
				// STRIPSInstantAction action = new STRIPSInstantAction();
				if (line.startsWith("(") == false)
					strTok.nextToken(); // skip action number

				OperatorName actionName = new OperatorName(strTok.nextToken());
				ArrayList<PDDLObject> vars = new ArrayList<PDDLObject>();
				while (strTok.hasMoreTokens()) {
					String tok = strTok.nextToken().toLowerCase();
					if (tok.startsWith("["))
						continue;

					Parameter var = ungroundProblem.objectMap.get(tok);
					if (var == null)
						throw new NullPointerException("Cannot find parameter mapping: " + tok);

					vars.add((PDDLObject) var);
				}

				UngroundInstantAction ungroundAction = null;
				for (Operator a : ungroundProblem.actions) {
					if (a.name.toString().equalsIgnoreCase(actionName.toString()))
						ungroundAction = (UngroundInstantAction) a;
				}
				if (ungroundAction == null)
					throw new NullPointerException("Cannot find action with name \"" + actionName + "\" in domain file");

				Action groundAction = ungroundAction.ground(vars);

				top.addAction(groundAction);
			}
		} catch (IOException e) {
			throw new IOException("Incorrectly formatted solution file");
		} finally {
			bufReader.close();
			fReader.close();
		}

		return top;
	}
}
