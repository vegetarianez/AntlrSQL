package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
//        String sql = """
//            SELECT id, name AS username, age
//            FROM users
//            WHERE age > 18 AND status = 'active'
//            """;

//        String sql = "SELECT id, name AS username, age FROM users WHERE age > 18 OR status = 'active'";

//        String sql = "SELECT * FROM users WHERE age > 18 AND status = 'active'";

//        String sql = "SELECT * FROM users";

//        String sql = "SELECT u, b + 2 FROM users WHERE a * 2 > b + 3 AND a + b = 5";

//        String sql = """
//                SELECT 2 * 2, c.customer_name * 2, SUM(o.total_amount) as total_spent
//                FROM Customers c
//                JOIN Orders o ON o.customer_id = (SELECT u.statementId FROM user u)
//                WHERE o.order_date >= '2023-01-01'
//                GROUP BY c.customer_name
//                HAVING SUM(o.total_amount) > (
//                    SELECT AVG(total_amount) FROM Orders
//                )
//                ORDER BY total_spent DESC;
//                """;

//        String sql = """
//                SELECT c.customer_name, SUM(o.total_amount) as total_spent
//                FROM Customers c
//                JOIN Orders o ON c.customer_id = o.customer_id
//                WHERE o.order_date >= '2023-01-01'
//                GROUP BY c.customer_name
//                HAVING SUM(o.total_amount) > (
//                    SELECT AVG(total_amount) FROM Orders
//                )
//                ORDER BY total_spent DESC
//                LIMIT 10 OFFSET 0;
//                """;

        String sql = """
    SELECT UPPER(customer_name) 
    FROM customers;
    """;
//        String sql = """
//                SELECT id, status, created_at, amount
//                FROM latest_transactions
//                WHERE status = 'processed'
//                ORDER BY created_at DESC
//                LIMIT 10
//                offset 5;
//                """;

        try {
            // 1. Синтаксический анализ (построение дерева)
            SqlNode ast = Parser.parse(sql);

            // 2. Семантический анализ (проверка смысла)
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(ast);
            System.out.println("✅ Семантический анализ пройден успешно!\n");

            // 3. Вывод AST
            System.out.println("SQL AST Structure:");
            System.out.println("==================");
            List<String> treeLines = ast.getTree();
            for (String line : treeLines) {
                System.out.println(line);
            }

        } catch (SemanticException e) {
            System.err.println("❌ Ошибка семантики: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Ошибка парсинга: " + e.getMessage());
            e.printStackTrace();
        }
    }
}