package com.mcs.owl.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import io.swagger.v3.oas.models.Operation;

public class ProcessService {
	
	
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	
	
	
	public OWLObjectProperty getObjectPropertyFromDomain(String objectName) {
		IRI paramObjectIRI = IRI.create(ontologyServices.getDomainObjectPropertyIRI() + objectName);
		Optional<OWLObjectProperty> objectProperties = ontologyServices.getObjectProperties().filter(object -> object.getIRI().equals(paramObjectIRI)).findAny();
		if(objectProperties.isPresent()) {
			return objectProperties.get();
		}
		System.out.println("Not Found property");
		return null;
	}
	
	public OWLDataProperty getDataPropertyFromProcess() {
		IRI parameterTypeIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
		Optional<OWLDataProperty> dataProperties = ontologyServices.getProcessOntology().dataPropertiesInSignature()
				.filter(object -> object.getIRI().equals(parameterTypeIRI)).findAny();
		if(dataProperties.isPresent()) {
			return dataProperties.get();
		}
		System.out.println("Not Found property");
		return null;
	}
	public OWLClass getAtomicProcessClass() {
		IRI atomicProcessClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#AtomicProcess");
		List<OWLClass> owlClassInProcess = ontologyServices.getProcessOntology().classesInSignature().collect(Collectors.toList());
	//	ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProcessClass = owlClassInProcess.stream().filter(cls -> cls.getIRI().equals(atomicProcessClassIRI)).findAny().get();
		return atomicProcessClass;
	}
	
	public OWLClass getInputClass() {
		IRI atomicProcessClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#Input");
		Stream<OWLClass> owlClassInProcess = ontologyServices.getProcessOntology().classesInSignature();
		//ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProcessClass = owlClassInProcess.filter(cls -> cls.getIRI().equals(atomicProcessClassIRI)).findAny().get();
		return atomicProcessClass;
	}
	
	public OWLIndividual addIndividual(Operation operation) {
		OWLIndividual methodIndividual =  ontologyServices.getDataFactory().getOWLNamedIndividual(ontologyServices.getOperationIdIRI("Process_"+operation.getOperationId()));
		OWLClassAssertionAxiom ax =  ontologyServices.getDataFactory().getOWLClassAssertionAxiom(getAtomicProcessClass(), methodIndividual);
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), ax);
		
		//comment
		OWLDatatype stringDatatype = ontologyServices.getDataFactory().getStringOWLDatatype();
		OWLLiteral commentLiteral =  ontologyServices.getDataFactory().getOWLLiteral(operation.getDescription(),stringDatatype);
		addRDFAxioms(OWLRDFVocabulary.RDFS_COMMENT,commentLiteral,ontologyServices.getOperationIdIRI(operation.getOperationId()));
		
		//label
		OWLLiteral labelLiteral =  ontologyServices.getDataFactory().getOWLLiteral(operation.getOperationId(),stringDatatype);
		addRDFAxioms(OWLRDFVocabulary.RDFS_LABEL,labelLiteral,ontologyServices.getOperationIdIRI(operation.getOperationId()));
		
		//param input
		OWLIndividual inputIndividal = addInputIndividual(operation,"latitude");
		addObjectPropertyAssertations(methodIndividual,inputIndividal);
		
		return methodIndividual;
	}


	public void addRDFAxioms(OWLRDFVocabulary voc , OWLLiteral literal, IRI inOdividualPropertyIRI) {
		 OWLAnnotation ann = ontologyServices.getDataFactory().getOWLAnnotation( ontologyServices.getDataFactory().getOWLAnnotationProperty(voc.getIRI()), literal);
		 OWLAxiom axiom = ontologyServices.getDataFactory().getOWLAnnotationAssertionAxiom(inOdividualPropertyIRI, ann);
		// OWLAnnotationAssertionAxiom  axiom =  ontologyServices.getDataFactory().getOWLAnnotationAssertionAxiom();
		 ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), axiom);
	}
	
	private OWLIndividual addInputIndividual(Operation operation, String parameterName) {
		OWLIndividual paramIndividual =  this.ontologyServices.getDataFactory().getOWLNamedIndividual(ontologyServices.getOperationIdIRI("Process_"+operation.getOperationId()+"_"+parameterName));
		OWLClassAssertionAxiom ax =  this.ontologyServices.getDataFactory().getOWLClassAssertionAxiom(getInputClass(), paramIndividual);
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), ax);
		
		OWLDatatype anyURIDatatype = this.ontologyServices.getDataFactory().getOWLDatatype(
				OWL2Datatype.XSD_ANY_URI.getIRI());
		OWLLiteral labelLiteral =  this.ontologyServices.getDataFactory().getOWLLiteral("http://www.daml.org/services/owl-s/1.2/Concepts.owl#FlightList",anyURIDatatype);
		OWLDataPropertyAssertionAxiom dataPropertyAssertion = ontologyServices.getDataFactory().getOWLDataPropertyAssertionAxiom(getDataPropertyFromProcess(), paramIndividual,
				labelLiteral);
		
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), dataPropertyAssertion);
		return paramIndividual;
		
	}
	public void addObjectPropertyAssertations(OWLIndividual methodIndividual, OWLIndividual inputIndividal) {   
		OWLAxiom axiom = ontologyServices.getDataFactory().getOWLObjectPropertyAssertionAxiom(ontologyServices.getObjectPropertyFromProcess(), methodIndividual,
				inputIndividal);
		
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(),axiom);
		
	}

	
}
