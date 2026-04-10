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

        String sql = "SELECT * FROM users WHERE type = 'empl' AND (salary > 20000 OR age = 30)";

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