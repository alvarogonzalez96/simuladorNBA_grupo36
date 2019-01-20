package negocio;

public class Usuario {

	protected String username;
	protected Equipo equipo;
	protected int equipoID;

	public Usuario() {}

	public Usuario(String user, int equipoID) {
		this.username = user;
		this.equipoID = equipoID;
	}

	protected Equipo calcularEquipo(int tid) {
		return null;
	}

	public Equipo getEquipo() {
		for(Equipo e: LigaManager.equipos) {
			if(e.tid == equipoID) {
				return e;
			}
		}
		return null;
	}
	
	public String getNombre() {
		return username;
	}

}
