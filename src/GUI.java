import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.DateCellRenderer;
import org.jfree.ui.NumberCellRenderer;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener
{
	private GraphPanel theGraph;
	private JButton loadButton;
	private JButton exitButton;
	private JFileChooser fileChooser;
	private List<double[]> sunData = new ArrayList<double[]>();

	public GUI()
	{
		setTitle("Sunlight Tracker with RPi3");
		setLayout(new BorderLayout());

		theGraph = new GraphPanel();
		add(theGraph);
		loadButton.addActionListener(this);
		exitButton.addActionListener(this);

		pack();
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private class GraphPanel extends JPanel implements ChartChangeListener
	{
		private int seriesCount = 0;
		private TimeSeriesCollection[] datasets = new TimeSeriesCollection[seriesCount];
		private TimeSeries[] series = new TimeSeries[seriesCount];
		private ChartPanel chartPanel;
		private DataTableModel model;
		private XYPlot plot;
		private JTable localTable;
		private boolean ignoreChange;

		public GraphPanel()
		{
			JPanel localPanel1 = new JPanel(new BorderLayout());
			JFreeChart localFreeChart = createChart();
			chartPanel = new ChartPanel(localFreeChart);
			chartPanel.setPreferredSize(new Dimension(800, 400));
			chartPanel.setDomainZoomable(true);
			chartPanel.setRangeZoomable(true);
			CompoundBorder localBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createEtchedBorder());
			chartPanel.setBorder(localBorder);
			localPanel1.add(chartPanel);

			JPanel localPanel2 = new JPanel(new BorderLayout());
			localPanel2.setPreferredSize(new Dimension(700, 100));
			localPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			model = new DataTableModel(seriesCount);
			localTable = new JTable(model);
			Object localObject = new DateCellRenderer(new SimpleDateFormat("HH:mm:ss (hh:mm:ss aa)"));
			NumberCellRenderer localRenderer = new NumberCellRenderer();
			localTable.getColumnModel().getColumn(1).setCellRenderer((TableCellRenderer)localObject);
			localTable.getColumnModel().getColumn(2).setCellRenderer(localRenderer);

			localPanel2.add(new JScrollPane(localTable));
			localPanel2.add(new GUI.ButtonPanel(), "East");
			localPanel1.add(localPanel2, "South");
			add(localPanel1);
		}

		private JFreeChart createChart()
		{
			String time = "Time of Day";
			String value = "Level of Brightness";

			JFreeChart output = ChartFactory.createTimeSeriesChart(null, time, value, null, true, true, false);
			plot = (XYPlot)output.getPlot();

			output.addChangeListener(this);
			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setDomainCrosshairVisible(true);
			plot.setDomainCrosshairLockedOnData(false);
			plot.setRangeCrosshairVisible(false);
			ChartUtilities.applyCurrentTheme(output);

			return output;
		}

		public void updateGraph()
		{
			ignoreChange = true;

			seriesCount = sunData.size();
			datasets = new TimeSeriesCollection[seriesCount];
			series = new TimeSeries[seriesCount];

			XYDataset[] datasetArray = new XYDataset[seriesCount];
			model = new DataTableModel(seriesCount);
			localTable.setModel(model);

			for (int i = 0; i < seriesCount; i++)
			{
				datasetArray[i] = getDataset(i, "Day " + (i + 1), new Minute(), 96);
				plot.setDataset(i, datasetArray[i]);
				plot.setRenderer(i, new XYLineAndShapeRenderer(true, false));
				localTable.getModel().setValueAt(plot.getDataset(i).getSeriesKey(0), i, 0);
				localTable.getModel().setValueAt(new Double("0.00"), i, 1);
				localTable.getModel().setValueAt(new Double("0.00"), i, 2);
			}

			Object localObject = new DateCellRenderer(new SimpleDateFormat("HH:mm:ss (hh:mm:ss aa)"));
			NumberCellRenderer localRenderer = new NumberCellRenderer();
			localTable.getColumnModel().getColumn(1).setCellRenderer((TableCellRenderer)localObject);
			localTable.getColumnModel().getColumn(2).setCellRenderer(localRenderer);

			ignoreChange = false;
		}

		private XYDataset getDataset(int index, String datasetName, RegularTimePeriod period, int domain)
		{
			series[index] = new TimeSeries(datasetName);
			RegularTimePeriod localTime = period;

			for (int i = 0; i < domain; i++)
			{
				series[index].add(localTime, sunData.get(index)[i]);
				localTime = localTime.next();
			}

			datasets[index] = new TimeSeriesCollection();
			datasets[index].addSeries(series[index]);

			return datasets[index];
		}

		@Override
		public void chartChanged(ChartChangeEvent e)
		{
			if (chartPanel == null)
			{
				return;
			}

			if (ignoreChange)
			{
				return;
			}

			JFreeChart localChart = chartPanel.getChart();

			if (localChart != null)
			{
				XYPlot localPlot = (XYPlot)localChart.getPlot();
				XYDataset localDataset = localPlot.getDataset();
				double d1 = localPlot.getDomainCrosshairValue();
				long l1 = (long)d1;

				for (int i = 0; i < seriesCount; i++)
				{
					model.setValueAt(new Long(l1), i, 1);
					int[] integerArray = datasets[i].getSurroundingItems(0, l1);
					long l2 = 0, l3 = 0;
					double d2 = 0.0, d3 = 0.0, d4;
					TimeSeriesDataItem localDataItem;
					Number localNumber;

					if (integerArray[0] >= 0)
					{
						localDataItem = series[i].getDataItem(integerArray[0]);
						l2 = localDataItem.getPeriod().getMiddleMillisecond();
						localNumber = localDataItem.getValue();

						if (localNumber != null)
						{
							d2 = localNumber.doubleValue();
						}
					}

					if (integerArray[1] >= 0)
					{
						localDataItem = series[i].getDataItem(integerArray[1]);
						l3 = localDataItem.getPeriod().getMiddleMillisecond();
						localNumber = localDataItem.getValue();

						if (localNumber != null)
						{
							d3 = localNumber.doubleValue();
						}
					}

					if (l3 - l2 > 0)
					{
						d4 = d2 + (l1 - l2) / (l3 - l2) * (d3 - d2);
					}
					else
					{
						d4 = d2;
					}

					model.setValueAt(new Double(d4), i, 2);
				}
			}
		}
	}

	private class ButtonPanel extends JPanel
	{
		public ButtonPanel()
		{
			setLayout(new GridLayout(2, 1));
			setPreferredSize(new Dimension(100, 100));
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

			loadButton = new JButton("Load");
			exitButton = new JButton("Exit");

			fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
			fileChooser.setCurrentDirectory(new File("./data/"));
			fileChooser.setDialogTitle("Sunlight Data From RPi3");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(filter);

			add(loadButton);
			add(exitButton);
		}
	}

	public static void main(String[] args)
	{
		new GUI();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == loadButton)
		{
			int returnValue = fileChooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION)
			{
				String selectedFile = fileChooser.getSelectedFile().toString();

				try
				{
					sunData = ReadData.RD(selectedFile);
					theGraph.updateGraph();
					repaint();
				}
				catch (Exception x)
				{
					x.printStackTrace();
					JOptionPane.showMessageDialog(null, x.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if (e.getSource() == exitButton)
		{
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}
}
