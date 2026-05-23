package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static TableContext resolveTable(SqlNode node, Map<String, TableContext> db) {
        if (node instanceof TableNode t) {
            return db.get(t.getName().toLowerCase());
        }
        if (node instanceof AsNode asNode) {
            TableContext t = resolveTable(asNode.getChildren().get(0), db);
            String alias = asNode.toString().replace("AS: ", "").trim();
            return t.withAlias(alias);
        }
        throw new RuntimeException("Неизвестный узел таблицы: " + node);
    }

    public static void main(String[] args) {
        String sql = """
                SELECT u.username, o.order_id * 2
                FROM users u
                JOIN orders o ON u.id = o.user_id
                """;

        SqlNode ast = Parser.parse(sql);
        SelectStatementNode selectNode = (SelectStatementNode) ast;
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        // --- СОЗДАЕМ МИНИ-БАЗУ ДАННЫХ ---
        Map<String, TableContext> database = new HashMap<>();

        database.put("users", new TableContext(
            new String[]{"id", "username", "age"},
            new Object[][]{
                {1, "Alice", 25.0},
                {2, "Bob", 17.0},
                {3, "Charlie", 30.0},
                {4, "Dave", 40.0}
            }
        ));

        database.put("orders", new TableContext(
            new String[]{"order_id", "user_id", "amount", "status"},
            new Object[][]{
                {101, 1, 500.0, "PAID"},
                {102, 1, 50.0, "PENDING"},
                {103, 3, 250.0, "PAID"},
                {104, 2, 1000.0, "PAID"},
                {105, 99, 777.0, "GHOST"}
            }
        ));

        // --- 1. ДОСТАЕМ ГЛАВНУЮ ТАБЛИЦУ (FROM) ---
        TableContext context = resolveTable(selectNode.getFromNode(), database);

        // --- 2. ПРИМЕНЯЕМ ВСЕ JOINS ---
        if (selectNode.getJoins() != null) {
            for (SqlNode joinNode : selectNode.getJoins()) {
                JoinNode jn = (JoinNode) joinNode;
                String type = jn.toString().replace("JOIN (", "").replace(")", "").trim(); // INNER, LEFT...
                SqlNode rightTableNode = jn.getChildren().get(0);
                SqlNode onCondition = jn.getChildren().get(1);

                TableContext rightTable = resolveTable(rightTableNode, database);

                context = context.join(rightTable, type, onCondition, evaluator);
            }
        }

        // --- 3. ФИЛЬТРУЕМ И ВЫВОДИМ РЕЗУЛЬТАТ ---
        SqlNode whereClause = selectNode.getWhereClause();
        List<SqlNode> selectedColumns = selectNode.getColumns();

        System.out.println("Результат выполнения запроса:");
        for (int i = 0; i < context.getRowCount(); i++) {
            Object isMatch = evaluator.evaluate(whereClause, context, i);

            if (isMatch instanceof Boolean && (Boolean) isMatch) {
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
    }
}