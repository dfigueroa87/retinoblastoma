/**
 * 
 */
package application;

import javafx.scene.image.Image;

/**
 * @author David
 *
 */
public abstract class Detection {
	
	public Detection() {
		
	}
	
	public abstract Image draw(Image im);
	
	public abstract int getX();
	public abstract int getY();
	public abstract int getWidth();
	public abstract int getHeight();

}
