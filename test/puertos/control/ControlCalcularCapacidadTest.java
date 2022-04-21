package puertos.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import puertos.persistencia.ListaBarcos;

/**
 * Pruebas del método calcularCapacidadTotal de ControlPuerto
 */
class ControlCalcularCapacidadTest {
	
	/**
	 * Se calcula la capacidad cuando no hay barcos registrados
	 */
	@Test
	void testCalcularSinBarcos() {
		ControlPuerto control = new ControlPuerto(new ListaBarcos());
		double capacidadEsperada = 0;
		double capacidad = control.calcularCapacidadTotal();
		assertEquals(capacidadEsperada,capacidad);
	}

	/**
	 * Se calcula la capacidad con varios barcos:
	 * un velero con menos de 10 pasajeros y otro con más de 10,
	 * un carguero con líquidos y otro que no.
	 * @throws BarcoException 
	 */
	@Test
	void testCalcularValido() throws BarcoException  {
		ControlPuerto control = new ControlPuerto(new ListaBarcos());
		control.adicionarBarco("Vel-001", "colombiana", 100, 'v', 8, false);
		control.adicionarBarco("Vel-002", "chilena", 150, 'v', 15, false);
		control.adicionarBarco("Car-001", "peruana", 500, 'c', 15, true);
		control.adicionarBarco("Car-002", "mexicano", 250, 'c', 25, false);
		double capacidadEsperada = 675;
		double capacidad = control.calcularCapacidadTotal();
		assertEquals(capacidadEsperada,capacidad);
	}
}
