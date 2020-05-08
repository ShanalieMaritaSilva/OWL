package com.mcs.owl.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class SwaggerParser {

	public OpenAPI Parser() {
		
		return new OpenAPIV3Parser().read("C:\\Users\\shana\\Documents\\MCS\\Project\\OWL-S\\Swagger-master\\Swagger-master\\LH_public_API_swagger_2_0.json");
		
	}
}
