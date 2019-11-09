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
import java.util.Comparator;
import java.util.List;

import com.google.gson.*;
import com.team3.main.entities.DataEntry;
import com.team3.main.entities.House;
import com.team3.main.entities.Obstacle;

import javax.xml.crypto.Data;

public class DataController {

    GsonBuilder builder;
    Gson gson, gson_pretty;
    private String data_path, id_path, data_pretty_path, run_path;
    private List<String> data, runs, b_id_string, a_id_string;
    private List<Integer> b_ids, a_ids, run_ids;
    private List<House> houses;
    private List<DataEntry> run_list;
    private final String VERSION = "v1";

    public DataController(String data_path, String id_path, String data_pretty_path, String run_path){
        data = new ArrayList<String>();
        a_id_string = new ArrayList<String>();
        b_id_string = new ArrayList<String>();
        b_ids = new ArrayList<Integer>();
        a_ids = new ArrayList<Integer>();
        run_ids = new ArrayList<Integer>();
        houses = new ArrayList<House>();
        runs = new ArrayList<String>();
        run_list = new ArrayList<DataEntry>();

        builder = new GsonBuilder();
        builder.registerTypeAdapter(Obstacle.class, new ObstacleAdapter());

        gson = builder.create();
        gson_pretty = builder.setPrettyPrinting().create();

        this.data_path = data_path;
        this.id_path = id_path;
        this.data_pretty_path = data_pretty_path;
        this.run_path = run_path;
        getData();
    }

    private void getData() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(id_path), StandardCharsets.US_ASCII)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) == 'A')
                    a_id_string.add(line);
                else
                    b_id_string.add(line);
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

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(run_path), StandardCharsets.US_ASCII)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                runs.add(line);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        for (String line : a_id_string) {
            int id;
            String number = line.substring(2);
            try {
                Integer.valueOf(number);
                id = Integer.parseInt(number);
            } catch (NumberFormatException e) {
                System.out.println("House ID is incorrectly formatted");
                id = 99999;
            }

            a_ids.add(id);
        }

        for (String line : b_id_string) {
            int id;
            String number = line.substring(2);
            try {
                Integer.valueOf(number);
                id = Integer.parseInt(number);
            } catch (NumberFormatException e) {
                System.out.println("House ID is incorrectly formatted");
                id = 99999;
            }

            b_ids.add(id);
        }

        for (int i = 0; i < run_list.size(); i++) {
            String number = run_list.get(i).getRunId();
            try {
                Integer.valueOf(number);
                int id = Integer.parseInt(number);
                run_ids.add(id);
            } catch (NumberFormatException e) {
                System.out.println("House ID is incorrectly formatted");
            }
        }

        Collections.sort(a_ids);
        Collections.sort(b_ids);

        for (String line : runs) {
            run_list.add(gson.fromJson(line.trim(), DataEntry.class));
        }

        run_list.sort(new RunComparator());
    }

    public void saveData(String id, double random, double snake, double spiral, double wall_follow) {
        int latest_id;
        if (run_list.size() > 0){
            String run_id = run_list.get(run_list.size() - 1).getRunId();
            String run_number = run_id.substring(run_id.indexOf('-')+1);

            try {
                Integer.valueOf(run_number);
                latest_id = Integer.parseInt(run_number) + 1;
            } catch (NumberFormatException e) {
                System.out.println("Run ID is incorrectly formatted.");
                latest_id = 99999;
            }
        } else {
            latest_id = 1;
        }

        DataEntry dataEntry = new DataEntry(VERSION+"-"+latest_id, id, random, snake, spiral, wall_follow);
        runs.add(gson.toJson(dataEntry));
        run_list.add(dataEntry);

        run_list.sort(new RunComparator());

        String data_output = "";
        for (DataEntry run_data : run_list) {
            data_output += gson.toJson(run_data) + "\n";
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(run_path), StandardCharsets.US_ASCII)) {
            writer.write(data_output, 0, data_output.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public String getHouseId (House house) {
        String floorPlan;
        int id;

        if (house.floorPlan == House.FloorPlan.A) {
            if (a_ids.size() > 0)
                id = a_ids.get(a_ids.size() - 1) + 1;
            else
                id = 1;
            floorPlan = "A";
        } else {
            if (b_ids.size() > 0)
                id = b_ids.get(b_ids.size() - 1) + 1;
            else
                id = 1;
            floorPlan = "B";
        }

        String full_id = floorPlan + "-" + id;
        if (house.floorPlan == House.FloorPlan.A) {
            a_ids.add(id);
            a_id_string.add(full_id);
        } else {
            b_ids.add(id);
            b_id_string.add(full_id);
        }

        return full_id;
    }

    public void saveHouse(House house) {
        data.add(gson.toJson(house));
        
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
        for (int id : a_ids) {
            id_output += "A-" + id + "\n";
        }
        for (int id : b_ids) {
            id_output += "B-" + id + "\n";
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(id_path), StandardCharsets.US_ASCII)) {
            writer.write(id_output, 0, id_output.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public List<DataEntry> getRunData() {
        return run_list;
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

    private class RunComparator implements Comparator<DataEntry> {
        @Override
        public int compare(DataEntry a, DataEntry b) {
            int a_id = Integer.parseInt(a.getRunId().substring(a.getRunId().indexOf('-')+1));
            int b_id = Integer.parseInt(b.getRunId().substring(b.getRunId().indexOf('-')+1));
            return a_id < b_id ? -1 : a_id == b_id ? 0 : 1;
        }
    }
}
