import exceptions.DukeException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Save {
    private String filePath;

    public Save(String filePath) {
        this.filePath = filePath;
    }

    public String storeList(ArrayList<Task> listArr) {
        StringBuilder listItems = new StringBuilder();
        for (int j = 0; j < listArr.size(); j++) {
            if (j > 0) {
                listItems.append(System.getProperty("line.separator")); // Add newline before each task except the first one
            }
            String task = listArr.get(j).simpleToString();
            listItems.append(task);
        }
        return listItems.toString();
    }

    public void writeList(String ls) throws DukeException {
        Path path = Paths.get(this.filePath);
        try {
            if (!Files.exists(path)) {
                Files.createFile(path); // Create the file if it doesn't exist
            }
            Files.writeString(path, ls, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DukeException("Error writing to file: " + e.getMessage());
        }
    }

    public void printList() throws DukeException {
        BufferedReader br = null;
        Path path = Paths.get(this.filePath);
        try {

            if (!Files.exists(path)) {
                Files.createFile(path); // Create the file if it doesn't exist
            }
            File file = new File(this.filePath);

            br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }
        } catch (FileNotFoundException e) {
            throw new DukeException("File not found: " + this.filePath);
        } catch (IOException e) {
            throw new DukeException("Error reading file: " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                throw new DukeException("Error closing file reader: " + e.getMessage());
            }
        }
    }

    public ArrayList<Task> setList() throws DukeException {
        BufferedReader br = null;
        ArrayList<Task> taskArr = new ArrayList<>();
        try {
            File file = new File(this.filePath);
            if (!file.exists()) {
                return taskArr;
            }
            br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                String[] splitStr = s.split(" \\| ");
                Task task;
                switch(splitStr[0]) {
                    case "T":
                        task = new ToDo(splitStr[2]);
                        break;
                    case  "D":
                        LocalDateTime by = LocalDateTime.parse(splitStr[3], DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm"));
                        task = new Deadline(splitStr[2], by);
                        break;
                    case "E":
                        String[] toFrom = splitStr[3].split("-");
                        task = new Event(splitStr[2], toFrom[0], toFrom[1]);
                        break;
                    default:
                        throw new DukeException("Sorry! There was an error parsing the file.");
                }
                if (splitStr[1].equals("1")) {
                    task.silentSetDone();
                }
                taskArr.add(task);
            }
        } catch (FileNotFoundException e) {
            throw new DukeException("File not found: " + this.filePath);
        } catch (IOException e) {
            throw new DukeException("Error reading file: " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                throw new DukeException("Error closing file reader: " + e.getMessage());
            }
        }

        return taskArr;
    }
}
