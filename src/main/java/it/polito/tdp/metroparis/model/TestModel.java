package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model() ;
		
		m.creaGrafo(); 
		
		Fermata p = m.trovaFermata("La Fourche");
		
		if(p==null)
			System.out.println("Fermata non trovata");
		else {
		List <Fermata> raggiungibili = m.fermateRaggiungibili(p);
		System.out.println(raggiungibili);
		}
	}

}
