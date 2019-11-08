package com.team3.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.team3.main.entities.House;
import com.team3.main.entities.Obstacle;

public class DataController {

    GsonBuilder builder;
    Gson gson, gson_pretty;
    private String data_path, id_path, data_pretty_path;
    private List<String> data, b_ids, a_ids;
    private List<House> houses;

    public DataController(String data_path, String id_path, String data_pretty_path){
        data = new ArrayList<String>();
        b_ids = new ArrayList<String>();
        a_ids = new ArrayList<String>();
        houses = new ArrayList<House>();

        builder = new GsonBuilder();
        builder.registerTypeAdapter(Obstacle.class, new ObstacleAdapter());

        gson = builder.create();
        gson_pretty = builder.setPrettyPrinting().create();

        this.data_path = data_path;
        this.id_path = id_path;
        this.data_pretty_path = data_pretty_path;
        getData();
    }

    private void getData() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(id_path), StandardCharsets.US_ASCII)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) == 'A')
                    a_ids.add(line);
                else
                    b_ids.add(line);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(data_path), StandardCharsets.US_ASCII)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                data.add(line);
                houses.add(gson.fromJson(line.trim(), House.class));
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        Collections.sort(a_ids);
        Collections.sort(b_ids);
    }

    public String getHouseId (House house) {
        String floorPlan;
        String id;

        if (house.floorPlan == House.FloorPlan.A) {
            id = a_ids.get(a_ids.size() - 1).substring(2);
            floorPlan = "A";
        } else {
            id = b_ids.get(b_ids.size() - 1).substring(2);
            floorPlan = "B";
        }

        int latest_id;
        try {
            Integer.valueOf(id);
            latest_id = Integer.parseInt(id) + 1;
        } catch (NumberFormatException e) {
            System.out.println("Error");
            latest_id = 99999;
        }

        String full_id = floorPlan + "-" + latest_id;
        if (house.floorPlan == House.FloorPlan.A) {
            a_ids.add(full_id);
        } else {
            b_ids.add(full_id);
        }

        return full_id;
    }

    public void saveHouse( House house) {
        data.add(gson.toJson(house));
        String output = data.get(data.size() - 1);
        String data_output = "";
        for (String house_data : data) {
            data_output += house_data + "\n";
        }

        String output_pretty = "[\n";

        for (House object : houses) {
            output_pretty += gson_pretty.toJson(object) + ",\n";
        }

        output_pretty = output_pretty.substring(0, output_pretty.length() - 2);
        output_pretty += "\n]";

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(data_path), StandardCharsets.US_ASCII)) {
            writer.write(data_output, 0, data_output.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(data_pretty_path), StandardCharsets.US_ASCII)) {
            writer.write(output_pretty, 0, output_pretty.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        String id_output = "";
        for (String id : a_ids) {
            id_output += id + "\n";
        }
        for (String id : b_ids) {
            id_output += id + "\n";
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(id_path), StandardCharsets.US_ASCII)) {
            writer.write(id_output, 0, id_output.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    private class ObstacleAdapter implements JsonSerializer<Obstacle>, JsonDeserializer<Obstacle>{

        private static final String CLASSNAME = "CLASSNAME";
        private static final String INSTANCE  = "INSTANCE";

        @Override
        public JsonElement serialize(Obstacle src, Type typeOfSrc,
                                     JsonSerializationContext context) {

            JsonObject retValue = new JsonObject();
            String className = src.getClass().getName();
            retValue.addProperty(CLASSNAME, className);
            JsonElement elem = context.serialize(src);
            retValue.add(INSTANCE, elem);
            return retValue;
        }

        @Override
        public Obstacle deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException  {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
            String className = prim.getAsString();

            Class<?> klass = null;
            try {
                klass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }
            return context.deserialize(jsonObject.get(INSTANCE), klass);
        }
    }
}
