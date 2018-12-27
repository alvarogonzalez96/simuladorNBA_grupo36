package negocio;

public class Renovacion {

	private Jugador jugador;
	private int cantidad, anyos;
	
	public Renovacion(Jugador j, int cantidad, int anyos) {
		this.jugador = j;
		this.cantidad = cantidad;
		this.anyos = anyos;
	}
	
	public String getNombre() {
		return jugador.nombre;
	}
	
	public int getCantidad() {
		return cantidad;
	}
	
	public int getAnyos() {
		return anyos;
	}
	
}
