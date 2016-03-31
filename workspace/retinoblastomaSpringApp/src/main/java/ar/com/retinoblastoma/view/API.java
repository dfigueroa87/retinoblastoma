package ar.com.retinoblastoma.view;

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

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import ar.com.retinoblastoma.controller.APIController;
import ar.com.retinoblastoma.controller.DTOJsonRequest;
import ar.com.retinoblastoma.controller.DTOJsonResponse;
import ar.com.retinoblastoma.model.detection.DetectionManager;
import ar.com.retinoblastoma.model.detection.DetectionManagerImpl;

@Path("/imagen")
public class API {

  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  private APIController controller = new APIController();


  @POST
  @Path("/upload_image")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public DTOJsonResponse receiveJSON(DTOJsonRequest json) throws IOException {
    DTOJsonResponse result = controller.calcularCriterio2(json);
    return result;    
  }  
  
   

  

  

  
}
