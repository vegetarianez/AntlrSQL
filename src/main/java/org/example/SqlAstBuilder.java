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

        SqlNode fromNode = null;
        if (ctx.tableSource() != null) {
            fromNode = visit(ctx.tableSource().tableReference());
        }

        List<SqlNode> joins = new ArrayList<>();
        if (ctx.tableSource() != null) {
            for (SqlParser.JoinPartContext joinCtx : ctx.tableSource().joinPart()) {
                joins.add(visit(joinCtx));
            }
        }

        SqlNode whereNode = (ctx.whereClause() != null) ? visit(ctx.whereClause()) : null;

        List<SqlNode> groupByNodes = new ArrayList<>();
        if (ctx.groupByClause() != null) {
            List<SqlNode> groupItems = new ArrayList<>();
            for (SqlParser.ExpressionContext exprCtx : ctx.groupByClause().expression()) {
                groupItems.add(visit(exprCtx));
            }
            groupByNodes.add(new GroupByNode(groupItems));
        }

        List<SqlNode> havingNodes = new ArrayList<>();
        if (ctx.havingClause() != null) {
            havingNodes.add(new HavingNode(visit(ctx.havingClause().expression())));
        }

        List<SqlNode> orderByNodes = new ArrayList<>();
        if (ctx.orderByClause() != null) {
            for (SqlParser.OrderByItemContext orderCtx : ctx.orderByClause().orderByItem()) {
                orderByNodes.add(visit(orderCtx));
            }
        }

        Integer limit = (ctx.limitClause() != null) ? Integer.parseInt(ctx.limitClause().NUMBER().getText()) : null;

        Integer offset = (ctx.offsetClause() != null) ? Integer.parseInt(ctx.offsetClause().NUMBER().getText()) : null;

        return new SelectStatementNode(columns, fromNode, joins, whereNode, groupByNodes, havingNodes, orderByNodes, limit, offset);
    }

    @Override
    public SqlNode visitJoinPart(SqlParser.JoinPartContext ctx) {
        String joinType = (ctx.joinType() != null) ? ctx.joinType().getText().toUpperCase() : "INNER";

        SqlNode tableNode = visit(ctx.tableReference());
        SqlNode condition = visit(ctx.expression());

        return new JoinNode(joinType, tableNode, condition);
    }

    @Override
    public SqlNode visitTableReference(SqlParser.TableReferenceContext ctx) {
        SqlNode child;
        if (ctx.IDENTIFIER(0) != null) {
            child = new TableNode(ctx.IDENTIFIER(0).getText());
        } else {
            child = visit(ctx.selectStatement());
        }

        if (ctx.IDENTIFIER().size() > 1 || (ctx.IDENTIFIER(0) == null && ctx.IDENTIFIER().size() == 1)) {
            String alias = ctx.IDENTIFIER(ctx.IDENTIFIER().size() - 1).getText();
            return new AsNode(alias, child);
        }
        return child;
    }

    @Override
    public SqlNode visitSubqueryExpr(SqlParser.SubqueryExprContext ctx) {
        return new SubqueryNode(visit(ctx.selectStatement()));
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
        SqlNode expr = visit(ctx.expression());
        String alias = (ctx.IDENTIFIER() != null) ? ctx.IDENTIFIER().getText() : null;

        if (alias != null) {
            return new AsNode(alias, expr);
        }
        return expr;
    }

    @Override
    public SqlNode visitWhereClause(SqlParser.WhereClauseContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public SqlNode visitOrderByItem(SqlParser.OrderByItemContext ctx) {
        SqlNode expr = visit(ctx.expression());
        String order = "ASC";
        if (ctx.DESC() != null) {
            order = "DESC";
        }
        return new OrderByItemNode(expr, order);
    }

    @Override
    public SqlNode visitComparisonExpr(SqlParser.ComparisonExprContext ctx) {
        SqlNode left = visit(ctx.expression(0));
        String op = ctx.getChild(1).getText();
        SqlNode right = visit(ctx.expression(1));
        return new BinaryNode(left, op, right);
    }

    @Override
    public SqlNode visitMathMulDivExpr(SqlParser.MathMulDivExprContext ctx) {
        SqlNode left = visit(ctx.expression(0));
        String op = ctx.getChild(1).getText();
        SqlNode right = visit(ctx.expression(1));
        return new BinaryNode(left, op, right);
    }

    @Override
    public SqlNode visitMathAddSubExpr(SqlParser.MathAddSubExprContext ctx) {
        SqlNode left = visit(ctx.expression(0));
        String op = ctx.getChild(1).getText();
        SqlNode right = visit(ctx.expression(1));
        return new BinaryNode(left, op, right);
    }

    @Override
    public SqlNode visitLogicalExpr(SqlParser.LogicalExprContext ctx) {
        SqlNode left = visit(ctx.expression(0));
        String op = ctx.getChild(1).getText();
        SqlNode right = visit(ctx.expression(1));
        return new LogicalNode(left, op, right);
    }

    @Override
    public SqlNode visitFunctionCallExpr(SqlParser.FunctionCallExprContext ctx) {
        String funcName = ctx.IDENTIFIER().getText();
        List<SqlNode> args = new ArrayList<>();
        for (SqlParser.ExpressionContext e : ctx.expression()) {
            args.add(visit(e));
        }
        return new FunctionCallNode(funcName, args);
    }

    @Override
    public SqlNode visitColumnExpr(SqlParser.ColumnExprContext ctx) {
        return new ColumnNode(ctx.getText(), null);
    }

    @Override
    public SqlNode visitLiteralExpr(SqlParser.LiteralExprContext ctx) {
        return new LiteralNode(ctx.literal().getText());
    }

    @Override
    public SqlNode visitParenExpr(SqlParser.ParenExprContext ctx) {
        return new ParenthesesNode(visit(ctx.expression()));
    }
}