package com.treebee.blog.tools;

import java.io.*;

public class Readhtml {
    public static String readfile(String filePath){
        File file = new File(filePath);
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder str = new StringBuilder("");
        try {
            InputStreamReader isr = new InputStreamReader(input,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                str.append(line + "\n");
                if(line.contains("</html>")){
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return str.toString();
    }
}
