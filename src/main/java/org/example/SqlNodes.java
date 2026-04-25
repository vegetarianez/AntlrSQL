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
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                SqlNode child = children.get(i);
                if (child == null) continue;
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
        }
        return result;
    }
}

class SelectStatementNode implements SqlNode {
    private final List<SqlNode> columns;
    private final SqlNode fromNode;
    private final List<SqlNode> joins;
    private final SqlNode whereClause;
    private final List<SqlNode> groupByNodes;
    private final List<SqlNode> havingNodes;
    private final List<SqlNode> orderBy;
    private final Integer limit;
    private final Integer offset;

    public SelectStatementNode(List<SqlNode> columns, SqlNode fromNode, List<SqlNode> joins,
                               SqlNode whereClause, List<SqlNode> groupByNodes, List<SqlNode> havingNodes,
                               List<SqlNode> orderBy, Integer limit, Integer offset) {
        this.columns = columns;
        this.fromNode = fromNode;
        this.joins = joins;
        this.whereClause = whereClause != null ? whereClause : new LiteralNode("TRUE");
        this.groupByNodes = (groupByNodes != null && !groupByNodes.isEmpty())
                ? groupByNodes
                : Collections.singletonList(new GroupByNode(Collections.singletonList(new LiteralNode("TRUE"))));
        this.havingNodes = havingNodes;
        this.orderBy = orderBy;
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public List<SqlNode> getChildren() {
        List<SqlNode> children = new ArrayList<>(columns);
        if (fromNode != null) children.add(fromNode);
        if (joins != null) children.addAll(joins);
        if (whereClause != null) children.add(whereClause);
        if (groupByNodes != null) children.addAll(groupByNodes);
        if (havingNodes != null) children.addAll(havingNodes);
        if (orderBy != null) children.addAll(orderBy);
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SELECT");
        if (limit != null) sb.append(" (LIMIT ").append(limit).append(")");
        if (offset != null) sb.append(" (OFFSET ").append(offset).append(")");
        return sb.toString();
    }
}

class TableNode implements SqlNode {
    private final String name;

    public TableNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TABLE: " + name;
    }
}

class AsNode implements SqlNode {
    private final String alias;
    private final SqlNode child;

    public AsNode(String alias, SqlNode child) {
        this.alias = alias;
        this.child = child;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Collections.singletonList(child);
    }

    @Override
    public String toString() {
        return "AS: " + alias;
    }
}

class JoinNode implements SqlNode {
    private final String type;
    private final SqlNode tableNode;
    private final SqlNode condition;

    public JoinNode(String type, SqlNode tableNode, SqlNode condition) {
        this.type = type;
        this.tableNode = tableNode;
        this.condition = condition;
    }

    @Override
    public List<SqlNode> getChildren() {
        return Arrays.asList(tableNode, condition);
    }

    @Override
    public String toString() {
        return "JOIN (" + type + ")";
    }
}

class GroupByNode implements SqlNode {
    private final List<SqlNode> items;
    public GroupByNode(List<SqlNode> items) { this.items = items; }
    @Override public List<SqlNode> getChildren() { return items; }
    @Override public String toString() { return "GROUP BY"; }
}

class HavingNode implements SqlNode {
    private final SqlNode condition;
    public HavingNode(SqlNode condition) { this.condition = condition; }
    @Override public List<SqlNode> getChildren() { return Collections.singletonList(condition); }
    @Override public String toString() { return "HAVING"; }
}

class OrderByItemNode implements SqlNode {
    private final SqlNode expression;
    private final String order;
    public OrderByItemNode(SqlNode expression, String order) {
        this.expression = expression;
        this.order = order;
    }
    @Override public List<SqlNode> getChildren() { return Collections.singletonList(expression); }
    @Override public String toString() { return "ORDER BY (" + order + ")"; }
}

class FunctionCallNode implements SqlNode {
    private final String functionName;
    private final List<SqlNode> arguments;
    public FunctionCallNode(String functionName, List<SqlNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }
    @Override public List<SqlNode> getChildren() { return arguments; }
    @Override public String toString() { return "FUNCTION: " + functionName; }
}

class BinaryNode implements SqlNode {
    private final SqlNode left;
    private final String op;
    private final SqlNode right;
    public BinaryNode(SqlNode left, String op, SqlNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }
    @Override public List<SqlNode> getChildren() { return Arrays.asList(left, right); }
    @Override public String toString() { return "OP: " + op; }
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
    @Override public List<SqlNode> getChildren() { return Arrays.asList(left, right); }
    @Override public String toString() { return "LOGIC: " + op.toUpperCase(); }
}

class ColumnNode implements SqlNode {
    private final String name;
    private final String alias;
    public ColumnNode(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }
    @Override public String toString() {
        return alias == null ? "COLUMN: " + name : "COLUMN: " + name + " AS " + alias;
    }
}

class SubqueryNode implements SqlNode {
    private final SqlNode selectStatement;
    public SubqueryNode(SqlNode selectStatement) { this.selectStatement = selectStatement; }
    @Override public List<SqlNode> getChildren() { return Collections.singletonList(selectStatement); }
    @Override public String toString() { return "SUBQUERY"; }
}

class AllColumnsNode implements SqlNode {
    @Override public String toString() { return "COLUMN: * (all)"; }
}

class LiteralNode implements SqlNode {
    private final String value;
    public LiteralNode(String value) { this.value = value; }
    @Override public String toString() { return "LITERAL: " + value; }
}

class ParenthesesNode implements SqlNode {
    private final SqlNode expression;
    public ParenthesesNode(SqlNode expression) { this.expression = expression; }
    @Override public List<SqlNode> getChildren() { return Collections.singletonList(expression); }
    @Override public String toString() { return "( )"; }
}

class ColumnListNode implements SqlNode {
    private final List<SqlNode> columns;
    public ColumnListNode(List<SqlNode> columns) { this.columns = columns; }
    public List<SqlNode> getColumns() { return columns; }
    @Override public String toString() { return "COLUMNS"; }
}