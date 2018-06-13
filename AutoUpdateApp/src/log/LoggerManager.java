package log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.logging.Level;  
import java.util.logging.Logger;  
    
public class LoggerManager {    
	
    private static Map<Class<?>,Logger> loggerCache = new HashMap<Class<?>,Logger>();  
    // 初始化LogManager    
    static {
        // 读取配置文件    
    	InputStream inputStream = null;
    	try {
			inputStream = new FileInputStream("./bin/log.properties");
			java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager(); 
	    	logManager.readConfiguration(inputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
//        ClassLoader cl = LoggerManager.class.getClassLoader();    
//        InputStream inputStream = null;    
//        if (cl != null) {    
//            inputStream = cl.getResourceAsStream("log.properties");    
//        } else {    
//            inputStream = ClassLoader.getSystemResourceAsStream("log.properties");    
//        }    
//        java.util.logging.LogManager logManager = java.util.logging.LogManager    
//                .getLogManager();    
//        if (inputStream == null){  
//            System.err.println("LoggerManager: Log configuration NOT found!");  
//      
//        }else try {    
//            // 重新初始化日志属性并重新读取日志配置。    
//            logManager.readConfiguration(inputStream);    
//            System.out.println("LoggerManager: Log configuration loaded.");  
//              
//                   } catch (Exception e) {    
//            System.err.println(e);    
//        }  finally{  
//            try {inputStream.close();}  
//            catch(Exception ex){  
//                ex.printStackTrace();  
//            }  
//        }  
    }    
    
    public static Logger getLogger(Class<?> clazz) {    
          
        Logger logger = loggerCache.get(clazz);  
        if (logger==null){  
            logger = Logger.getLogger(clazz.getCanonicalName());  
            loggerCache.put(clazz, logger);   
        }  
        return logger;    
    }    

    public static Level getEffectiveLevel(Logger logger)
    {  
        Level level = null;  
        Logger parent = logger;  
        while (level==null){  
            if (parent==null)  
                break;  
            level = parent.getLevel();  
            parent = parent.getParent();  
        }  
        return level;  
    }  
    
}    