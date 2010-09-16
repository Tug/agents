/* Generated By:JJTree&JavaCC: Do not edit this line. RPLParserConstants.java */
package uchicago.src.sim.parameter.rpl;

public interface RPLParserConstants {

  int EOF = 0;
  int SPACE = 1;
  int CONTINUATION = 4;
  int NEWLINE1 = 5;
  int NEWLINE = 6;
  int NEWLINE2 = 7;
  int NEWLINE3 = 8;
  int CRLF1 = 12;
  int DEDENT = 14;
  int INDENT = 15;
  int TRAILING_COMMENT = 16;
  int SINGLE_LINE_COMMENT = 17;
  int LPAREN = 18;
  int RPAREN = 19;
  int LBRACKET = 20;
  int RBRACKET = 21;
  int SEMICOLON = 22;
  int COMMA = 23;
  int DOT = 24;
  int COLON = 25;
  int EQUAL = 26;
  int DEF = 27;
  int PARAMETER = 28;
  int CONSTANT = 29;
  int TRUE = 30;
  int FALSE = 31;
  int NAME = 32;
  int LETTER = 33;
  int DECNUMBER = 34;
  int HEXNUMBER = 35;
  int OCTNUMBER = 36;
  int FLOAT = 37;
  int COMPLEX = 38;
  int EXPONENT = 39;
  int DIGIT = 40;
  int SINGLE_STRING = 45;
  int SINGLE_STRING2 = 46;
  int TRIPLE_STRING = 47;
  int TRIPLE_STRING2 = 48;

  int DEFAULT = 0;
  int FORCE_NEWLINE1 = 1;
  int FORCE_NEWLINE2 = 2;
  int FORCE_NEWLINE = 3;
  int INDENTING = 4;
  int INDENTATION_UNCHANGED = 5;
  int UNREACHABLE = 6;
  int IN_STRING11 = 7;
  int IN_STRING21 = 8;
  int IN_STRING13 = 9;
  int IN_STRING23 = 10;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\f\"",
    "<CONTINUATION>",
    "<NEWLINE1>",
    "<NEWLINE>",
    "<NEWLINE2>",
    "<NEWLINE3>",
    "\"\\t\"",
    "\" \"",
    "\"\\f\"",
    "<CRLF1>",
    "\"\"",
    "\"\"",
    "\"<INDENT>\"",
    "<TRAILING_COMMENT>",
    "<SINGLE_LINE_COMMENT>",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\";\"",
    "\",\"",
    "\".\"",
    "\":\"",
    "\"=\"",
    "\"def\"",
    "\"parameter\"",
    "\"constant\"",
    "\"true\"",
    "\"false\"",
    "<NAME>",
    "<LETTER>",
    "<DECNUMBER>",
    "<HEXNUMBER>",
    "<OCTNUMBER>",
    "<FLOAT>",
    "<COMPLEX>",
    "<EXPONENT>",
    "<DIGIT>",
    "<token of kind 41>",
    "<token of kind 42>",
    "<token of kind 43>",
    "<token of kind 44>",
    "\"\\\'\"",
    "\"\\\"\"",
    "\"\\\'\\\'\\\'\"",
    "\"\\\"\\\"\\\"\"",
    "\"\\\\\\r\\n\"",
    "<token of kind 50>",
    "<token of kind 51>",
    "<token of kind 52>",
    "\"\\r\\n\"",
    "\"\\n\"",
    "\"\\r\"",
    "<token of kind 56>",
    "<token of kind 57>",
  };

}
