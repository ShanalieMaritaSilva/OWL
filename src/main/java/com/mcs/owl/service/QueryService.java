//package com.mcs.owl.service;
//
//import org.semanticweb.owlapi.model.OWLOntology;
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
//	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
//	private static QueryEngine engine;
//	
//	public QueryResult query() {
//		try {
//			// Create an ontology manager
//			OWLOntologyManager manager = ontologyServices.getOntologyManager();
//
//			// Load the wine ontology from the web.
//			OWLOntology ont = ontologyServices.getServiceTemplateOntology();
//
//			// Create an instance of an OWL API reasoner (we use the OWL API built-in
//			// StructuralReasoner for the purpose of demonstration here)
//			StructuralReasonerFactory factory = new StructuralReasonerFactory();
//			OWLReasoner reasoner = factory.createReasoner(ont);
//			// Optionally let the reasoner compute the most relevant inferences in advance
//			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS, InferenceType.DATA_PROPERTY_ASSERTIONS,InferenceType.DIFFERENT_INDIVIDUALS);
//
//			// Create an instance of the SPARQL-DL query engine
//			engine = QueryEngine.create(manager, reasoner, true);
//					 
////		  return  processQuery(
////		                "PREFIX process: <http://www.daml.org/services/owl-s/1.2/Process.owl#>\n" +
////		                "PREFIX expression: <http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#>\n" +
////						"SELECT DISTINCT ?pro ?param WHERE { PropertyValue(?pro, process:hasOutput, ?param) }"
////					);
//		  
//		  return  processQuery(
//	                "PREFIX process: <http://www.daml.org/services/owl-s/1.2/Process.owl#>\n" +
//	                "PREFIX expression: <http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#>\n" +
//					"SELECT DISTINCT ?pro ?param WHERE { PropertyValue(?param, process:parameterType , ?pro) }"
//				);
//		
//
//
//		} catch (UnsupportedOperationException exception) {
//			System.out.println("Unsupported reasoner operation.");
//		}
//		return null;
//	}
//
//	public QueryResult processQuery(String q) {
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
//			return result;
//		} catch (QueryParserException e) {
//			System.out.println("Query parser error: " + e);
//		} catch (QueryEngineException e) {
//			System.out.println("Query engine error: " + e);
//		}
//		return null;
//	}
//}