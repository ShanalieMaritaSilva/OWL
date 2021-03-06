package com.mcs.owl.service;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.mcs.owl.utils.SwaggerParser;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

public class ServicesService {

	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	ProcessService processService = new ProcessService();
	ProfileService profileService = new ProfileService();
	GroundingService groundingService = new GroundingService();

	public void addAgentIndividual(String pathUrl, PathItem pathItem) {

		IRI agentIRI = ontologyServices.getServiceOperationIdIRI(pathItem.getGet().getOperationId() + "_Agent");

		OWLIndividual agentIndividual = ontologyServices.addIndividualToClass(agentIRI, getServiceClass(),
				ontologyServices.getServiceTemplateOntology());
		
		addWebUrl(pathUrl, agentIndividual);

		OWLIndividual profileIndividual = profileService.addProfileIndividual(pathItem.getGet(), agentIndividual);
		
		//add Data Properties
		profileService.addDescriptionsAndServiceName(profileIndividual,pathItem.getGet());
		
		profileService.addServiceClass(pathItem.getGet(), profileIndividual);
		
		OWLIndividual mainProcessIndi = processService.addProcessToAgentIndividual(agentIndividual,pathItem.getGet().getOperationId());
		
		groundingService.addGroundingToAgentIndividual(agentIndividual,pathItem.getGet().getOperationId());

		for (Parameter parm : pathItem.getGet().getParameters()) {
			String pathName = parm.getName().replace("Code", "");
			
			if(pathName.equalsIgnoreCase("Accept") || pathName.equalsIgnoreCase("Limit") || pathName.equalsIgnoreCase("Offset")) {
				continue;
			}
			OWLEntity paramRefInDomin = new SwaggerParser().getResourceFromDomain(pathName);
			
			if(paramRefInDomin == null) {
				pathName = pathName.substring(0, 1).toUpperCase() + pathName.substring(1);
				paramRefInDomin = new SwaggerParser().getResourceFromDomain(pathName);
			}
			if (paramRefInDomin != null) {
				OWLIndividual processIndi = processService.addInputIndividual(pathItem.getGet(), parm, paramRefInDomin);
				profileService.addProcessIndividualProfileIndividual(profileIndividual, processIndi,mainProcessIndi);
			
			}

		}

		String responseRef = getResponse(pathItem);
		String response = responseRef.replace("Response", "");
		OWLEntity paramRefInDomin = new SwaggerParser().getResourceFromDomain(response);
		if (paramRefInDomin != null) {
			processService.addOutputIndividual(paramRefInDomin,responseRef,profileIndividual,mainProcessIndi);
		}

		
	}

	private String getResponse(PathItem pathItem) {
		ApiResponses responses = pathItem.getGet().getResponses();

		String responseRef = "";
		for (Entry<String, ApiResponse> entry : responses.entrySet()) {

			Content apiResponse = entry.getValue().getContent();

			for (Entry<String, MediaType> resp : apiResponse.entrySet()) {
				responseRef = resp.getValue().getSchema().get$ref();
				System.out.println(resp.getValue().getSchema().get$ref());
			}
		}
		if (!responseRef.isEmpty()) {
			responseRef = responseRef.replace("#/components/schemas/", "");
		}
		return responseRef;
	}

	private OWLClass getServiceClass() {
		IRI serviceClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Service.owl#Service");
		Stream<OWLClass> owlClassInProcess = ontologyServices.getServiceOntology().classesInSignature();
		return owlClassInProcess.filter(cls -> cls.getIRI().equals(serviceClassIRI)).findAny().get();
	}

	private void addWebUrl(String url, OWLIndividual agentProfile) {

		OWLDatatype datatype = this.ontologyServices.getDataFactory().getOWLDatatype(OWL2Datatype.XSD_ANY_URI.getIRI());
		OWLLiteral parmValueLiteral = this.ontologyServices.getDataFactory().getOWLLiteral(url, datatype);
		OWLDataPropertyAssertionAxiom dataPropertyAssertion = ontologyServices.getDataFactory()
				.getOWLDataPropertyAssertionAxiom(getDataProperty(), agentProfile, parmValueLiteral);

		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceTemplateOntology(),
				dataPropertyAssertion);
	}

	public OWLDataProperty getDataProperty() {
		IRI parameterTypeIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/ActorDefault.owl#webURL");
		Optional<OWLDataProperty> dataProperties = ontologyServices.getActorDefaultOntology()
				.dataPropertiesInSignature().filter(object -> object.getIRI().equals(parameterTypeIRI)).findAny();
		if (dataProperties.isPresent()) {
			return dataProperties.get();
		}
		System.out.println("Not Found property" + "- getDataProperty");
		return null;
	}
}
