package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.Collection;

public class BaseTable implements Table {
    private Collection<Column> data;

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {

    }

    @Override
    public Collection<String> getColumnNames() {
        return null;
    }

    @Override
    public Collection<String> getColumnData(String column) {
        return null;
    }

    @Override
    public int getRowsCount() {
        return 0;
    }
}
