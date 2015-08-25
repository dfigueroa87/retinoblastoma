package ar.com.retinoblastoma.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class Utils {
	
	public static Image ConvertMatToImage(Mat mat) {
		// Mat to AWT.Image 
		java.awt.Image img = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_INT_ARGB);
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", mat, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		try {
			img = ImageIO.read(in);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// awt to javafx.Image
		BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics g = bufferedImage.createGraphics();
	    g.drawImage(img, 0, 0, null);
	    g.dispose();
	    img = bufferedImage;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try {
	    	ImageIO.write((RenderedImage) img, "png", out);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    in = new ByteArrayInputStream(out.toByteArray());
	    return new javafx.scene.image.Image(in);
	}

}
