package ar.com.retinoblastoma.model.processing;

public class ColorHSV {
	
	private String name;
	
	private Rank h;
	private Rank v;
	private Rank s;
	
	private int occurrence;
	
	private Double percentage;
	
	public ColorHSV(String name,Rank h, Rank s, Rank v){
		this.name=name;
		this.h=h;
		this.s=s;
		this.v=v;
		this.occurrence=0;
	}

	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}


	public int getOccurrence() {
		return occurrence;
	}



	public boolean isColor(Double h,Double s,Double v){
		if ( this.h.isRank(h) && this.s.isRank(s) && this.v.isRank(v)) return true;
		return false;
		
	}
	
	public void addOcurrence(){
		occurrence++;
	}
	
	public Double getPercentage(){
		return percentage;
	}
	
	public void setPercentage(int total){
		percentage = ((double)occurrence)/((double) total);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColorHSV other = (ColorHSV) obj;
		if (h == null) {
			if (other.h != null)
				return false;
		} else if (!h.equals(other.h))
			return false;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		if (v == null) {
			if (other.v != null)
				return false;
		} else if (!v.equals(other.v))
			return false;
		return true;
	}
	
	
	
	

}
