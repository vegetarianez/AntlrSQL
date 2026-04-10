package org.example;

import org.example.antlr.*;
import java.util.ArrayList;
import java.util.List;

public class SqlAstBuilder extends SqlBaseVisitor<SqlNode> {

    @Override
    public SqlNode visitParse(SqlParser.ParseContext ctx) {
        return visit(ctx.selectStatement());
    }

    @Override
    public SqlNode visitSelectStatement(SqlParser.SelectStatementContext ctx) {
        ColumnListNode columnList = (ColumnListNode) visit(ctx.selectList());
        List<SqlNode> columns = columnList.getColumns();

        String tableName = ctx.tableSource().getText();

        SqlNode whereNode = (ctx.whereClause() != null) ? visit(ctx.whereClause()) : null;

        return new SelectStatementNode(columns, tableName, whereNode);
    }

    @Override
    public SqlNode visitSelectAll(SqlParser.SelectAllContext ctx) {
        List<SqlNode> items = new ArrayList<>();
        items.add(new AllColumnsNode());
        return new ColumnListNode(items);
    }

    @Override
    public SqlNode visitSelectItems(SqlParser.SelectItemsContext ctx) {
        List<SqlNode> items = new ArrayList<>();
        for (SqlParser.ColumnItemContext colCtx : ctx.columnItem()) {
            items.add(visit(colCtx));
        }
        return new ColumnListNode(items);
    }

    @Override
    public SqlNode visitColumnItem(SqlParser.ColumnItemContext ctx) {
        String name = ctx.IDENTIFIER(0).getText();
        String alias = (ctx.IDENTIFIER().size() > 1) ? ctx.IDENTIFIER(1).getText() : null;
        return new ColumnNode(name, alias);
    }

    @Override
    public SqlNode visitWhereClause(SqlParser.WhereClauseContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public SqlNode visitComparisonExpr(SqlParser.ComparisonExprContext ctx) {
        String left = ctx.IDENTIFIER().getText();
        String op = ctx.getChild(1).getText();
        String right = ctx.literal().getText();
        return new ComparisonNode(left, op, right);
    }

    @Override
    public SqlNode visitLogicalExpr(SqlParser.LogicalExprContext ctx) {
        SqlNode left = visit(ctx.expression(0));
        String op = ctx.getChild(1).getText(); // AND или OR
        SqlNode right = visit(ctx.expression(1));
        return new LogicalNode(left, op, right);
    }

    @Override
    public SqlNode visitParenExpr(SqlParser.ParenExprContext ctx) {
        return new ParenthesesNode(visit(ctx.expression()));
    }
}