package org.example;

import java.util.*;

public class SqlNodes {

}


interface SqlNode {
    String toString();

    default List<SqlNode> getChildren() {
        return Collections.emptyList();
    }

    default List<String> getTree() {
        List<String> result = new ArrayList<>();
        result.add(this.toString());
        List<SqlNode> children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            SqlNode child = children.get(i);
            String prefix = (i == children.size() - 1) ? "└ " : "├ ";
            String childPrefix = (i == children.size() - 1) ? "  " : "│ ";

            List<String> childTree = child.getTree();
            for (int j = 0; j < childTree.size(); j++) {
                String line = childTree.get(j);
                if (j == 0) {
                    result.add(prefix + line);
                } else {
                    result.add(childPrefix + line);
                }
            }
        }
        return result;
    }
}

class SelectStatementNode implements SqlNode {
    private final List<SqlNode> columns;
    private final String table;
    private final SqlNode whereClause;

    public SelectStatementNode(List<SqlNode> columns, String table, SqlNode whereClause) {
        this.columns = columns;
        this.table = table;
        this.whereClause = whereClause;
    }

    @Override
    public List<SqlNode> getChildren() {
        List<SqlNode> children = new ArrayList<>(columns);
        if (whereClause != null) {
            children.add(whereClause);
        }
        return children;
    }

    @Override
    public String toString() {
        return "SELECT (FROM " + table + ")";
    }
}

class ColumnNode implements SqlNode {
    private final String name;
    private final String alias;

    public ColumnNode(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    @Override
    public String toString() {
        return alias == null ? "COLUMN: " + name : "COLUMN: " + name + " AS " + alias;
    }
}

class AllColumnsNode implements SqlNode {
    @Override
    public String toString() {
        return "COLUMN: * (all)";
    }
}

class LogicalNode implements SqlNode {
    private final SqlNode left;
    private final String op;
    private final SqlNode right;

    public LogicalNode(SqlNode left, String op, SqlNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Arrays.asList(left, right);
    }

    @Override
    public String toString() {
        return "LOGIC: " + op.toUpperCase();
    }
}

class ComparisonNode implements SqlNode {
    private final String column;
    private final String operator;
    private final String value;

    public ComparisonNode(String column, String operator, String value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        return "CONDITION: " + column + " " + operator + " " + value;
    }
}

class ParenthesesNode implements SqlNode {
    private final SqlNode expression;

    public ParenthesesNode(SqlNode expression) {
        this.expression = expression;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Collections.singletonList(expression);
    }

    @Override
    public String toString() {
        return "( )";
    }
}

class ColumnListNode implements SqlNode {
    private final List<SqlNode> columns;

    public ColumnListNode(List<SqlNode> columns) {
        this.columns = columns;
    }

    public List<SqlNode> getColumns() {
        return columns;
    }

    @Override
    public String toString() { return "COLUMNS"; }
}