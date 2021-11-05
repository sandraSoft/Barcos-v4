package puertos.control;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import puertos.persistencia.ListaBarcos;

/**
 * Pruebas del método adicionarBarco del Puerto.
 */
class PuertoAdicionarBarcoTest {

	/**
	 * Adicionar un barco con datos correctos
	 */
	@Test
	void adicionarVeleroTest() throws BarcoException {
		Puerto puerto = new Puerto(new ListaBarcos());
		
		assertTrue(puerto.validarMatriculaUnica("123"));
		puerto.adicionarBarco("123","colombiana", 200,'v', 10, true);	
		assertFalse(puerto.validarMatriculaUnica("123"));
	}
	
	/**
	 * Se verifica que no permita adicionar un barco con matrícula repetida
	 */
	@Test
	void testAdicionarBarcoRepetido() throws BarcoException {
		Puerto puerto = new Puerto(new ListaBarcos());
		assertTrue(puerto.validarMatriculaUnica("245"));
		puerto.adicionarBarco("245", "peruana", 100, 'v', 5, false);
		assertFalse(puerto.validarMatriculaUnica("245"));
		assertThrows(Exception.class, 
				() -> puerto.adicionarBarco("245", "peruana", 100, 'v', 5, false));
	}
	
	/**
	 * Se verifica que no permita adicionar un barco con volumen negativo
	 */
	@Test
	void testAdicionarBarcoVolumenNegativo() {
		Puerto puerto = new Puerto(new ListaBarcos());
		
		assertThrows(BarcoException.class,
				() ->  puerto.adicionarBarco("789", "italiano", -79, 'v', 10, true));
		assertTrue(puerto.validarMatriculaUnica("789"));
	}
	
	/**
	 * Se verifica que no permita adicionar un barco con volumen mayor 
	 * a lo permitido (en este caso 1000)
	 */
	@Test
	void testAdicionarBarcoVolumenAlto() {
		Puerto puerto = new Puerto(new ListaBarcos());
		assertThrows(BarcoException.class,
				() ->  puerto.adicionarBarco("003", "canadiense", 1500, 'c', 30, false));
		assertTrue(puerto.validarMatriculaUnica("003"));
	}
}
