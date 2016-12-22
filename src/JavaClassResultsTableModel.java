import javax.swing.table.DefaultTableModel;


public class JavaClassResultsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    public JavaClassResultsTableModel() {
    }

    public JavaClassResultsTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public JavaClassResultsTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public JavaClassResultsTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

}
