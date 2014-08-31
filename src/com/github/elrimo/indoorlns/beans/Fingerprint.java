package com.github.elrimo.indoorlns.beans;

import java.io.Serializable;
import java.util.List;
public class Fingerprint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -950209724949432777L;
	/**
	 * 
	 */

	private double abscisse  ;
	private double ordonnee;
	private List<WifiItem> RSSi;
	
	public double getAbscisse() {
		return abscisse;
	}
	public void setAbscisse(double abscisse) {
		this.abscisse = abscisse;
	}
	public double getOrdonnee() {
		return ordonnee;
	}
	public void setOrdonnee(double ordonnee) {
		this.ordonnee = ordonnee;
	}
	
	public List<WifiItem> getRSSi() {
		return RSSi;
	}
	public void setRSSi(List<WifiItem> rSSi) {
		RSSi = rSSi;
	}
	
}
