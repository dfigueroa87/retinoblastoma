package ar.com.retinoblastoma.model.processing;

import java.util.ArrayList;
import java.util.List;


public class HistogramHSV {
	private List<ColorHSV> colors;
	private int total;
	
	public HistogramHSV(){
		colors = new ArrayList<ColorHSV>();
		total=0;
	}
	
	public boolean addColor(ColorHSV color){
		if(!colors.contains(color)){
			colors.add(color);
			return true;
		}
		return false;
	}
	
	public boolean removeColor(ColorHSV color){
		return colors.remove(color);
	}

	public List<ColorHSV> getColors() {
		return colors;
	}

	public void setColors(List<ColorHSV> colors) {
		this.colors = colors;
	}
	
	public void calculate(Double h,Double s,Double v){
		total++;
		boolean found=false;
		for(int i=0; i< colors.size() && !found; i++){
			found=false;
			ColorHSV color= colors.get(i);
			if(color.isColor(h, s, v)){
				color.addOcurrence();
				color.setPercentage(total);
				found=true;
			}else{
				color.setPercentage(total);
			}
			
		}
	}

	public int getTotal() {
		return total;
	}
		
	

}
