package org.example;

import java.util.List;

public class ExpressionEvaluator {

    public Object evaluate(SqlNode node, TableContext context, int rowIndex) {
        if (node instanceof AsNode asNode) {
            return evaluate(asNode.getChildren().get(0), context, rowIndex);
        }
        if (node instanceof LiteralNode lit) {
            String val = lit.toString().replace("LITERAL: ", "").replace("'", "");
            if (val.equalsIgnoreCase("null")) return null;
            if (val.equalsIgnoreCase("true")) return true;
            if (val.equalsIgnoreCase("false")) return false;
            try {
                return Double.parseDouble(val);
            } catch (NumberFormatException e) {
                return val;
            }
        }

        if (node instanceof LikeNode like) {
            Object leftObj = evaluate(like.getChildren().get(0), context, rowIndex);
            Object rightObj = evaluate(like.getChildren().get(1), context, rowIndex);

            if (leftObj == null || rightObj == null) return false;

            String text = leftObj.toString();
            String pattern = rightObj.toString().replace("'", "");

            String regex = pattern.replace("%", ".*").replace("_", ".");

            boolean matches = text.matches(regex);

            return like.toString().equals("NOT LIKE") ? !matches : matches;
        }

        // --- Обработка IS NULL и IS NOT NULL ---
        if (node instanceof IsNullNode isNullNode) {
            Object leftObj = evaluate(isNullNode.getChildren().get(0), context, rowIndex);
            boolean isNull = (leftObj == null);
            return isNullNode.isNot() ? !isNull : isNull;
        }

        // --- Обработка IN и NOT IN ---
        if (node instanceof InNode inNode) {
            Object leftObj = evaluate(inNode.getChildren().get(0), context, rowIndex);
            SqlNode rightNode = inNode.getChildren().get(1);

            if (leftObj == null) return false;

            boolean match = false;

            // Если это список значений IN (1, 2, 'Alice')
            if (rightNode instanceof ExpressionListNode listNode) {
                for (SqlNode itemNode : listNode.getChildren()) {
                    Object itemObj = evaluate(itemNode, context, rowIndex);

                    // Сравниваем через String, чтобы избежать ошибок с типами Double/String
                    if (itemObj != null && String.valueOf(leftObj).equals(String.valueOf(itemObj))) {
                        match = true;
                        break;
                    }
                }
            }
            else if (rightNode instanceof SelectStatementNode) {
                throw new RuntimeException("Подзапросы внутри IN пока не поддерживаются интерпретатором.");
            }

            return inNode.isNot() ? !match : match;
        }

        if (node instanceof ColumnNode col) {
            String colName = col.toString().replace("COLUMN: ", "").split(" AS ")[0].trim();


            if (context.hasColumn(colName)) {
                return context.getValue(colName, rowIndex);
            }
            if (colName.contains(".")) {
                colName = colName.substring(colName.lastIndexOf(".") + 1);
            }
            return context.getValue(colName, rowIndex);
        }


        if (node instanceof ParenthesesNode paren) {
            return evaluate(paren.getChildren().get(0), context, rowIndex);
        }

        if (node instanceof FunctionCallNode func) {
            String funcName = func.toString().replace("FUNCTION: ", "").toLowerCase();
            List<SqlNode> args = func.getChildren();

            if (funcName.equals("upper")) {
                Object arg = evaluate(args.get(0), context, rowIndex);
                return arg != null ? arg.toString().toUpperCase() : null;
            }
            if (funcName.equals("length")) {
                Object arg = evaluate(args.get(0), context, rowIndex);
                return arg != null ? arg.toString().length() : null;
            }
            throw new RuntimeException("Unknown function: " + funcName);
        }

        if (node instanceof BinaryNode bin) {
            String op = bin.toString().replace("OP: ", "").trim();
            Object left = evaluate(bin.getChildren().get(0), context, rowIndex);
            Object right = evaluate(bin.getChildren().get(1), context, rowIndex);

            if (left == null || right == null) return null;

            if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
                double l = ((Number) left).doubleValue();
                double r = ((Number) right).doubleValue();
                return switch (op) {
                    case "+" -> l + r;
                    case "-" -> l - r;
                    case "*" -> l * r;
                    case "/" -> l / r;
                    default -> throw new RuntimeException("Unknown math op: " + op);
                };
            }

            if (op.equals("=") || op.equals("!=") || op.equals("<>")) {
                boolean isEqual = String.valueOf(left).equals(String.valueOf(right));
                return op.equals("=") ? isEqual : !isEqual;
            }
            double l = ((Number) left).doubleValue();
            double r = ((Number) right).doubleValue();
            return switch (op) {
                case ">" -> l > r;
                case "<" -> l < r;
                case ">=" -> l >= r;
                case "<=" -> l <= r;
                default -> throw new RuntimeException("Unknown comparison op: " + op);
            };
        }

        if (node instanceof LogicalNode log) {
            String op = log.toString().replace("LOGIC: ", "").trim();
            Object left = evaluate(log.getChildren().get(0), context, rowIndex);
            Object right = evaluate(log.getChildren().get(1), context, rowIndex);

            boolean l = (Boolean) left;
            boolean r = (Boolean) right;

            return switch (op) {
                case "AND" -> l && r;
                case "OR" -> l || r;
                default -> throw new RuntimeException("Unknown logical op: " + op);
            };
        }

        throw new RuntimeException("Cannot evaluate node: " + node.getClass().getSimpleName());
    }
}