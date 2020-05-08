package com.mcs.owl.service;

import java.util.List;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;

import io.swagger.v3.oas.models.Operation;

public class ProfileService {
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();

	public OWLIndividual addProfileIndividual(Operation operation, OWLIndividual agentIndi) {
		return ontologyServices.addIndividualToObjectProperty(
				ontologyServices.getProfileOperationIdIRI("Profile_" + operation.getOperationId()+"_Agent"), agentIndi,
				ontologyServices.getServiceTemplateOntology(),
				ontologyServices.getPresentObjectPropertyFromProcess());

	}

	public void addServiceClass(Operation operation, OWLIndividual profileIndividual) {

		OWLClass serviceClass = ontologyServices.addSubClass(
				ontologyServices.getDataFactory().getOWLClass(
						ontologyServices.getProfileOperationIdIRI(operation.getOperationId() + "_Service")),
				getProfileClass(), ontologyServices.getServiceProfileTemplateOntology());

		ontologyServices.addIndividualToClass(profileIndividual, serviceClass,
				ontologyServices.getServiceProfileTemplateOntology());

	}

	private OWLClass getProfileClass() {
		IRI atomicProfileClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Profile.owl#Profile");
		List<OWLClass> owlClassInProcess = ontologyServices.getProfileOntology().classesInSignature()
				.collect(Collectors.toList());
		// ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProfileClass = owlClassInProcess.stream()
				.filter(cls -> cls.getIRI().equals(atomicProfileClassIRI)).findAny().get();
		return atomicProfileClass;
	}

//
//	public void addSubClass(Operation operation, OWLIndividual processIndi)
//			throws OWLOntologyCreationException, OWLOntologyStorageException {
//
//		ontologyServices.addSubClass(
//				ontologyServices.getDataFactory().getOWLClass(
//						ontologyServices.getProfileOperationIdIRI(operation.getOperationId() + "_Service")),
//				getProfileClass(), ontologyServices.getServiceProfileTemplateOntology());
//
////		
////	    OWLClass profileClass = getProfileClass();
////	    OWLClass subProfileClass = 
////	    OWLAxiom axiom = ontologyServices.getDataFactory().getOWLSubClassOfAxiom( subProfileClass,profileClass);
////	    ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProfileTemplateOntology(),axiom);
////	    addProfileIndividual(subProfileClass, operation,processIndi);
//	}
	public void addProcessIndividualProfileIndividual(OWLIndividual profileIndividual, OWLIndividual processIndi) {

		OWLAxiom axiom = ontologyServices.getDataFactory().getOWLObjectPropertyAssertionAxiom(
				ontologyServices.getHasInputObjectPropertyFromProcess(), profileIndividual, processIndi);

		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProfileTemplateOntology(), axiom);
	}

}
