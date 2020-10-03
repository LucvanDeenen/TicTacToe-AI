import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Random; 
import java.lang.Math; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TicTacToe extends PApplet {




// Playfield & 
int[] grid;

// Turn
boolean turn;
boolean aiStart;
boolean twoPlayers = false;

// Winner
String Winner;

// method invokes
Random rd = new Random();

public void setup(){
  //Set frontend
  
  background(255, 255, 255);
  noCursor();
    
  //Initialize values
  grid = new int[9]; 
  turn = rd.nextBoolean();
  aiStart = turn;
  Winner = null;
}

public void draw(){  
  //Make AI move
  if (turn) { MoveAI(1); }
  //2nd AI
  if (!turn && twoPlayers) { MoveAI(-1); }
  
  //Width and height per square
  int w = (width - 200) / 3;
  int h = height / 3;
  
  for(int i = 0; i < 3; i++){
    for(int j = 0; j < 3; j++){
      ////Start positions for drawing
      int x = w * i;
      int y = h * j;
      int xr = w / 4;
            
      switch (grid[3*i+j]) {
      //Player
      case -1:
        fill(255);
        rect(w * i, h * j, w, h);
        fill(0);
        line(x, y, x + w, y + h);
        fill(0);
        line(x + w, y, x, y + h);
        break;
        
      //AI
      case 1:
        fill(255);
        rect(w * i, h * j, w, h);
        fill(30);
        ellipse(x + (w / 2), y + (h / 2), w / 1.05f, h / 1.05f);
        break;
        
      //Empty
      case 0:
      default: 
        fill(255);
        rect(x, y, w, h);
        break;
      }
    }
  }   
  
  //Menu     
  fill(255);
  rect(w * 3, 0, 200, height);
  textSize(17);
  fill(10);
  text("Menu", 610, 20, -30);
  textSize(13);
  fill(25);
  text("Left click to select square! \nRight click to restart! \nPress space bar to let 2 AI \nplay against eachother", 610, 40, -30);
  textSize(13);
  fill(25);
  String s = (Winner != null) ? Winner + " has won the game!" : " ";
  text(s, 610, 140, -30);
  
  //Custom mouse
  fill(0);
  ellipse(mouseX - 5, mouseY - 5, 10, 10);
}

public void keyPressed() { if (key == ' ') { twoPlayers = !twoPlayers; setup(); } }
 
public void mouseClicked(){
  if (mouseButton == RIGHT) { setup(); }
  
  if (mouseButton == LEFT && turn == false && Winner == null && !twoPlayers) { 
   int jClicked = 0;
   int iClicked = 0;
   
    //Get clicked box
    if (mouseX > (width - 200) / 3.0f){ jClicked++; }
    if (mouseX > 2 * (width - 200) / 3.0f){ jClicked++; }
    if (mouseY > height / 3.0f){ iClicked++; }
    if (mouseY> 2 * height / 3.0f){ iClicked++; }
    
    if (grid[jClicked * 3 + iClicked] == 0) {
      grid[jClicked * 3 + iClicked] = -1;
      Evaluate(grid, true);
      turn = !turn;
    }
  }
}

public void MoveAI(int player) {
  if (Winner == null) { 
    int bestMove = -100;
    int move = -1;
    
    for (int i = 0; i < grid.length; i++) {
      if (grid[i] == 0) {
        grid[i] = player;
        int currentMove = MiniMax(grid, 0, aiStart);
        grid[i] = 0;
        if (currentMove > bestMove) {
          move = i;
          bestMove = currentMove;
        }   
      }  
    }
    
    if (MovesLeft(grid) != 0) { grid[move] = player; }
    Evaluate(grid, true);
    turn = !turn;
  }  
}

public int MiniMax(int[] board, int depth, boolean isMaximizing) {
  int score = Evaluate(board, false);
  if (score == 10) { return score - depth; }
  if (score == -10) { return score + depth; }
  if (MovesLeft(board) == 0) { return 0; }
  
  //If maximizing
  if (isMaximizing) {
    int best = -100;
    
    for (int i = 0; i < board.length; i++) {
      if (board[i] == 0) {
        board[i] = 1;
        best = Math.max(best, MiniMax(board, depth + 1, !isMaximizing));
        board[i] = 0;
      }
    }
    return (best - depth);
  }
  
  //If minimizing 
  else {
    int best = 100;
    
    for (int i = 0; i < board.length; i++) {
      if (board[i] == 0) {
        board[i] = -1;
        best = Math.min(best, MiniMax(board, depth + 1, !isMaximizing));
        board[i] = 0;
      }
    }
    return (best + depth);
  }
}

public int Evaluate(int[] board, boolean checkWin) {
  int score = 0;
   
  //Vertical check
  if (board[1] != 0 && (board[1] == board[0] && board[1] == board[2])) { score = (board[1] == 1) ? 10 : -10; }
  if (board[4] != 0 && (board[4] == board[3] && board[4] == board[5])) { score = (board[4] == 1) ? 10 : -10; }
  if (board[7] != 0 && (board[7] == board[6] && board[7] == board[8])) { score = (board[7] == 1) ? 10 : -10; }
  
  //Horizontal check
  if (board[3] != 0 && (board[3] == board[0] && board[3] == board[6])) { score = (board[3] == 1) ? 10 : -10; }
  if (board[4] != 0 && (board[4] == board[1] && board[4] == board[7])) { score = (board[4] == 1) ? 10 : -10; }
  if (board[5] != 0 && (board[5] == board[2] && board[5] == board[8])) { score = (board[5] == 1) ? 10 : -10; }
  
  //Diagonal check
  if (board[4] != 0 && (board[4] == board[0] && board[4] == board[8])) { score = (board[4] == 1) ? 10 : -10; }
  if (board[4] != 0 && (board[4] == board[2] && board[4] == board[6])) { score = (board[4] == 1) ? 10 : -10; }
    
  if (checkWin && score != 0) { Winner = (score == 10) ? "AI" : "Player"; }
  return score;
}

public int MovesLeft(int[] board) { 
  int moves = 0;
  for (int i = 0; i < board.length; i++) { if (board[i] == 0) { moves += 1; } } 
  return moves;
} 

   
  public void settings() {  size(800, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TicTacToe" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
