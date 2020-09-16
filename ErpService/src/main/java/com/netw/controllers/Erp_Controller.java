

package com.netw.controllers;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.google.gson.Gson;
import com.netw.models.AccesToken;
import com.netw.models.PdfModel;
import com.netw.models.ResponseDto;
import com.netw.util.PdfUtil;
import com.netw.util.RestClient;


@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class Erp_Controller {
	
	
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private  RestClient restClient;
	
	
	//Servicio GET ERP generico el endpoint es /erpservice/(enpoint ERP)
	@CrossOrigin(origins = "*")
	@RequestMapping(method=RequestMethod.GET, value = {"/erpservice/**"})
	public  ResponseEntity<String> getErp(HttpServletRequest request, @RequestHeader HttpHeaders headers) throws UnsupportedEncodingException, HttpClientErrorException, MalformedURLException, NamingException  {	
		ResponseEntity<String> response = restClient.get(request,headers);
		return response;				
					
	};
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method=RequestMethod.GET, value = {"/getnoprocesados"})
	public  String noPorcesados(HttpServletRequest request, @RequestHeader HttpHeaders headers) throws UnsupportedEncodingException, HttpClientErrorException, MalformedURLException, NamingException  {	
		 try {
			 String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/changeuserdateSet?$filter=Action eq 'Npr'&$format=json";
			ResponseEntity<String> response = restClient.get(path,headers);
			 return response.getBody();
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		
		return null;
	};
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method=RequestMethod.POST, value = {"/uploadPdf"})
	public  ResponseEntity<String> uploadFile(HttpServletRequest request, @RequestHeader HttpHeaders headers, @RequestBody String body) throws HttpClientErrorException, NamingException   {	
		PdfModel pdfModel = null;
		//file is MultipartFile
		Gson g = new Gson(); 
		PdfModel pdfModel2 = g.fromJson(body, PdfModel.class); //Procesa el pdf para obtener la informacion necesaria 
		try {
			pdfModel = PdfUtil.readPdf(pdfModel2.getFilevalu64());
			String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV";
			ResponseEntity<String> response = restClient.get(path,headers);
			log.error(pdfModel.getAction());
			pdfModel2.setAction(pdfModel.getAction());
			pdfModel2.setUserchange(pdfModel.getUserchange());
			if(pdfModel.getAction().equals("ACTUALIZAR")) {
				pdfModel2.setMonthsuser(pdfModel.getMonthsuser());
				pdfModel2.setNumdate(pdfModel.getNumdate());
				actualizar(pdfModel.getUserchange(), pdfModel.getNumdate(), pdfModel.getMonthsuser(),headers);
			}else if(pdfModel.getAction().equals("BLOQUEAR")) {
				bloquear(pdfModel.getUserchange(),headers);
			}else if(pdfModel.getAction().equals("DESBLOQUEAR")) {
				desbloquear(pdfModel.getUserchange(),headers);
			};
			
			
			String json = g.toJson(pdfModel2);			
			headers.add("x-csrf-token", response.getHeaders().get("x-csrf-token").get(0));
			headers.add("Cookie", getCookiFinal(response.getHeaders().get("set-cookie").toString()));
			
			ResponseEntity<String> response2 = restClient.post("sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/changeuserdateSet", json, headers);
			if(response2.getStatusCode() == HttpStatus.CREATED) {
				ResponseDto bodyresponse = new ResponseDto();
				bodyresponse.setStatus("Exitoso");
				bodyresponse.setMensaje("Se ejecuto la accion " + pdfModel.getAction() + " sobre el usuario " + pdfModel.getUserchange());
				
				String responseMensaje = g.toJson(bodyresponse);
				HttpHeaders headers2 = new HttpHeaders();
				headers2.add("Authorization", "Basic c2ItNDNiMTViY2YtNDBkOS00ZWI2LWE3MTktZWEzZjdkMjRlMTBjIWI1NTgwNnxzYXBtbGlycGEtLWlycGF0cmlhbC0tdHJpYWwtLXVhYS1zZXJ2aWNlLWJyb2tlciFiMzA2MTA6SDFDTnFGTW92MUlRVFNEMFg5d3N1ZFFlSko0PQ==");
				RestTemplate cliente = new RestTemplate();
				String responseToken = restClient.tokenAuth(cliente,"https://fbfa4d1atrial.authentication.eu10.hana.ondemand.com/oauth/token?grant_type=client_credentials", "{}", headers2);
				log.error("??????????????" + responseToken);
				HttpHeaders headers3 = new HttpHeaders();
				AccesToken token = g.fromJson(responseToken, AccesToken.class);
				headers3.add("Authorization", "Bearer "+ token.getAccess_token() );
				headers3.add("irpa-trigger-token", "D6DmDv0j0giMegaTSt2d50bDB020K2WS");
				headers3.add("Content-Type", "application/json");
				String resApi = restClient.tokenAuth(cliente, "https://api.irpa-trial.cfapps.eu10.hana.ondemand.com/apitrigger/v1/triggers/ec8c84dd-43b7-4a96-9b18-2c86f8e0dc4a/run", "{}", headers3);
				log.error("**************" + resApi);
				return ResponseEntity
			            .status(HttpStatus.CREATED).body(responseMensaje)
			            ;
			}
			return response2;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;				
					
	};
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method=RequestMethod.POST, value = {"/procesarPdf"})
	public  ResponseEntity<String> procesarFile(HttpServletRequest request, @RequestHeader HttpHeaders headers, @RequestBody String body) throws HttpClientErrorException, NamingException   {	
		try {
			
			String path = "sap/opu/odata/sap/ZGW_BASIS_USERS_SRV";
			log.error("-----------Body------------------");
			log.error(body);
			log.error("-----------------------------");
			headers.add("X-CSRF-Token", "Fetch");
			ResponseEntity<String> response = restClient.get(path,headers); // El header debe contener X-CSRF-Token: Fetch para poder obtener el Token
			headers.remove("X-CSRF-Token","Fetch");
			headers.add("x-csrf-token", response.getHeaders().get("x-csrf-token").get(0)); // alma
			headers.add("Cookie", getCookiFinal(response.getHeaders().get("set-cookie").toString()));
			log.error("-----------Headers------------------");
			log.error(headers.toString());
			log.error("-----------------------------");
			ResponseEntity<String> response2 = restClient.post("sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/clasificarArchivosSet", body, headers);
			
			
			if(response2.getStatusCode() == HttpStatus.CREATED) {
				ResponseDto bodyresponse = new ResponseDto();
				bodyresponse.setStatus("Exitoso");
				bodyresponse.setMensaje("El Archivo fue procesado Exitosamente");
				
				return ResponseEntity
			            .status(HttpStatus.CREATED).body(bodyresponse.toString())
			            ;
			}
			return response2;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;				
					
	};
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method=RequestMethod.POST, value = "/erpservice/**")
	public  ResponseEntity<String> postErp(HttpServletRequest request, @RequestBody String body, @RequestHeader HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, NamingException, UnsupportedEncodingException {
		
		log.error(headers.toString());
		ResponseEntity<String> response = restClient.post(request, body, headers);
		log.error(response.getBody().toString());
		return response;			
	
	}
	
	public String getCookiFinal(String Cookie){
		String[] stringCookie = Cookie.split(";");
		String[] session = stringCookie[3].split(",");
		String cookieFinal = stringCookie[0]+";" + stringCookie[1]+";" + stringCookie[2]+";"+session[1];
        return cookieFinal;
    }
	
	
	public String bloquear( String usuario,  HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, UnsupportedEncodingException, NamingException {
		String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/usuariosBloquearSet(UserErp='" + usuario
				+ "')?&$format=json";
		ResponseEntity<String> response = restClient.get(path,headers);
		log.error(response.getBody());
		return null;
	}
	
	
	
	public String desbloquear(String usuario,  HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, UnsupportedEncodingException, NamingException {
		String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/obtenerUsuariosBlockSet(Bname='" + usuario
				+ "')?&$format=json";
		
		ResponseEntity<String> response = restClient.get(path,headers);
		log.error(response.getBody());
		return null;
	}

	public String actualizar(String usuario,
			 String fecha,
			 String month,  HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, UnsupportedEncodingException, NamingException {
		String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/user_updateSet?$filter=Months eq " + month
				+ " and Username eq '" + usuario + "' and Olddate eq '" + fecha + "' &$format=json";
		ResponseEntity<String> response = restClient.get(path,headers);
		log.error(response.getBody());
		return null;
	}
	
	
}
