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
	 * Se adiciona un barco al puerto, es decir, se registra su información y se guarda.
	 * @see puertos.entidades.Barco#Barco(String, String, double)
	 * @param tipo	qué tipo de barco es: 'v' para velero, 'c' para carguero
	 * @param pasajeros	la cantidad de pasajeros que lleva el barco (solo sirve para veleros)
	 * @param liquidos	indicación (true/false) de si puede llevar líquidos o no (solo aplica para cargueros)
	 * @return	un valor booleano indicando si se pudo adicionar el barco
	 * 			o no (porque ya existía otro con esa matrícula).
	 * @throws BarcoException cuando algunos de las reglas del negocio no se cumple
	 */
	public boolean adicionarBarco(String matricula, String nacionalidad, double volumen, 
			char tipo, int pasajeros, boolean liquidos) throws BarcoException {
		
		if (!validarVolumenBarco(volumen)) {
			throw new BarcoException("Volumen incorrecto: debe estar entre cero y mil [0 - 1000]");
		}
		
		Barco barcoBuscado = buscarBarco(matricula);
		
		if (barcoBuscado == null) {
			switch (tipo) {
			  case 'v':
			  case 'V': 
				  return repositorio.adicionarBarco(new Velero(matricula, nacionalidad, volumen, pasajeros));
			  case 'c':
			  case 'C':
				  return repositorio.adicionarBarco(new Carguero(matricula, nacionalidad, volumen, liquidos));
			}
		}
		
		return false;
	}

	/**
	 * Busca un barco entre los que están registrados, por su número de matrícula
	 * @return el objeto barco con la matrícula dada, o null si no se encuentra
	 */
	private Barco buscarBarco(String matricula) {
		return repositorio.buscarBarco(matricula);
	}
	
	/**
	 * Valida que el volumen de un barco se conserve en los rangos permitidos
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
