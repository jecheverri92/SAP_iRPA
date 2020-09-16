package com.netw.util;


import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;


@Controller
public class RestClient {

  private static RestTemplate rest;
  private HttpHeaders headers;
  private HttpStatus status;
  private static String sesionid;
  
  private final String ON_PREMISE_PROXY = "OnPremise";

  
  Logger log = LoggerFactory.getLogger(getClass());
  
  private  String destinationLocationID;
  

	

  public RestClient() {
	  
	  //RestClient.rest = new RestTemplate();
	  // Necesario para consumir Destination
    if(RestClient.rest == null) {
    	setProxy(ON_PREMISE_PROXY);
    }
    
  }

  public ResponseEntity<String> get(HttpServletRequest request,  HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, NamingException, UnsupportedEncodingException {
	  String endpoint = request.getServletPath().replace("/erpservice","");
		log.error(request.getQueryString());
		headers.remove("Accept-Encoding"); // Remueve el header, de tipo de codificacion por defecto queda en gzip y causa problemas
		if(request.getQueryString() != null) {
			endpoint = endpoint + "?"+ request.getQueryString();
			endpoint = java.net.URLDecoder.decode(endpoint, StandardCharsets.UTF_8.name());
		}
		
		log.error(endpoint);
		 
	  try {
		  Context ctx = new InitialContext();
		  TenantContext tenantctx = (TenantContext) ctx.lookup("java:comp/env/TenantContext");

		  URL url = new URL(firmaDigital("ComfamaIRPA")+ endpoint);
		  headers.add("SAP-Connectivity-ConsumerAccount", tenantctx.getTenant().getAccount().getId());
		  headers.add("SAP-Connectivity-SCC-Location_ID", this.destinationLocationID);
		  HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		    log.error(requestEntity.toString());
		    log.error(url.toString());
		    ResponseEntity<String> responseEntity = rest.exchange(url.toString(), HttpMethod.GET, requestEntity, String.class);
		    log.info(responseEntity.toString());
		    this.setStatus(responseEntity.getStatusCode());
		    return responseEntity;
		} catch (HttpStatusCodeException exception) {
		    int statusCode = exception.getStatusCode().value();
		    log.info(Integer.toString(statusCode));
		    return ResponseEntity
		            .status(exception.getStatusCode()).body(exception.toString())
		            ;
		}
 
    
  }
  
  
  public ResponseEntity<String> get(String endpoint,  HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, NamingException, UnsupportedEncodingException {
		log.error(endpoint);
	  try {
		  headers.remove("Accept-Encoding"); // Remueve el header, de tipo de codificacion por defecto queda en gzip y causa problemas
		  Context ctx = new InitialContext();
		  TenantContext tenantctx = (TenantContext) ctx.lookup("java:comp/env/TenantContext");

		  URL url = new URL(firmaDigital("ComfamaIRPA")+ endpoint);
		  headers.add("SAP-Connectivity-ConsumerAccount", tenantctx.getTenant().getAccount().getId());
		  headers.add("SAP-Connectivity-SCC-Location_ID", this.destinationLocationID);
		  HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		    log.error(requestEntity.toString());
		    log.error(url.toString());
		    ResponseEntity<String> responseEntity = rest.exchange(url.toString(), HttpMethod.GET, requestEntity, String.class);
		    log.info(responseEntity.toString());
		    this.setStatus(responseEntity.getStatusCode());
		    return responseEntity;
		} catch (HttpStatusCodeException exception) {
		    int statusCode = exception.getStatusCode().value();
		    log.info(Integer.toString(statusCode));
		    return ResponseEntity
		            .status(exception.getStatusCode()).body(exception.toString())
		            ;
		}
 
    
  }

  public ResponseEntity<String> post(HttpServletRequest request, String json, HttpHeaders headers)  throws HttpClientErrorException, NamingException, MalformedURLException, UnsupportedEncodingException {   
	  String endpoint = request.getServletPath().replace("/erpservice","");
		headers.remove("Accept-Encoding"); // Remueve el header, de tipo de codificacion por defecto queda en gzip y causa problemas
		if(request.getQueryString() != null) {
			endpoint = endpoint + "?"+ request.getQueryString();
			endpoint = java.net.URLDecoder.decode(endpoint, StandardCharsets.UTF_8.name());
		}
		
	  RestClient.sesionid = request.getSession().toString();
	  try {
		  Context ctx = new InitialContext();
		  TenantContext tenantctx = (TenantContext) ctx.lookup("java:comp/env/TenantContext");

		  URL url = new URL(firmaDigital("ComfamaIRPA")+ endpoint);
		  headers.add("SAP-Connectivity-ConsumerAccount", tenantctx.getTenant().getAccount().getId());
		  headers.add("SAP-Connectivity-SCC-Location_ID",  this.destinationLocationID);
		  HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
		  log.error(requestEntity.toString());
		  log.error("********************************************");
		  log.error(url.toString());
		    ResponseEntity<String> responseEntity = rest.exchange(url.toString(), HttpMethod.POST, requestEntity, String.class);
		    this.setStatus(responseEntity.getStatusCode());
		    return responseEntity;
	  }
	  
	  catch (HttpStatusCodeException exception) {
		    int statusCode = exception.getStatusCode().value();
		    log.info(Integer.toString(statusCode));
		    return ResponseEntity
		            .status(exception.getStatusCode()).body(exception.toString())
		            ;
		}
  
  }
  
  
  public ResponseEntity<String> post(String endpoint, String json, HttpHeaders headers)  throws HttpClientErrorException, NamingException, MalformedURLException, UnsupportedEncodingException {   
		headers.remove("Accept-Encoding"); // Remueve el header, de tipo de codificacion por defecto queda en gzip y causa problemas
	
	  try {
		  Context ctx = new InitialContext();
		  TenantContext tenantctx = (TenantContext) ctx.lookup("java:comp/env/TenantContext");

		  URL url = new URL(firmaDigital("ComfamaIRPA")+ endpoint);
		  headers.add("SAP-Connectivity-ConsumerAccount", tenantctx.getTenant().getAccount().getId());
		  headers.add("SAP-Connectivity-SCC-Location_ID",  this.destinationLocationID);
		  HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
		  log.error(requestEntity.toString());
		  log.error("********************************************");
		  log.error(url.toString());
		    ResponseEntity<String> responseEntity = rest.exchange(url.toString(), HttpMethod.POST, requestEntity, String.class);
		    this.setStatus(responseEntity.getStatusCode());
		    return responseEntity;
	  }
	  
	  catch (HttpStatusCodeException exception) {
		    int statusCode = exception.getStatusCode().value();
		    log.info(Integer.toString(statusCode));
		    return ResponseEntity
		            .status(exception.getStatusCode()).body(exception.toString())
		            ;
		}
  
  }
  
  

  public ResponseEntity<String> post2(String endpoint, String json, HttpHeaders headers)  throws HttpClientErrorException, NamingException, MalformedURLException, UnsupportedEncodingException {   
		headers.remove("Accept-Encoding"); // Remueve el header, de tipo de codificacion por defecto queda en gzip y causa problemas
		RestClient.rest = new RestTemplate();
		try {
		  Context ctx = new InitialContext();
		  TenantContext tenantctx = (TenantContext) ctx.lookup("java:comp/env/TenantContext");

		  URL url = new URL(firmaDigital("IRPA")+ endpoint);
		  //headers.add("SAP-Connectivity-ConsumerAccount", tenantctx.getTenant().getAccount().getId());
		  //headers.add("SAP-Connectivity-SCC-Location_ID",  this.destinationLocationID);
		  HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
		  log.error(requestEntity.toString());
		  log.error("********************************************");
		  log.error(url.toString());
		    ResponseEntity<String> responseEntity = rest.exchange(url.toString(), HttpMethod.POST, requestEntity, String.class);
		    this.setStatus(responseEntity.getStatusCode());
		    return responseEntity;
	  }
	  
	  catch (HttpStatusCodeException exception) {
		    int statusCode = exception.getStatusCode().value();
		    log.info(Integer.toString(statusCode));
		    return ResponseEntity
		            .status(exception.getStatusCode()).body(exception.toString())
		            ;
		}
  
  }
  
  
  
  

  public String tokenAuth(RestTemplate cliente, String endpoint, String json, HttpHeaders headers)  throws HttpClientErrorException, NamingException, MalformedURLException, UnsupportedEncodingException {   
		
	  try {
		 
		  URL url = new URL(endpoint);
		  HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
		  
		  log.error("URL  ¿¿¿¿¿¿¿"+ url.toString());
		  log.error("REQUEST  ¿¿¿¿¿¿¿¿¿¿¿"+ requestEntity);
		    ResponseEntity<String> responseEntity = cliente.exchange(url.toString(), HttpMethod.POST, requestEntity, String.class);
		    log.error("¿¿¿¿¿¿¿¿¿¿¿"+ responseEntity.getStatusCode().toString());
		    this.setStatus(responseEntity.getStatusCode());
		    return responseEntity.getBody();
	  }
	  
	  catch (HttpStatusCodeException exception) {
		    int statusCode = exception.getStatusCode().value();
		    log.error(Integer.toString(statusCode));
		    log.error("EXEPCION **** " + exception.getResponseBodyAsString());
		    return null;
		    }
  
  }
  
	public String firmaDigital(String destinationName) {
		try {
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");
		
		} catch (Exception e) {
			// connectivity operation failed
			String errorMessage = "1 Connectivity operation failed with reason:" + e.getMessage() + ". see "
					+ "logs for details. HInt: Make sure to have an HTTP proxy configures in your"
					+ "local enviroment in case your environment" + "an HTTP proxy for the outbound Internet"
					+ "communication";
			return errorMessage;
		}

		try {

			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");
			DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);

			if (destConfiguration == null) {

				return "Destination %s is not found. Hint: Make sure to have the destination configured.....";
			}

			String value = destConfiguration.getProperty("URL");
			this.destinationLocationID = destConfiguration.getProperty("CloudConnectorLocationId");
			log.error(this.destinationLocationID);
			
			//setProxy();

			return value;

		} catch (Exception e) {
			// connectivity operation failed
			String errorMessage = "2 Connectivity operation failed with reason:" + e.getMessage() + ". see "
					+ "logs for details. HInt: Make sure to have an HTTP proxy configures in your"
					+ "local enviroment in case your environment" + "an HTTP proxy for the outbound Internet"
					+ "communication";
			return errorMessage;
		}

	}
	
	private Proxy getProxy(String proxyType) {
		String proxyHost = null;
		int proxyPort;

		if (proxyType.equals(ON_PREMISE_PROXY)) {
			
			// get proxy for on-premise destinations
			proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
			proxyPort = Integer.parseInt(System.getenv("HC_OP_HTTP_PROXY_PORT"));
			log.error(proxyHost);
			log.error(String.valueOf(proxyPort));

		} else {
			// Get proxy for internet destinationms
			proxyHost = System.getProperty("http.proxyHost");
			log.error(System.getProperty("http.proxyPort"));
			proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));

		}
		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

	}
	

	public void setProxy(String tipo) {
		 SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		  Proxy proxy = getProxy(tipo);
		   requestFactory.setProxy(proxy);
		RestClient.rest = new RestTemplate(requestFactory);
	}
	
	

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  } 
  
  public void setHeaders(HttpHeaders headers) {
	  this.headers = headers;
  }
}