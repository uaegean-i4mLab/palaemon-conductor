package gr.aegean.palaemon.conductor.utils;

import org.springframework.util.StringUtils;

public class EnvUtils {

    public static String getEnvVar(String name, String defaultValue){
        if(!StringUtils.  isEmpty(System.getenv(name)) ){
            return System.getenv(name);
        }else{
            return  defaultValue;
        }
    }
}
