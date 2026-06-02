package org.example;

public class Main {

    public static void main(String[] args) {
        SqlEngine engine = new SqlEngine();

        engine.addTable("users", new TableContext(
            new String[]{"id", "username", "age"},
            new Object[][]{
                {1, "Влад", 25.0},
                {2, "Егор", 17.0},
                {3, "ыаыбва", 30.0},
                {4, "чеснок", 40.0}
            }
        ));

        engine.addTable("orders", new TableContext(
            new String[]{"order_id", "user_id", "amount", "status"},
            new Object[][]{
                {101, 1, 500.0, "PAID"},
                {102, 1, 50.0, "PENDING"},
                {103, 3, 250.0, "PAID"},
                {104, 2, 1000.0, "PAID"},
                {105, 99, 777.0, "GHOST"}
            }
        ));

        engine.execute("""
                SELECT u.username, o.order_id
                FROM users u
                JOIN orders o ON u.id = o.user_id
                """);
    }
}