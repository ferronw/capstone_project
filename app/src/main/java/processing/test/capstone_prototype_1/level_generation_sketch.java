package processing.test.capstone_prototype_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

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

public class level_generation_sketch extends PApplet {

    Uri imageUri;

    PImage img;
    PImage rotImg;
    PImage thresImg;
    float rot = 0;
    float drawX;
    float drawY;
    float transX = 0;
    float transY = 0;
    int currentMode = 0;
    float threshold = 127;
    int columns = 32;
    int rows = 18;
    int boxSize;
    int[] square;
    int[] playerStartPos;
    int[] goalPos;
    int clickCount = 0;
    int darkCount = 0;
    boolean runOnce = true;
    boolean runOnce2 = true;
    String filePath = "/data/data/processing.test.capstone_prototype_1/app_Images/levelToGenerate.jpg";
    String rotFilePath = "/data/data/processing.test.capstone_prototype_1/files/rot_output.jpg";

    level_generation_sketch(Uri selectedImage){
        imageUri = selectedImage;

    }

    public void setup(){
        size(1280,720);
        requestPermission("android.permission.READ_EXTERNAL_STORAGE", "initRead");
        requestPermission("android.permission.WRITE_EXTERNAL_STORAGE", "initWrite");

        thresImg = createImage(width,height, RGB);
        boxSize = width/columns;
        drawX = width/2;
        drawY = height/2;
        square = new int[boxSize*boxSize];
        playerStartPos = new int[2];
        goalPos = new int[2];
    }

    public void draw(){

        if (currentMode == 0) {
            //have the user rotate the image to the corrent orientation
            translate(width/2, height/2);
            rotate(rot);
            if (img != null) {
                image(img,-width/2,-height/2,width, height);
                save("temp_output.jpg");
            }
            /*rotate(-rot);
            translate(-width/2, -height/2);
            ellipseMode(CENTER);
            fill(127, 127);
            ellipse(950, 100, 100,100);
            ellipse(1100, 100, 100,100);
            fill(255);
            textSize(26);
            text("R", 950, 100);
            text("P", 1100, 100);*/
            //
        } else {
            //convert to black and white
            rotImg = loadImage("temp_output.jpg");
            if (rotImg != null) {
                rotImg.loadPixels();
                thresImg.loadPixels();

                for (int x = 0; x < rotImg.width; x++) {
                    for (int y = 0; y < rotImg.height; y++) {
                        int loc = x + y * rotImg.width;
                        if (brightness(rotImg.pixels[loc]) > threshold) {
                            thresImg.pixels[loc] = color(255);
                        } else {
                            thresImg.pixels[loc] = color(0);
                        }
                    }
                }
                thresImg.updatePixels();
                image(thresImg, 0, 0);
                save("thresh_output.jpg");
                String outData[] = new String[rows];
                for (int i = 0; i < outData.length; i++) {
                    outData[i] = "";
                }
                thresImg.loadPixels();
                for (int i = 0; i < columns; i++) {
                    for (int j = 0; j < rows; j++) {
                        //determine if the square has over 50% black squares
                        int x = i * boxSize;
                        int y = j * boxSize;
                        for (int k = 0; k < boxSize; k++) {
                            for (int l = 0; l < boxSize; l++) {
                                square[(k * boxSize) + l] = thresImg.pixels[(x + l) + ((y + k) * thresImg.width)];
                            }
                        } //<>//
                        for (int n = 0; n < square.length; n++) {
                            if (brightness(square[n]) < threshold) {
                                darkCount++;
                            }
                        }
                        if (i == playerStartPos[0] && j == playerStartPos[1]){
                            outData[j] += "4,";
                        } else if (i == goalPos[0] && j == goalPos[1]){
                            outData[j] += "3,";
                        } else {
                            if (darkCount >= 800) {
                                outData[j] += "1,";
                            } else {
                                outData[j] += "0,";
                            }
                        }
                        //outData[j] += String.valueOf(darkCount);
                        darkCount = 0;
                    }
                }
                saveStrings("level_output.txt", outData);
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        }

    }


    int findAverageColor(int[] squareArray){
        int size = squareArray.length;
        float r = 0;
        float g = 0;
        float b = 0;
        int num = 0;
        for (int i = 0; i < size; i++){
            r += red(squareArray[i])*red(squareArray[i]);
            g += green(squareArray[i])*green(squareArray[i]);
            b += blue(squareArray[i])*blue(squareArray[i]);
            num++;
        }
        return color(sqrt(r/num), sqrt(g/num), sqrt(b/num));
    }

    int extractColorFromImage(PImage img) {
        img.loadPixels();
        int r = 0, g = 0, b = 0;
        for (int i=0; i<img.pixels.length; i++) {
            int c = img.pixels[i];
            r += c>>16&0xFF;
            g += c>>8&0xFF;
            b += c&0xFF;
        }
        r /= img.pixels.length;
        g /= img.pixels.length;
        b /= img.pixels.length;

        return color(r, g, b);
    }

    void initRead(boolean granted) {
        if (granted) {
            println("init read sdcard OK");
            img = loadImage(filePath);
            img.resize(width, height);
        } else {
            println("Read storage is not available");
        }
    }
    void initWrite(boolean granted) {
        if (granted) {
            println("init write storage OK");
        } else {
            println("Write storage is not available");
        }
    }

    public void mousePressed(){
        if (currentMode == 0) {
            if (dist(mouseX, mouseY, 100, 100) < 50) {
                rot += PI;
                //println("Rotate Button pressed");
            } else if (dist(mouseX, mouseY, 1100, 100) < 50) {
                currentMode = 1;
                println("Process Button pressed");
            } else {
                if (clickCount == 0){
                    //set coordinates for player spawn
                    playerStartPos[0] = mouseX / boxSize;
                    playerStartPos[1] = mouseY / boxSize;
                    clickCount++;
                    println("Setting player start to: " + playerStartPos[0] + " , " + playerStartPos[1]);
                } else if (clickCount == 1){
                    //set coordinates for goal spawn
                    goalPos[0] = mouseX / boxSize;
                    goalPos[1] = mouseY / boxSize;
                    clickCount++;
                    println("Setting goal pos to: " + goalPos[0] + " , " + goalPos[1]);
                }

            }
        }
    }

    public void settings() {  size(1280,720); }
}
