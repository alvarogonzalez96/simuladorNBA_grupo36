package negocio;

public class Jugador {

	protected String nombre;
	protected Posicion posicion;
	protected Rol rol;
	protected int tiroCerca;
	protected int tiroLejos;
	protected int asistencia;
	protected int rebote;
	protected int salto;
	protected int altura; //0-100
	protected int condicionFisica;
	protected int ataque;
	protected int defensa;
	protected int overall;
	protected int minutos;
	protected int tiempoJugado;
	
	public Jugador(String nombre,Posicion posicion, Rol rol, int tiroCerca, int tiroLejos, int asistencia, int rebote, int salto, int altura,
			int condicionFisica) {
		super();
		this.nombre = nombre;
		this.posicion = posicion;
		this.rol = rol;
		this.tiroCerca = tiroCerca;
		this.tiroLejos = tiroLejos;
		this.asistencia = asistencia;
		this.rebote = rebote;
		this.salto = salto;
		this.altura = altura;
		this.condicionFisica = condicionFisica;
		this.ataque = (tiroCerca + tiroLejos + asistencia) / 3;
		this.defensa = (rebote + salto + altura) / 3;
		this.overall = (ataque+defensa) / 2;
		this.minutos = 0;
		this.tiempoJugado = 0;
	}
	
	public Jugador() {
		super();
		this.nombre = "Sin especificar";
		this.posicion = Posicion.BASE;
		this.rol = Rol.SUPLENTE;
		this.tiroCerca = 0;
		this.tiroLejos = 0;
		this.asistencia = 0;
		this.rebote = 0;
		this.salto = 0;
		this.altura = 0;
		this.condicionFisica = 0;
		this.ataque = 0;
		this.defensa = 0;
		this.overall = 0;
		this.minutos = 0;
		this.tiempoJugado = 0;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Posicion getPosicion() {
		return posicion;
	}

	public void setPosicion(Posicion posicion) {
		this.posicion = posicion;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public int getTiroCerca() {
		return tiroCerca;
	}

	public void setTiroCerca(int tiroCerca) {
		this.tiroCerca = tiroCerca;
	}

	public int getTiroLejos() {
		return tiroLejos;
	}

	public void setTiroLejos(int tiroLejos) {
		this.tiroLejos = tiroLejos;
	}

	public int getAsistencia() {
		return asistencia;
	}

	public void setAsistencia(int asistencia) {
		this.asistencia = asistencia;
	}

	public int getRebote() {
		return rebote;
	}

	public void setRebote(int rebote) {
		this.rebote = rebote;
	}

	public int getSalto() {
		return salto;
	}

	public void setSalto(int salto) {
		this.salto = salto;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public int getCondicionFisica() {
		return condicionFisica;
	}

	public void setCondicionFisica(int condicionFisica) {
		this.condicionFisica = condicionFisica;
	}

	public int getAtaque() {
		return ataque;
	}

	/*public void setAtaque(int ataque) {
		this.ataque = ataque;
	}*/

	public int getDefensa() {
		return defensa;
	}

	/*public void setDefensa(int defensa) {
		this.defensa = defensa;
	}*/

	public int getOverall() {
		return overall;
	}

	public int getMinutos() {
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}

	public int getTiempoJugado() {
		return tiempoJugado;
	}

	public void setTiempoJugado(int tiempoJugado) {
		this.tiempoJugado = tiempoJugado;
	}

	@Override
	public String toString() {
		double min = (double) minutos/60 ;
		
		return "Jugador [nombre=" + nombre + ", posicion=" + posicion + ", rol=" + rol + ", tiroCerca=" + tiroCerca
				+ ", tiroLejos=" + tiroLejos + ", asistencia=" + asistencia + ", rebote=" + rebote + ", salto=" + salto
				+ ", altura=" + altura + ", condicionFisica=" + condicionFisica + ", ataque=" + ataque + ", defensa="
				+ defensa + ", overall=" + overall + ", segundos=" + minutos + ", minutos= "+ min + ", m="+ tiempoJugado + "]";
	}

	/*public void setOverall(int overall) {
		this.overall = overall;
	}*/
	
	
	
}
