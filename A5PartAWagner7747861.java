import java.io.*;
import java.util.*;

/**
 /*** A5PartAWagner7747861
 * COMP 2140   SECTION A01
 * INSTRUCTOR    Helen Cameron
 * ASSIGNMENT    Assignment 5 (Part A)
 * Natasha Wagner 7747861
 * December 4, 2016
 *
 * PURPOSE: To read in assignment statements with postfix expressions
 *          on the right size of the assignment, keeping track of the
 *          variables and their values.  The expressions can have
 *          integer or variables operands and operators +, -, *, and /.
 *          At the end, all variables and their values are printed.
 */

public class A5PartAWagner7747861
{
  
  /***********************************************************************
    * main - Read in the input and process it, printing out any output
    ************************************************************************/
  public static void main( String[] args)
  { 
    
    // Print output headers.
    System.out.println( );
    System.out.println("Comp 2140  Assignment 5 Part A Fall 2016");
    System.out.println("Evaluating postfix expressions with variables.");
    
    processAllAssignmentStatements();
    
    System.out.println("\nProgram ends.");
    System.out.println( );
  } // end method main
  
  /***********************************************************************
    * processAllAssignmentStatements
    *
    * Get the input file's name (prompt user for keyboard input).
    * Then read in and process all the assignment statements
    * (which contain postfix expressions using variables and constants).
    ************************************************************************/
  public static void processAllAssignmentStatements()
  {
    // For reading in each line in the file
    Scanner file;
    int numStatements;
    String inputLine;
    // For processing an assignment statement
    String variableID; 
    Expression postfix = null;
    int value;
    int equalsPosn;
    Table variables = new Table();
    Variable variable;
    
    try
    {      
      file = new Scanner( new File( getInputFileName() ) );
      System.out.println( "\n\n**********************************************\n");
      
      // Print out a title for the assignment statements processing
      
      System.out.println( "The assignment statements and the values of their right sides"
                           + "\n============================================================" );
      
      // Read in the number of assignment statements to read in
      
      numStatements = file.nextInt();
      file.nextLine(); // skip to the next line
      
      // Process each assignment statement
      
      for ( int i = 0; i < numStatements; i++ )
      {
        inputLine = file.nextLine().trim();
        equalsPosn = inputLine.indexOf( "=" );
        if ( equalsPosn > 0 )
        {
          variableID = (inputLine.substring( 0, equalsPosn )).trim();
          if ( Variable.isVariableID( variableID ) )
          {
            System.out.println( "\nThe variable to be assigned to: " + variableID );
            postfix = new Expression( inputLine.substring( equalsPosn+1 ) );
            System.out.println( "The postfix expression on the right side: " + postfix );
            value = postfix.evaluatePostfix( variables );
            if ( value != Integer.MIN_VALUE )
            {
              System.out.println( "The value of the expression: " + value );
              variable = variables.findKey( variableID );
              if ( variable != null )
                variable.setValue( value );
              else
                variables.insert( new Variable( variableID, value ) );
            }
            else
              System.out.println( "Expression erroneous, value cannot be computed;"
                                   + " invalid assignment." );
          }
          else // invalid variable ID
          {
            System.out.println( "\nInvalid variable ID (" + variableID + ")"
                                 + " on the left side of the assignment statement;"
                                 + " invalid assignment." );
            System.out.println( "Input line: " + inputLine );
          }
        } // end if ( equalsPosn > 0 )
        else
        {
          System.out.println( "\nNo \"=\" in assignment statement;"
                               + " invalid assignment." );
          System.out.println( "Input line: " + inputLine );
        }
      } // end for
      
      System.out.println( "\n\nVariables and Their Values"
                           + "\n--------------------------" );
      
      variables.printAll();
      
    }
    catch (IOException e) 
    {
      System.out.println("IO Error: " + e.getMessage());
    }
    
  } // end processAllAssignmentStatements
  
  /***********************************************************************
    * getInputFileName
    *
    * Return the input file's name (retrieved using keyboard input).
    ************************************************************************/
  private static String getInputFileName()
  {
    // For reading in the file name (using keyboard input)
    Scanner keyboard; 
    String fileName;
    
    // Allow user to choose file with keyboard input.
    keyboard = new Scanner( System.in );
    System.out.println( "\nEnter the postfix expressions file name (.txt files only): " );
    fileName = keyboard.nextLine();
    
    return fileName;
  } // end getInputFileName
  
  
} // end class A4SolutionFall2016



/***********************************************************************
  ************************************************************************
  *  The Expression class.
  *
  *  An expression is simply an array of tokens (which are stored as Strings).
  ***********************************************************************
  ************************************************************************/
class Expression
{
  private static final String DIGITS = "0123456789";
  private static final String OPERATORS = "+-*/";
  
  private String[] tokens;
  
  public Expression( )
  {
    tokens = null;
  }
  
  public Expression( String inputLine )
  {
    tokens = inputLine.trim().split( "\\s+" );
  }
  
  
  /***********************************************************************
    * evaluatePostfix
    *
    * Return the value of the calling postfix expression (or Integer.MIN_VALUE,
    * if the postfix expression contains one or more errors)
    ************************************************************************/
  public int evaluatePostfix( Table variables )
  {
    int result = Integer.MIN_VALUE;
    boolean errorFree = true;
    Stack operandStack = new Stack();
    int operand1, operand2;
    Variable var;
    
    // Process tokens from left to right
    for ( int i = 0; i < tokens.length && errorFree; i++ )
    {
      tokens[i] = tokens[i].trim();
      if ( !tokens[i].equals( "" ) ) // ignore the empty-string token.
      {
        if ( isInteger( tokens[i] ) ) // if constant operand, push onto the stack.
        {
          operandStack.push( Integer.parseInt( tokens[i] ) );
        }
        else if ( Variable.isVariableID( tokens[i] ) )
        {
          var = variables.findKey( tokens[i] );
          if ( var != null )
            operandStack.push( var.getValue() );
          else
          {
            errorFree = false;
            System.out.println( "ERROR: undeclared variable " + tokens[i] );
          }
        }
        else if ( isOperator( tokens[i] ) ) // if operator, pop its operands and 
        {                                   // push the result of performing the 
          if ( !operandStack.isEmpty() )    // operator on its operands.
          {
            operand2 = operandStack.pop();
            if ( !operandStack.isEmpty() )
            {
              operand1 = operandStack.pop();
              operandStack.push( operationResult( operand1, tokens[i], operand2 ) );
            }
            else
            {
              // missing only one operand (the first one)
              errorFree = false;
              System.out.println( "ERROR: Operator " + tokens[i] 
                                   + " is missing its first operand (second operand is "
                                   + operand2 + ")" );
            }
          }
          else
          {
            // missing both operands
            errorFree = false;
            System.out.println( "ERROR: Operator " + tokens[i] 
                                 + " is missing both of its operands." );
          }
        }
        else
        {
          // invalid token: not an operator or an operand (constant or variable)
          errorFree = false;
          System.out.println( "ERROR: Token " + tokens[i] 
                               + " is neither an operator nor an operand." );
        }
      }  // end if ( !tokens[i].equals( "" ) )
    } // end for
    
    if ( errorFree && !operandStack.isEmpty() )
    {
      result = operandStack.pop();
      if ( !operandStack.isEmpty() )
      {
        result = Integer.MIN_VALUE;
        System.out.println( "ERROR: missing operator(s) "
                             + "(stack contains more than one value at the end)" );
      }
    }
    // else something went terribly wrong (return Integer.MIN_VALUE)
    
    return result;
  } // end evaluatePostfix
  
  
  /***********************************************************************
    * isOperand
    *
    * Return true if the String contains an integer;
    * Return false otherwise.
    ************************************************************************/
  private static boolean isInteger( String token )
  {
    return token.matches( "\\d+|[-+]\\d+" );
  }
  
  
  /***********************************************************************
    * isOperator
    *
    * Return true if the String consists of a single char that is one of
    * + or - or * or /.
    * Return false otherwise.
    ************************************************************************/
  private static boolean isOperator( String token )
  {
    return (token.length() == 1) && (OPERATORS.indexOf( token.charAt(0) ) >= 0);
  }
  
  
  /***********************************************************************
    * operationResult
    *
    * Perform the operation on its two operands and return the result.
    ************************************************************************/
  private static int operationResult( int operand1, String operation, int operand2 )
  {
    int result = Integer.MIN_VALUE;
    char op = operation.charAt(0); // switch statments work with char, not String
    
    switch ( op )
    {
      case '+': result = operand1 + operand2; break;
      case '-': result = operand1 - operand2; break;
      case '*': result = operand1 * operand2; break;
      case '/': result = operand1 / operand2; break;
    }
    
    return result;
  }
  
  
  /***********************************************************************
    * toString
    *
    * Return a String representation of the Expression for printing.
    ************************************************************************/
  public String toString()
  {
    String result = "";
    
    for ( int i = 0; i < tokens.length; i++ )
    {
      result += tokens[i];
      if ( (i+1) != tokens.length ) // a blank between consecutive tokens
        result += " ";
    }
    
    return result;
  }
  
} // end class Expression



/***********************************************************************
  ************************************************************************
  *  The Stack class 
  *
  *  Implemented as an ordinary linked list with a top pointer (pointing
  *  to the first node) and no dummy nodes.
  *
  *  The stack stores ints (as Integers).
  *
  *  Push, pop, and top all happen at the top (of course), with NO loops.
  *
  ***********************************************************************
  ************************************************************************/
class Stack
{
  private Node top; // a pointer to the top (first node) of the stack.
  
  
  /***********************************************************************
    * constructor creates an empty stack.
    ************************************************************************/
  public Stack()
  {
    top = null;
  }
  
  
  /***********************************************************************
    * isEmpty
    *
    * Returns true if the stack is empty, false if it is not empty.
    ************************************************************************/
  public boolean isEmpty()
  {
    return top == null;
  }
  
  
  /***********************************************************************
    * push
    *
    * Add the new item to the top of the stack: add a new first node,
    * (and point top at the new first node).
    ************************************************************************/
  public void push( int newItem )
  {
    top = new Node( new Integer(newItem), top );
  }
  
  
  /***********************************************************************
    * top
    *
    * Return the item contained in the node that top points at
    * (or Integer.MAX_VALUE, if the stack is empty).
    ************************************************************************/
  public int top()
  {
    int result = Integer.MAX_VALUE;
    
    if ( top != null )
      result = ((Integer)top.getItem()).intValue();
    
    return result;
  }
  
  
  /***********************************************************************
    * pop 
    *
    * Remove the node pointed at by top (and return the item it contains).
    * Returns Integer.MAX_VALUE if the stack is empty.
    ************************************************************************/
  public int pop()
  {
    int result = Integer.MAX_VALUE;
    
    if ( top != null )
    {
      result = ((Integer)top.getItem()).intValue();
      top = top.getNext();
    }
    
    return result;
  }
  
} // end class Stack



/***********************************************************************
  ************************************************************************
  *  The Node class.
  *
  *  Ordinary linked-list nodes, storing an Object item (so it can
  *  be used by both the Stack class (which stores ints as Integers)
  *  and the Table class (which stores Variables).
  ***********************************************************************
  ************************************************************************/
class Node
{
  private Object item;
  private Node next;
  
  public Node( Object i, Node n )
  {
    item = i;
    next = n;
  }
  
  public Object getItem()
  {
    return item;
  }
  
  public Node getNext()
  {
    return next;
  }
  
  public void setNext( Node n )
  {
    next = n;
  }
} // end class Node


/***********************************************************************
  ************************************************************************
  *  The Table class.
  *
  *  A binary search tree that stores Variable records (key = variable id).
  *  It's sorted alphabetically by their Variable IDs.
  ***********************************************************************
  ************************************************************************/
class Table
{
  private BSTNode root;   //the top Node in the BST
  
  public Table()
  {
    root = null;
  }//Table constructor
  
  private class BSTNode
  {
    Variable data;          //The Variable
    BSTNode left;          //a pointer for the BSTNode's left child 
    BSTNode right;         //a pointer for the BSTNode's right child
    
    public BSTNode(Variable item, BSTNode left, BSTNode right)
    {
      data = item;
      this.left = left;
      this.right = right;
    }//BSTNode constructor
    
    public Variable getData()
    {
      return data;
    }//getData
    
    public BSTNode getLeft()
    { 
      return left; 
    }//getLeft
    
    public BSTNode getRight()
    {
      return right; 
    }//getRight
    
    public void setLeft(BSTNode left)
    {
      this.left = left;
    }//setLeft
    
    public void setRight(BSTNode right)
    {
      this.right = right;
    }//setRight
    
    /*** PURPOSE: This method prints out the BST in order. It prints the left values until there isn't a left value and then it prints out it's right value
      * if it has one.It does this by using recursion.It has no parameters and it returns nothing.
      */
    public void printAll()
    { 
      if(this.getLeft() != null)
        this.left.printAll();
      
      System.out.println(this.getData());
      
      if(this.getRight() != null)
        this.right.printAll();
    } // printAll
  }//BSTNode
  
  /*** PURPOSE: This method searches the BST for a variable that has the same ID as the key. Since the list is in order, it compares it's ID's value to the key.
    *  If it's smaller, it checks it's left child. If it's larger, it checks the right child. If they are equal, then key was found. It has the parameter String key,
    * which is the key being searched for, and it returns a null Variable if the variable wasn't found or the Variable with the matching ID if it was found.
    */
  public Variable findKey( String key )
  {
    BSTNode curr = root;     //the BSTNode being used to traverse the list
    boolean found = false;   //to know if the key was found in the list
    Variable result = null;  //the variable being returned
    
    while(curr != null && !found)
    {  
      if(key.equals(curr.getData().getIdentifier()))
        found = true;
      
      else
      {  
        if(key.compareTo(curr.getData().getIdentifier()) < 0)
        {
          if(curr.getLeft() == null)
            curr = null;
          
          else
            curr = curr.getLeft();
        }
        else
          if(curr.getRight() == null)
          curr = null;
        
        else
          curr = curr.getRight();
      }
    } // while 
    if(curr != null)
      result = curr.getData();
    
    return result;
  } // findKey
  
  /*** PURPOSE: This method creates and inserts a new BSTNode with the Variable var into the list if it wasn't already in it. If it was, it changes it's value
    * to it's new value. If the list was empty, it is inserted as the root. Otherwise, it compares the Variable from the findKey method to determine if it matches 
    * the variable being compared. If it is, it's value is changed. If it isn't, it gets inserted into it's position in the list by comparing it to the BSTNodes. If it 
    * is less than the BSTNode being compared, it checks it's left child. If it's more, it checks the right child. It does this until it reaches the leaf and can add the
    * new value. It has Variable var as a parameter and it returns nothing.
    */
  public void insert( Variable var )
  {
    Variable temp = findKey(var.getIdentifier());    //the either null or a Variable that was found to match Variable var
    BSTNode curr;                                    //the BSTNode used to search the list
    
    if(root == null)
      root = new BSTNode(var, null, null);
    
    else if(temp != null && temp.getIdentifier().equals(var.getIdentifier()))
    {
      if(temp.getValue() != var.getValue())
        temp.setValue(var.getValue());
    }
    
    else
    {  
      curr = root;
      
      while(curr != null)
      {
        if(var.getIdentifier().compareTo(curr.getData().getIdentifier()) < 0)
        {
          if(curr.getLeft() == null)
          {
            curr.setLeft(new BSTNode(var, null,null));
            curr = null;
          }
          else
            curr = curr.getLeft();
        }
        else
          
          if(curr.getRight() == null)
        {
          curr.setRight(new BSTNode(var, null,null));
          curr = null;
        }
        else
          curr = curr.getRight();
      }//while
    }
  } // insert
  
  /*** PURPOSE: This method calls another method to print out the list, or if it was empty, it prints a message saying it's empty.It has no parameters and returns nothing.
    */
  public void printAll()
  { 
    if(root == null)
      System.out.println("The tree is empty.");
    
    else
      root.printAll();
  } // end printAll
  
} // end class Table



/***********************************************************************
  ************************************************************************
  *  The Variable class.
  *
  *  It contains the variable's name (identifier) and its value (an int).
  ***********************************************************************
  ************************************************************************/
class Variable
{
  private String identifier;
  private int value;
  
  public Variable( String id, int val )
  {
    identifier = id;
    value = val;
  }
  
  public String getIdentifier() { return identifier; }
  public int getValue() { return value; }
  public void setValue( int v ) { value = v; }
  
  public static boolean isVariableID( String name )
  {
    return name.length() != 0 && name.toLowerCase().matches("[a-z][a-z[0-9]]*" );
  }
  
  public boolean equals( String id )
  {
    return id.equals( this.identifier );
  }
  public boolean equals( Variable var )
  {
    return (var.identifier).equals( this.identifier );
  }
  
  public String toString()
  {
    return "Variable " + identifier + " = " + value;
  }
} // end class Variable
