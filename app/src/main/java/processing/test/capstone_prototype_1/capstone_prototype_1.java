package processing.test.capstone_prototype_1;

import android.content.Intent;

import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

//contains all code for the actual gameplay
/* NEED TO ADD:
character assets and animation
multitouch for controls
collision with enemies and failstate

 */
public class capstone_prototype_1 extends PApplet {

ArrayList<Block> environment;
ArrayList<Enemy> enemies;
PlayerCharacter player;
Block testBlock;
Goal goal;
int columns = 32;
int rows = 18;
int boxSize;
PImage blockImg;
PImage flagImg;
PImage tempBackground;
boolean runOnce = true;
float time = 0;
String gamestate = "play";
String path = "/data/data/processing.test.capstone_prototype_1/files/temp_background.jpg";
String levelPath = "/data/data/processing.test.capstone_prototype_1/files/level_output.txt";


public void setup(){
  
 background(255);
 
 boxSize = width/columns;
 
 blockImg = loadImage("block.png");
 flagImg = loadImage("flag.png");
 
 environment = new ArrayList();
 enemies = new ArrayList();

 //load level
 // 1 is block, 2 is enemy, 3 is goal, 4 is playerCharacter
 String[] data = loadStrings(levelPath);
 for (int i = 0; i<data.length; i++){
   int[] line = PApplet.parseInt (split(data[i],","));
   for (int j = 0; j<line.length; j++){
     if (line[j] == 1){
       environment.add( new Block((j)*boxSize, (i)*boxSize)); 
     } else if ((goal == null) && (line[j] == 3)) {
       goal = new Goal((j)*boxSize, (i)*boxSize);
     } 
   }
 }
 
 
  for (int i = 0; i<data.length; i++){
   int[] line = PApplet.parseInt (split(data[i],","));
   for (int j = 0; j<line.length; j++){
      if (line[j] == 2) {
       enemies.add( new Enemy((j)*boxSize, (i)*boxSize, environment));
     }  
   }
 }
 
 
 for (int i = 0; i<data.length; i++){
   int[] line = PApplet.parseInt (split(data[i],","));
   for (int j = 0; j<line.length; j++){
      if ((player == null) && (line[j] == 4)) {
       player = new PlayerCharacter((j)*boxSize, (i)*boxSize, environment, enemies);
     }  
   }
 }
}

public void draw(){
  if (runOnce){
    background(0,100,255);
    for (int i = environment.size()-1; i>=0; i--){
      Block b = (Block) environment.get(i);
      b.show();
    }
    goal.show();
    save("temp_background.jpg");

    tempBackground = loadImage(path);
  }
  runOnce = false;
  //tempBackground = loadImage(path);
  if (gamestate.equals("play")){
    if (tempBackground != null) {
      image(tempBackground, 0, 0);
    } else {
      tempBackground = loadImage(path);
    }
    player.motion();
    player.enemyCollision();
    player.completion();
    player.show();
    
    for (int i = enemies.size()-1; i>=0; i--){
        Enemy e = (Enemy) enemies.get(i);
        e.move();
        e.show();
      }
    
    time = frameCount/60.0f;
    

  } else if (gamestate.equals("win")){
      textSize(48);
      fill(0);
      text("LEVEL COMPLETE!", width/2+2, height/2+2);
      fill(255);
      text("LEVEL COMPLETE!", width/2, height/2);
      startActivity(new Intent(getContext(), MainActivity.class));
  } else if (gamestate.equals("fail")){
      textSize(48);
      fill(0);
      text("Level failed!", width/2+2, height/2+2);
      fill(255);
      text("Level failed!", width/2, height/2);
      startActivity(new Intent(getContext(), MainActivity.class));
  }
  /*
  for (int i = environment.size()-1; i>=0; i--){
   Block b = (Block) environment.get(i);
   b.show();
  }*/


  //draw ui
  fill(150, 120);
  textSize(20);
  rect(100, 650, 150, 50);
  fill(255);
  text("Left", 160,680);
  fill(150, 120);
  rect(300, 650, 150, 50);
  fill(255);
  text("Right", 360,680);
  fill(150, 120);
  rect(1000,640, 100, 70);
  fill(255);
  text("Jump", 1025,680);

//player input
  for (int i = 0; i < touches.length; i++) {
    if (touches[i].x > 100 && touches[i].x < 250 && touches[i].y > 650 && touches[i].y < 700){
      //player.hMotion = -5;
      player.leftMove = true;
    } else if (touches[i].x > 300 && touches[i].x < 450 && touches[i].y > 650 && touches[i].y < 700){
      //player.hMotion = 5;
      player.rightMove = true;
    } else if (touches[i].x > 1000 && touches[i].x < 1100 && touches[i].y > 640 && touches[i].y < 720){
      //player.vMotion = -10;
      if (player.canJumpAgain == true) {
        player.jump = true;
        player.canJumpAgain = false;
      }
    }else {
      //player.hMotion = 0;
    }
  }
  showDebugText();
  /*for (int j = 0; j < columns; j++){
    for (int k = 0; k < rows; k++){
       stroke(127);
       noFill();
       rect(j*boxSize, k*boxSize, boxSize, boxSize);
    }
  }*/
}

public void showDebugText(){
  fill(255);
  textSize(12);
  text("x: " + player.x, 25,25);
  text("hmotion: " + player.hMotion, 25,50);
  text("time: " + time, 25,75);
  text("gamestate: " + gamestate, 25,100);
  text("jump: " + player.jump, 25,125);
  text("canJumpAgain: " + player.canJumpAgain, 25,150);
  text("mousePressed: " + mousePressed, 25, 175);
  text("touches: " + touches.length, 25, 200);
}

class PlayerCharacter {
  
  float x;          // Current x-coordinate
  float y;          // Current y-coordinate
  float mWidth = 12;
  float mHeight = 36;
  float grav = 0.5f;      // force of gravity
  boolean rightMove = false;
  boolean leftMove = false;
  boolean jump = false;
  boolean canJumpAgain = true;
  int jumpResetCounter = 0;
  float hMotion = 0;   //current horizontal motion (speed)
  float vMotion = 0;   //current verticle motion (speed)
  
  ArrayList<Block> environment;
  ArrayList<Enemy> enemies;
  
  PlayerCharacter(int spawnX, int spawnY, ArrayList<Block> envin, ArrayList<Enemy> enems){
    x = spawnX;
    y = spawnY;
    
    environment = envin;
    enemies = enems;
  }
  
  //all physics and movement here
  public void motion(){
    
    if (touches.length == 0){
      leftMove = false;
      rightMove = false;
    }
    
    //handle inputs
    if (leftMove && rightMove){
      hMotion = 0;
    } else if (rightMove){
      hMotion = 5;
    } else if (leftMove){
      hMotion = -5;
    } else {
      hMotion = 0;
    }
    
    if (jump){
      vMotion = -10;
      jump = false;
    }
    
    vMotion+=grav;
      
      //for every block in the environment, we need to check for collision
      for (int i = environment.size()-1; i>=0; i--){
         Block b = (Block) environment.get(i);
         
         if (x+mWidth+hMotion>b.x && x+hMotion<b.x+boxSize && y+mHeight+vMotion>b.y && y+vMotion<b.y+boxSize){
           //so we've detected a collision

           //VERTICLE MOTION PRESERVATION
           float oldVMotion = vMotion;
           
           float signOldVMotion;
           if (oldVMotion > 0){signOldVMotion = 1;}
           else if (oldVMotion < 0) {signOldVMotion = (-1);}
           else {signOldVMotion = 0;}
           
           vMotion = 0;
           
           while 
                 (!(x+mWidth>b.x && x<b.x+boxSize && y+mHeight+vMotion+signOldVMotion>b.y && y+vMotion+signOldVMotion<b.y+boxSize) &&
                 (abs(vMotion) < abs(oldVMotion))) 
           {
             vMotion = vMotion + signOldVMotion; 
           }
           
           
           //HORIZONTAL MOTION PRESERVATION
           float oldHMotion = hMotion;
           
           float signOldHMotion;
           if (oldHMotion > 0){signOldHMotion = 1;}
           else if (oldHMotion < 0) {signOldHMotion = (-1);}
           else {signOldHMotion = 0;}
           
           hMotion = 0;
           
           while 
                 (!(x+mWidth+hMotion+signOldHMotion>b.x && x+hMotion+signOldHMotion<b.x+boxSize && y+mHeight>b.y && y<b.y+boxSize) &&
                 (abs(hMotion) < abs(oldHMotion))) 
           {
             hMotion = hMotion + signOldHMotion; 
           }
   
         }
       }
      
      //jump reset check
      if (canJumpAgain == false && vMotion == 0) {
        jumpResetCounter++;
        if (jumpResetCounter == 2){
          canJumpAgain = true;
          jumpResetCounter = 0;
        }
      }
      
      if (x+mWidth+hMotion>width || x+hMotion<0){
       hMotion = 0; 
      }
      //add the final h and v motions to our position
      x+=hMotion;
      y+=vMotion;
      
  }
  
  public void enemyCollision(){
    for (int i = enemies.size()-1; i>=0; i--){
         Enemy e = (Enemy) enemies.get(i);
         if (x+mWidth>e.x && x<e.x+e.size && y+mHeight>e.y && y<e.y+e.size){
           gamestate = "fail";
         }
    }
    if (y > height+50){
      gamestate = "fail";
    }
  }
  
  public void completion(){
    if (x+mWidth>goal.x && x<goal.x+boxSize && y+mHeight>goal.y && y<goal.y+boxSize){
      gamestate = "win";
    }
  }
  
  public void show() {
    noStroke();
    fill(5,200,0);
    rect(x,y,mWidth,mHeight);
  }
  
  
}

class Enemy {
  float x;
  float y;
  float size = 30;
  float speed = 1;
  float tempDirection;
  int direction = 1;
  
  ArrayList<Block> environment;
  
  Enemy(int xStart, int yStart, ArrayList<Block> envin) {
    x = xStart;
    y = yStart;
    tempDirection = random(-1,1);
    if (tempDirection > 0){
      direction = (-1);
    }
    environment = envin;
  }
  
  public void show(){
    noStroke();
    fill(255,0,10);
    rect(x,y,size,size);
  }
  
  public void move(){
    //check if we're going off screen
    if (x+size+(speed*direction)>width || x+(speed*direction)<0){
      direction*=(-1);
    }
    
    //check if we're going to collide with a block
    for (int i = environment.size()-1; i>=0; i--){
         Block b = (Block) environment.get(i);
         if (x+size+speed>b.x && x+speed<b.x+boxSize && y+size>b.y && y<b.y+boxSize){
           direction*=(-1);
         }
    
    }
    
    x += speed*direction;
  }
  
}

class Goal {
  int x;
  int y;
   
  
  Goal(int xStart, int yStart){
    x = xStart;
    y = yStart;
  }
  
  public void show(){
    noStroke();
    fill(0);
    //rect(x,y,boxSize,boxSize);
    image(flagImg, x+(boxSize*0.25f), y, (boxSize*0.75f), boxSize);
  }
  
}

class Block {
  int x;
  int y;
   
  
  Block(int xStart, int yStart){
    x = xStart;
    y = yStart;
  }
  
  public void show(){
    noStroke();
    fill(0);
    //rect(x,y,boxSize,boxSize);
    image(blockImg, x, y, boxSize, boxSize);
  }
  
}

public void mousePressed(){

  /*
  if (mouseX > 100 && mouseX < 250 && mouseY > 650 && mouseY < 700){
    //player.hMotion = -5;
    player.leftMove = true;
  } else if (mouseX > 300 && mouseX < 450 && mouseY > 650 && mouseY < 700){
    //player.hMotion = 5;
    player.rightMove = true;
  } else if (mouseX > 1000 && mouseX < 1075 && mouseY > 650 && mouseY < 720){
    //player.vMotion = -10;
    player.jump = true;
    player.canJumpAgain = false;
  }else {
    //player.hMotion = 0;
  }
  */
}

public void keyPressed(){
  if(key == 'a'){
    player.leftMove = true;
  }
  if(key == 'd'){
    player.rightMove = true;
  }
  if(key == 'w'){
    if (player.canJumpAgain){
      player.jump = true;
      player.canJumpAgain = false;
    }
  }
  if(key == 'x'){
    String fileName = dataPath("temp_background2.jpg");
  File f = new File(fileName);
  if (f.exists()) {
    f.delete();
  }
  
  }
}


public void keyReleased(){
  if(key == 'a'){
    player.leftMove = false;
  }
  if(key == 'd'){
    player.rightMove = false;
  } 
  if(key == 'w'){
    player.jump = false;
  }
}
  public void settings() {  size(1280,720); }
}
