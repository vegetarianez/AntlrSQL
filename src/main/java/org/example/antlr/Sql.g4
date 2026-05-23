grammar Sql;

// --- Ключевые слова ---
SELECT: 'SELECT' | 'select';
FROM: 'FROM' | 'from';
WHERE: 'WHERE' | 'where';
AS: 'AS' | 'as';
AND: 'AND' | 'and';
OR: 'OR' | 'or';
NOT: 'NOT' | 'not';

JOIN: 'JOIN' | 'join';
ON: 'ON' | 'on';
INNER: 'INNER' | 'inner';
LEFT: 'LEFT' | 'left';
RIGHT: 'RIGHT' | 'right';
FULL: 'FULL' | 'full';
OUTER: 'OUTER' | 'outer';
GROUP: 'GROUP' | 'group';
BY: 'BY' | 'by';
HAVING: 'HAVING' | 'having';
ORDER: 'ORDER' | 'order';
ASC: 'ASC' | 'asc';
DESC: 'DESC' | 'desc';
LIMIT: 'LIMIT' | 'limit';
OFFSET: 'OFFSET' | 'offset';

LIKE: 'LIKE' | 'like';
IN: 'IN' | 'in';
IS: 'IS' | 'is';
NULL_VAL: 'NULL' | 'null'; // Назвали NULL_VAL, чтобы не было конфликтов в Java

STAR: '*';
DIV: '/';
PLUS: '+';
MINUS: '-';
COMMA: ',';
EQ: '=';
NEQ: '<>' | '!=';
GT: '>';
LT: '<';
GE: '>=';
LE: '<=';
DOT: '.';
SEMI: ';';

NUMBER: [0-9]+ ('.' [0-9]+)?;
STRING: '\'' .*? '\'';
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;

WS: [ \t\r\n]+ -> skip;

parse: selectStatement SEMI? EOF;

selectStatement: SELECT selectList
                 FROM tableSource
                 (WHERE whereClause)?
                 (GROUP BY groupByClause)?
                 (HAVING havingClause)?
                 (ORDER BY orderByClause)?
                 (LIMIT limitClause)?
                 (OFFSET offsetClause)?;

selectList: STAR # selectAll
          | columnItem (COMMA columnItem)* # selectItems;

columnItem: expression (AS? IDENTIFIER)?;

tableSource: tableReference (joinPart)*;

joinPart: joinType? JOIN tableReference ON expression;

joinType: INNER
        | LEFT OUTER?
        | RIGHT OUTER?
        | FULL OUTER?;

whereClause: expression;

groupByClause: expression (COMMA expression)*;

havingClause: expression;

orderByClause: orderByItem (COMMA orderByItem)*;

orderByItem: expression (ASC | DESC)?;

limitClause: NUMBER;

offsetClause: NUMBER;

tableReference: (IDENTIFIER | '(' selectStatement ')') (AS? IDENTIFIER)?;

expression: expression (STAR | DIV) expression                            # mathMulDivExpr
          | expression (PLUS | MINUS) expression                          # mathAddSubExpr
          | expression (EQ | NEQ | GT | LT | GE | LE) expression          # comparisonExpr
          | expression NOT? LIKE expression                               # likeExpr
          | expression NOT? IN '(' (selectStatement | expression (COMMA expression)*) ')' # inExpr
          | expression IS NOT? NULL_VAL                                   # isNullExpr
          | expression (AND | OR) expression                              # logicalExpr
          | IDENTIFIER '(' (expression (COMMA expression)*)? ')'          # functionCallExpr
          | IDENTIFIER (DOT IDENTIFIER)* # columnExpr
          | literal                                                       # literalExpr
          | '(' expression ')'                                            # parenExpr
          | '(' selectStatement ')'                                       # subqueryExpr
          ;

literal: NUMBER | STRING | NULL_VAL;