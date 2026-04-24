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
    private final List<SqlNode> joins;
    private final SqlNode whereClause;
    private final SqlNode groupByNode;
    private final SqlNode havingNode;
    private final List<SqlNode> orderBy;
    private final String limit;

    public SelectStatementNode(List<SqlNode> columns, String table, List<SqlNode> joins,
                               SqlNode whereClause, SqlNode groupByNode, SqlNode havingNode,
                               List<SqlNode> orderBy, String limit) {
        this.columns = columns;
        this.table = table;
        this.joins = joins;
        this.whereClause = whereClause;
        this.groupByNode = groupByNode;
        this.havingNode = havingNode;
        this.orderBy = orderBy;
        this.limit = limit;
    }

    @Override
    public List<SqlNode> getChildren() {
        List<SqlNode> children = new ArrayList<>(columns);
        if (joins != null) children.addAll(joins);
        if (whereClause != null) children.add(whereClause);
        if (groupByNode != null) children.add(groupByNode);
        if (havingNode != null) children.add(havingNode);
        if (orderBy != null) children.addAll(orderBy);
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SELECT (FROM " + table + ")");
        if (limit != null) {
            sb.append(" LIMIT ").append(limit);
        }
        return sb.toString();
    }
}

// --- Новые узлы для базовых операций ---

class JoinNode implements SqlNode {
    private final String type;
    private final String table;
    private final SqlNode condition;

    public JoinNode(String type, String table, SqlNode condition) {
        this.type = type;
        this.table = table;
        this.condition = condition;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Collections.singletonList(condition);
    }

    @Override
    public String toString() {
        return "JOIN (" + type + ") " + table;
    }
}

class GroupByNode implements SqlNode {
    private final List<SqlNode> items;

    public GroupByNode(List<SqlNode> items) {
        this.items = items;
    }

    @Override
    public List<SqlNode> getChildren() {
        return items;
    }

    @Override
    public String toString() {
        return "GROUP BY";
    }
}

class HavingNode implements SqlNode {
    private final SqlNode condition;

    public HavingNode(SqlNode condition) {
        this.condition = condition;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Collections.singletonList(condition);
    }

    @Override
    public String toString() {
        return "HAVING";
    }
}

class OrderByItemNode implements SqlNode {
    private final SqlNode expression;
    private final String order;

    public OrderByItemNode(SqlNode expression, String order) {
        this.expression = expression;
        this.order = order;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Collections.singletonList(expression);
    }

    @Override
    public String toString() {
        return "ORDER BY (" + order + ")";
    }
}

class FunctionCallNode implements SqlNode {
    private final String functionName;
    private final List<SqlNode> arguments;

    public FunctionCallNode(String functionName, List<SqlNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public List<SqlNode> getChildren() {
        return arguments;
    }

    @Override
    public String toString() {
        return "FUNCTION: " + functionName;
    }
}

// --- Существующие и исправленные узлы ---

class BinaryNode implements SqlNode {
    private final SqlNode left;
    private final String op;
    private final SqlNode right;

    public BinaryNode(SqlNode left, String op, SqlNode right) {
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
        return "OP: " + op;
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
class SubqueryNode implements SqlNode {
    private final SqlNode selectStatement;

    public SubqueryNode(SqlNode selectStatement) {
        this.selectStatement = selectStatement;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Collections.singletonList(selectStatement);
    }

    @Override
    public String toString() {
        return "SUBQUERY";
    }
}
class AllColumnsNode implements SqlNode {
    @Override
    public String toString() {
        return "COLUMN: * (all)";
    }
}

class LiteralNode implements SqlNode {
    private final String value;

    public LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LITERAL: " + value;
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