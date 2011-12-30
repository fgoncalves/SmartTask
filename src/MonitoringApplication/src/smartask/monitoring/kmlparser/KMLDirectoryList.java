package smartask.monitoring.kmlparser;

import java.io.File;
import java.io.FilenameFilter;

import android.util.Log;

/**
 * This class is used to list all kml files in a given directory. The search
 * pattern is [0-9]{4,}\\.kml, which means that all files beginning with 4 or
 * more digits and ending in .kml are listed. The idea is to have files to each
 * emulator, where the emulator bound to port 5554 will receive the coordinates
 * in file 5554.kml
 * 
 * @author Grupo 1
 */
public class KMLDirectoryList {
	private final static String KML_REGEX = "[0-9]{4,}\\.kml";
	private String[] files;

	/**
	 * @param directoryName
	 *            the directory name to list.
	 */
	public KMLDirectoryList(String directoryName) {
		File dir = new File(directoryName);
		files = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.matches(KML_REGEX))
					return true;
				return false;
			}
		});

		if (files != null) {
			Log.d("KMLDirectoryList", "Parsing kml files:");
			for (String file : files) {
				Log.d("KMLDirectoryList", "\t" + file);
			}
		}
	}

	/**
	 * Method to obtain the listed files.
	 * 
	 * @return An array of strings, where each position represents a kml file
	 *         name.
	 */
	public String[] getFiles() {
		if (files == null) {
			Log.w("KMLDirectoryList",
					"Didn't find any kml file in supplied directory."
							+ " Did you forget to push them to the emulator?");
			return new String[] {};
		}
		return files;
	}

}
