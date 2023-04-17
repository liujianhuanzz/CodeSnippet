grammar Expr;

expr: term (('+'|'-') term)*;

term: factor (('*'|'/') factor)*;

factor: INT | '(' expr ')' ;

INT: [0-9]+ ;

WS : [ \t\r\n]+ -> skip;