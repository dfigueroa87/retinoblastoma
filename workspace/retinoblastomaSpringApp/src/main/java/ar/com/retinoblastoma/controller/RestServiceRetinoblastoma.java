package ar.com.retinoblastoma.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import ar.com.retinoblastoma.model.detection.DetectionManager;
import ar.com.retinoblastoma.model.detection.DetectionManagerImpl;
import ar.com.retinoblastoma.utils.Utils;

import javafx.scene.image.Image;

@Path("/imagen")
public class RestServiceRetinoblastoma {
	
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private Random  rnd= new Random();
	private DetectionManager detMan = new DetectionManagerImpl();
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	@POST
	@Path("/upload_image")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public DTOJson receiveJSON(DTOJson json) throws IOException {
		String fileName= randomString(8);
		File file=convertFile(json.getFile(), fileName);
		String path;
		if(file!=null){
			path = file.getAbsolutePath();
			Mat detectionMat = detMan.detect(Highgui.imread(path));
			String pathResult = path.substring(0, path.length()-4)+"1.jpg";
			Highgui.imwrite(pathResult, detectionMat);
			File fileResult = new File(pathResult);
			DTOJson result = new DTOJson(convertFileToString(fileResult));
			return result;
		}	
		
		return null;
	}

	// Convert a Base64 string and create a file
	private File convertFile(String file_string, String file_name) throws IOException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decodedByteArray = decoder.decode(file_string);//
		File file = new File(file_name);
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(decodedByteArray);
		fop.flush();
		fop.close();
		return file;
	}
	
	String randomString( int len ){
		   StringBuilder sb = new StringBuilder( len );
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		   return sb.toString() + ".jpg";
		}
	
	 private String convertFileToString(File file) throws IOException{
	        byte[] bytes = Files.readAllBytes(file.toPath());
	        Base64.Encoder encoder = Base64.getEncoder();
	        String b = encoder.encodeToString(bytes);	        
	        return b;        
	    }
}
