package model.processing;

public class Rank {
	
	Double inf;
	Double sup;
	
	public Double getInf() {
		return inf;
	}
	public void setInf(Double inf) {
		this.inf = inf;
	}
	public Double getSup() {
		return sup;
	}
	public void setSup(Double sup) {
		this.sup = sup;
	}
	
	public boolean isRank(Double value){
		if( (value >= this.inf) && (value <= this.sup)) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rank other = (Rank) obj;
		if (inf == null) {
			if (other.inf != null)
				return false;
		} else if (!inf.equals(other.inf))
			return false;
		if (sup == null) {
			if (other.sup != null)
				return false;
		} else if (!sup.equals(other.sup))
			return false;
		return true;
	}
	
	
	
	
	

}
