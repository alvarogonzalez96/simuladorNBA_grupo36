package presentacion;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import negocio.Jugador;
import negocio.LigaManager;

public class PanelLideres extends PanelTab {

	private JPanel panelPrincipal;
	private ChartPanel panelPuntos, panelRebotes, panelAsistencias;
	
	private JFreeChart chartPuntos, chartRebotes, chartAsistencias;
	private CategoryDataset datasetPuntos, datasetRebotes, datasetAsistencias;
	
	private Jugador[] puntos, rebotes, asistencias;
	
	public PanelLideres() {
		super();
	}
	
	@Override
	protected void initComponentes() {
		
	}

	@Override
	protected void crearPaneles() {
		panelPrincipal = new JPanel(new GridLayout(1,3));
		
		cargarDatasets();
		
		chartPuntos = ChartFactory.createBarChart("Puntos", "Temporada", "Puntos anotados", datasetPuntos);
		chartRebotes = ChartFactory.createBarChart("Rebotes", "Temporada", "Rebotes atrapados", datasetRebotes);
		chartAsistencias = ChartFactory.createBarChart("Asistencias", "Temporada", "Asistencias repartidas", datasetAsistencias);		
		
		panelPuntos = new ChartPanel(chartPuntos);
		panelRebotes = new ChartPanel(chartRebotes);
		panelAsistencias = new ChartPanel(chartAsistencias);
		
		panelPrincipal.add(panelPuntos);
		panelPrincipal.add(panelRebotes);
		panelPrincipal.add(panelAsistencias);
		
		add(panelPrincipal);
	}

	@Override
	protected void setListeners() {}

	@Override
	protected void seleccionado() {
		cargarDatasets();

		CategoryPlot plotPuntos = (CategoryPlot) chartPuntos.getPlot();
		plotPuntos.setDataset(datasetPuntos);
		CategoryPlot plotRebotes = (CategoryPlot) chartRebotes.getPlot();
		plotRebotes.setDataset(datasetRebotes);
		CategoryPlot plotAsistencias = (CategoryPlot) chartAsistencias.getPlot();
		plotAsistencias.setDataset(datasetAsistencias);
		
		BarRenderer rendererPuntos = ((BarRenderer) plotPuntos.getRenderer());
		BarRenderer rendererRebotes = ((BarRenderer) plotRebotes.getRenderer());
		BarRenderer rendererAsistencias = ((BarRenderer) plotAsistencias.getRenderer());
		for(int i = 0; i < 5; i++) {
			rendererPuntos.setSeriesPaint(i, LigaManager.equipos[puntos[i].getTid()].getColorPrimario());
			rendererRebotes.setSeriesPaint(i, LigaManager.equipos[rebotes[i].getTid()].getColorPrimario());
			rendererAsistencias.setSeriesPaint(i, LigaManager.equipos[asistencias[i].getTid()].getColorPrimario());
			
		}
		
		repaint();
	}
	
	private void cargarDatasets() {
		puntos = LigaManager.getMejoresEn(0);
		rebotes = LigaManager.getMejoresEn(1);
		asistencias = LigaManager.getMejoresEn(2);
		
		DefaultCategoryDataset dataP, dataR, dataA;
		dataP = new DefaultCategoryDataset();
		dataR = new DefaultCategoryDataset();
		dataA = new DefaultCategoryDataset();
		
		for(int i = 0; i < 5; i++) {
			dataP.addValue((Number) puntos[i].getPuntosTemporada(), puntos[i].getNombre(), LigaManager.anyo);
			dataR.addValue((Number) rebotes[i].getRebotesTemporada(), rebotes[i].getNombre(), LigaManager.anyo);
			dataA.addValue((Number) asistencias[i].getAsistenciasTemporada(), asistencias[i].getNombre(), LigaManager.anyo);
		}
		
		datasetPuntos = dataP;
		datasetRebotes = dataR;
		datasetAsistencias = dataA;
	}

}
