package server.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Account {

	private static final int CRYPTION_ID = 85461564;

	private Account() {
	}

	public static void saveGame(Player player) {
		BufferedWriter file;
		try {
			File directory = new File("./server_data/account/" + player.getName() + "/");
			if (!directory.exists()) {
				directory.mkdir();
			}
			file = new BufferedWriter(new FileWriter("./server_data/account/" + player.getName() + "/session.txt"));
			String[][] characterData = { 
					{"USERNAME = ", player.getName()},
					{"X = ", "" + player.getX()},
					{"Y = ", "" + player.getY()},
					{"Z = ", "" + player.getZ()},
					{"GC = ", "" + player.getGC()},
			};
			for (int i = 0; i < characterData.length; i ++) {
				file.write(characterData[i][0], 0, characterData[i][0].length());
				file.write(characterData[i][1], 0, characterData[i][1].length());
				file.newLine();
			}
			for (int i = 0; i < player.getSkills().length; i ++) {
				file.write("SKILL[" + i + "] = ", 0, ("SKILL[" + i + "] = ").length());
				file.write("" + player.getSkills()[i], 0, ("" + player.getSkills()[i]).length());
				file.newLine();
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error saving profile " + player.getName());
		}
	}

	public static void loadGame(Player player) {
		BufferedReader file;
		try {
			file = new BufferedReader(new FileReader("./server_data/account/" + player.getName() + "/session.txt"));
			String line = file.readLine();
			String index;
			String data;

			float x = 120;
			float y = -10.7f;
			float z = -120;
			while (line != null) {
				data = line.substring(line.indexOf("= ") + 1).trim();
				index = line.substring(line.indexOf("[") + 1).replaceAll("] = " + data, "");
				if (line.startsWith("USERNAME")) {
				} else if (line.startsWith("X")) {
					x = Float.parseFloat(data);
				} else if (line.startsWith("Y")) {
					y = Float.parseFloat(data);
				} else if (line.startsWith("Z")) {
					z = Float.parseFloat(data);
				} else if (line.startsWith("GC")) {
					player.addGC(Integer.parseInt(data));
				} else if (line.startsWith("SKILL")) {
					player.getSkills()[Integer.parseInt(index)] = Integer.parseInt(data);
				}
				line = file.readLine();
			}
			player.setPosition(x, y, z);
			file.close();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Error loading profile " + player.getName());
		}
	}

//	String user = "admin"; // test accounts: admin, gmod, mod, user
//	int response = ForumIntegration.checkUser(user, user);
//	if (response == 2) {
//		System.out.println("Login success!");
//	} else {
//		System.out.println("Login request denied. Response code: " + response);
//	}
	
	public static int checkUser(String username, String password) {
		username = username.toLowerCase().replace(" ", "_");
		password = username.toLowerCase().replace(" ", "_");
		try {
			String urlString = "http://localhost/smf/smf_auth.php?crypt="+CRYPTION_ID+"&name="+username+"&pass="+password;
			HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
			BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			int returnCode = Integer.parseInt(read.readLine().trim());
			switch(returnCode) {
			case 0:
				System.out.println("User is not registered");
				return returnCode;
			case 1:
				System.out.println("Invalid password");
				return returnCode;
			case 3:
				System.out.println("User is admin");
				return 2;
			case 4:
				System.out.println("User is gmod");
				return 2;
			case 2:
				System.out.println("User is registered");
				return 2;
			}
			System.out.println("Error authorizing smf login!");
			return -1;
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Login server offline!");
			return -1;
		}
	}

}

