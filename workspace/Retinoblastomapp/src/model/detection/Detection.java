/**
 * 
 */
package model.detection;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * @author David
 *
 */
public abstract class Detection {
	
	public Detection() {
		
	}
	
	public abstract void draw(Mat im,Scalar sc);
	
	public abstract int getX();
	public abstract int getY();
	public abstract int getWidth();
	public abstract int getHeight();

}
