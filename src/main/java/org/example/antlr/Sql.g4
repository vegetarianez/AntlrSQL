grammar Sql;

SELECT: 'SELECT' | 'select';
FROM: 'FROM' | 'from';
WHERE: 'WHERE' | 'where';
AS: 'AS' | 'as';
AND: 'AND' | 'and';
OR: 'OR' | 'or';

STAR: '*';
COMMA: ',';
EQ: '=';
NEQ: '<>' | '!=';
GT: '>';
LT: '<';
GE: '>=';
LE: '<=';

NUMBER: [0-9]+ ('.' [0-9]+)?;
STRING: '\'' .*? '\'';
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;

WS: [ \t\r\n]+ -> skip;

parse: selectStatement EOF;

selectStatement: SELECT selectList FROM tableSource (WHERE whereClause)?;

selectList: STAR # selectAll
          | columnItem (COMMA columnItem)* # selectItems;

columnItem: IDENTIFIER (AS? IDENTIFIER)?;

tableSource: IDENTIFIER;

whereClause: expression;

expression: expression (AND | OR) expression  # logicalExpr
          | IDENTIFIER (EQ | NEQ | GT | LT | GE | LE) literal     # comparisonExpr
          | '(' expression ')'                # parenExpr
          ;

literal: NUMBER | STRING;