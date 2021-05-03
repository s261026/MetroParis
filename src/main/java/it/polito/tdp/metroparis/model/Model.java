package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo ;

	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class) ;
		
		MetroDAO dao = new MetroDAO() ;
		List<Fermata> fermate = dao.getAllFermate() ;
		
//		for(Fermata f : fermate) {
//			this.grafo.addVertex(f) ;
//		}
		
		Graphs.addAllVertices(this.grafo, fermate) ;
		
		// Aggiungiamo gli archi
		
//		for(Fermata f1: this.grafo.vertexSet()) {
//			for(Fermata f2: this.grafo.vertexSet()) {
//				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
//					this.grafo.addEdge(f1, f2) ;
//				}
					// con questa query però il tempo è molto lungo perchè matcho molte connessioni
//			}
//		}
		
		List<Connessione> connessioni = dao.getAllConnessioni(fermate) ;
		for(Connessione c: connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA()) ;
		}
		
		//così ho fatto una sola query che usa davvero solo qualche frazione di secondo quindi top
		System.out.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size()) ;
//		System.out.println(this.grafo) ;
		
	//	Fermata f;
	//	Set <DefaultEdge> archi = this.grafo.edgesOf(f);
	
	/*	for(DefaultEdge e: archi) {
			/*
			Fermata f1 = this.grafo.getEdgeSource(e);
			Fermata f2 = this.grafo.getEdgeTarget(e);
			//questi metodi mi danno sorgente e destinazione di ogni grafo 
			//in un grafo non orientato o f1 o f2 coincide con f, l'altro è quello che mi interessa
			
		
			if(f1.equals(f)) {
				//f2 è quello che mi serve
			}
			else {
				//f1 è quello che mi serve
			}
			
			//se invece ho un grafo orientato so quali sono o gli incoming o gli outgoing quindi 
			//chiedo direttamente quello che mi serve ... ma non lo siamo mai, meglio fare:
			f1 = Graphs.getOppositeVertex(this.grafo, e, f); //dato un arco associato a quel vertice restituisce f1 o f2 opposti che mi serviva
		}*/
		
		//un altro metodo che mi ritorna la lista di vertici successori a quello dato è:
	//	List <Fermata> fermateAdiacenti = Graphs.successorListOf(this.grafo, f);
	//	List <Fermata> fermateSuccessive = Graphs.predecessorListOf(this.grafo, f);
		//in un grafo orientato queste due liste coincidono
	}
	
	public Fermata trovaFermata (String nome) {
		for(Fermata f: this.grafo.vertexSet())
			if(f.getNome().equals(nome))
				return f;
		return null;
	}
	
	
	
	public List <Fermata> fermateRaggiungibili(Fermata partenza){
		//VISITA IN AMPIEZZA
		BreadthFirstIterator <Fermata, DefaultEdge> bfv = new BreadthFirstIterator<Fermata, DefaultEdge>(this.grafo, partenza);
		DepthFirstIterator <Fermata, DefaultEdge> dfv = new DepthFirstIterator<Fermata, DefaultEdge>(this.grafo, partenza);
		
		List <Fermata> result = new ArrayList <Fermata>();
		
		while (bfv.hasNext()) {
			Fermata f = bfv.next();
			result.add(f);
		}
		return result;
	}
	
}
