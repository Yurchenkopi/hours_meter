package ru.yurch;

import java.io.*;

public class FileOutput implements Output {

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    @Override
    public void print(Object obj) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(obj.toString()));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
            int sym = in.read();
            while (sym > 0) {
                out.write(sym);
                sym = in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
