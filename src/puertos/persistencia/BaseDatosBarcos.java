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
 * matricula,nacionalidad,volumen,pasajeros,liquidos y tipo 
 * 
 * @version 1.0
 */
public class BaseDatosBarcos implements RepositorioBarcos {
		
	@Override
	public List<Barco> consultarBarcos() {
		List<Barco> barcos = new ArrayList<Barco>();
		Connection conexion = null;
		try {
			conexion = abrirConexion();
			String consultaSQL = "Select matricula,nacionalidad,volumen,pasajeros,liquidos,tipo "
					+ "from Barcos ";
			PreparedStatement sentencia = conexion.prepareStatement(consultaSQL);
			ResultSet resultadoConsulta = sentencia.executeQuery();
			if (resultadoConsulta != null) {
				while (resultadoConsulta.next()) {
					Barco barco = instanciarBarco(resultadoConsulta);
					barcos.add(barco);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error con la base de datos en consultarBarcos: \n" + e);
		} finally {
			cerrarConexion(conexion);
		}
		return barcos;
	}

	@Override
	public boolean adicionarBarco(Barco barco) {
		Connection conexion = null;
		try {
			conexion = abrirConexion();
			String sentenciaSQL = crearSentenciaInsert(barco);
			Statement sentencia = conexion.createStatement();
			int cantidadInserciones = sentencia.executeUpdate(sentenciaSQL);
			return (cantidadInserciones > 0);
		} catch (SQLException e) {
			System.err.println("Error con la base de datos en adicionarBarco: \n" + e);
		} finally {
			cerrarConexion(conexion);
		}
		return false;
	}

	@Override
	public Barco buscarBarco(String matricula) {
		Connection conexion = null;
		try {
			conexion = abrirConexion();
			String consultaSQL = "Select matricula,nacionalidad,volumen,pasajeros,liquidos,tipo " + " from barcos "
					+ " where matricula = ?";
			PreparedStatement sentencia = conexion.prepareStatement(consultaSQL);
			sentencia.setString(1, matricula);
			ResultSet resultadoConsulta = sentencia.executeQuery();
			if (resultadoConsulta != null && resultadoConsulta.next()) {
				return instanciarBarco(resultadoConsulta);
			}
		} catch (SQLException e) {
			System.err.println("Error con la base de datos en buscarBarco: \n" + e);
		} finally {
			cerrarConexion(conexion);
		}
		return null;
	}

	/**
	 * Permite obtener un objeto "Connection" para conectarse con la base de datos.
	 * Se llamará este método cada vez que se necesite crear una nueva conexión
	 * a la base de datos para realizar alguna operación (SQL). 
	 * 
	 * El método que obtiene esta conexión deberá llamar al método cerrarConexión
	 * (después de realizar la operación), para que no queden conexiones abiertas
	 * que consuman recursos.
	 * 
	 * @throws SQLException si no se puede crear la conexión con la base de datos
	 */
	private Connection abrirConexion() throws SQLException {
		DriverManager.registerDriver(new org.sqlite.JDBC());
		String cadenaConexion = "jdbc:sqlite:barcos.db";
		Connection conexion = DriverManager.getConnection(cadenaConexion);
		return conexion;
	}
	
	/**
	 * Cierra una conexión para liberar así los recursos de la misma.
	 * Si se presenta alguna excepción en el proceso, se escribe el error. 
	 */
	private void cerrarConexion(Connection conexion) {
		if (conexion != null) {
			try {
				conexion.close();
				conexion = null;
			} catch (SQLException e) {
				System.err.println("No se pudo obtener la conexión con la Base de Datos:"+conexion);
			}
		}
	}
	
	/**
	 * Crea un objeto barco a partir de los datos de un ResultSet.
	 * @param datosBarco el ResultSet resultante de una consulta de un barco en la base de datos.
	 * 						Debe ser diferente de null.
	 * @return	el objeto barco con sus valores (tomados del ResultSet), o null
	 * 			si el ResultSet está vacío (es decir, no se encontró al consultar en la BD).
	 */
	private Barco instanciarBarco(ResultSet datosBarco) {
		Barco barco = null;
		try {
			String matricula = datosBarco.getString("matricula");
			String nacionalidad = datosBarco.getString("nacionalidad");
			double volumen = datosBarco.getDouble("volumen");
			int pasajeros = datosBarco.getInt("pasajeros");
			boolean liquidos = datosBarco.getBoolean("liquidos");
			String tipo = datosBarco.getString("tipo");
			if (tipo.equalsIgnoreCase("velero")) {
				barco = new Velero (matricula, nacionalidad,volumen, pasajeros);
			}
			else if (tipo.equalsIgnoreCase("carguero")) {
				barco = new Carguero (matricula, nacionalidad,volumen, liquidos);
			}
		} catch (SQLException e) {
			System.err.println("No se pudo obtener el barco con el ResultSet: "+datosBarco);
		}
		return barco;
	}
	
	/**
	 * Elabora la instrucción SQL para insertar un barco en la base de datos,
	 *  usando los métodos get del objeto.
	 * @param barco objeto Barco que se desea insertar en la base de datos,
	 * 			debe ser diferente de null
	 */
	private String crearSentenciaInsert(Barco barco) {
		String tipo = "carguero";
		if (barco instanceof Velero) {
			tipo = "velero";
		}
		String sentenciaSQL = "Insert into barcos(matricula,nacionalidad,volumen,pasajeros,liquidos,tipo)"
				+ " values ('"+barco.getMatricula()+"','"+barco.getNacionalidad()+"',"
				+ barco.getVolumen()+",";
		if (barco instanceof Velero) {
			Velero velero = (Velero)barco;
			sentenciaSQL += velero.getPasajeros()+",null,'"+tipo+"')";
		}
		else if (barco instanceof Carguero) {
			Carguero carguero = (Carguero)barco;
			int liquido = carguero.getLiquidos()?1:0;
			sentenciaSQL += "null,"+liquido+","+"'"+tipo+"')";
		}
		return sentenciaSQL;
	}
	
}
