package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	public boolean fermateCollegate(Fermata f1, Fermata f2) {
		String sql = "SELECT COUNT(*) AS cnt " +
				"FROM connessione " +
				"WHERE (id_stazP=? AND id_stazA=?) OR " +
				"(id_stazP=? AND id_stazA=?)" ;
		//perchè non è detto che nel grafo non orientato siano implementati sia l'arco inverso che quello diretto tra 2 stazioni
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, f1.getIdFermata());
			st.setInt(2, f2.getIdFermata());
			st.setInt(3, f2.getIdFermata());
			st.setInt(4, f1.getIdFermata());
			
			ResultSet rs = st.executeQuery() ;
			
			rs.first(); //perchè mi darà una sola riga
			
			int conteggio = rs.getInt("cnt") ;
						
			conn.close();
			
			return (conteggio>0) ; // ritorna vero se >0, falso se =0
			
		} catch (SQLException e) {
			throw new RuntimeException("Errore query", e);
		}
	}
	
	//interroghiamo direttamente il database per avere più efficneza di calcolo per vedere tutte le connessioni -> creo un oggetto connnesione
	public List<Connessione> getAllConnessioni(List<Fermata> fermate) {
		String sql = "SELECT  id_connessione, id_linea, id_stazP, id_stazA "
				+ "FROM connessione "
				+ "WHERE id_stazP>id_stazA" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Connessione> result = new ArrayList<Connessione>();
			while(rs.next()) {
				
				int id_partenza = rs.getInt("id_stazP") ;
				Fermata fermata_partenza = null ;
				for(Fermata f: fermate) 
					if(f.getIdFermata()==id_partenza)
						fermata_partenza = f ;
				
				int id_arrivo= rs.getInt("id_stazA") ;
				Fermata fermata_arrivo = null ;
				for(Fermata f: fermate) 
					if(f.getIdFermata()==id_arrivo)
						fermata_arrivo = f ;

				Connessione c = new Connessione(rs.getInt("id_connessione"),
						null, // ingnoro la linea, adesso non ci serve
						fermata_partenza,	//ho bisogno dell'oggetto fermata, non dell'id 
						fermata_arrivo) ;
				result.add(c);
			}
			
			conn.close() ;
			return result ;
		} catch (SQLException e) {
			throw new RuntimeException("Errore DB", e) ;
		}
	}

}
