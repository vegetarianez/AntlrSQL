package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlEngine {
    private final Map<String, TableContext> database = new HashMap<>();
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    public void addTable(String name, TableContext table) {
        database.put(name.toLowerCase(), table);
    }

    private TableContext resolveTable(SqlNode node) {
        if (node instanceof TableNode t) {
            return database.get(t.getName().toLowerCase());
        }
        if (node instanceof AsNode asNode) {
            TableContext t = resolveTable(asNode.getChildren().get(0));
            String alias = asNode.toString().replace("AS: ", "").trim();
            return t.withAlias(alias);
        }
        throw new RuntimeException("Неизвестный узел таблицы: " + node);
    }

    public void execute(String sql) {
        System.out.println("Выполнение запроса: \n" + sql.trim());
        System.out.println("--------------------------------------------------");

        SqlNode ast = Parser.parse(sql);
        if (!(ast instanceof SelectStatementNode selectNode)) {
            throw new RuntimeException("Поддерживаются только SELECT запросы!");
        }

        TableContext context = resolveTable(selectNode.getFromNode());

        if (selectNode.getJoins() != null) {
            for (SqlNode joinNode : selectNode.getJoins()) {
                JoinNode jn = (JoinNode) joinNode;
                String type = jn.toString().replace("JOIN (", "").replace(")", "").trim();
                SqlNode rightTableNode = jn.getChildren().get(0);
                SqlNode onCondition = jn.getChildren().get(1);

                TableContext rightTable = resolveTable(rightTableNode);
                context = context.join(rightTable, type, onCondition, evaluator);
            }
        }

        SqlNode whereClause = selectNode.getWhereClause();
        List<SqlNode> selectedColumns = selectNode.getColumns();

        int matchCount = 0;

        for (int i = 0; i < context.getRowCount(); i++) {
            Object isMatch = evaluator.evaluate(whereClause, context, i);

            if (isMatch instanceof Boolean && (Boolean) isMatch) {
                matchCount++;
                StringBuilder rowOutput = new StringBuilder();
                boolean isSelectAll = selectedColumns.size() == 1 && selectedColumns.get(0) instanceof AllColumnsNode;

                if (isSelectAll) {
                    for (String colName : context.getColumnNames()) {
                        rowOutput.append(colName).append(": ").append(context.getValue(colName, i)).append(" | ");
                    }
                } else {
                    for (SqlNode colNode : selectedColumns) {
                        Object value = evaluator.evaluate(colNode, context, i);

                        String colName;
                        if (colNode instanceof AsNode asNode) {
                            colName = asNode.toString().replace("AS: ", "").trim();
                        } else if (colNode instanceof ColumnNode) {
                            colName = colNode.toString().replace("COLUMN: ", "").trim();
                        } else {
                            colName = "expr_result";
                        }

                        rowOutput.append(colName).append(": ").append(value).append(" | ");
                    }
                }
                System.out.println(rowOutput.substring(0, rowOutput.length() - 3));
            }
        }
        System.out.println("--------------------------------------------------");
        System.out.println("Найдено строк: " + matchCount + "\n");
    }
}