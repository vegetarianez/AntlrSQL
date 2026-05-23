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

    // Главный метод, запускающий анализ
    public void analyze(SqlNode node) {
        if (node == null) return;

        // 1. Правило проверки таблиц
        if (node instanceof TableNode tableNode) {
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
}