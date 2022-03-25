package com.qa.framework;


public interface Sikuli {
    public String getImageRootDir();
    public void setImageRootDir(String dir);
    public void setDOR(DOR dor);
    public void setLogger(Logger logger);
    public boolean canFind(String key);
    public void setSikuliObject(String key);
    public void setSikuliObject(String key, String value);
    public void sikuliDragDrop(String from_key, int x_offset, int y_offset);
    public void sikuliDragDrop(String from_key, int from_offsetX, int from_offsetY,
        String to_key, int to_offsetX, int to_offsetY) ;
    public boolean waitForSikuliObject(String key, int maxWaitMS) ;
    public boolean waitForSikuliObject(String key) ;
    public boolean waitForSikuliObjectGone(String key,int maxWaitMS);
    public boolean waitForSikuliObjectGone(String key) ;
    public int sikuliGetX(String key) ;
    public int sikuliGetY(String key) ;
    public void captureBitmap(int x, int y, int width, int height,String filePath) ;
    public void mouseMove(String key,int offsetX,int offsetY);
    public void mouseMove(String key);
    public void mouseDoubleClick(String key,int offsetX,int offsetY);
    public void sikuliDoubleClick(String key);
    public void mouseRightClick(String key,int offsetX,int offsetY);
    public void sikuliRightClick(String key);
}
