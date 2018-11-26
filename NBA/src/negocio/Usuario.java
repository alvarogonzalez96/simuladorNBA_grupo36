package negocio;

import datos.BD;

public class Usuario {

	protected String username;
	protected int id;
	protected Equipo equipo;
	protected int equipoID;
	
	public Usuario() {}
	
	public Usuario(String user, int id, int equipoID) {
		this.username = user;
		this.id = id;
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
	
}
