package negocio;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.json.JSONObject;

public class Equipo {
	
	/**
	 * Nota: todas las unidades monetarias estan en miles de dolares.
	 * */	
	static final int limiteSalarial = 108000; 
	
	private static OrdenadorJugadores ordenador;
	static {
		ordenador = new OrdenadorJugadores();
	}
	
	private TableModel modelo, modeloFinanzas;
	
	protected int salarioTotal;

	protected int victorias, derrotas;
	
	protected Conferencia conferencia;
	protected Division division;
	protected String nombre;
	protected String abrev;
	protected ArrayList<Jugador> jugadores;
	protected int ataque;
	protected int defensa;
	protected int overall;
	int tid;
	
	public Equipo(int tid, ArrayList<Jugador> jugadores) {
		super();
		//this.nombre = nombre;
		//this.jugadores = jugadores;
		int at = 0;
		int def = 0;
		int ov = 0;
		this.tid = tid;
		this.jugadores = new ArrayList<>();
		
		for (Jugador j: jugadores) {
			if (j.tid == tid) {
				this.jugadores.add(j);
			}
		}
		
		ordenarJugadores();
		asignarRoles();
		
		this.ataque = (at/10);
		this.defensa = (def/10);
		this.overall = (ov/10);
	}
	
	/*
	 * CONSTRUCTOR DE PRUEBA PARA LA 
	 * PESTANYA CLASIFICACION, ES TEMPORAL
	 * */
	public Equipo(String nombre, int victorias, int derrotas) {
		this.nombre = nombre;
		this.victorias = victorias;
		this.derrotas = derrotas;
	}
	
	public Equipo(JSONObject json) {
		this.tid = json.getInt("tid");
		this.nombre = json.getString("region")+" "+json.getString("name");
		this.abrev = json.getString("abbrev");
		int cid = json.getInt("cid");
		if(cid == 0) {
			this.conferencia = Conferencia.ESTE;
		} else {
			this.conferencia = Conferencia.OESTE;
		}
		int did = json.getInt("did");
		this.division = ordinalADivision(did);
		this.jugadores = new ArrayList<>();
	}
	
	/**
	 * Constructor copia
	 * Aparte de devolver un objeto identico al que se 
	 * le pasa como argumento, la lista de jugadores es 
	 * otra copia identica (no es el mismo objeto, son 
	 * jugadores "nuevos" pero identicos)
	 * */
	public Equipo(Equipo e) {
		this.tid = e.tid;
		this.nombre = e.nombre;
		this.abrev = e.abrev;
		this.jugadores = new ArrayList<>();
		for(Jugador j: e.jugadores) {
			this.jugadores.add(new Jugador(j));
		}
	}
	
	private Division ordinalADivision(int ord) {
		switch(ord) {
		case 0: return Division.ATLANTICO;
		case 1: return Division.CENTRAL; 
		case 2: return Division.SURESTE;
		case 3: return Division.SUROESTE;
		case 4: return Division.NOROESTE;
		case 5: return Division.PACIFICO;
		default: return null;
		}
	}
	
	public void calcSalarioTotal() {
		for(Jugador j: jugadores) {
			salarioTotal += j.salario;
		}
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getAtaque() {
		return ataque;
	}

	public int getDefensa() {
		return defensa;
	}

	public int getOverall() {
		return overall;
	}
	
	public int getTid() {
		return tid;
	}
	
	protected void asignarRoles() {
		ordenarJugadores();
		boolean titBase, titEsc, titAlero, titAlap, titPivot;
		titBase = titEsc = titAlero = titAlap = titPivot = false;
		int basesTit, escTit, alerosTit, alapsTit, pivotsTit;
		basesTit = escTit = alerosTit = alapsTit = pivotsTit = 0;
		Jugador estrella1 = asignarEstrellaAlMejor();
		switch(estrella1.getPosicion()) {
		case BASE: titBase = true; basesTit++; break;
		case ESCOLTA: titEsc = true; escTit++; break;
		case ALERO: titAlero = true; alerosTit++; break;
		case ALAPIVOT: titAlap = true; alapsTit++; break;
		default:
			titPivot = true;
			pivotsTit++;
		}
		Jugador estrella2 = asignarEstrellaAlSegundoMejor(estrella1);
		switch(estrella2.getPosicion()) {
		case BASE: titBase = true; basesTit++; break;
		case ESCOLTA: titEsc = true; escTit++; break;
		case ALERO: titAlero = true; alerosTit++; break;
		case ALAPIVOT: titAlap = true; alapsTit++; break;
		default:
			titPivot = true;
			pivotsTit++;
		}
		for(Jugador j: jugadores) {
			if(j.getRol() != Rol.ESTRELLA) {
				if(!titBase && j.getPosicion() == Posicion.BASE) {
					j.setRol(Rol.TITULAR);
					titBase = true;
					basesTit++;
				}  else if(!titEsc && j.getPosicion() == Posicion.ESCOLTA) {
					j.setRol(Rol.TITULAR);
					titEsc = true;
					escTit++;
				} else if(!titAlero && j.getPosicion() == Posicion.ALERO) {
					j.setRol(Rol.TITULAR);
					titAlero = true;
					alerosTit++;
				} else if(!titAlap && j.getPosicion() == Posicion.ALAPIVOT) {
					j.setRol(Rol.TITULAR);
					titAlap = true;
					alapsTit++;
				} else if(!titPivot && j.getPosicion() == Posicion.PIVOT) {
					j.setRol(Rol.TITULAR);
					titPivot = true;
					pivotsTit++;
				} else {
					if(j.getPosicion() == Posicion.BASE) {
						if(basesTit < 2) {
							j.setRol(Rol.SUPLENTE);
							basesTit++;
						} else {
							j.setRol(Rol.NOJUEGA);
						}
					} else if(j.getPosicion() == Posicion.ESCOLTA) {
						if(escTit < 2) {
							j.setRol(Rol.SUPLENTE);
							escTit++;
						} else {
							j.setRol(Rol.NOJUEGA);
						}
					} else if(j.getPosicion() == Posicion.ALERO) {
						if(alerosTit < 2) {
							j.setRol(Rol.SUPLENTE);
							alerosTit++;
						} else {
							j.setRol(Rol.NOJUEGA);
						}
					} else if(j.getPosicion() == Posicion.ALAPIVOT) {
						if(alapsTit < 2) {
							j.setRol(Rol.SUPLENTE);
							alapsTit++;
						} else {
							j.setRol(Rol.NOJUEGA);
						}
					} else {
						if(pivotsTit < 2) {
							j.setRol(Rol.SUPLENTE);
							pivotsTit++;
						} else {
							j.setRol(Rol.NOJUEGA);
						}
					}
				}
			}
		}
	}
	
	public String getAbrev() {
		return abrev;
	}
	
	
	private Jugador asignarEstrellaAlMejor() {
		Jugador mejor = new Jugador();
		for(Jugador j: jugadores) {
			if(j.getRol() != Rol.ESTRELLA && j.getOverall() > mejor.getOverall()) {
				mejor = j;
			}
		}
		mejor.setRol(Rol.ESTRELLA);
		return mejor;
	}
	
	private Jugador asignarEstrellaAlSegundoMejor(Jugador mejor) {
		Jugador segmejor = new Jugador();
		for(Jugador j: jugadores) {
			if(j.getRol() != Rol.ESTRELLA && j.getPosicion() != mejor.getPosicion() && j.getOverall() > segmejor.getOverall()) {
				segmejor = j;
			}
		}
		segmejor.setRol(Rol.ESTRELLA);
		return segmejor;
	}
	
	public void ordenarJugadores() {
		jugadores.sort(ordenador);
	}
	
	public int getVictorias() {
		return victorias;
	}
	
	public void nuevaVictoria() {
		victorias++;
	}
	
	public int getDerrotas() {
		return derrotas;
	}

	public void nuevaDerrota() {
		derrotas++;
	}
	
	public boolean equals(Equipo e) {
		if(e.tid == tid) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Equipo [nombre=" + nombre + ", jugadores="  /*Arrays.toString(jugadores)*/ + ", ataque=" + ataque
				+ ", defensa=" + defensa + ", overall=" + overall + "]";
	}
	
	public TableModel getTableModel() {
		if(modelo == null) {
			modelo = new ModeloTablaEquipo();
		}
		return modelo;
	}
	
	public TableModel getModeloFinanzas() {
		if(modeloFinanzas == null) {
			modeloFinanzas = new ModeloFinanzasEquipo();
		}
		return modeloFinanzas;
	}
	
	public class ModeloFinanzasEquipo implements TableModel{
		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Edad";
			case 2: return "Años de contrato";
			case 3: return "Salario";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return jugadores.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Jugador j = jugadores.get(rowIndex);
			switch(columnIndex) {
			case 0: return j.nombre;
			case 1: return j.anyoNac;
			case 2: return j.anyosContratoRestantes;
			case 3: return j.salario;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		
	}
	
	private class ModeloTablaEquipo implements TableModel {

		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 11;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Posicion";
			case 2: return "Rol";
			case 3: return "Overall";
			case 4: return "PPP";
			case 5: return "APP";
			case 6: return "RPP";
			case 7: return "Puntos Totales";
			case 8: return "Asistencias Totales";
			case 9: return "Rebotes Totales";
			case 10: return "Edad";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return jugadores.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Jugador j = jugadores.get(rowIndex);
			switch(columnIndex) {
			case 0: return j.nombre;
			case 1: return j.posicion;
			case 2: return j.rol;
			case 3: return j.overall;
			case 4: return j.getPuntosPorPartido();
			case 5: return j.getAsistenciasPorPartido();
			case 6: return j.getRebotesPorPartido();
			case 7: return j.puntosPartido;
			case 8: return j.asistenciasPartido;
			case 9: return j.rebotesPartido;
			case 10: return j.anyoNac;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		
	}

}
