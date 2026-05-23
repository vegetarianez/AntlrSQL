package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableContext {
    private final Map<String, Integer> columnIndices;
    private final Object[][] data;
    private final String[] columnNames;

    public TableContext(String[] columnNames, Object[][] data) {
        this.columnNames = columnNames;
        this.columnIndices = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            this.columnIndices.put(columnNames[i].toLowerCase(), i);
        }
        this.data = data;
    }

    public Object getValue(String columnName, int rowIndex) {
        Integer colIdx = columnIndices.get(columnName.toLowerCase());
        if (colIdx == null) {
            throw new RuntimeException("Колонка не найдена: " + columnName);
        }
        return data[rowIndex][colIdx];
    }

    public boolean hasColumn(String columnName) {
        return columnIndices.containsKey(columnName.toLowerCase());
    }

    public int getRowCount() { return data.length; }
    public String[] getColumnNames() { return columnNames; }

    public TableContext withAlias(String alias) {
        if (alias == null) return this;
        String[] newCols = new String[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            newCols[i] = alias + "." + columnNames[i];
        }
        return new TableContext(newCols, this.data);
    }

    public TableContext join(TableContext rightTable, String joinType, SqlNode onCondition, ExpressionEvaluator evaluator) {
        String[] newCols = new String[this.columnNames.length + rightTable.columnNames.length];
        System.arraycopy(this.columnNames, 0, newCols, 0, this.columnNames.length);
        System.arraycopy(rightTable.columnNames, 0, newCols, this.columnNames.length, rightTable.columnNames.length);

        List<Object[]> resultData = new ArrayList<>();
        TableContext tempContext = new TableContext(newCols, new Object[1][newCols.length]);

        // Массив, чтобы запоминать, какие строки из правой таблицы нашли пару
        boolean[] rightMatched = new boolean[rightTable.getRowCount()];

        String type = joinType.toUpperCase();

        // 2. Перемножаем строки
        for (int i = 0; i < this.getRowCount(); i++) {
            boolean leftMatched = false;
            for (int j = 0; j < rightTable.getRowCount(); j++) {
                Object[] combinedRow = new Object[newCols.length];
                System.arraycopy(this.data[i], 0, combinedRow, 0, this.columnNames.length);
                System.arraycopy(rightTable.data[j], 0, combinedRow, this.columnNames.length, rightTable.columnNames.length);

                tempContext.data[0] = combinedRow;

                Object match = evaluator.evaluate(onCondition, tempContext, 0);
                if (match instanceof Boolean && (Boolean) match) {
                    resultData.add(combinedRow);
                    leftMatched = true;
                    rightMatched[j] = true; // Отмечаем, что правая строка нашла пару
                }
            }
            // Поддержка LEFT JOIN и FULL JOIN (добиваем null-ами левую строку, если нет пары)
            if (!leftMatched && (type.contains("LEFT") || type.contains("FULL"))) {
                Object[] combinedRow = new Object[newCols.length];
                System.arraycopy(this.data[i], 0, combinedRow, 0, this.columnNames.length);
                resultData.add(combinedRow);
            }
        }

        // 3. Поддержка RIGHT JOIN и FULL JOIN
        // Проходимся по правой таблице и ищем "одиноких" (у кого rightMatched == false)
        if (type.contains("RIGHT") || type.contains("FULL")) {
            for (int j = 0; j < rightTable.getRowCount(); j++) {
                if (!rightMatched[j]) {
                    Object[] combinedRow = new Object[newCols.length];
                    // Левая часть остается пустой (null), копируем только правую
                    System.arraycopy(rightTable.data[j], 0, combinedRow, this.columnNames.length, rightTable.columnNames.length);
                    resultData.add(combinedRow);
                }
            }
        }

        return new TableContext(newCols, resultData.toArray(new Object[0][]));
    }
}