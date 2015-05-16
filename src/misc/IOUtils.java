package misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IOUtils {

	public static Object readObjectFromFile(File file) throws IOException, ClassNotFoundException {
		Object object = null;

		try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file))) {
			object = oin.readObject();
		}

		return object;
	}

	public static Object readObjectFromFile(String file) throws IOException, ClassNotFoundException {
		return readObjectFromFile(new File(file));
	}

	public static void writeObjectToFile(File file, Object object) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(object);
		}
	}

	public static void writeObjectToFile(String file, Object object) throws IOException {
		writeObjectToFile(new File(file), object);
	}

}
