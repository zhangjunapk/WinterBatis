package org.zj.winterbatis.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @BelongsProject: WinterBatis
 * @BelongsPackage: org.zj.winterbatis.util
 * @Author: Java
 * @CreateTime: 2018-09-21 22:22
 * @Description: ${Description}
 */
public class BaseClassLoader extends ClassLoader{
    /**
     * 通过class文件来获得class
     * @param className
     * @param file
     * @return
     * @throws IOException
     */
    public Class getClass(String className,File file) throws IOException, ClassNotFoundException {
        if(file.isDirectory())
            return null;
        byte[] bytes=getByte(file);
        Class c;
        try {
            c=this.defineClass(className, bytes, 0, bytes.length);
        }
        catch (Exception e){
            c=Class.forName(className);
        }
        return c;
    }

    private byte[] getByte(File file) throws IOException {
        FileInputStream fis=new FileInputStream(file);
        FileChannel channel = fis.getChannel();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        WritableByteChannel wbc= Channels.newChannel(baos);
        ByteBuffer bf=ByteBuffer.allocate(1024);
        while(true){
            int i=channel.read(bf);
            if(i==0||i==-1)
                break;
            bf.flip();
            wbc.write(bf);
            bf.clear();
        }
        fis.close();
        return baos.toByteArray();
    }

}
