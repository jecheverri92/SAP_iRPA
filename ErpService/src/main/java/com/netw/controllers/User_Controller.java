package com.netw.controllers;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.netw.util.RestClient;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@EnableAsync
@RequestMapping("/user")
public class User_Controller {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private  RestClient restClient;
	
	String resp;

	@GetMapping("/bloquear")
	public String bloquear(@RequestParam(name = "usuario", required = true) String usuario, @RequestHeader HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, UnsupportedEncodingException, NamingException {
		String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/usuariosBloquearSet(UserErp=" + usuario
				+ ")?&$format=json";
		ResponseEntity<String> response = restClient.get(path,headers);
		return response.getBody();
	}

	@GetMapping("/desbloquear")
	public String desbloquear(@RequestParam(name = "usuario", required = true) String usuario, @RequestHeader HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, UnsupportedEncodingException, NamingException {
		String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/obtenerUsuariosBlockSet(Bname=" + usuario
				+ ")?&$format=json";
		ResponseEntity<String> response = restClient.get(path,headers);
		return response.getBody();
	}

	@GetMapping("/actualizar")
	public String actualizar(@RequestParam(name = "usuario", required = true) String usuario,
			@RequestParam(name = "fecha", required = true) String fecha,
			@RequestParam(name = "month", required = true) String month,
			@RequestHeader HttpHeaders headers) throws HttpClientErrorException, MalformedURLException, UnsupportedEncodingException, NamingException {
		String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/user_updateSet?$filter=Months eq " + month
				+ " and Username eq " + usuario + " and Olddate eq " + fecha + " &$format=json";
		ResponseEntity<String> response = restClient.get(path,headers);
		return response.getBody();
	}

	@GetMapping("/obtenerUsuarios")
	public String obtenerUsuarios() {
		String path = "/sap/opu/odata/sap/ZGW_BASIS_USERS_SRV/obtenerAllUserSet";
		return null;
	}

}
