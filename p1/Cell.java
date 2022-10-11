public class Cell{

  //determines the type of cell from file
  // will either be "+" "-" or a letter
  // if the letter is a fixed letter then isFixed = true
  char cell_type;
  boolean isFixed;
  char value;

  // constructs a new cellar
  //params: character in the cell and boolean value for if the char
  //is a fixed char for the board
  public Cell(char cell_type, char value ){
    this.cell_type = cell_type;
    this.value = value;
    if(this.cell_type == '+'){
      this.isFixed = false;
    }else{
      this.isFixed = true;
    }
  }
  //returns if a cell is fixed
  //returns true if there is a fixed letter or minus
  //false if there is a plus
  public boolean getIsFixed(){
    return this.isFixed;
  }
  //returns the cell type
  // either a minus, plus, or a fixed letter
  //cell type does not get changed when trying different letters at a cell
  public char getCellType(){
    return this.cell_type;
  }
  //returns the value of the cell
  //if the cell type is a minus, then the value is a minus
  // if the cell type is a plus, then the value is either a plus
  //or a possible letter
  //if the cell type is a fixed letter, then the value is that letter
  public char getValue(){
    return this.value;
  }
  //sets the value of a cell
  //used when adding letters into a Cell[][] board
  public void setValue(char value){
    this.value = value;
  }
}
