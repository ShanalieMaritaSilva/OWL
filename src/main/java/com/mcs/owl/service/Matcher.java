package com.mcs.owl.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;

import ru.avicomp.ontapi.DataFactory;
import ru.avicomp.ontapi.OntManagers;
import ru.avicomp.ontapi.OntologyManager;
import ru.avicomp.ontapi.OntologyModel;
import ru.avicomp.ontapi.internal.InternalObjectFactory;

public class Matcher {
	
	//EXACT
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	ProcessService processService = new ProcessService();
	
	public void checkForExactMatch(String keyword) {
		
		OntologyModel o = getOntology();
		String processPrefix = "PREFIX process:<http://www.daml.org/services/owl-s/1.2/Process.owl#> \n";
		String rdfPrefix = "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		String expPrefix = "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
		
		
		  try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory
		            .create(rdfPrefix + processPrefix + expPrefix+
		                    "SELECT ?process \n" + 
		                    "WHERE { \n" + 
//		                    "?param expression:refURI ?refUrl ; \n"+
//		                    "?param rdf:type process:Output. " + 
		              //       "?process process:hasOutput ?param." + 
		                    "?process rdf:type owl:NamedIndividual ; process:parameterValue ?v . FILTER regex(str(?v), \"o\", \"i\")" + 
		                    "}"), o.asGraphModel())) {
		        ResultSet res = qexec.execSelect();
		        while (res.hasNext()) {
		            System.out.println(res.next());
		        }
		    }

}
	public OntologyModel getOntology() {

	    // Get ONT-API manager:
	    OntologyManager ontManager = OntManagers.createONT();

	  //new QueryService().query();
	  	OWLOntology owlOntology = ontologyServices.getServiceTemplateOntology();
	    // Copy from OWL- to ONT-Manager.
	    // This will produce an OWL-ontology (Ontology) with a jena Graph inside:
	    OntologyModel ontOntology = ontManager.copyOntology(owlOntology, OntologyCopy.DEEP);

	    return ontOntology;
	}
//	private void gg() {
//		 // use pizza, since no example data provided in the question:
//	    IRI pizza = IRI.create("https://raw.githubusercontent.com/owlcs/ont-api/master/src/test/resources/ontapi/pizza.ttl");
//	    // get OWLOntologyManager instance from ONT-API
//	    OntologyManager manager = OntManagers.createONT();
//	    // as extended Jena model:
//	    OntModel model = (OntModel) manager.loadOntology(pizza).asGraphModel();
//
//	    // prepare query that looks like the original, but for pizza
//	    String txt = "SELECT DISTINCT ?source ?is_succeeded_by\n" +
//	            "WHERE {\n" +
//	            "    ?source rdfs:subClassOf ?restriction . \n" +
//	            "    ?restriction owl:onProperty :hasTopping . \n" +
//	            "    ?restriction owl:allValuesFrom  ?is_succeeded_by .\n" +
//	            "    FILTER (REGEX(STR(?source), 'Am'))\n" +
//	            "}";
//	    Query q = new Query();
//	    q.setPrefixMapping(model);
//	    q = QueryFactory.parse(q, txt, null, Syntax.defaultQuerySyntax);
//
//	    // from owlapi-parsers package:
//	    OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();
//	    // from ont-api (although it is a part of internal API, it is public):
//	    DataFactory iof = manager.getOWLDataFactory();
//
//	    // exec SPARQL query:
//	    try (QueryExecution exec = QueryExecutionFactory.create(q, model)) {
//	        ResultSet res = exec.execSelect();
//	        while (res.hasNext()) {
//	            QuerySolution qs = res.next();
//	            List<Resource> vars = Iter.asStream(qs.varNames()).map(qs::getResource).collect(Collectors.toList());
//	            if (vars.size() != 2)
//	                throw new IllegalStateException("For the specified query and valid OWL must not happen");
//	            // Resource (Jena) -> OntCE (ONT-API) -> ONTObject (ONT-API) -> OWLClassExpression (OWL-API)
//	            OWLClassExpression ex = iof.getOWLClass((IRI) vars.get(1).inModel(model).as(OntClass.class));
//	            // format: 'class local name' ||| 'superclass string in ManSyn'
//	            System.out.println(vars.get(0).getLocalName() + " ||| " + renderer.render(ex));
//	        }
//	    }
//	}
}
