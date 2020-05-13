package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata, DefaultEdge> graph ;
	private List<Fermata> fermate ;
	private Map<Integer, Fermata> fermateIdMap ;
	
	public Model() {
		this.graph = new SimpleDirectedGraph<>(DefaultEdge.class) ;
		
		MetroDAO dao = new MetroDAO() ;
		
		// CREAZIONE DEI VERTICI
		
		this.fermate = dao.getAllFermate() ;
		
		this.fermateIdMap = new HashMap<>() ;
		for(Fermata f: this.fermate) {
			fermateIdMap.put(f.getIdFermata(), f) ;
		}
		
		Graphs.addAllVertices(this.graph, this.fermate) ;	// Popolazione del grafo tramite il metodo del dao
		
//		System.out.println(this.graph) ;
		
		// CREAZIONE DEGLI ARCHI
		
		// METODO 1 : Coppie di vertici
		// -> Metodo meno efficiente perchè è un metodo molto lento in quanto ha tantissime operazioni da fare
		// -> Complessità = n^2
		// Sarebbe il metodo migliore se il numero di vertici fosse < 100 
		
		/*for(Fermata fp : this.fermate) {
			for(Fermata fa : this.fermate) {
				if( dao.fermateConnesse(fp, fa) ) {	// Se esiste una connessione che va da fp a fa
					this.graph.addEdge(fp, fa) ;
				}
			}
		}*/
		
//		System.out.println(this.graph) ;
		
		// METODO 2 : Da un vertice, trova tutti i connessi
		// -> Più efficiente del METODO 1 se il grado medio dei vertici è basso rispetto al numero di vertici => se la densità è bassa
		// -> Complessità = n
		
		/*for(Fermata fp: this.fermate) {
			List<Fermata> connesse = dao.fermateSuccessive(fp, fermateIdMap); // Lista di fermate adiacenti alla fermata in questione
			
			for(Fermata fa: connesse) {
				this.graph.addEdge(fp, fa);
			}
		}*/
		
		
		// METODO 3 : Chiedo al DB l'elenco degli archi
		// -> Metodo decisamente più efficiente e veloce perchè faccio fare tutto il lavoro al DB  
		// -> Complessità = lineare
		
		List<CoppiaFermate> coppie = dao.coppieFermate(fermateIdMap);
		for(CoppiaFermate c : coppie) {
			this.graph.addEdge(c.getFp(), c.getFa()) ;
		}
		
		System.out.println(this.graph) ;
		System.out.format("Grafo caricato con %d vertici %d archi",
				this.graph.vertexSet().size(),
				this.graph.edgeSet().size());

	}
	
	public static void main(String args[]) {
		new Model() ;
	}

}
