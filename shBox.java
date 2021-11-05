import java.util.ArrayList;
import java.util.Scanner;
import java.io.Console;
import java.io.*;
import java.lang.ProcessBuilder;


class ShBox {
  public static void main(String[] args) {
    String[] codeLines = args[0].split("\\n");
    Box myBox = new Box(".   ");
    //System.out.println("arg length: "+args.length);
    for (int i = 0; i<codeLines.length; i++) {
      if (codeLines[i].trim()!="") {
        myBox.code.add(codeLines[i].trim());
      }
    }

    for (int i = 0; i<myBox.code.size(); i++) {
      //System.out.println("line "+i+":"+myBox.code.get(i));
    }

    myBox.runScript();
  }
}

  class Box {
    public static ArrayList<String> code = new ArrayList<String>();
    private ArrayList<Integer> stack = new ArrayList<Integer>();
    private String[] varNames = new String[200];
    private String tag;
    private String password = "open";
    private String[] data = new String[200];

    Box(String tg) {
      if (tg.length() == 3) {
        tag = tg;
      } else {
        tag = tg.substring(0, 3);
      }
      password = "open";
    }

    String getTag() {
      return tag;
    }

    String getData(int i) {
      if (i < 100) {
        return data[i];
      } else {
        return "ERROR";
      }
    }

    String getData(int i, String pw) {
      if (i < 100) {
        return data[i];
      } else if (pw.equals(password)) {
        return data[i];
      } else {
        return "ERROR";
      }
    }

    private boolean isNumeric(String str) {
      try {
        float f = Float.parseFloat(str);
        return true;
      } catch (Exception e) {
        return false;
      }
    }

    void handleError(Exception e, int i) {
      System.out.println("Error on line " + i + "! Error details: " + e.toString());
    }

    void runScript() {
      int i = 0;
      int maxLineCount = 0;
      stack.add(i);

      while (true) {
        boolean isInstruction = false;
        stack.set(stack.size() - 1, i);
        ArrayList<String> instruction = new ArrayList<String>();
        boolean inString = false;
        String strSoFar = "";
        for (int j = 0; j < code.get(i).length(); j++) {
          if (code.get(i).charAt(j) == '\"') {
            inString = !inString;
          }
          if (j < code.get(i).length() - 1) {
            if (!inString & (code.get(i).charAt(j) == '/' & code.get(i).charAt(j + 1) == '/')) {
              break;
            }
          }
          if (!inString & (code.get(i).charAt(j) == ',' || (code.get(i).charAt(j) == ' ' & instruction.size() == 0))) {
            instruction.add(strSoFar);
            strSoFar = "";
          } else {
            strSoFar += code.get(i).charAt(j);
          }
        }

        instruction.add(strSoFar);
        instruction.set(0, instruction.get(0).toLowerCase().trim());

        // System.out.println(instruction.toString());
        if (instruction.get(0).equals("memset")) {
          isInstruction = true;
          try {
            Parser inParser = new Parser(instruction.get(2));
            Parser addressParser = new Parser(instruction.get(1));
            if (isNumeric(addressParser.value())) {
              data[Integer.valueOf(addressParser.value())] = inParser.value();
            } else {
              for (int k = 0; k < data.length; k++) {
                if (varNames[k] != null) {
                  if (varNames[k].equals(instruction.get(1))) {
                    data[k] = inParser.value();
                    break;
                  }
                }
              }
            }
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }
        if (instruction.get(0).equals("memname")) {
          isInstruction = true;
          try {
            if (isNumeric(instruction.get(2))) {
              throw new Exception("Variable name can't be a number!");
            }
            Parser inParser = new Parser(instruction.get(1));
            varNames[Integer.parseInt(inParser.value())] = instruction.get(2).trim();
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("print")) {
          isInstruction = true;
          try {
            Parser inParser = new Parser(instruction.get(1));
            System.out.println(inParser.value());
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("printn")) {
          isInstruction = true;
          try {
            Parser inParser = new Parser(instruction.get(1));
            System.out.print(inParser.value());
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("input")) {
          isInstruction = true;
          try {
            if (instruction.size() == 3) {
              Parser inParser = new Parser(instruction.get(2));
              System.out.println(inParser.value());
            }
            Parser addressParser = new Parser(instruction.get(1));
            String strValue = "NO INPUT ERROR";
            Scanner scan = new Scanner(System.in);
            if (scan.hasNextLine()) {
              strValue = scan.nextLine();
            }
            scan.close();

            //System.out.println(strValue+" "+addressParser.value());

            if (isNumeric(addressParser.value())) {
              data[Integer.valueOf(addressParser.value())] = strValue;
            } else {
              for (int k = 0; k < data.length; k++) {
                if (varNames[k] != null) {
                  if (varNames[k].equals(instruction.get(1))) {
                    data[k] = strValue;
                    break;
                  }
                }
              }
            }
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("goto")) {
          isInstruction = true;
          try {
            if (instruction.get(1).charAt(0) != '@') {
              throw (new Exception("Non-tag passed to GOTO!"));
            }
            for (int k = 0; k < code.size(); k++) {
              if (code.get(k).equals(instruction.get(1))) {
                i = k;
              }
            }
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).length() > 1) {
          if (instruction.get(0).charAt(0) == '@') {
            isInstruction = true;
          }
        }

        if (instruction.get(0).equals("else")) {
          isInstruction = true;
          try {
            for (int k = i; k < code.size() + 1; k++) {
              if (k == code.size()) {
                throw (new Exception("IF without END!"));
              }
              if (code.get(k).toLowerCase().equals("end")) {
                i = k;
                break;
              }
            }
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("def")) {
          isInstruction = true;
          try {
            int ifCount = 0;
            for (int k = i; k < code.size() + 1; k++) {
              if (k == code.size()) {
                throw (new Exception("DEF without FEND!"));
              }
              if (code.get(k).toLowerCase().equals("fend")) {
                ifCount--;
                if (ifCount <= 0) {
                  i = k;
                  break;
                }
              }
              if (code.get(k).substring(0, 2).equals("if")) {
                ifCount++;
              }
            }
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }
        if (instruction.get(0).equals("fend")) {
          isInstruction = true;
          try {
            stack.remove(stack.size() - 1);
            i = stack.get(stack.size() - 1);
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("fn")) {
          isInstruction = true;
          try {
            for (int k = 0; k < code.size() + 1; k++) {
              if (k == code.size()) {
                throw (new Exception("Function " + instruction.get(1) + " called without definition!"));
              }
              String header = "def " + instruction.get(1).toLowerCase() + "," + (instruction.size() - 2);
              if (code.get(k).length() > header.length()) {
                if (code.get(k).substring(0, header.length()).toLowerCase().equals(header)) {
                  stack.add(i);
                  i = k;
                  int argNum = Integer.valueOf(code.get(i).split(",")[code.get(i).split(",").length - 1]);
                  for (int memB = argNum; memB < argNum + instruction.size() - 2; memB++) {
                    Parser newParser = new Parser(instruction.get(memB - argNum + 2));
                    data[memB] = newParser.value();
                  }
                  break;
                }
              }
            }
          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("if")) {
          isInstruction = true;
          try {
            Parser inParser = new Parser(instruction.get(1));
            if (!(inParser.value().equals("TRUE") || inParser.value().equals("FALSE"))) {
              throw (new Exception("Non-boolean passed to IF!"));
            }

            if (inParser.value().equals("FALSE")) {
              int ifCount = 0;
              for (int k = i; k < code.size() + 1; k++) {
                if (k == code.size()) {
                  throw (new Exception("IF without END!"));
                }

                if (code.get(k).toLowerCase().equals("end")) {
                  ifCount--;
                  if (ifCount == 0) {
                    i = k;
                    break;
                  }
                }
                if (code.get(k).substring(0, 2).toLowerCase().equals("if")) {
                  ifCount++;
                }
                if (code.get(k).toLowerCase().equals("else") && ifCount == 1) {
                  i = k;
                  break;
                }
              }
            }

          } catch (Exception e) {
            handleError(e, i);
            break;
          }
        }

        if (instruction.get(0).equals("end")) {
          isInstruction = true;
        }

        if (instruction.get(0).toLowerCase().equals("stop")) {
          isInstruction = true;
          break;
        }

        if (instruction.get(0).length() == 0) {
          isInstruction = true;
        }

        if (!isInstruction) {
          if (instruction.get(0).equals("end")) {
            isInstruction = true;
          }
          handleError(new Exception("unknown instruction: " + instruction.get(0)), i);
          break;
        }

        i++;
        maxLineCount++;
        if (i >= code.size()) {
          break;
        }
        if (maxLineCount >= 50000) {
          System.out.println("Error on line " + i + "! Details: Maximum runtime exceeded");
          break;
        }
      }
    }

    class Parser {
      Parser input1;
      Parser input2;
      String operator;
      int numInputs = 1;
      String strValue;
      String baseType;

      Parser(String str) {
        String newStr = "";
        Boolean inStr = false;
        for (int i = 0; i < str.length(); i++) {
          if (str.charAt(i) == '\"') {
            inStr = !inStr;
          }
          if (inStr || str.charAt(i) != ' ') {
            newStr += str.charAt(i);
          }
        }
        Parser thisParser = fromString(newStr);
        input1 = thisParser.input1;
        input2 = thisParser.input2;
        operator = thisParser.operator;
        numInputs = thisParser.numInputs;
        strValue = thisParser.strValue;
      }

      Parser() {
      }

      private Parser fromString(String str) {
        boolean inString = false;
        boolean hasNonString = false;
        int parenGroup = 0;
        int minParenG = 1;
        int maxParenG = 0;
        int minParenGpost3 = 1;
        int minParenGpost1 = 1;

        if (str.equals("\"\"")) { // checks for empty string
          Parser retParser = new Parser();
          retParser.strValue = "";
          retParser.numInputs = 0;
          return retParser;
        }

        if (str.toLowerCase().equals("true")) {
          Parser retParser = new Parser();
          retParser.strValue = "TRUE";
          retParser.numInputs = 0;
          return retParser;
        }
        if (str.toLowerCase().equals("false")) {
          Parser retParser = new Parser();
          retParser.strValue = "FALSE";
          retParser.numInputs = 0;
          return retParser;
        }

        if (str.length() == 0) { // checks for empty parser argument
          Parser retParser = new Parser();
          retParser.strValue = "ERROR";
          retParser.numInputs = 0;
          return retParser;
        }
        if (str.charAt(0) == '#' & isNumeric(str.substring(1, str.length()))) { // Check for pointer
          Parser retParser = new Parser();
          retParser.strValue = str;
          retParser.numInputs = 0;
          return retParser;
        }

        if (isNumeric(str)) { // Check for pure number
          Parser retParser = new Parser();
          retParser.strValue = str;
          retParser.numInputs = 0;
          return retParser;
        }

        if (str.toLowerCase() == "true" || str.toLowerCase() == "false") { // Check for pure bool
          Parser retParser = new Parser();
          retParser.strValue = str;
          retParser.numInputs = 0;
          return retParser;
        }

        for (int i = 0; i < str.length(); i++) {
          if (str.charAt(i) == '(' & !inString) {
            parenGroup++;
          }
          if (str.charAt(i) == ')' & !inString) {
            parenGroup--;
          }
          if (str.charAt(i) == '\"') {
            inString = !inString;
          }

          if (parenGroup == 0 & !inString) {
            if (i < str.length() - 1) {
              if (str.charAt(i) == '&' & str.charAt(i + 1) == '&') {
                Parser retParser = new Parser();
                retParser.input1 = fromString(str.substring(0, i));
                retParser.input2 = fromString(str.substring(i + 2, str.length()));
                retParser.operator = "&&";
                retParser.numInputs = 2;
                return retParser;
              }
            }
            if (str.charAt(i) == '+') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "+";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '-') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "-";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '*') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "*";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '/') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "/";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '^') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "^";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '%') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "%";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '&') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "&";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '$') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "$";
              retParser.numInputs = 2;
              return retParser;
            }
            if (i + 1 < str.length()) {
              if (str.charAt(i) == '>' & str.charAt(i + 1) == '=') {
                Parser retParser = new Parser();
                retParser.input1 = fromString(str.substring(0, i));
                retParser.input2 = fromString(str.substring(i + 2, str.length()));
                retParser.operator = ">=";
                retParser.numInputs = 2;
                return retParser;
              }
              if (str.charAt(i) == '=' & str.charAt(i + 1) == '=') {
                Parser retParser = new Parser();
                retParser.input1 = fromString(str.substring(0, i));
                retParser.input2 = fromString(str.substring(i + 2, str.length()));
                retParser.operator = "==";
                retParser.numInputs = 2;
                return retParser;
              }
              if (str.charAt(i) == '<' & str.charAt(i + 1) == '=') {
                Parser retParser = new Parser();
                retParser.input1 = fromString(str.substring(0, i));
                retParser.input2 = fromString(str.substring(i + 2, str.length()));
                retParser.operator = "<=";
                retParser.numInputs = 2;
                return retParser;
              }
              if (str.charAt(i) == '!' & str.charAt(i + 1) == '=') {
                Parser retParser = new Parser();
                retParser.input1 = fromString(str.substring(0, i));
                retParser.input2 = fromString(str.substring(i + 2, str.length()));
                retParser.operator = "!=";
                retParser.numInputs = 2;
                return retParser;
              }
              if (str.charAt(i) == '|' & str.charAt(i + 1) == '|') {
                Parser retParser = new Parser();
                retParser.input1 = fromString(str.substring(0, i));
                retParser.input2 = fromString(str.substring(i + 2, str.length()));
                retParser.operator = "||";
                retParser.numInputs = 2;
                return retParser;
              }
            }

            if (str.charAt(i) == '>') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = ">";
              retParser.numInputs = 2;
              return retParser;
            }
            if (str.charAt(i) == '<') {
              Parser retParser = new Parser();
              retParser.input1 = fromString(str.substring(0, i));
              retParser.input2 = fromString(str.substring(i + 1, str.length()));
              retParser.operator = "<";
              retParser.numInputs = 2;
              return retParser;
            }
          }

          if (!inString & i < str.length() - 1) {
            hasNonString = true;
          }
          if (i < str.length() - 1) {
            minParenG = Math.min(minParenG, parenGroup);
          }
          if (!inString) {
            maxParenG = Math.max(maxParenG, parenGroup);
          }
          if (i > 2 & i < str.length() - 1) {
            minParenGpost3 = Math.min(minParenGpost3, parenGroup);
          }
          if (i > 0 & i < str.length() - 1) {
            minParenGpost1 = Math.min(minParenGpost1, parenGroup);
          }
          // System.out.println(i+" "+str.charAt(i));
        }
        // System.out.println(minParenG);
        if (minParenG == 1) { // check for big paren wrap
          return fromString(str.substring(1, str.length() - 1));
        }
        if (!hasNonString) { // check for big String
          Parser retParser = new Parser();
          retParser.strValue = str.substring(1, str.length() - 1);
          retParser.numInputs = 0;
          retParser.baseType = "String";
          return retParser;
        }

        if (minParenGpost3 == 1 & str.substring(0, 3).equals("sin")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "sin";
          return retParser;
        }

        if (minParenGpost3 == 1 & str.substring(0, 3).equals("cos")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "cos";
          return retParser;
        }

        if (minParenGpost3 == 1 & str.substring(0, 3).equals("log")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "log";
          return retParser;
        }
        if (minParenGpost3 == 1 & str.substring(0, 3).equals("tan")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "tan";
          return retParser;
        }
        if (minParenGpost3 == 1 & str.substring(0, 3).equals("rnd")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "rnd";
          return retParser;
        }
        if (minParenGpost3 == 1 & str.substring(0, 3).equals("flr")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "flr";
          return retParser;
        }
        if (minParenGpost3 == 1 & str.substring(0, 3).equals("cel")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "cel";
          return retParser;
        }
        if (minParenGpost3 == 1 & str.substring(0, 3).equals("rdm")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "rdm";
          return retParser;
        }
        if (minParenGpost3 == 1 & str.substring(0, 3).equals("len")) {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(3, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "len";
          return retParser;
        }
        if (minParenGpost1 == 1 & str.charAt(0) == '!') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(1, str.length()));
          retParser.numInputs = 1;
          retParser.operator = "!";
          return retParser;
        }

        if (str.charAt(0) == '#' & maxParenG == 0) { // Check for variable
          Parser retParser = new Parser();
          retParser.strValue = str;
          retParser.numInputs = 0;
          return retParser;
        }

        Parser retParser = new Parser();
        retParser.strValue = "UT ERROR";
        retParser.numInputs = 0;
        return retParser;
      }

      private boolean isNumeric(String str) {
        if (str == null) {
          return false;
        }
        try {
          float f = Float.parseFloat(str);
        } catch (NumberFormatException nfe) {
          return false;
        }
        return true;
      }

      public String value() {
        if (type() == "ERROR") {
          return "ERROR";
        }

        if (numInputs == 0) {
          if (strValue.length() < 2) {
            return strValue;
          }
          if (strValue.charAt(0) == '#' & isNumeric(strValue.substring(1, strValue.length()))) {
            return data[Integer.valueOf(strValue.substring(1, strValue.length()))];
          }
          if (strValue.charAt(0) == '#') {
            for (int i = 0; i < varNames.length; i++) {
              if (varNames[i] != null) {
                if (varNames[i].equals(strValue.substring(1, strValue.length()))) {
                  return data[i];
                }
              }
            }
          }
          return strValue;
        } else if (numInputs == 1) {
          switch (operator) {
            case "log":
              return "" + Math.log(Float.parseFloat(input1.value()));
            case "sin":
              return "" + Math.sin(Float.parseFloat(input1.value()));
            case "cos":
              return "" + Math.cos(Float.parseFloat(input1.value()));
            case "tan":
              return "" + Math.tan(Float.parseFloat(input1.value()));
            case "rnd":
              return "" + Math.round(Float.parseFloat(input1.value()));
            case "flr":
              return "" + Math.floor(Float.parseFloat(input1.value()));
            case "cel":
              return "" + Math.ceil(Float.parseFloat(input1.value()));
            case "rdm":
              return "" + Math.random() * Float.parseFloat(input1.value());
            case "len":
              return "" + input1.value().length();
            case "!":
              return input1.value().equals("TRUE") ? "FALSE" : "TRUE";
            default:
              return "ERROR";
          }
        } else if (numInputs == 2) {
          switch (operator) {
            case "+":
              return "" + (Float.parseFloat(input1.value()) + Float.parseFloat(input2.value()));
            case "-":
              return "" + (Float.parseFloat(input1.value()) - Float.parseFloat(input2.value()));
            case "*":
              return "" + (Float.parseFloat(input1.value()) * Float.parseFloat(input2.value()));
            case "/":
              return "" + (Float.parseFloat(input1.value()) / Float.parseFloat(input2.value()));
            case "%":
              return "" + (Float.parseFloat(input1.value()) % Float.parseFloat(input2.value()));
            case "^":
              return "" + (Math.pow(Float.parseFloat(input1.value()), Float.parseFloat(input2.value())));
            case "&":
              return input1.value() + input2.value();
            case "$":
              return "" + input1.value()
                      .charAt((int) Math.min(input1.value().length(), (int) Float.parseFloat(input2.value())));
            case "==":
              if (isNumeric(input1.value()) && isNumeric(input2.value())) {
                return (Float.parseFloat(input1.value()) == Float.parseFloat(input2.value()) ? "TRUE" : "FALSE");
              } else {
                return (input1.value().equals(input2.value()) ? "TRUE" : "FALSE");
              }
            case "!=":
              return (input1.value().equals(input2.value()) ? "FALSE" : "TRUE");
            case "<":
              return (Float.parseFloat(input1.value()) < Float.parseFloat(input2.value())) ? "TRUE" : "FALSE";
            case ">":
              return (Float.parseFloat(input1.value()) > Float.parseFloat(input2.value())) ? "TRUE" : "FALSE";
            case "<=":
              return (Float.parseFloat(input1.value()) <= Float.parseFloat(input2.value())) ? "TRUE" : "FALSE";
            case ">=":
              return Float.parseFloat(input1.value()) >= Float.parseFloat(input2.value()) ? "TRUE" : "FALSE";
            case "&&":
              return (input1.value().equals("TRUE") && input2.value().equals("TRUE")) ? "TRUE" : "FALSE";
            case "||":
              return (input1.value().equals("TRUE") || input2.value().equals("TRUE")) ? "TRUE" : "FALSE";
            default:
              return "ERROR";
          }
        }
        return "ERROR";
      }

      public String toString() {
        String retString = "";
        if (numInputs == 1) {
          retString = operator + "(" + input1.toString() + ")";
        } else if (numInputs == 2) {
          retString = "(" + input1.toString() + " " + operator + " " + input2.toString() + ")";
        } else {
          if (baseType == "String") {
            retString = "\"" + strValue + "\"";
          } else if (isNumeric(strValue)) {
            retString = strValue;
          } else if (isNumeric(strValue.substring(1, strValue.length()))) {
            retString = strValue;
          } else {
            retString = "\"" + strValue + "\"";
          }
        }
        return retString;
      }

      public String type() {
        if (numInputs >= 1) {
          if (input1.type() == "ERROR") {
            return "ERROR";
          }
        }
        if (numInputs == 2) {
          if (input2.type() == "ERROR") {
            return "ERROR";
          }
        }
        if (numInputs == 0) {
          return "Ambiguous";
        }
        switch (operator) {
          case "+":
            return "Number";
          case "-":
            return "Number";
          case "/":
            return "Number";
          case "*":
            return "Number";
          case "log":
            return "Number";
          case "sin":
            return "Number";
          case "cos":
            return "Number";
          case "tan":
            return "Number";
          case "!":
            return "Boolean";
          case "&&":
            return "Boolean";
          case "||":
            return "Boolean";
          case "String":
            return "String";
          case "&":
            return "String";
          case "Number":
            return "Number";
          case "<":
            return "Boolean";
          case ">":
            return "Boolean";
          case "==":
            return "Boolean";
          case ">=":
            return "Boolean";
          case "<=":
            return "Boolean";
          case "!=":
            return "Boolean";
          case "%":
            return "Number";
          case "^":
            return "Number";
          case "$":
            return "String";
          case "rnd":
            return "Number";
          case "cel":
            return "Number";
          case "flr":
            return "Number";
          case "rdm":
            return "Number";
          case "len":
            return "Number";
          default:
            return "ERROR";
        }
      }
    }
  }
