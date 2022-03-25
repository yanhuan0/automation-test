package com.qa.framework.imagetool;

import javax.imageio.*;
import javax.swing.ImageIcon;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

public class PngStuff {

    private BufferedImage img;
    ArrayList<Color> backgrounds = new ArrayList<Color>();

    public PngStuff() {

    }
    
    public PngStuff(BufferedImage image){
        img = image;
    }

    public PngStuff(int width, int height) {
    	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
        	  img.setRGB(x,y,0xff000000);
          }
        }
    }
    
    public PngStuff(int width, int height, boolean white){
    	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
                if (white){
                	img.setRGB(x,y,0xffffffff);
                } else{
                	img.setRGB(x,y,0xff000000);
                }
          }
        }
    }

    public void read(String filename)
    throws IOException {
        InputStream istream = new FileInputStream(filename);
        img = ImageIO.read(istream);
    }

    public void write(String filename)
    throws IOException {
        ImageIO.write(img, "png", new File(filename));
    }

    public int getHeight() {
        return img.getHeight();
    }

    public int getWidth() {
        return img.getWidth();
    }

    public int getRed(int x, int y) {
        return (img.getRGB(x, y) & 0x00ff0000) >> 16;
    }

    public int getGreen(int x, int y) {
        return (img.getRGB(x, y) & 0x0000ff00) >> 8;
    }

    public int getBlue(int x, int y) {
        return (img.getRGB(x, y) & 0x000000ff);
    }

    public void setRed(int x, int y, int value) {
        img.setRGB(x, y, (img.getRGB(x, y) & 0xff00ffff) | (value << 16));
    }

    public void setGreen(int x, int y, int value) {
        img.setRGB(x, y, (img.getRGB(x, y) & 0xffff00ff) | (value << 8));
    }

    public void setBlue(int x, int y, int value) {
        img.setRGB(x, y, (img.getRGB(x, y) & 0xffffff00) | value);
    }

    public void setRGB(int x, int y, int r, int g, int b) {
        img.setRGB(x, y, 0xff000000 | (r << 16) | (g << 8) | b);
    }
    
    public Color getColor(int x, int y){
    	int red = getRed(x,y);
    	int green = getGreen(x,y);
    	int blue = getBlue(x,y);
    	return new Color(red,green,blue);
    }
    
    public boolean isWhite(int x, int y){
    	if (getRed(x,y)==255 && getBlue(x,y)==255 && getGreen(x,y)==255){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public void scale(int scaledWidth, int scaledHeight) throws IOException {
	    int imageType = BufferedImage.TYPE_INT_RGB;
	    BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
	    Graphics2D g = scaledBI.createGraphics();
	    g.setComposite(AlphaComposite.Src);
	    g.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
	    g.dispose();
	    //ImageIO.write(scaledBI, "png", new File("components/testscale.png"));
	    img = scaledBI;
    }
    
    public void addBackground(Color c){
    	backgrounds.add(c);
    }
    
    public ArrayList<Color> getBackgrounds(){
    	return backgrounds;
    }


}
