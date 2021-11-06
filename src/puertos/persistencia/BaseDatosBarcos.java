package puertos.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import puertos.entidades.Barco;
import puertos.entidades.Carguero;
import puertos.entidades.Velero;

/**
 * Usa una base de datos como repositorio de los datos de los barcos,
 * y ofrece los servicios definidos en RepositorioBarcos.
 *
 * Se usa como ejemplo una base de datos llamada "barcos.db" (motor SQLite),
 * que tiene una sola tabla llamada "barcos", con campos: 
 * matricula,nacionalidad,volumen,pasajeros,liquidos y tipo. 
 * 
 * @version 1.0
 */
public class BaseDatosBarcos implements RepositorioBarcos {
	private String cadenaConexion;
	
	public BaseDatosBarcos(){
		try {
			DriverManager.registerDriver(new org.sqlite.JDBC());
			cadenaConexion = "jdbc:sqlite:barcos.db";
		} catch (SQLException e) {
			System.err.println("Error de conexión con la base de datos: " + e);
		}
	}

	@Override
	public List<Barco> consultarBarcos() {
		List<Barco> barcos = new ArrayList<Barco>();
		try 
		(Connection conexion = DriverManager.getConnection(cadenaConexion)) {			
			String consultaSQL = "Select matricula,nacionalidad,"
					+ "volumen,pasajeros,liquidos,tipo "
					+ "from Barcos ";
			PreparedStatement sentencia = conexion.prepareStatement(consultaSQL);
			ResultSet resultadoConsulta = sentencia.executeQuery();
			if (resultadoConsulta != null) {
				while (resultadoConsulta.next()) {
					Barco barco = null;
					String matricula = resultadoConsulta.getString("matricula");
					String nacionalidad = resultadoConsulta.getString("nacionalidad");
					double volumen = resultadoConsulta.getDouble("volumen");
					int pasajeros = resultadoConsulta.getInt("pasajeros");
					boolean liquidos = resultadoConsulta.getBoolean("liquidos");
					String tipo = resultadoConsulta.getString("tipo");
					if (tipo.equalsIgnoreCase("velero")) {
						barco = new Velero (matricula, nacionalidad,
								volumen, pasajeros);
					}
					else if (tipo.equalsIgnoreCase("carguero")) {
						barco = new Carguero (matricula, nacionalidad,
								volumen, liquidos);
					}
					barcos.add(barco);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error con la base de datos"
					+ " en consultarBarcos: \n" + e);
		}
		return barcos;
	}

	@Override
	public boolean adicionarBarco(Barco barco) {
		try 
		(Connection conexion = DriverManager.getConnection(cadenaConexion)) {
			String tipo = "carguero";
			if (barco instanceof Velero) {
				tipo = "velero";
			}
			String sentenciaSQL = "Insert into barcos(matricula,"
					+ "nacionalidad,volumen,pasajeros,liquidos,tipo)"
					+ " values ('"+barco.getMatricula()+ "','"
					+ barco.getNacionalidad()+"',"
					+ barco.getVolumen()+",";
			if (barco instanceof Velero) {
				Velero velero = (Velero)barco;
				sentenciaSQL += velero.getPasajeros() + ",null,'"+ tipo + "')";
			}
			else if (barco instanceof Carguero) {
				Carguero carguero = (Carguero)barco;
				int liquido = carguero.getLiquidos()?1:0;
				sentenciaSQL += "null," + liquido + ",'" + tipo + "')";
			}
			Statement sentencia = conexion.createStatement();
			int cantidadInserciones = sentencia.executeUpdate(sentenciaSQL);
			return (cantidadInserciones > 0);
		} catch (SQLException e) {
			System.err.println("Error con la base de datos"
					+ " en adicionarBarco: \n" + e);
		} 
		return false;
	}

	@Override
	public Barco buscarBarco(String matricula) {
		try 
		(Connection conexion = DriverManager.getConnection(cadenaConexion)) {
			String consultaSQL = "Select matricula,nacionalidad,"
					+ "volumen,pasajeros,liquidos,tipo " + " from barcos "
					+ " where matricula = ?";
			PreparedStatement sentencia = conexion.prepareStatement(consultaSQL);
			sentencia.setString(1, matricula);
			ResultSet datosBarco = sentencia.executeQuery();
			if (datosBarco != null && datosBarco.next()) {
				Barco barco = null;
				String nacionalidad = datosBarco.getString("nacionalidad");
				double volumen = datosBarco.getDouble("volumen");
				int pasajeros = datosBarco.getInt("pasajeros");
				boolean liquidos = datosBarco.getBoolean("liquidos");
				String tipo = datosBarco.getString("tipo");
				if (tipo.equalsIgnoreCase("velero")) {
					barco = new Velero (matricula, nacionalidad,
							volumen, pasajeros);
				}
				else if (tipo.equalsIgnoreCase("carguero")) {
					barco = new Carguero (matricula, nacionalidad,
							volumen, liquidos);
				}
				return barco;
			}
		} catch (SQLException e) {
			System.err.println("Error con la base de datos "
					+ "en buscarBarco: \n" + e);
		} 
		return null;
	}
}
