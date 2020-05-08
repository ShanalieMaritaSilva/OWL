//package com.mcs.owl.service;
//
//import org.semanticweb.owlapi.model.OWLOntology;
//import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//import org.semanticweb.owlapi.model.OWLOntologyManager;
//import org.semanticweb.owlapi.reasoner.InferenceType;
//import org.semanticweb.owlapi.reasoner.OWLReasoner;
//import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
//
//import de.derivo.sparqldlapi.Query;
//import de.derivo.sparqldlapi.QueryEngine;
//import de.derivo.sparqldlapi.QueryResult;
//import de.derivo.sparqldlapi.exceptions.QueryEngineException;
//import de.derivo.sparqldlapi.exceptions.QueryParserException;
//
//public class QueryService {
//
//	OntologyServices_ ontologyServices = new OntologyServices_();
//	private static QueryEngine engine;
//	
//	public void query() {
//		try {
//			// Create an ontology manager
//			OWLOntologyManager manager = ontologyServices.getOntologyManager();
//
//			// Load the wine ontology from the web.
//			OWLOntology ont = ontologyServices.getCustomOntology();
//
//			// Create an instance of an OWL API reasoner (we use the OWL API built-in
//			// StructuralReasoner for the purpose of demonstration here)
//			StructuralReasonerFactory factory = new StructuralReasonerFactory();
//			OWLReasoner reasoner = factory.createReasoner(ont);
//			// Optionally let the reasoner compute the most relevant inferences in advance
//			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS);
//
//			// Create an instance of the SPARQL-DL query engine
//			engine = QueryEngine.create(manager, reasoner, true);
//
//			// Some queries which cover important basic language constructs of SPARQL-DL
//
//			// All white wines (all individuals of the class WhiteWine and sub classes
//			// thereof)
//			processQuery("SELECT * WHERE {\n"
//					+ "Type(?x, <http://www.di.unipi.it/~brogi/projects/owls2pnml/ontologies/bankontology.owl.xml#Description>)" + "}");
////
////			// The white wines (the individuals of WhiteWine but not of it's sub classes)
////			processQuery("SELECT * WHERE {\n"
////					+ "DirectType(?x, <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#RedTableWine>)" + "}");
////
////			// Is PinotBlanc a sub class of Wine?
////			processQuery("PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>\n" + "ASK {\n"
////					+ "SubClassOf(wine:PinotBlanc, wine:Wine)" + "}");
////
////			// The direct sub classes of FrenchWine
////			processQuery("PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>\n"
////					+ "SELECT ?x WHERE {\n" + "DirectSubClassOf(?x, wine:FrenchWine)" + "}");
////
////			// All individuals
////			processQuery("PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>\n" + "SELECT * WHERE {\n"
////					+ "Individual(?x)" + "}");
////
////			// All functional ObjectProperties
////			processQuery("PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>\n" + "SELECT * WHERE {\n"
////					+ "ObjectProperty(?x), " + "Functional(?x)" + "}");
////
////			// The strict sub classes of DryWhiteWine (sub classes with are not equivalent
////			// to DryWhiteWine)
////			processQuery("PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>\n"
////					+ "SELECT ?x WHERE {\n" + "StrictSubClassOf(?x, wine:DryWhiteWine)" + "}");
////
////			// All the grapes from which RedTableWines are made from (without duplicates)
////			processQuery("PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>\n"
////					+ "SELECT DISTINCT ?v WHERE {\n" + "Type(?i, wine:RedTableWine),\n"
////					+ "PropertyValue(?i, wine:madeFromGrape, ?v)" + "}");
//
//		} catch (UnsupportedOperationException exception) {
//			System.out.println("Unsupported reasoner operation.");
//		}
//	}
//
//	public static void processQuery(String q) {
//		try {
//			long startTime = System.currentTimeMillis();
//
//			// Create a query object from it's string representation
//			Query query = Query.create(q);
//
//			System.out.println("Excecute the query:");
//			System.out.println(q);
//			System.out.println("-------------------------------------------------");
//
//			// Execute the query and generate the result set
//			QueryResult result = engine.execute(query);
//
//			if (query.isAsk()) {
//				System.out.print("Result: ");
//				if (result.ask()) {
//					System.out.println("yes");
//				} else {
//					System.out.println("no");
//				}
//			} else {
//				if (!result.ask()) {
//					System.out.println("Query has no solution.\n");
//				} else {
//					System.out.println("Results:");
//					System.out.print(result);
//					System.out.println("-------------------------------------------------");
//					System.out.println("Size of result set: " + result.size());
//				}
//			}
//
//			System.out.println("-------------------------------------------------");
//			System.out.println("Finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + "s\n");
//		} catch (QueryParserException e) {
//			System.out.println("Query parser error: " + e);
//		} catch (QueryEngineException e) {
//			System.out.println("Query engine error: " + e);
//		}
//	}
//}