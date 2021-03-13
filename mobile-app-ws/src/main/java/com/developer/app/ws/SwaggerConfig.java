package com.developer.app.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	Contact contact = new Contact("Bruno Vieira", "http://www.developer.com", "correiodovieira@gmail.com");
	
	List<VendorExtension> vendorExtensions = new ArrayList<>();
	
	ApiInfo apiInfo = new ApiInfo(
			"Photo app RESTful Web Service documentation", 
			"This pages documents Photo app RESTful Web Service endpoints", 
			"1.0", 
			"http://www.developer.com/service.html", 
			contact, 
			"Apache 2.0", 
			"http://www.apache.org/licenses/LICENSE-2.0",
			vendorExtensions);

	public Docket apiDocket() {
		Docket socket = new Docket(DocumentationType.SWAGGER_2)
				.protocols(new HashSet<>(Arrays.asList("HTTP", "HTTPs")))
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.developer.app.ws"))
				.paths(PathSelectors.any())
				.build();
		return socket;
	}
}
