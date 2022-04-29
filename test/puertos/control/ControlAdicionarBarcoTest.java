package puertos.control;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import puertos.persistencia.ListaBarcos;

/**
 * Pruebas del método adicionarBarco de ControlPuerto.
 */
class ControlAdicionarBarcoTest {

	/**
	 * Adicionar un barco con datos correctos
	 * @throws BarcoException 
	 */
	@Test
	void adicionarVeleroTest() throws BarcoException {
		ControlPuerto control = new ControlPuerto(new ListaBarcos());
		
		assertFalse(control.existeMatricula("123"));
		control.adicionarBarco("123","colombiana", 200,'v', 10, true);
		assertTrue(control.existeMatricula("123"));
	}
	
	/**
	 * Se verifica que no permita adicionar un barco con matrícula repetida
	 */
	@Test
	void testAdicionarBarcoRepetido() throws BarcoException {
		ControlPuerto control = new ControlPuerto(new ListaBarcos());
		assertFalse(control.existeMatricula("245"));
		control.adicionarBarco("245", "peruana", 100, 'v', 5, false);
		assertTrue(control.existeMatricula("245"));
		assertThrows(Exception.class, 
				() -> control.adicionarBarco("245", "peruana", 100, 'v', 5, false));
	}
	
	/**
	 * Se verifica que no permita adicionar un barco con volumen negativo
	 */
	@Test
	void testAdicionarBarcoVolumenNegativo() {
		ControlPuerto control = new ControlPuerto(new ListaBarcos());
		
		assertThrows(BarcoException.class,
				() ->  control.adicionarBarco("789", "italiano", -79, 'v', 10, true));
		assertFalse(control.existeMatricula("789"));
	}
	
	/**
	 * Se verifica que no permita adicionar un barco con volumen mayor 
	 * a lo permitido (en este caso 1000)
	 */
	@Test
	void testAdicionarBarcoVolumenAlto() {
		ControlPuerto control = new ControlPuerto(new ListaBarcos());
		double volumen = control.VOLUMEN_MAXIMO + 500;
		assertThrows(BarcoException.class,
				() ->  control.adicionarBarco("003", "canadiense", volumen, 'c', 30, false));
		assertFalse(control.existeMatricula("003"));
	}
}
