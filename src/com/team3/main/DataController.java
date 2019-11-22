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

public class DataController {

    GsonBuilder builder;
    Gson gson;
    private String data_path, id_path, run_path;
    private List<String> data, runs, b_id_string, a_id_string;
    private List<Integer> b_ids, a_ids, run_ids;
    private List<House> houses;
    private List<DataEntry> run_list;
    private final String VERSION = "v3";

    public DataController(String data_path, String id_path, String run_path){
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

        this.data_path = data_path;
        this.id_path = id_path;
        this.run_path = run_path;
        getData(); // Get saved data
    }

    private void getData() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(id_path), StandardCharsets.US_ASCII)) { // Try to read the house Id file
            String line = null;
            while ((line = reader.readLine()) != null) { // Read all lines and add the strings to the string-id arrays
                if (line.charAt(0) == 'A')
                    a_id_string.add(line);
                else
                    b_id_string.add(line);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(data_path), StandardCharsets.US_ASCII)) { // Try to read the house data file
            String line = null;
            while ((line = reader.readLine()) != null) { // Read all lines and add the strings to the string-data array and the House array
                data.add(line);
                houses.add(gson.fromJson(line.trim(), House.class));
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(run_path), StandardCharsets.US_ASCII)) { // Try tot read the runs path
            String line = null;
            while ((line = reader.readLine()) != null) { // Read all lines and add the strings to the string-runs array
                runs.add(line);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        for (String line : a_id_string) { // Add all integer Ids to the A array
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

        for (String line : b_id_string) { // Add all integer Ids to the B array
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

        for (int i = 0; i < run_list.size(); i++) { // Add all integer run Ids to the runs array
            String number = run_list.get(i).getRunId();
            try {
                Integer.valueOf(number);
                int id = Integer.parseInt(number);
                run_ids.add(id);
            } catch (NumberFormatException e) {
                System.out.println("House ID is incorrectly formatted");
            }
        }

        // Sort the ids by alphabetical order
        Collections.sort(a_ids);
        Collections.sort(b_ids);

        for (String line : runs) { // Populate the DataEntry runs array by de-serializing the string-runs array
            run_list.add(gson.fromJson(line.trim(), DataEntry.class));
        }

        // Sort the runs array using a custom comparator
        run_list.sort(new RunComparator());
    }

    public void saveData(String id, double random, double snake, double spiral, double wall_follow) {
        int latest_id;
        if (run_list.size() > 0){ // Get the latest run Id as long as there are previous runs
            String run_id = run_list.get(run_list.size() - 1).getRunId();
            String run_number = run_id.substring(run_id.indexOf('-')+1);

            try {
                Integer.valueOf(run_number);
                latest_id = Integer.parseInt(run_number) + 1;
            } catch (NumberFormatException e) {
                System.out.println("Run ID is incorrectly formatted.");
                latest_id = 99999;
            }
        } else { // If this is the first run, use 1
            latest_id = 1;
        }

        // Create a new DataEntry, add it to the string-runs and runs array
        DataEntry dataEntry = new DataEntry(VERSION+"-"+latest_id, id, random, snake, spiral, wall_follow);
        runs.add(gson.toJson(dataEntry));
        run_list.add(dataEntry);

        // Re-sort the array
        run_list.sort(new RunComparator());

        // Create a string output of the runs array
        String data_output = "";
        for (DataEntry run_data : run_list) {
            data_output += gson.toJson(run_data) + "\n";
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(run_path), StandardCharsets.US_ASCII)) { // Try to output the run data to the runs file
            writer.write(data_output, 0, data_output.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public String getHouseId (House house) {
        String floorPlan;
        int id;

        if (house.floorPlan == House.FloorPlan.A) { // Assign a new Id to the house
            if (a_ids.size() > 0) // If there are existing A Ids, use next integer
                id = a_ids.get(a_ids.size() - 1) + 1;
            else
                id = 0;
            floorPlan = "A";
        } else {
            if (b_ids.size() > 0) // If there are existing B Ids, use next integer
                id = b_ids.get(b_ids.size() - 1) + 1;
            else
                id = 0;
            floorPlan = "B";
        }

        String full_id = floorPlan + "-" + id;
        if (house.floorPlan == House.FloorPlan.A) { // Add the new Id to the respective arrays
            a_ids.add(id);
            a_id_string.add(full_id);
        } else {
            b_ids.add(id);
            b_id_string.add(full_id);
        }

        return full_id;
    }

    public void saveHouse(House house) {
        data.add(gson.toJson(house)); // Add the house to the string-house array
        
        String data_output = "";
        for (String house_data : data) { // Create string output of the array
            data_output += house_data + "\n";
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(data_path), StandardCharsets.US_ASCII)) { // Try to output to the house data file
            writer.write(data_output, 0, data_output.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        String id_output = "";
        for (int id : a_ids) { // Create string output of the A id array
            id_output += "A-" + id + "\n";
        }
        for (int id : b_ids) { // Create string output of the B id array
            id_output += "B-" + id + "\n";
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(id_path), StandardCharsets.US_ASCII)) { // Try to output to the id data file
            writer.write(id_output, 0, id_output.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public List<DataEntry> getRunData() {
        return run_list;
    }

    public List<String> getHouseData() { // Get a list of all house Ids
        List<String> house_ids = new ArrayList<String>();
        for (String id : a_id_string) {
            house_ids.add(id);
        }
        for (String id : b_id_string) {
            house_ids.add(id);
        }

        return house_ids;
    }

    public House loadHouse(String house_id) { // Get the associated house from the House array
        for (House house : houses) {

            if (house.id.equals(house_id)) {
                return house;
            }
        }

        return null;
    }

    private class ObstacleAdapter implements JsonSerializer<Obstacle>, JsonDeserializer<Obstacle>{ // De-/Serializer for the Obstacle class to handle abstract implementations

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

    private class RunComparator implements Comparator<DataEntry> { // Custom comparator to sort DataEntries by Id
        @Override
        public int compare(DataEntry a, DataEntry b) {
            int a_id = Integer.parseInt(a.getRunId().substring(a.getRunId().indexOf('-')+1));
            int b_id = Integer.parseInt(b.getRunId().substring(b.getRunId().indexOf('-')+1));
            return a_id < b_id ? -1 : a_id == b_id ? 0 : 1;
        }
    }
}
