package com.json.project;

import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonSchemaConvertorEngine {

	public static void main(String[] args) {
		 String file = "src/main/resources/player.json";
	        String json = null;
			try {
				json = readFileAsString(file);
				JsonSchemaGenerator.outputAsFile("title", "description", json, "src/main/resources/filename.json");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println(json);

	        
	}
	 public static String readFileAsString(String file)throws Exception
	    {
	        return new String(Files.readAllBytes(Paths.get(file)));
	    }

}
