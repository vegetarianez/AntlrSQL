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

        SqlParser.TableReferenceContext tableRefCtx = ctx.tableSource().tableReference();
        String tableName = tableRefCtx.IDENTIFIER(0).getText();
        if (tableRefCtx.IDENTIFIER().size() > 1) { // Если есть алиас
            tableName += " AS " + tableRefCtx.IDENTIFIER(1).getText();
        }

        List<SqlNode> joins = new ArrayList<>();
        for (SqlParser.JoinPartContext joinCtx : ctx.tableSource().joinPart()) {
            joins.add(visit(joinCtx));
        }

        SqlNode whereNode = (ctx.whereClause() != null) ? visit(ctx.whereClause()) : null;

        SqlNode groupByNode = null;
        if (ctx.groupByClause() != null) {
            List<SqlNode> groupItems = new ArrayList<>();
            for (SqlParser.ExpressionContext exprCtx : ctx.groupByClause().expression()) {
                groupItems.add(visit(exprCtx));
            }
            groupByNode = new GroupByNode(groupItems);
        }

        SqlNode havingNode = null;
        if (ctx.havingClause() != null) {
            havingNode = new HavingNode(visit(ctx.havingClause().expression()));
        }

        List<SqlNode> orderByNodes = new ArrayList<>();
        if (ctx.orderByClause() != null) {
            for (SqlParser.OrderByItemContext orderCtx : ctx.orderByClause().orderByItem()) {
                orderByNodes.add(visit(orderCtx));
            }
        }

        String limit = (ctx.limitClause() != null) ? ctx.limitClause().NUMBER().getText() : null;

        return new SelectStatementNode(columns, tableName, joins, whereNode, groupByNode, havingNode, orderByNodes, limit);
    }

    @Override
    public SqlNode visitJoinPart(SqlParser.JoinPartContext ctx) {
        String joinType = (ctx.joinType() != null) ? ctx.joinType().getText().toUpperCase() : "INNER";

        // ИСПРАВЛЕНИЕ АЛИАСОВ В JOIN
        SqlParser.TableReferenceContext tableRefCtx = ctx.tableReference();
        String tableName = tableRefCtx.IDENTIFIER(0).getText();
        if (tableRefCtx.IDENTIFIER().size() > 1) {
            tableName += " AS " + tableRefCtx.IDENTIFIER(1).getText();
        }

        SqlNode condition = visit(ctx.expression());
        return new JoinNode(joinType, tableName, condition);
    }
    @Override
    public SqlNode visitSubqueryExpr(SqlParser.SubqueryExprContext ctx) {
        return new SubqueryNode(visit(ctx.selectStatement()));
    }

    @Override
    public SqlNode visitOrderByItem(SqlParser.OrderByItemContext ctx) {
        SqlNode expr = visit(ctx.expression());
        String order = "ASC"; // По умолчанию
        if (ctx.ASC() != null) order = "ASC";
        else if (ctx.DESC() != null) order = "DESC";

        return new OrderByItemNode(expr, order);
    }

    @Override
    public SqlNode visitFunctionCallExpr(SqlParser.FunctionCallExprContext ctx) {
        String funcName = ctx.IDENTIFIER().getText();
        List<SqlNode> args = new ArrayList<>();
        for (SqlParser.ExpressionContext exprCtx : ctx.expression()) {
            args.add(visit(exprCtx));
        }
        return new FunctionCallNode(funcName, args);
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
        // Поскольку теперь колонка это expression, нам нужно парсить её как выражение
        SqlNode expr = visit(ctx.expression());
        String alias = (ctx.IDENTIFIER() != null) ? ctx.IDENTIFIER().getText() : null;

        // Если это просто колонка (Identifier), сохраним старый вид отображения.
        // Иначе это может быть функция: COUNT(id) AS val
        if (expr instanceof ColumnNode && alias != null) {
            return new ColumnNode(((ColumnNode)expr).toString().replace("COLUMN: ", ""), alias);
        } else if (alias != null) {
            // Можно создать отдельный узел для алиасов сложных выражений,
            // но для простоты вернём просто выражение (алиасы отобразим как часть родителя, если нужно)
            // В базовой версии возвращаем само выражение.
        }
        return expr;
    }

    @Override
    public SqlNode visitWhereClause(SqlParser.WhereClauseContext ctx) {
        return visit(ctx.expression());
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