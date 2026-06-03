package org.example;

public class Main {

    public static void main(String[] args) {
        SqlEngine engine = new SqlEngine();

        engine.addTable("users", ExcelLoader.loadTable("/Users/win122333/IdeaProjects/AntlrSQL/src/main/java/org/example/data/users.xlsx"));
        engine.addTable("orders", ExcelLoader.loadTable("/Users/win122333/IdeaProjects/AntlrSQL/src/main/java/org/example/data/orders.xlsx"));

//        engine.addTable("users", new TableContext(
//            new String[]{"id", "username", "age"},
//            new Object[][]{
//                {1, "Влад", 25.0},
//                {2, "Егор", 17.0},
//                {3, "ыаыбва", 30.0},
//                {4, "чеснок", 40.0}
//            }
//        ));
//
//        engine.addTable("orders", new TableContext(
//            new String[]{"order_id", "user_id", "amount", "status"},
//            new Object[][]{
//                {101, 1, 500.0, "PAID"},
//                {102, 1, 50.0, "PENDING"},
//                {103, 3, 250.0, "PAID"},
//                {104, 2, 1000.0, "PAID"},
//                {105, 99, 777.0, "GHOST"}
//            }
//        ));

        engine.execute("""
                SELECT u2.username, o.order_id, o2.order_id
                FROM users u2
                JOIN orders o ON u2.id = o.user_id
                left join orders o2 ON u2.id = o2.user_id + 1
                where u2.username LIKE '%l%'
                ORDER BY u2.username DESC
                """);
    }
}