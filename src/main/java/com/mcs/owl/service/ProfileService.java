package com.mcs.owl.service;

import java.util.List;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;

public class ProfileService {
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();

	public void addProfileIndividual(OWLClass subProfileClass,Operation operation, OWLIndividual processIndi) {
		OWLIndividual individual =  ontologyServices.getDataFactory().getOWLNamedIndividual(ontologyServices.getProfileOperationIdIRI("Profile_"+operation.getOperationId()));
		OWLClassAssertionAxiom ax =  ontologyServices.getDataFactory().getOWLClassAssertionAxiom(subProfileClass, individual);
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProfileTemplateOntology(), ax);
		
		OWLAxiom axiom = ontologyServices.getDataFactory().getOWLObjectPropertyAssertionAxiom(ontologyServices.getObjectPropertyFromProcess(), individual,
				processIndi);
		
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProfileTemplateOntology(),axiom);
	}
	
	public void addSubClass(Operation operation, OWLIndividual processIndi) throws OWLOntologyCreationException, OWLOntologyStorageException  {
		
	    OWLClass profileClass = getProfileClass();
	    OWLClass subProfileClass = ontologyServices.getDataFactory().getOWLClass(ontologyServices.getProfileOperationIdIRI(operation.getOperationId()+"_Service"));
	    OWLAxiom axiom = ontologyServices.getDataFactory().getOWLSubClassOfAxiom( subProfileClass,profileClass);
	    ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProfileTemplateOntology(),axiom);
	    addProfileIndividual(subProfileClass, operation,processIndi);
	}
	private OWLClass getProfileClass() {
		IRI atomicProfileClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Profile.owl#Profile");
		List<OWLClass> owlClassInProcess = ontologyServices.getProfileOntology().classesInSignature().collect(Collectors.toList());
	//	ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProfileClass = owlClassInProcess.stream().filter(cls -> cls.getIRI().equals(atomicProfileClassIRI)).findAny().get();
		return atomicProfileClass;
	}


	
	
}
