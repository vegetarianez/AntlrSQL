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

//        String sql = "SELECT * FROM users WHERE a * 2 > b + 3";

//        String sql = "SELECT u, b + 2 FROM users WHERE a * 2 > b + 3 AND a + b = 5";

//        String sql = """
//                SELECT c.customer_name, SUM(o.total_amount) as total_spent
//                FROM Customers c
//                JOIN Orders o ON c.customer_id = o.customer_id
//                WHERE o.order_date >= '2023-01-01'
//                GROUP BY c.customer_name
//                HAVING SUM(o.total_amount) > (
//                    SELECT AVG(total_amount) FROM Orders
//                )
//                ORDER BY total_spent DESC;
//                """;
        String sql = """
                SELECT id, status, created_at, amount
                FROM latest_transactions
                WHERE status = 'processed'
                ORDER BY created_at DESC
                LIMIT 10;
                """;

        try {
            SqlNode ast = Parser.parse(sql);

            System.out.println("SQL AST Structure:");
            System.out.println("==================");

            List<String> treeLines = ast.getTree();
            for (String line : treeLines) {
                System.out.println(line);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}