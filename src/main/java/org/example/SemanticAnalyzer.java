package org.example;

import java.util.List;
import java.util.Set;

public class SemanticAnalyzer {

    // Наша имитация базы данных (существующие таблицы)
    private static final Set<String> DATABASE_TABLES = Set.of(
        "customers", "orders", "users", "products"
    );

    // Разрешенные SQL-функции
    private static final Set<String> ALLOWED_FUNCTIONS = Set.of(
        "sum", "avg", "count", "min", "max"
    );

    public void analyze(SqlNode node) {
        if (node == null) return;

        // --- НОВАЯ ПРОВЕРКА: Агрегатные функции и GROUP BY ---
        if (node instanceof SelectStatementNode selectNode) {
            boolean hasAgg = hasAggregateFunction(selectNode.getColumns());
            boolean hasCol = hasRegularColumn(selectNode.getColumns());
            boolean hasGroup = hasRealGroupBy(selectNode.getGroupByNodes());

            // Если есть агрегатные функции И обычные колонки, но нет реального GROUP BY
            if (hasAgg && hasCol && !hasGroup) {
                throw new SemanticException("Ошибка: Запрос содержит агрегатные функции и обычные колонки, но отсутствует секция GROUP BY!");
            }
        }

        // 1. Правило проверки таблиц
        else if (node instanceof TableNode tableNode) {
            String tableName = tableNode.getName().toLowerCase();
            if (!DATABASE_TABLES.contains(tableName)) {
                throw new SemanticException("Таблица '" + tableNode.getName() + "' не существует в базе данных!");
            }
        }

        // 2. Правило проверки функций
        else if (node instanceof FunctionCallNode callNode) {
            String funcName = callNode.getFunctionName().toLowerCase();
            if (!ALLOWED_FUNCTIONS.contains(funcName)) {
                throw new SemanticException("Неизвестная функция '" + callNode.getFunctionName() + "'!");
            }
        }

        // Рекурсивно обходим всех потомков текущего узла
        List<SqlNode> children = node.getChildren();
        if (children != null) {
            for (SqlNode child : children) {
                analyze(child);
            }
        }
    }

    // --- Вспомогательные методы для проверки узлов ---

    // Ищет агрегатную функцию в списке узлов
    private boolean hasAggregateFunction(List<SqlNode> nodes) {
        if (nodes == null) return false;
        for (SqlNode node : nodes) {
            if (node instanceof FunctionCallNode callNode && ALLOWED_FUNCTIONS.contains(callNode.getFunctionName().toLowerCase())) {
                return true;
            }
            if (hasAggregateFunction(node.getChildren())) {
                return true;
            }
        }
        return false;
    }

    // Ищет обычную колонку (например, c.customer_name)
    private boolean hasRegularColumn(List<SqlNode> nodes) {
        if (nodes == null) return false;
        for (SqlNode node : nodes) {
            if (node instanceof ColumnNode || node instanceof AllColumnsNode) {
                return true;
            }
            if (hasRegularColumn(node.getChildren())) {
                return true;
            }
        }
        return false;
    }

    // Проверяет, написан ли GROUP BY по-настоящему, или это наша заглушка "TRUE"
    private boolean hasRealGroupBy(List<SqlNode> groupByNodes) {
        if (groupByNodes == null || groupByNodes.isEmpty()) return false;
        for (SqlNode groupNode : groupByNodes) {
            if (groupNode instanceof GroupByNode) {
                List<SqlNode> children = groupNode.getChildren();
                if (children.size() == 1 && children.get(0) instanceof LiteralNode lit) {
                    // Если это сгенерированная заглушка TRUE, значит реального GROUP BY нет
                    if ("TRUE".equalsIgnoreCase(lit.getValue()) || "'TRUE'".equalsIgnoreCase(lit.getValue())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}