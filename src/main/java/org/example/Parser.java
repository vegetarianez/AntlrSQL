package org.example;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.example.antlr.*;


public class Parser {
    public static SqlNode parse(String sql) {
        SqlLexer lexer = new SqlLexer(CharStreams.fromString(sql));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        SqlParser parser = new SqlParser(tokens);

        SqlParser.ParseContext tree = parser.parse();

        SqlAstBuilder builder = new SqlAstBuilder();
        return builder.visit(tree);
    }
}