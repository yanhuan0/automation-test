package com.qa.framework;

public class SikuliWrapper extends ParameterConstants implements Sikuli{
    
    public SikuliWrapper() {
//        loadSikuli();
//        Settings.MinSimilarity=0.5;
    }

	@Override
	public String getImageRootDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setImageRootDir(String dir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDOR(DOR dor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLogger(Logger logger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canFind(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSikuliObject(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSikuliObject(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sikuliDragDrop(String from_key, int x_offset, int y_offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sikuliDragDrop(String from_key, int from_offsetX,
			int from_offsetY, String to_key, int to_offsetX, int to_offsetY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean waitForSikuliObject(String key, int maxWaitMS) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean waitForSikuliObject(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean waitForSikuliObjectGone(String key, int maxWaitMS) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean waitForSikuliObjectGone(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int sikuliGetX(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sikuliGetY(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void captureBitmap(int x, int y, int width, int height,
			String filePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMove(String key, int offsetX, int offsetY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMove(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDoubleClick(String key, int offsetX, int offsetY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sikuliDoubleClick(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseRightClick(String key, int offsetX, int offsetY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sikuliRightClick(String key) {
		// TODO Auto-generated method stub
		
	}

//    public String getImageRootDir(){
//        return imageRootDir;
//    }
//    public void setImageRootDir(String dir){
//    	imageRootDir=dir;
//    }
//    
//    public void setDOR(DOR dor){
//        m_dor=dor;
//    }
//    private Screen getScreen(){
//    	if(screen==null)
//            screen=new Screen();
//        return screen;
//    }
////    private static void loadSikuli(){
////        System.out.println("loadSikuli()");
////        try {
////            ClassLoader myClassLoader = ClassLoader.getSystemClassLoader(); 
////            //TODO: path?
////            String screenClassPath = "edu.mit.csail.uid.Screen";
////            String regionClassPath = "edu.mit.csail.uid.Region";
////            String FindFailedClassPath = "edu.mit.csail.uid.FindFailed";
////            String matchClassPath = "edu.mit.csail.uid.Match";
////            String locationClassPath = "edu.mit.csail.uid.Location";
////            String screenImageClassPath = "edu.mit.csail.uid.ScreenImage";
////            if(isWindows()){
////                screenClassPath = "org.sikuli.script.Screen";
////                regionClassPath = "org.sikuli.script.Region";
////                FindFailedClassPath = "org.sikuli.script.FindFailed";
////                matchClassPath = "org.sikuli.script.Match";
////                locationClassPath = "org.sikuli.script.Location";
////                screenImageClassPath = "org.sikuli.script.ScreenImage";
////            } 
////            screenClass = myClassLoader.loadClass(screenClassPath);
////            regionClass = myClassLoader.loadClass(regionClassPath);
////            FindFailedClass = myClassLoader.loadClass(FindFailedClassPath);
////            matchClass = myClassLoader.loadClass(matchClassPath);
////            locationClass = myClassLoader.loadClass(locationClassPath);
////            screenImageClass = myClassLoader.loadClass(screenImageClassPath);
////            screen = screenClass.newInstance();
////        } catch (Exception e){
////            //TODO: System.out.println? ok not to specify?
////            e.printStackTrace();
////            throw new RuntimeException(e);
////        }
////    }
//
//    private static boolean isWindows(){
//        String os = System.getProperty("os.name").toLowerCase();
//        return (os.indexOf( "win" ) >= 0);
//    }
//
//    private static void sleep(int milliSec) {
//        try {
//            Thread.sleep(milliSec);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//        
//    public boolean canFind(String key){
//        logger.debugMethod(key);
//        String locator=getSikuliLocator(key);
//        try{
//            getScreen().find(locator);
//            System.out.println("true");
//            return true;
//        } catch(Exception e){
//            System.out.println("false");
//            return false;
//        }
//    }
//    
//    public void setSikuliObject(String key){
//        setSikuliObject(key, null);
//    }
//    
//    private String getSikuliLocator(String key){ 
//        String oriLocator=m_dor.getLocator(key);
//        String locator=null;
//        if(oriLocator==null)
//            throw new RuntimeException("key ["+key+"] not found in DOR");
//        try{
////            locator =  BaseBISeleniumTest.getWritableDir()+File.separator+oriLocator.substring(1+oriLocator.indexOf("="));
////            if(!new File(locator).exists())
//            locator=getImageRootDir()+File.separator+oriLocator.substring(1+oriLocator.indexOf("="));
//            if(!new File(locator).exists())
//                throw new FileNotFoundException(locator);
//        }catch(Exception e){
//                throw new RuntimeException(e);
//        }
//        System.out.println("sikuli locator: "+locator);
//        return locator;
//    }
//    public void setSikuliObject(String key, String value){
//        logger.debugMethod(key, value);
//        try {
//            if (key == null && value != null){
//                getScreen().type(null, value, 0);
//            }else {
//                waitForSikuliObject(key);
//                String locator=getSikuliLocator(key);
//                int waitMS=WAIT_TIMEOUT_MS;
//                //Screen s = new Screen();
//                boolean found = false;
//                while(waitMS>0){
//                    if(canFind(key)){
//                        found = true;
//                        break;
//                    }
//                    sleep(1000);
//                    waitMS-=1000;
//                }
//                if(!found)
//                    throw new FrameworkException("Object not found: "+key);   
//                if (value == null){
//                    getScreen().click(locator, 0);
//                } else {
//                    getScreen().type(locator,value,0);
//                }
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//    
//    public void sikuliDragDrop(String from_key, int x_offset, int y_offset){
//        logger.debugMethod(from_key, x_offset, y_offset);
//        try {
//            waitForSikuliObject(from_key);
//            String from_locator=getSikuliLocator(from_key);
//            getScreen().wait(from_locator, 5);
//            Match fromMatch=getScreen().find(from_locator);
//            Location fromLoc=fromMatch.getTarget();
//            Location toLoc=new Location((int)fromLoc.getX()+x_offset, (int)fromLoc.getY()+y_offset);
//            getScreen().dragDrop(fromMatch, toLoc, 0);
//            sleep(500);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    
//    public void sikuliDragDrop(String from_key, int from_offsetX, int from_offsetY,
//        String to_key, int to_offsetX, int to_offsetY){
//        logger.debugMethod(from_key, from_offsetX, from_offsetY, to_key, to_offsetX, to_offsetY);
//        waitForSikuliObject(from_key);
//        waitForSikuliObject(to_key);
//        try{
//            String from_locator=getSikuliLocator(from_key);
//            getScreen().wait(from_locator, 5);
//            Match fromMatch=getScreen().find(from_locator);
//            Location fromLoc=fromMatch.getTarget();
//            fromLoc=new Location((int)fromLoc.getX()+from_offsetX, (int)fromLoc.getY()+from_offsetY);
//                                 
//            String to_locator=getSikuliLocator(to_key);
//            getScreen().wait(to_locator, 5);
//            Match toMatch=getScreen().find(to_locator);
//            Location toLoc=toMatch.getTarget();
//            toLoc=new Location((int)toLoc.getX()+to_offsetX, (int)toLoc.getY()+to_offsetY);            
//            getScreen().dragDrop(fromLoc, toLoc, 0);
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//        sleep(500);
//    }
//    
//    public boolean waitForSikuliObject(String key, int maxWaitMS){
//        logger.debugMethod(key, maxWaitMS);
//        int waitMS= maxWaitMS<0?WAIT_TIMEOUT_MS:maxWaitMS;
//        //String locator=m_dor.getLocator(key);
//        //locator = this.getTestDir()+"/"+locator.substring(1+locator.indexOf("="));
//        //Screen s = new Screen();
//        boolean found = false;
//        while(waitMS>0){
//            if(canFind(key)){
//                found = true;
//                break;
//            }
//            sleep(1000);
//            waitMS-=1000;
//        }
//        System.out.println(""+found);
//        return logger.debugReturn(found);
//    }
//    
//    public boolean waitForSikuliObject(String key) {
//        return waitForSikuliObject(key, WAIT_TIMEOUT_MS);
//    }
//    
//    public boolean waitForSikuliObjectGone(String key,int maxWaitMS){
//        logger.debugMethod(key, maxWaitMS);
//        int waitMS= maxWaitMS<0?WAIT_TIMEOUT_MS:maxWaitMS;
//        String locator=getSikuliLocator(key);
//        //Screen s = new Screen();
//        boolean gone = false;
//        while(waitMS>0){
//            if(!canFind(key)){
//                gone = true;
//                break;
//            }
//            sleep(1000);
//            waitMS-=1000;
//        }
//        return logger.debugReturn(gone);
//    }
//    
//    public boolean waitForSikuliObjectGone(String key) {
//        return waitForSikuliObjectGone(key, WAIT_TIMEOUT_MS);
//    }
//    
//    public int sikuliGetX(String key) {
//        waitForSikuliObject(key);
//        String locator=getSikuliLocator(key);
//        try{
//            return (int)getScreen().find(locator).getTarget().getX();
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//    }
//    
//    public int sikuliGetY(String key) {
//        waitForSikuliObject(key);
//        String locator=getSikuliLocator(key);
//        try{
//            return (int)getScreen().find(locator).getTarget().getY();
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//    }
//    
//    public void captureBitmap(int x, int y, int width, int height,
//                                     String filePath) {
//        logger.debugMethod(x, y, width, height, filePath);
//        PngStuff image = new PngStuff(getScreen().capture(x, y, width, height).getImage());
//        try{
//            image.write(filePath);
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//    }
//    
//    /**
//     * Move mouse to the specified object in FireFox window
//     * @param key key in DOR to identify object
//     * @param offsetX mouse will be moved to object top-left corner plus the offset
//     * @param offsetY mouse will be moved to object top-left corner plus the offset
//     * @throws FrameworkException
//     */
//    public void mouseMove(String key,int offsetX,int offsetY){
//        logger.debugMethod(key, offsetX, offsetY);
//        waitForSikuliObject(key);
//        String locator=getSikuliLocator(key);
//        try{
//            getScreen().wait(locator,5);
//            getScreen().hover(getScreen().find(locator).getTarget());
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//    }
//    /**
//     *This method is implemented by Sikuli
//     * @param key key in DOR to identify object. DOR entry needs to have sikulipath="..."
//     */
//    public void mouseMove(String key){
//        logger.debugMethod(key);
//        waitForSikuliObject(key);
//        String locator=getSikuliLocator(key);
//        try{
//            getScreen().wait(locator,5 );
//            getScreen().hover(locator);
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//    }
//    
//    public void mouseDoubleClick(String key,int offsetX,int offsetY){
//        logger.debugMethod(key, offsetX, offsetY);
//        sikuliDoubleClick(key);
//        throw new FrameworkException("not implemented yet");
//    }
//    /**
//     *This method is implemented by Sikuli
//     * @param key key in DOR to identify object. DOR entry needs to have sikulipath="..."
//     * @param offsetX doubleclick on object top-left corner plus the offset
//     * @param offsetY doubleclick on object top-left corner plus the offset
//     */
//    public void sikuliDoubleClick(String key){
//        logger.debugMethod(key);
//        waitForSikuliObject(key);
//        String locator=getSikuliLocator(key);
//        try{
//            getScreen().doubleClick(locator, 0);
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//    }
//    public void mouseRightClick(String key,int offsetX,int offsetY){
//        logger.debugMethod(key, offsetX, offsetY);
//        sikuliRightClick(key);
//    }
//    /**
//     *This method is implemented by Sikuli
//     * @param key key in DOR to identify object. DOR entry needs to have sikulipath="..."
//     * @param offsetX click on object top-left corner plus the offset
//     * @param offsetY click on object top-left corner plus the offset
//     */
//    public void sikuliRightClick(String key){
//        logger.debugMethod(key);
//        waitForSikuliObject(key);
//        String locator=getSikuliLocator(key);
//        try{
//            getScreen().rightClick(locator, 0);
//        }catch(Exception e){
//            throw new FrameworkException(e);
//        }
//    }
//
//	@Override
//	public void setLogger(Logger logger) {
//		this.logger=logger;
//	}
}
