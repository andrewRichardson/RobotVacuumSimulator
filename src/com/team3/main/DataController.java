package com.team3.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.google.gson.*;
import com.team3.main.entities.DataEntry;
import com.team3.main.entities.House;
import com.team3.main.entities.Obstacle;

public class DataController {

    GsonBuilder builder;
    Gson gson;
    private final String data_path;
    private final String id_path;
    private final String run_path;
    private final List<String> data;
    private final List<String> runs;
    private final List<String> b_id_string;
    private final List<String> a_id_string;
    private final List<Integer> b_ids;
    private final List<Integer> a_ids;
    private final List<House> houses;
    private final List<DataEntry> run_list;

    public DataController(String data_path, String id_path, String run_path){
        data = new ArrayList<>();
        a_id_string = new ArrayList<>();
        b_id_string = new ArrayList<>();
        b_ids = new ArrayList<>();
        a_ids = new ArrayList<>();
        houses = new ArrayList<>();
        runs = new ArrayList<>();
        run_list = new ArrayList<>();

        builder = new GsonBuilder();
        builder.registerTypeAdapter(Obstacle.class, new ObstacleAdapter());

        gson = builder.create();

        this.data_path = data_path;
        this.id_path = id_path;
        this.run_path = run_path;
        getData(); // Get saved data
    }

    private void getData() {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(Objects.requireNonNull(getClass().getResource(id_path)).toURI()), StandardCharsets.US_ASCII)) { // Try to read the house ID file
            String line;
            while ((line = reader.readLine()) != null) { // Read all lines and add the strings to the string-id arrays
                if (line.charAt(0) == 'A')
                    a_id_string.add(line);
                else
                    b_id_string.add(line);
            }
        } catch (IOException x) {
            System.err.format("IDs IOException: %s%n", x);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = Files.newBufferedReader(Path.of(Objects.requireNonNull(getClass().getResource(data_path)).toURI()), StandardCharsets.US_ASCII)) { // Try to read the house data file
            String line;
            while ((line = reader.readLine()) != null) { // Read all lines and add the strings to the string-data array and the House array
                data.add(line);
                houses.add(gson.fromJson(line.trim(), House.class));
            }
        } catch (IOException x) {
            System.err.format("House data IOException: %s%n", x);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = Files.newBufferedReader(Path.of(Objects.requireNonNull(getClass().getResource(run_path)).toURI()), StandardCharsets.US_ASCII)) { // Try to read the runs file
            String line;
            while ((line = reader.readLine()) != null) { // Read all lines and add the strings to the string-runs array
                runs.add(line);
            }
        } catch (IOException x) {
            System.err.format("Runs IOException: %s%n", x);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        readHouseData(a_id_string, a_ids);

        readHouseData(b_id_string, b_ids);

        // Sort the ids by alphabetical order
        Collections.sort(a_ids);
        Collections.sort(b_ids);

        for (String line : runs) { // Populate the DataEntry runs array by de-serializing the string-runs array
            run_list.add(gson.fromJson(line.trim(), DataEntry.class));
        }

        // Sort the runs array using a custom comparator
        run_list.sort(new RunComparator());
    }

    private void readHouseData(List<String> aIdString, List<Integer> aIds) {
        for (String line : aIdString) { // Add all integer Ids to the A array
            int id;
            String number = line.substring(2);
            try {
                Integer.valueOf(number);
                id = Integer.parseInt(number);
            } catch (NumberFormatException e) {
                System.out.println("House ID is incorrectly formatted");
                id = 99999;
            }

            aIds.add(id);
        }
    }

    public void saveData(String id, double random, double snake, double spiral, double wall_follow) {
        int latest_id;
        if (!run_list.isEmpty()){ // Get the latest run ID as long as there are previous runs
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
        String VERSION = "v3";
        DataEntry dataEntry = new DataEntry(VERSION +"-"+latest_id, id, random, snake, spiral, wall_follow);
        runs.add(gson.toJson(dataEntry));
        run_list.add(dataEntry);

        // Re-sort the array
        run_list.sort(new RunComparator());

        // Create a string output of the runs array
        StringBuilder data_output = new StringBuilder();
        for (DataEntry run_data : run_list) {
            data_output.append(gson.toJson(run_data)).append("\n");
        }

        saveData(data_output, run_path);
    }

    public String getHouseId (House house) {
        String floorPlan;
        int id;

        if (house.floorPlan.equals("A")) { // Assign a new ID to the house
            if (!a_ids.isEmpty()) // If there are existing A Ids, use next integer
                id = a_ids.get(a_ids.size() - 1) + 1;
            else
                id = 0;
            floorPlan = "A";
        } else {
            if (!b_ids.isEmpty()) // If there are existing B Ids, use next integer
                id = b_ids.get(b_ids.size() - 1) + 1;
            else
                id = 0;
            floorPlan = "B";
        }

        String full_id = floorPlan + "-" + id;
        if (house.floorPlan.equals("A")) { // Add the new ID to the respective arrays
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
        
        StringBuilder data_output = new StringBuilder();
        for (String house_data : data) { // Create string output of the array
            data_output.append(house_data).append("\n");
        }

        saveData(data_output, data_path);

        StringBuilder id_output = new StringBuilder();
        for (int id : a_ids) { // Create string output of the A id array
            id_output.append("A-").append(id).append("\n");
        }
        for (int id : b_ids) { // Create string output of the B id array
            id_output.append("B-").append(id).append("\n");
        }

        saveData(id_output, id_path);
    }

    private void saveData(StringBuilder id_output, String idPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(Objects.requireNonNull(getClass().getResource(idPath)).toURI()), StandardCharsets.US_ASCII)) { // Try to output to the id data file
            writer.write(id_output.toString(), 0, id_output.length());
        } catch (IOException x) {
            System.err.format("Runs IOException: %s%n", x);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DataEntry> getRunData() {
        return run_list;
    }

    public List<String> getHouseData() { // Get a list of all house Ids
        List<String> house_ids = new ArrayList<>();
        house_ids.addAll(a_id_string);
        house_ids.addAll(b_id_string);

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

    private static class ObstacleAdapter implements JsonSerializer<Obstacle>, JsonDeserializer<Obstacle>{ // De-/Serializer for the Obstacle class to handle abstract implementations

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

            Class<?> klass;
            try {
                klass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.out.println("Error:" + Arrays.toString(e.getStackTrace()));
                throw new JsonParseException(e.getMessage());
            }
            return context.deserialize(jsonObject.get(INSTANCE), klass);
        }
    }

    private static class RunComparator implements Comparator<DataEntry> { // Custom comparator to sort DataEntries by ID
        @Override
        public int compare(DataEntry a, DataEntry b) {
            int a_id = Integer.parseInt(a.getRunId().substring(a.getRunId().indexOf('-')+1));
            int b_id = Integer.parseInt(b.getRunId().substring(b.getRunId().indexOf('-')+1));
            return Integer.compare(a_id, b_id);
        }
    }
}
