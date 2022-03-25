package com.qa.framework.imagetool;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageCompareFinal {
	PngStuff img1;
	PngStuff img2;
	PngStuff seenPixels1; //pixel is white if has been seen, otherwise black
	PngStuff seenPixels2;
	ArrayList<PngStuff> components1 = new ArrayList<PngStuff>();
	ArrayList<PngStuff> components2 = new ArrayList<PngStuff>();
	int numChange = 1;
	boolean scale;
	boolean background;
	boolean equalSizes;
        String output;
        String file = "comp";
	
    public ImageCompareFinal(BufferedImage image1, BufferedImage image2, String outputFolder, 
        boolean scl, boolean bckgrnd, boolean eqSizes) {
        img1 = new PngStuff(image1);
        img2 = new PngStuff(image2);
        output = outputFolder;
        scale = scl;
        background = bckgrnd;
        equalSizes = eqSizes;
        seenPixels1 = new PngStuff(img1.getWidth(), img1.getHeight());
        seenPixels2 = new PngStuff(img2.getWidth(), img2.getHeight());
    }
    
    public ImageCompareFinal(BufferedImage image1, BufferedImage image2, String outputFolder, String fileName,
            boolean scl, boolean bckgrnd, boolean eqSizes) {
            img1 = new PngStuff(image1);
            img2 = new PngStuff(image2);
            output = outputFolder;
            scale = scl;
            background = bckgrnd;
            equalSizes = eqSizes;
            seenPixels1 = new PngStuff(img1.getWidth(), img1.getHeight());
            seenPixels2 = new PngStuff(img2.getWidth(), img2.getHeight());
            file = fileName;            
        }
    
    public ImageCompareFinal(String filename1, String filename2, String outputFolder,
        boolean scl, boolean bckgrnd, boolean eqSizes) {
    	img1 = new PngStuff();
        img2 = new PngStuff();
        output = outputFolder;
        scale = scl;
        background = bckgrnd;
        equalSizes = eqSizes;
        try {
            img1.read(filename1);
            img2.read(filename2);
            seenPixels1 = new PngStuff(img1.getWidth(), img1.getHeight());
            seenPixels2 = new PngStuff(img2.getWidth(), img2.getHeight());
    	} catch(Exception e){
            e.printStackTrace();
    	}
    }
    
    public ImageCompareFinal(String filename1, String filename2, String outputFolder, String fileName,
        boolean scl, boolean bckgrnd, boolean eqSizes) {
        img1 = new PngStuff();
        img2 = new PngStuff();
        output = outputFolder;
        scale = scl;
        background = bckgrnd;
        equalSizes = eqSizes;
        file = fileName; 
        try {
            img1.read(filename1);
            img2.read(filename2);
            seenPixels1 = new PngStuff(img1.getWidth(), img1.getHeight());
            seenPixels2 = new PngStuff(img2.getWidth(), img2.getHeight());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * returns list of points adjacent to Point p inside array of width w and height h
     * @param p
     * @param w
     * @param h
     * @return ArrayList of points adjacent to p
     */
    public ArrayList<Point> getAdjacent(Point p, int w, int h){
        ArrayList<Point> ptlist = new ArrayList<Point>();
        if (p.x+1<w){
                ptlist.add(new Point(p.x+1,p.y));
        }
        if (p.x-1>=0){
                ptlist.add(new Point(p.x-1,p.y));
        }
        if (p.y+1<h){
                ptlist.add(new Point(p.x,p.y+1));
        }
        if (p.y-1>=0){
                ptlist.add(new Point(p.x,p.y-1));
        }
        if (p.x-1>=0 && p.y-1 >=0){
                ptlist.add(new Point(p.x-1,p.y-1));
        }
        if (p.x-1>=0 && p.y+1 <h){
                ptlist.add(new Point(p.x-1,p.y+1));
        }
        if (p.x+1<w && p.y-1 >=0){
                ptlist.add(new Point(p.x+1,p.y-1));
        }
        if (p.x+1<w && p.y+1 <h){
                ptlist.add(new Point(p.x+1,p.y+1));
        }
        return ptlist;
    }
    
    /**
     * Goes through original image breadth-first starting with point (x,y), and finds all non-white pixels
     * reachable from (x,y) by going through non-white pixels. Places these pixels inside a minimum
     * bounding box with white background -- this is the PngStuff component. If background is set to true, checks if
     * any color takes up more than 50% of the pixels, and if so adds it to the list of background colors for
     * this component. Adds the component to the ArrayList components.
     * @param image
     * @param sp
     * @param components
     * @param x
     * @param y
     */
    public void makeComponent(PngStuff image, PngStuff sp, ArrayList<PngStuff> components, int x, int y){
    	HashMap<Color,Integer> colorMap = new HashMap<Color,Integer>();
    	ArrayList<Point> component = new ArrayList<Point>(); //add to seen below
    	component.add(new Point(x,y));
    	int w = image.getWidth();
    	int h = image.getHeight();
    	ArrayList<Point> q = new ArrayList<Point>();
    	q.add(new Point(x,y));
    	int minx = x;
    	int miny = y;
    	int maxx = x;
    	int maxy = y;
    	while (!q.isEmpty()){
    		Point p = q.remove(0);
    		for (Point pt: getAdjacent(p, w, h)){
    			if (!sp.isWhite(pt.x, pt.y) && !image.isWhite(pt.x, pt.y)){
    				component.add(pt);
    				if (pt.x<minx){
    					minx = pt.x;
    				} else if (pt.x>maxx){
    					maxx = pt.x;
    				}
    				if (pt.y<miny){
    					miny = pt.y;
    				} else if (pt.y>maxy){
    					maxy = pt.y;
    				}
    				sp.setRGB(pt.x, pt.y, 255, 255, 255);
    				q.add(pt);
    			}
    		}
    	}
    	int compw = maxx-minx+1;
    	int comph = maxy-miny+1;
    	int size = compw*comph;
    	PngStuff comp = new PngStuff(compw,comph, true);
    	for (Point pt: component){
    		int red = image.getRed(pt.x, pt.y);
    		int green = image.getGreen(pt.x, pt.y);
    		int blue = image.getBlue(pt.x, pt.y);
    		comp.setRed(pt.x-minx, pt.y-miny, red);
    		comp.setGreen(pt.x-minx, pt.y-miny, green);
    		comp.setBlue(pt.x-minx, pt.y-miny, blue);
    		if (background){
	    		Color c = new Color(red,green,blue);
	    		if (colorMap.containsKey(c)){
	    			int val = colorMap.get(c);
	    			colorMap.put(c, val+1);
	    		} else {
	    			colorMap.put(c, 1);
	    		}
    		}
    	}
    	if (background){
	    	int threshSize = 10000;
	    	if (compw*comph>threshSize){
		    	for (Color color: colorMap.keySet()){
		    		if (100.0*colorMap.get(color)/(1.0*size)>50){
		    			comp.addBackground(color);
		    			//System.out.println("added");
		    		}
		    	}
	    	}
    	}
    	components.add(comp);
    }
    
    /**
     * Goes through each image left-right, top-bottom and whenever finds a non-white pixel 
     * that has not been seen before, makes a component starting with that pixel
     */
    public void makeComps(){
    	for (int x=0; x<img1.getWidth(); x++){
            for (int y=0; y<img1.getHeight(); y++){
            	if (!seenPixels1.isWhite(x, y) && !img1.isWhite(x, y)){
            		seenPixels1.setRGB(x,y,255,255,255);
            		makeComponent(img1,seenPixels1,components1,x,y);
            	}
            }
    	}
    	for (int x=0; x<img2.getWidth(); x++){
            for (int y=0; y<img2.getHeight(); y++){
            	if (!seenPixels2.isWhite(x, y) && !img2.isWhite(x, y)){
            		seenPixels2.setRGB(x,y,255,255,255);
            		makeComponent(img2,seenPixels2,components2,x,y);
            	}
            }
    	}
    }
    
    /**
     * compares two components and counts the total number of pixels and the number of pixels that are different;
     * if background is set to true, filters out colors that are the background of both components;
     * if changes is true, writes the two components and the difference image;
     * @param comp1
     * @param comp2
     * @param changes
     * @return Point(difference count, total number of pixels)
     */
    public Point compareComp(PngStuff comp1, PngStuff comp2, boolean changes){
    	int w = Math.min(comp1.getWidth(), comp2.getWidth());
    	int h = Math.min(comp1.getHeight(), comp2.getHeight());
    	double threshold = 100;
    	
    	ArrayList<Color> backgrounds = new ArrayList<Color>();
    	if (background){
	    	for (Color c: comp1.getBackgrounds()){
	    		for (Color c2: comp2.getBackgrounds()){
	    			if (c.equals(c2)){
	    				backgrounds.add(c);
	    				break;
	    			} else {
	    				int redDiff = Math.abs(c.getRed()-c2.getRed());
	                    int blueDiff = Math.abs(c.getBlue()-c2.getBlue());
	                    int greenDiff = Math.abs(c.getGreen()-c2.getGreen());
	                    double dist = Math.sqrt(redDiff*redDiff + blueDiff*blueDiff + greenDiff*greenDiff);
	                    if (dist<=threshold){
	                    	backgrounds.add(c);
	                    	backgrounds.add(c2);
	                    	break;
	                    }
	    			}
	    		}
	    	}
	    	//System.out.println(backgrounds);
    	}
    	
        PngStuff changesImg = new PngStuff(w,h);
        
    	int diffcount = 0;
    	int totalPixels = 0;
        for (int x=0; x<w; x++){
            for (int y=0; y<h; y++){
            	if (!(comp1.isWhite(x,y) && comp2.isWhite(x,y))){
                    int redDiff = Math.abs(comp1.getRed(x,y)-comp2.getRed(x,y));
                    int blueDiff = Math.abs(comp1.getBlue(x,y)-comp2.getBlue(x,y));
                    int greenDiff = Math.abs(comp1.getGreen(x,y)-comp2.getGreen(x,y));
                    double dist = Math.sqrt(redDiff*redDiff + blueDiff*blueDiff + greenDiff*greenDiff);
                    if (!(dist<=threshold && backgrounds.contains(comp1.getColor(x,y)) 
                    		&& backgrounds.contains(comp2.getColor(x,y)))){
                    	totalPixels+=1;
                    	if (dist>threshold){
                            diffcount+=1;
                            if (changes){
                                changesImg.setRGB(x,y,0,255,0);
                            }
                        } else {
                            if (changes){
                                changesImg.setRGB(x,y,255,255,255);
                            }
                        }
                    }
            	}
            }
        }
        if (changes && diffcount!=0){
        	try {                    
                    if (comp1.getWidth()*comp1.getHeight()>10000){
                        changesImg.write(output + "/bigcomponents/"+file+"_"+String.valueOf(numChange)+"_changes.png");
                    }
                    else{
                        changesImg.write(output + "/smallcomponents/"+file+"_"+String.valueOf(numChange)+"_changes.png");
                    }
        	} catch (Exception e){
        		e.printStackTrace();
        	}
        }
        //double percent = 100*(1.0*(totalPixels-diffcount))/(1.0*totalPixels);
        return new Point(diffcount, totalPixels);
    }
    
    /**
     * Compares two images: makes components for each, if scale is set to true scales the components,
     * pairs up components using greedy algorithm, counts different pixels and total pixels for each pair 
     * of matched components, adds the pixels of the unmatched components, writes components and difference images
     * @return similarity percentage
     * @throws IOException
     */
    public double compareImages() throws IOException{
        double startTime = System.currentTimeMillis();
        makeComps();
        
        if (components1.size()==0 && components2.size()==0){
            return 100.0;
        } else if (components1.size()==0 || components2.size()==0){
            return 0.0;
        }
        
        int totdiffcount = 0;
        int tottotalPixels = 0;
        
        if (scale){
	        for (int i=0;i<components1.size();i++){
	        	PngStuff comp = components1.get(i);
	        	if (comp.getWidth()*comp.getHeight()>30000){
	        		comp.scale(800,400);
	        	} else {
	        		comp.scale(150,50);
	        	}
	        	//comp.("components/comp1_"+String.valueOf(i)+".png");
	        }
	        for (int i=0;i<components2.size();i++){
	        	PngStuff comp = components2.get(i);
	        	if (comp.getWidth()*comp.getHeight()>30000){
	        		comp.scale(800,400);
	        	} else {
	        		comp.scale(150,50);
	        	}
	        	//comp.write("components/comp2_"+String.valueOf(i)+".png");
	        }
        }
        
        ArrayList<PngStuff> comps1 = null;
        ArrayList<PngStuff> comps2 = null;
        if (components1.size()<components2.size()){
        	comps1 = components1;
        	comps2 = components2;
        } else {
        	comps1 = components2;
        	comps2 = components1;
        }
      //do greedy algorithm to choose matches
        while (comps1.size()>0){
        	PngStuff comp1 = comps1.get(0);
        	int diffcount = 0;
        	int totalPixels = 0;
        	double maxperc = 0;
        	PngStuff best = null;
        	//TODO: what if best stays null?
        	for (PngStuff comp2: comps2){
        		double sizeRatio = comp1.getHeight()*comp1.getWidth()/(1.0*comp2.getHeight()*comp2.getWidth());
        		if (!equalSizes || (sizeRatio>0.9 && sizeRatio<1.1)){
	        		Point info = compareComp(comp1,comp2,false);
	        		double percent = 100*(1.0*(info.y-info.x))/(1.0*info.y);
	        		if (percent>=maxperc){
	        			diffcount = info.x;
	        			totalPixels = info.y;
	        			maxperc = percent;
	        			best = comp2;
	        		}
        		}
        	}
        	
        	totdiffcount += diffcount;
        	tottotalPixels += totalPixels;
                if (best!=null){
                    compareComp(comp1,best,true);
                }
        	if (maxperc<100){
                    if (best==null){
                        if (comp1.getWidth()*comp1.getHeight()>10000){
                            comp1.write(output + "/bigcomponents/"+file+"_"+String.valueOf(numChange)+"_1.png");
                        }
                        else{
                            comp1.write(output + "/smallcomponents/"+file+"_"+String.valueOf(numChange)+"_1.png");
                        }
                            System.out.println("could not find match for component"+file+"_"+String.valueOf(numChange)+"_1");
                            numChange+=1;
                    } else {
                        if (comp1.getWidth()*comp1.getHeight()>10000){
                            comp1.write(output + "/bigcomponents/"+file+"_"+String.valueOf(numChange)+"_1.png");
                            best.write(output + "/bigcomponents/"+file+"_"+String.valueOf(numChange)+"_2.png");
                        }
                        else{
                            comp1.write(output + "/smallcomponents/"+file+"_"+String.valueOf(numChange)+"_1.png");
                            best.write(output + "/smallcomponents/"+file+"_"+String.valueOf(numChange)+"_2.png");
                        }
                            numChange+=1;
                    }
        	}
        	comps2.remove(best);
        	comps1.remove(0);
        	
        	
        }
        for (PngStuff comp: comps2){
        	for (int x=0;x<comp.getWidth();x++){
        		for (int y=0;y<comp.getHeight();y++){
        			if (!comp.isWhite(x, y)){
        				totdiffcount +=1;
        				tottotalPixels+=1;
        			}
        		}
        	}
        }
        
        System.out.println("Difference count: " + totdiffcount);
        System.out.println("Total number of pixels: " + tottotalPixels);
        
        double percent = 100*(1.0*(tottotalPixels-totdiffcount))/(1.0*tottotalPixels);
        System.out.println("Percent same: " + percent);
        double endTime = System.currentTimeMillis();
        System.out.println("Time to calculate: " + (endTime-startTime)/1000.0);
        
        //Create log file
        FileWriter logFile = new FileWriter(output + "/bigcomponents/" + file + ".log");
        BufferedWriter out = new BufferedWriter(logFile);
        out.write("Difference count: " + totdiffcount);
        out.newLine();
        out.write("Total number of pixels: " + tottotalPixels);
        out.newLine();
        out.write("Percent same: " + percent);
        out.newLine();
        out.write("Time to calculate: " + (endTime-startTime)/1000.0);
        out.close();
        return percent;
    }
    
    
    /**
     * creates directory if it does not exist
     * @param dirStr -- directory path
     * @return dirStr
     */
    private String createDirIfNotExist(String dirStr){
        File dir=new File(dirStr);
        if(!dir.exists())
            dir.mkdir();
        return dirStr;
    }

}
