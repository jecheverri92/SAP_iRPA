package com.netw.util;

import java.io.IOException;
import java.util.Base64;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netw.models.PdfModel;

public  class PdfUtil {
	
	private final static String BLOQUEAR = "Bloquear";
	private final static String DESBLOQUEAR = "Desbloquear";
	private final static String ACTUALIZAR = "Actualizar";
	private final static String CAMPO_USUARIO = "Usuario";
	

	
	public static PdfModel readPdf(String fileBytes) {
		
		 String text = "";
		try {
			
			byte[] decodedBytes = Base64.getDecoder().decode(fileBytes);
			 //String file64 = Base64.getEncoder().encodeToString(file.getBytes());
			PDDocument document = PDDocument.load(decodedBytes);
		      //Instantiate PDFTextStripper class
		      PDFTextStripper pdfStripper = new PDFTextStripper();
		      //Retrieving text from PDF document
		      text = pdfStripper.getText(document);
		      //Closing the document
		      document.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		PdfModel pdf = getInfoPdf(text);
		pdf.setFilevalu64(fileBytes);
		return pdf;
	}
	
	public String getTipo(String texto) {
		
		
		
		return null;
	}
	
	public static PdfModel getInfoPdf(String texto) {
	
		PdfModel infoPdf = new PdfModel();
		texto = texto.replace("\n", "").replace("\r", "");
		
        String[] arrOfStr = texto.trim().split("\\.");
        
        System.out.println(arrOfStr[1]); 
        
        
        if(arrOfStr[0].contains(BLOQUEAR)) {
        	infoPdf.setAction("BLOQUEAR");
        }
        else if(arrOfStr[0].contains(DESBLOQUEAR)) {
        	infoPdf.setAction("DESBLOQUEAR");
        }
        else if(arrOfStr[0].contains(ACTUALIZAR)) {
        	infoPdf.setAction("ACTUALIZAR");
        } else {
        	infoPdf.setAction("Formato no valido");
        }
        
        if(arrOfStr[1].contains(CAMPO_USUARIO)) {
        	infoPdf.setUserchange(arrOfStr[1].split(":")[1].trim());
        }else {
        	infoPdf.setAction("Formato no valido");
        }
        
        if(infoPdf.getAction().equals("ACTUALIZAR")) {
        	  if(arrOfStr[2].contains("numeroMeses")) {
              	infoPdf.setMonthsuser(arrOfStr[2].split(":")[1].trim());
              }
        	  if(arrOfStr[3].contains("FechaAnterior")) {
                	infoPdf.setNumdate(arrOfStr[3].split(":")[1].trim());
                }
        }
        
        
        return infoPdf;
	}
	

}
