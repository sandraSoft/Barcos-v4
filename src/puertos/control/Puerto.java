package puertos.control;

import java.util.List;

import puertos.entidades.Barco;
import puertos.entidades.Carguero;
import puertos.entidades.Velero;
import puertos.persistencia.BaseDatosBarcos;
import puertos.persistencia.RepositorioBarcos;

/**
 * Clase donde se registran los barcos que llegan al puerto,
 * y tiene la principales funciones del programa (es el control).
 * @version 4.1
 */
public class Puerto {
	private RepositorioBarcos repositorio;
	private final double VOLUMEN_MAXIMO = 1000;
	
	public Puerto() {
		repositorio = new BaseDatosBarcos();
		/* También se puede tener lo siguiente:  */
		// repositorioBarcos = new ListaBarcos();
	}
	
	public Puerto(RepositorioBarcos repositorio) {
		this.repositorio = repositorio;
	}
	
	/**
	 * Calcula la capacidad de todos los barcos en el puerto,
	 * para poder determinar la carga que puede recibir.
	 * @return	la capacidad total de los barcos, en m3
	 */
	public double calcularCapacidadTotal() {
		List<Barco> barcos = repositorio.consultarBarcos();
		double capacidadTotal = 0;
		for (Barco barco : barcos) {
			capacidadTotal += barco.calcularCapacidad();
		}
		return capacidadTotal;
	}
	
	/**
	 * Se adiciona un barco al puerto, es decir, 
	 * se registra su información y se guarda.
	 * @see puertos.entidades.Barco#Barco(String, String, double)
	 * @param tipo	qué tipo de barco es: 'v' para velero, 'c' para carguero
	 * @param pasajeros	la cantidad de pasajeros que lleva (para veleros)
	 * @param liquidos	si puede llevar líquidos o no (para cargueros)
	 * @throws BarcoException cuando no cumple alguna de las reglas del negocio
	 */
	public void adicionarBarco(String matricula, String nacionalidad, 
			double volumen, 
			char tipo, int pasajeros, boolean liquidos) throws BarcoException {
		
		if (!validarMatriculaUnica(matricula)) {
			throw new BarcoException("No se puede guardar: "
					+ "Ya existe un barco registrado con esa matrícula");
		}
		
		if (!validarVolumenBarco(volumen)) {
			throw new BarcoException("Volumen incorrecto: "
					+ "debe estar entre cero y mil [0 - 1000]");
		}

		switch (tipo) {
		  case 'v':
		  case 'V':
			repositorio.adicionarBarco(
					new Velero(matricula, nacionalidad, volumen, pasajeros));
			break;
		  case 'c':
		  case 'C':
			repositorio.adicionarBarco(
					new Carguero(matricula, nacionalidad, volumen, liquidos));
			break;
		}
	}

	/**
	 * Valida que la matrícula no esté registrada en la lista de barcos.
	 * param matricula	la identificación del barco que se desea revisar
	 * @return true si la matrícula es única, o false si ya existe 
	 */
	boolean validarMatriculaUnica(String matricula) {
		Barco barco = repositorio.buscarBarco(matricula);
		if (barco == null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Valida que el volumen de un barco se conserve en los rangos permitidos.
	 * @param volumen el volumen que se desea evaluar
	 * @return	si el volumen es aceptado o no
	 */
	private boolean validarVolumenBarco(double volumen) {
		if (volumen < 0 || volumen > VOLUMEN_MAXIMO) {
			return false;
		}
		return true;
	}
}
