package com.json.project;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.jsonschema2pojo.SchemaMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.sun.codemodel.JCodeModel;

public final class JsonSchemaGenerator {

    private static final Logger LOGGER = Logger.getLogger(JsonSchemaGenerator.class.getName());
    private static ObjectMapper objectMapper = new ObjectMapper();
   
    public static String outputAsString(String title, String description,
                                        String json) throws IOException {
        return outputAsString(title, description, json, null);
    }

    @SuppressWarnings("deprecation")
	public static void outputAsFile(String title, String description,
                                    String json, String filename) throws IOException {
        FileUtils.writeStringToFile(
                new File(filename),
                outputAsString(title, description, json));
    }

    public static void outputAsPOJO(String title, String description,
                                    String json, String packageName,
                                    String outputDirectory) throws IOException {
        String schema = JsonSchemaGenerator.outputAsString(title, title, json);
        LOGGER.info("Generating POJO(s) ...");

        File fDirectory = new File(outputDirectory);
        if (!fDirectory.exists()) FileUtils.forceMkdir(fDirectory);

        JCodeModel codeModel = new JCodeModel();
        SchemaMapper mapper = new SchemaMapper();
        mapper.generate(codeModel, title, packageName, schema);
        codeModel.build(fDirectory);
        LOGGER.info("DONE.");
    }

    private static String outputAsString(String title, String description,
                                         String json, JsonNodeType type) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(json);
        StringBuilder output = new StringBuilder();
        output.append("{");

        if (type == null) output.append(
                "\"title\": \"" +
                        title + "\", \"description\": \"" +
                        description + "\", \"type\": \"object\", \"properties\": {");

        for (Iterator<String> iterator = jsonNode.fieldNames(); iterator.hasNext();) {
            String fieldName = iterator.next();
            LOGGER.info("processing " + fieldName + "...");

            JsonNodeType nodeType = jsonNode.get(fieldName).getNodeType();

            output.append(convertNodeToStringSchemaNode(jsonNode, nodeType, fieldName));
        }

     if (type == null) output.append("}");

        output.append("}");

        LOGGER.info("generated schema = " + output.toString());
        return output.toString();
    }

    private static String convertNodeToStringSchemaNode(
            JsonNode jsonNode, JsonNodeType nodeType, String key) throws IOException {
        StringBuilder result = new StringBuilder("\"" + key + "\": { \"type\": \"");

        LOGGER.info(key + " node type " + nodeType + " with value " + jsonNode.get(key));
        JsonNode node = null;
        switch (nodeType) {
            case ARRAY :
            
                node = jsonNode.get(key);
                LOGGER.info(key + " is an array with value of " + node.toString());
                result.append("array\", \"items\": { \"type\":");
                result.append(outputAsString(null, null, node.toString(), JsonNodeType.ARRAY));          
                result.append("}}");
                break;
            case BOOLEAN:
                result.append("boolean\" },");
                break;
            case NUMBER:
                result.append("number\" },");
                break;
            case OBJECT:
                node = jsonNode.get(key);
                result.append("object\", \"properties\": ");
                result.append(outputAsString(null, null, node.toString(), JsonNodeType.OBJECT));
                result.append("}");
                break;
            case STRING:
                result.append("string\" },");
                break;
		default:
			break;
        }
        

        return result.toString();
    }

 
}