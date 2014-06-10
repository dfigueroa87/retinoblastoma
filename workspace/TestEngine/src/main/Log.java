package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
	
	File logFile;
	FileWriter fileWriter;
	BufferedWriter bw;
	
	public Log(String path) {
		this.logFile = new File(path);
		try {
			if (!logFile.exists())
				logFile.createNewFile();
			this.fileWriter = new FileWriter(logFile.getAbsoluteFile());
			this.bw = new BufferedWriter(this.fileWriter);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	public void Write(String text) {
		try {
			bw.write(text);
			bw.write("\n");
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Close() {
		try {
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
