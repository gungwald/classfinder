import javax.swing.table.DefaultTableModel;

public class SearchResultsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    public SearchResultsTableModel() {
        super();
        this.setDataVector(new Object[0][4], new String[] { "Container Type", "File", "Major Version", "Java Version" });
    }

}
