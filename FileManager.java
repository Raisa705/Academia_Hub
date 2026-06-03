import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FILE_NAME = "users.txt";

    // Save new user
    public static void saveUser(User user) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true));
        bw.write(user.getUsername() + "," + user.getPassword());
        bw.newLine();
        bw.close();
    }

    // Load all users
    public static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) file.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if(parts.length == 2) {
                users.add(new User(parts[0], parts[1]));
            }
        }
        br.close();
        return users;
    }

    // Check if username already exists
    public static boolean userExists(String username) throws IOException {
        for(User u : loadUsers()) {
            if(u.getUsername().equals(username)) return true;
        }
        return false;
    }
}
