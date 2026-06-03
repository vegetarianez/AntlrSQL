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

        List<Integer> validRowIndices = new java.util.ArrayList<>();

        // Сначала просто собираем номера строк, которые прошли WHERE
        for (int i = 0; i < context.getRowCount(); i++) {
            Object isMatch = evaluator.evaluate(whereClause, context, i);
            if (isMatch instanceof Boolean && (Boolean) isMatch) {
                validRowIndices.add(i);
            }
        }

        List<SqlNode> orderByNodes = selectNode.getOrderBy();
        if (orderByNodes != null && !orderByNodes.isEmpty()) {

            final TableContext finalContext = context;

            validRowIndices.sort((row1, row2) -> {
                for (SqlNode orderNode : orderByNodes) {
                    OrderByItemNode item = (OrderByItemNode) orderNode;

                    Object val1 = evaluator.evaluate(item.getExpression(), finalContext, row1);
                    Object val2 = evaluator.evaluate(item.getExpression(), finalContext, row2);

                    int cmp = compareValues(val1, val2);
                    if (cmp != 0) {
                        return item.isDesc() ? -cmp : cmp;
                    }
                }
                return 0;
            });
        }

        for (int i : validRowIndices) {
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

        System.out.println("--------------------------------------------------");
        System.out.println("Найдено строк: " + validRowIndices.size() + "\n");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compareValues(Object o1, Object o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        if (o1 instanceof Number n1 && o2 instanceof Number n2) {
            return Double.compare(n1.doubleValue(), n2.doubleValue());
        }

        return String.valueOf(o1).compareTo(String.valueOf(o2));
    }
}