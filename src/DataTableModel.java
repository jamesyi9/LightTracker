import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class DataTableModel extends AbstractTableModel implements TableModel
{
	private Object[][] data;
	private String seriesLabel = "";
	private String timeLabel = "Time:";
	private String valueLabel = "Brightness:";

	public DataTableModel(int entries)
	{
		data = new Object[entries][3];
	}

	@Override
	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public String getColumnName(int index)
	{
		switch (index)
		{
			case 0:
				return seriesLabel;
			case 1:
				return timeLabel;
			case 2:
				return valueLabel;
			default:
				return null;
		}
	}

	@Override
	public int getRowCount()
	{
		return data.length;
	}

	@Override
	public Object getValueAt(int index, int y)
	{
		return data[index][y];
	}

	@Override
	public void setValueAt(Object newEntry, int index, int y)
	{
		data[index][y] = newEntry;
		fireTableDataChanged();
	}
}
