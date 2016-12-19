import java.util.Vector;

import javax.swing.table.DefaultTableModel;


public class JavaClassResultsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    public JavaClassResultsTableModel() {
        // TODO Auto-generated constructor stub
    }

    public JavaClassResultsTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
        // TODO Auto-generated constructor stub
    }

    public JavaClassResultsTableModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
        // TODO Auto-generated constructor stub
    }

    public JavaClassResultsTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
        // TODO Auto-generated constructor stub
    }

    public JavaClassResultsTableModel(Vector data, Vector columnNames) {
        super(data, columnNames);
        // TODO Auto-generated constructor stub
    }

    public JavaClassResultsTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        // TODO Auto-generated constructor stub
    }

}
