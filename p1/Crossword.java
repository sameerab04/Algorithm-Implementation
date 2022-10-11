/*
Sameera Boppana - ssb40@pitt.edu
4254417
Ramirez 1501 - section 1020
Recitation - 1320
*/

import java.util.*;
import java.io.*;


public class Crossword{

  public  int num_solutions;
  public int count =0;
  public  Cell [][] result;
  public  String prefix_across;
  public  String prefix_down;
  public static final char[] ALPHABET = {'a','b','c','d','e','f','g','h','i',
    'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

  //finds all the possible characters to form a valid prefix at a given location
  //direction = "across" or "down"
  public String findPrefix(Cell [][] board, TrieSTNew<String> dict, int i, int j, String direction){

        //finding the prefix going across the board
        if(direction.equals("across")){
          int k = 0;
          while(k<j){
            //If the cell value is not a mimus, add the value to the prefix
            //else reset the prefix and continue until reach the current location
            if(board[i][k].getCellType() != '-'){
              prefix_across+= board[i][k].getValue();
            }
            else{
              prefix_across = "";
            }
            k++;
        }
      return prefix_across;

    //find column prefix
    }else{
      int k =0;
      while(k<i){
        //If the cell value is not a mimus, add the value to the prefix
        //else reset the prefix and continue until reach the current location
        if(board[k][j].getCellType() != '-'){
          prefix_down+= board[k][j].getValue();
        }else {
          prefix_down= "";
        }
        k++;
     }
  }
  return prefix_down;
}



  //method to return only the valid letters at a given location
  //uses searchPrefix to only return possible letters, eliminating letters that would make the board fail
  public ArrayList<Character> getValidLetters(Cell [][] board, TrieSTNew<String> dict, int i, int j){

    int n = board.length;
    ArrayList<Character> valid_letters = new ArrayList<Character>();

    //get the prefix across and down
    prefix_across = findPrefix(board, dict, i, j, "across");
    prefix_down = findPrefix(board, dict, i, j, "down");

    String temp_across = "";
    String temp_down = "";
    char[] possible_letters = new char[0];

    //calls helper method to check if the given location is a letter
    //if the cell is a fixed letter - only possible option for the cell and possible_letters should only contain that letter
    //else the entire alphabet is possible
    if(isFixedLetter(board, i, j)){
        possible_letters = new char[1];
        possible_letters[0] = board[i][j].getValue();
    }else if(isPlus(board, i, j)){
      possible_letters = ALPHABET;
     }

     //loop through possible_letters and check to see if a given letter is valid
     for(int k=0; k<possible_letters.length; k++){
       //append possible_letters[k] to the two prefixes
       temp_across =  prefix_across + possible_letters[k];
       temp_down = prefix_down + possible_letters[k];

       //two cases: 1) need to check if we get to the edge of the board or a minue - then temp_down must be a full word
       // or 2) not at an edge and temp_down is a prefix to a large
       // checking to see if column is valid
       boolean c1 = (isEdge(board, i, j, "down") && isWord(dict, temp_down)) || (!isEdge(board, i, j, "down")  && isPrefix(dict, temp_down));
       // check to see if row is valid
       boolean c2 = (isEdge(board,i,j,"across") && isWord(dict, temp_across)) || (!isEdge(board, i, j, "across") && isPrefix(dict, temp_across));

     //if true then letter is valid
     if(c1 && c2){
       valid_letters.add(possible_letters[k]);
     }
   }
    return valid_letters;
  }

  //driver method that recursively finds all the solutions to a given board
  public int find_puzzles(Cell[][] board, TrieSTNew<String> dict, int i, int j){

    ArrayList<Character> valid_letters = new ArrayList<Character>();


    Cell[][] tempBoard = copyBoard(board);
    prefix_across = "";
    prefix_down = "";
     int num_solutions = 0;


    //if i == board.length - found one solution
    if(i==board.length){
      count ++;
      if((count-1) % 10000 == 0){
        System.out.println(count-1);
        printBoard(tempBoard);
      }
      return 1;
    }
    //finding the next non-minus cell
    //uses helper method findNextPosition(Cell[][] board, int i, int j)
    while(board[i][j].getCellType() == '-'){
      int [] next_loc = findNextPosition(board,i,j);
      i = next_loc[0];
      j = next_loc[1];
    }

    valid_letters = getValidLetters(board, dict, i, j);

      char original_value = board[i][j].getValue();

    //loops through the valid letters and sets each letter to the current location
    //continues with the tenpBoard and the next location
    for(int k=0; k<valid_letters.size(); k++){
      tempBoard[i][j].setValue(valid_letters.get(k));

      int[] next_loc = findNextPosition(board, i, j);
      int new_i = next_loc[0];
      int new_j = next_loc[1];

     //keeping track of the number of solutions found
     num_solutions += find_puzzles(tempBoard,dict, new_i,new_j);

    }
    //resetting the board
    tempBoard[i][j].setValue(original_value);

    return num_solutions;
  }


  //helper method to find a non-minus cell on a given board
  public int[] findNextPosition(Cell[][] board, int i, int j){

    int [] next_loc = new int[2];
    int size = board.length;
      //if j is not at the end of a row, increment j
      //else at the end of a row, increment i and set j back to 0
      if(j<size-1){
        j+=1;
      }
      else{
        i+=1;
        j=0;
      }

    next_loc[0] = i;
    next_loc[1] = j;

    return next_loc;
  }

  //helper method to see if a prefix is a full word
  public boolean isWord(TrieSTNew<String> dict, String prefix){
    int result = dict.searchPrefix(prefix);
    return result == 2 || result == 3;
  }

  //helper method to see if a prefix is a prefix to a larger word
  public boolean isPrefix(TrieSTNew<String> dict, String prefix){
    int result = dict.searchPrefix(prefix);
    return result == 1 || result == 3;
  }

  //helper method to see if at the edge of a board or at a minus
  public boolean isEdge(Cell[][] board, int i, int j, String direction ){
    if(direction.equals("down")){
      return (i == board.length-1 || board[i+1][j].getCellType() == '-');
    }
    return j == board.length-1 || board[i][j+1].getCellType() == '-';
  }
  //helper method to see if a given location is a fixed letters
  public boolean isFixedLetter(Cell[][] board, int i, int j){
    return board[i][j].getIsFixed() && board[i][j].getCellType() != '-';
  }
    //helper method to see if a given location is a plus
  public boolean isPlus(Cell[][] board, int i, int j){
    return board[i][j].getCellType() == '+';
  }

  //helper method to print a given Cell[][] board
  public void printBoard(Cell[][] board){
    for(int i=0; i<board.length; i++){
      for(int j=0; j<board.length; j++){
        System.out.print(board[i][j].getValue() + " " );
      }
      System.out.println();
    }
    System.out.println();
  }

  //helper method to copy a Cell [][]
  public Cell[][] copyBoard(Cell[][] board){
    Cell[][] new_board = new Cell[board.length][board.length];
    for(int i=0; i<board.length; i++){
      for(int j=0; j<board.length; j++){
        new_board[i][j] = board[i][j];
      }
    }
    return new_board;
  }

  //overloaded find_puzzles - called in main() method
  public int find_puzzles(Cell[][] board, TrieSTNew<String>dict){
    return find_puzzles(board, dict, 0,0);
  }


  public static void main(String[] args) throws IOException {

    //reading in a dictionary of words and forming a TrieSTNew
    //file name is inputted through the command line
    Scanner dict_fname = new Scanner(new FileInputStream(args[0]));
    TrieSTNew<String> dict = new TrieSTNew<String>();
    String str;

    while (dict_fname.hasNext()){
      str = dict_fname.nextLine();
      dict.put(str,str);
    }

    //reading in a crossword board from a file
    //file is specified through the command line
    Scanner board_fname = new Scanner(new FileInputStream(args[1]));
    int size = Integer.parseInt(board_fname.nextLine());
    Cell [][] board = new Cell[size][size];
    while(board_fname.hasNext()){
      for(int i=0; i<size; i++){
        String row = board_fname.nextLine();
        for(int j=0; j<size; j++){
          char cell_type = row.charAt(j);
          switch(cell_type){
            case '+':
            board[i][j] = new Cell('+', '+');
            break;
            case '-':
            board[i][j] = new Cell('-', '-');
            break;
            default:
            board[i][j] = new Cell(cell_type, cell_type);
            break;
          }

        }
      }

    }

    Crossword crossword = new Crossword();
    int num_solutions = crossword.find_puzzles(board, dict);
   System.out.println("Total number of solutions: " + num_solutions);

  }
}
