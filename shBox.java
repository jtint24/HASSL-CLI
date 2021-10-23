import java.util.ArrayList;
import java.util.Scanner;

class ShBox {
  public static void main(String[] args) {
    Box myBox = new Box(".   ");
    Scanner sc = new Scanner(System.in);

    while (sc.hasNextLine()) {
      myBox.code.add(sc.nextLine().trim());
    }
    myBox.runScript();
  }
}
class Box {
  public ArrayList<String> code = new ArrayList<String>();
  private ArrayList<Integer> stack = new ArrayList<Integer>();
  private String tag;
  private String password = "open";
  private String[] data = new String[200];
  Parser evaluate;

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
    if (i<100) {
      return data[i];
    } else {
      return "ERROR";
    }
  }

  String getData(int i, String pw) {
    if (i<100) {
      return data[i];
    } else if (pw.equals(password)) {
      return data[i];
    } else {
      return "ERROR";
    }
  }

  void demo() {
    /*code.add("print \"\"");
    code.add("def substr,3,0");
    code.add("memset 4,\"\"");
    code.add("@substrloop");
    code.add("if (#1<=#2)");
    code.add("memset 4,#4&(#0$#1)");
    code.add("memset 1,#1+1");
    code.add("goto @substrloop");
    code.add("else");
    code.add("print #4");
    code.add("end");
    code.add("end");
    code.add("fn substr,\"hello there substring\",2,9");*/
    
    /*code.add("memset 0,1");
    code.add("@loop");
    code.add("if (#0%2)==0");
    code.add("if (#0%3)==0");
    code.add("print #0&\" is divisible by 2 and 3\"");
    code.add("else");
    code.add("print #0&\" is divisible by 2 but not 3\"");
    code.add("end");
    code.add("else");
    code.add("if (#0%3)==0");
    code.add("print #0&\" is divisible by 3 but not 2\"");
    code.add("else");
    code.add("print #0&\" is not divisible by  2 or 3\"");
    code.add("end");
    code.add("end");
    code.add("memset 0,#0+1");
    code.add("goto @loop");*/

    /*code.add("def hello,1,0");
    code.add("if #0==1");
    code.add("print #0");
    code.add("else");
    code.add("print \"not a 1!\"");
    code.add("end");
    code.add("fend");
    code.add("def goodbye,0,0");
    code.add("print \"goodbye!\"");
    code.add("fend");
    code.add("def goodbye,1,0");
    code.add("print \"goodbye! #0\"");
    code.add("fend");
    code.add("fn hello,4");
    code.add("fn hello,1");
    code.add("fn goodbye,1");
    code.add("fn goodbye");*/
    


    runScript();
  }

  void runScript() {
    int i = 0;
    int maxLineCount = 0;
    stack.add(i);
    while(true) {
      stack.set(stack.size()-1,i);
      ArrayList<String> instruction = new ArrayList<String>();
      boolean inString = false;
      String strSoFar = "";
      for (int j = 0; j<code.get(i).length(); j++) {
        if (code.get(i).charAt(j)=='\"') {
          inString=!inString;
        }
        if (j<code.get(i).length()-1) {
          if (!inString & (code.get(i).charAt(j)=='/' & code.get(i).charAt(j+1)=='/')) {
            break;
          }
        }
        if (!inString & (code.get(i).charAt(j)==',' || (code.get(i).charAt(j)==' ' & instruction.size()==0))) {
          instruction.add(strSoFar);
          strSoFar = "";
        } else {
          strSoFar+=code.get(i).charAt(j);
        }
      }

      instruction.add(strSoFar);
      instruction.set(0,instruction.get(0).toLowerCase().trim());
      
      //System.out.println(instruction.toString());
      if (instruction.get(0).equals("memset")) {
        //System.out.println("memset back");
        try {
          Parser inParser = new Parser(instruction.get(2));
          data[Integer.valueOf(instruction.get(1))] = inParser.value();
        } catch(Exception e) {
          System.out.println("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }

      if (instruction.get(0).equals("print")) {
        //System.out.println("print back");
        try {
          Parser inParser = new Parser(instruction.get(1));
          System.out.println(inParser.value());
        } catch(Exception e) {
          System.out.println("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }

      if (instruction.get(0).equals("printn")) {
        //System.out.println("printn back");
        try {
          Parser inParser = new Parser(instruction.get(1));
          System.out.print(inParser.value());
        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }

      if (instruction.get(0).equals("goto")) {
        //System.out.println("goto back");
        try {
          if (instruction.get(1).charAt(0)!='@') {
            throw(new Exception("Non-tag passed to GOTO!"));
          }
          for (int k = 0; k<code.size(); k++) {
            if (code.get(k).equals(instruction.get(1))) {
              i=k;
            }
          }
        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }
      if (instruction.get(0).equals("else")) {
        try  {
          for (int k = i; k<code.size()+1; k++) {
              if (k==code.size()) {
                throw(new Exception("IF without END!"));
              }
              if (code.get(k).toLowerCase().equals("end")) {
                i=k;
                break;
              }
            }
        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }

      if (instruction.get(0).equals("else")) {
        try  {
          for (int k = i; k<code.size()+1; k++) {
              if (k==code.size()) {
                throw(new Exception("IF without END!"));
              }
              if (code.get(k).toLowerCase().equals("end")) {
                i=k;
                break;
              }
            }
        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }

      if (instruction.get(0).equals("def")) {
        try  {
          int ifCount = 0;
          for (int k = i; k<code.size()+1; k++) {
            if (k==code.size()) {
              throw(new Exception("DEF without FEND!"));
            }
            if (code.get(k).toLowerCase().equals("fend")) {
              ifCount--;
              if (ifCount <= 0) {
                i=k;
                break;
              }
            }
            if (code.get(k).substring(0,2).equals("if")) {
              ifCount++;
            }
          }
        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }
      if (instruction.get(0).equals("fend")) {
        try  {
          stack.remove(stack.size()-1);
          i=stack.get(stack.size()-1);
        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }
      if (instruction.get(0).equals("fn")) {
        try  {
          for (int k = 0; k<code.size()+1; k++) {
            if (k==code.size()) {
              throw(new Exception("Function "+instruction.get(1)+" called without definition!"));
            }
            String header = "def "+instruction.get(1).toLowerCase()+","+(instruction.size()-2);
            if (code.get(k).length()>header.length()) {
              if (code.get(k).substring(0,header.length()).toLowerCase().equals(header)) {
                stack.add(i);
                i=k;
                int argNum = Integer.valueOf(code.get(i).split(",")[code.get(i).split(",").length-1]);
                for (int memB = argNum; memB<argNum+instruction.size()-2; memB++) {
                  data[memB] = instruction.get(memB-argNum+2);
                }
                break;
              }
            }
          }
        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }

      if (instruction.get(0).equals("if")) {
        //System.out.println("goto back");
        try {
          Parser inParser = new Parser(instruction.get(1));
          if (!(inParser.value().equals("TRUE") || inParser.value().equals("FALSE"))) {
            throw(new Exception("Non-boolean passed to IF!"));
          }

          if (inParser.value().equals("FALSE")) {
            int ifCount = 0;
            for (int k = i; k<code.size()+1; k++) {
              if (k==code.size()) {
                throw(new Exception("IF without END!"));
              }
              
              if (code.get(k).toLowerCase().equals("end")) {
                ifCount--;
                if (ifCount == 0) {
                  i=k;
                  break;
                }
              }
              if (code.get(k).substring(0,2).toLowerCase().equals("if")) {
                ifCount++;
              }
              if (code.get(k).toLowerCase().equals("else") && ifCount==1) {
                i=k;
                break;
              }
            }
          }

        } catch(Exception e) {
          System.out.print("Error on line "+i+"! Details: "+e.toString());
          break;
        }
      }

      if (instruction.get(0).toLowerCase().equals("stop")) {
        break;
      }

      i++;
      maxLineCount++;
      if (i>=code.size()) {
        break;
      }
      if (maxLineCount>=50000) {
        System.out.println("Error on line "+i+"! Details: Maximum runtime exceeded");
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
    for (int i = 0; i<str.length(); i++) {
      if (str.charAt(i)=='\"') {
        inStr = !inStr;
      }
      if (inStr || str.charAt(i)!=' ') {
        newStr+=str.charAt(i);
      }
    }
    Parser thisParser = fromString(newStr);
    input1 = thisParser.input1;
    input2 = thisParser.input2;
    operator = thisParser.operator;
    numInputs = thisParser.numInputs;
    strValue = thisParser.strValue;
  }
  Parser() {}

  private Parser fromString(String str) {
    boolean inString = false;
    boolean hasNonString = false;
    int parenGroup = 0;
    int minParenG = 1;
    int minParenGpost3 = 1;
    int minParenGpost1 = 1;
    String activeOp = "";

    //System.out.println(str);

    if (str.equals("\"\"")) { //checks for empty string
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

    if (str.length()==0) { //checks for empty parser argument
      Parser retParser = new Parser();
      retParser.strValue = "ERROR";
      retParser.numInputs = 0;
      return retParser;
    }
    if (str.charAt(0)=='#' & isNumeric(str.substring(1,str.length()))) { //Check for pointer
      Parser retParser = new Parser();
      retParser.strValue = str;
      retParser.numInputs = 0;
      return retParser;
    }

    if (isNumeric(str)) { //Check for pure number
      Parser retParser = new Parser();
      retParser.strValue = str;
      retParser.numInputs = 0;
      return retParser;
    }

    if (str.toLowerCase() == "true" || str.toLowerCase() == "false") { //Check for pure bool
      Parser retParser = new Parser();
      retParser.strValue = str;
      retParser.numInputs = 0;
      return retParser;
    }

    for (int i = 0; i<str.length(); i++) {
      if (str.charAt(i)=='(' & !inString) {
        parenGroup++;
      }
      if (str.charAt(i)==')' & !inString) {
        parenGroup--;
      }
      if (str.charAt(i)=='\"') {
        inString=!inString;
      }

      if (parenGroup == 0 & !inString) {
        if (i<str.length()-1) {
          if (str.charAt(i)=='&' & str.charAt(i+1)=='&') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+2,str.length()));
          retParser.operator = "&&";
          retParser.numInputs = 2;
          return retParser;
        }
        }
        if (str.charAt(i)=='+') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "+";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='-') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "-";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='*') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "*";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='/') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "/";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='^') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "^";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='%') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "%";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='&') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "&";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='$') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "$";
          retParser.numInputs = 2;
          return retParser;
        }
        if (i+1<str.length()) {
        if (str.charAt(i)=='>' & str.charAt(i+1)=='=') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+2,str.length()));
          retParser.operator = ">=";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='=' & str.charAt(i+1)=='=') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+2,str.length()));
          retParser.operator = "==";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='<' & str.charAt(i+1)=='=') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+2,str.length()));
          retParser.operator = "<=";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='!' & str.charAt(i+1)=='=') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+2,str.length()));
          retParser.operator = "!=";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='|' & str.charAt(i+1)=='|') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+2,str.length()));
          retParser.operator = "||";
          retParser.numInputs = 2;
          return retParser;
        }
        }
        
        if (str.charAt(i)=='>') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = ">";
          retParser.numInputs = 2;
          return retParser;
        }
        if (str.charAt(i)=='<') {
          Parser retParser = new Parser();
          retParser.input1 = fromString(str.substring(0,i));
          retParser.input2 = fromString(str.substring(i+1,str.length()));
          retParser.operator = "<";
          retParser.numInputs = 2;
          return retParser;
        }
      }

      if (!inString & i<str.length()-1) {
        hasNonString = true;
      }
      if (i<str.length()-1) {
        minParenG = Math.min(minParenG,parenGroup);
      }
      if (i>2 & i<str.length()-1) {
        minParenGpost3 = Math.min(minParenGpost3,parenGroup);
      }
      if (i>0 & i<str.length()-1) {
        minParenGpost1 = Math.min(minParenGpost1,parenGroup);
      }
      //System.out.println(i+" "+str.charAt(i));
    }
    //System.out.println(minParenG);
    if (minParenG==1) { //check for big paren wrap
      return fromString(str.substring(1,str.length()-1));
    }
    if (!hasNonString) { //check for big String
      Parser retParser = new Parser();
      retParser.strValue = str.substring(1,str.length()-1);
      retParser.numInputs = 0;
      retParser.baseType = "String";
      return retParser;
    }

    if (minParenGpost3==1 & str.substring(0,3).equals("sin")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "sin";
      return retParser;
    }

    if (minParenGpost3==1 & str.substring(0,3).equals("cos")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "cos";
      return retParser;
    }

    if (minParenGpost3==1 & str.substring(0,3).equals("log")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "log";
      return retParser;
    }
    if (minParenGpost3==1 & str.substring(0,3).equals("tan")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "tan";
      return retParser;
    }
    if (minParenGpost3==1 & str.substring(0,3).equals("rnd")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "rnd";
      return retParser;
    }
    if (minParenGpost3==1 & str.substring(0,3).equals("flr")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "flr";
      return retParser;
    }
    if (minParenGpost3==1 & str.substring(0,3).equals("cel")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "cel";
      return retParser;
    }
    if (minParenGpost3==1 & str.substring(0,3).equals("rdm")) {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(3,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "rdm";
      return retParser;
    }
    if (minParenGpost1==1 & str.charAt(0)=='!') {
      Parser retParser = new Parser();
      retParser.input1 = fromString(str.substring(1,str.length()));
      retParser.numInputs = 1;
      retParser.operator = "!";
      return retParser;
    }



    Parser retParser = new Parser();
    retParser.strValue = "ERROR";
    retParser.numInputs = 0;
    return retParser;
  }

  private boolean isNumeric(String str) {
    if (str == null) {return false;}
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
      if (strValue.length()<2) {
        return strValue;
      }
      if (strValue.charAt(0)=='#' & isNumeric(strValue.substring(1,strValue.length()))) {
        return data[Integer.valueOf(strValue.substring(1,strValue.length()))];
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
          return "" + Math.random()*Float.parseFloat(input1.value());
        case "!":
          return input1.value().equals("TRUE")?"FALSE":"TRUE";
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
          return "" + (Math.pow(Float.parseFloat(input1.value()),Float.parseFloat(input2.value())));
        case "&":
          return input1.value()+input2.value();
        case "$":
          return ""+input1.value().charAt((int)Math.min(input1.value().length(),(int)Float.parseFloat(input2.value())));
        case "==":
          if (isNumeric(input1.value()) && isNumeric(input2.value())) {
            return (Float.parseFloat(input1.value())==Float.parseFloat(input2.value())?"TRUE":"FALSE");
          } else {
            return (input1.value().equals(input2.value())?"TRUE":"FALSE");
          }
        case "!=":
          return (input1.value().equals(input2.value())?"FALSE":"TRUE");
        case "<":
          return (Float.parseFloat(input1.value())<Float.parseFloat(input2.value()))?"TRUE":"FALSE";
        case ">":
          return (Float.parseFloat(input1.value())>Float.parseFloat(input2.value()))?"TRUE":"FALSE";
        case "<=":
          return (Float.parseFloat(input1.value())<=Float.parseFloat(input2.value()))?"TRUE":"FALSE";
        case ">=":
          return Float.parseFloat(input1.value())>=Float.parseFloat(input2.value())?"TRUE":"FALSE";
        case "&&":
          return (input1.value().equals("TRUE") && input2.value().equals("TRUE"))?"TRUE":"FALSE";
        case "||":
          return (input1.value().equals("TRUE") || input2.value().equals("TRUE"))?"TRUE":"FALSE";
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
        retString = "\""+strValue+"\"";
      } else if (isNumeric(strValue)) {
      retString = strValue;
      } else if (isNumeric(strValue.substring(1,strValue.length()))) {
        retString = strValue;
      } else {
        retString = "\""+strValue+"\"";
      }
    }
    return retString;
  }

  public String type() {
    if (numInputs>=1) {
      if (input1.type() == "ERROR") {
       return "ERROR";
      }
    }
    if (numInputs == 2) {
      if (input2.type() == "ERROR") {
        return "ERROR";
      }
    }
    if (numInputs==0) {
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
      default:
        return "ERROR";
    }
  }
}
}