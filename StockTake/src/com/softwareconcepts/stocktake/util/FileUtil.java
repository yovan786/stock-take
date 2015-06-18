package com.softwareconcepts.stocktake.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

import com.softwareconcepts.stocktake.model.Batch;
import com.softwareconcepts.stocktake.model.BatchLine;

public class FileUtil {
	private static final String LOG_TAG = "FileUtil";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			Constant.COMPLETE_DATE_FORMAT, Locale.ENGLISH);

	public static boolean createCSVFiles(Batch batch, List<String> fileNames,
			boolean isRackVisible) {
		boolean isCSVCreated = false;
		String fileName = "";
		try {
			if (isRackVisible) {
				fileName = createCSVFile(batch, isRackVisible);
				fileNames.add(fileName);
			}
			fileName = createCSVFile(batch, false);
			fileNames.add(fileName);
			isCSVCreated = true;
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"An error has occured while creating the CSV files: " + 
							Utility.getStackTrace(e));
		}
		return isCSVCreated;
	}

	private static String createCSVFile(Batch batch, boolean isRackVisible)
			throws IOException {
		FileWriter fileWriter = null;
		String fileName = "";
		try {
			fileName = FileUtil.getOutputFolderPath() + File.separator + sdf.format(new Date());
		} catch (Exception e) {
			Log.e(LOG_TAG, "An error has occured to get the output folder: "
					+ e.getMessage());
		}
		int recordsIndex = 3;
		if (isRackVisible) {
			recordsIndex = 4;
			fileName += "_rack_";
		}
		fileName += Constant.CSV_EXTENSION;
		String[] record = new String[recordsIndex];
		fileWriter = new FileWriter(fileName);
		for (BatchLine batchLine : batch.getBatchLinesList()) {
			record[0] = String.valueOf(batchLine.getItem());
			record[1] = String.valueOf(batchLine.getQuantity());
			record[2] = batch.getWarehouse().getName();
			if (isRackVisible) {
				record[3] = batchLine.getRackNo();
			}
			fileWriter.append(String.valueOf(record[0]));
			fileWriter.append(Constant.CSV_SEPARATOR);
			fileWriter.append(String.valueOf(record[1]));
			fileWriter.append(Constant.CSV_SEPARATOR);
			fileWriter.append(String.valueOf(record[2]));
			fileWriter.append(Constant.CSV_SEPARATOR);
			if (isRackVisible) {
				fileWriter.append(String.valueOf(record[3]));
				fileWriter.append(Constant.CSV_SEPARATOR);
			}
			fileWriter.append(NEW_LINE_SEPARATOR);
		}
		if (fileWriter != null) {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}

	public static String getOutputFolderPath() throws Exception {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
				Constant.STORAGE_FOLDER);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e(Constant.STORAGE_FOLDER, "Oops! Failed create "
						+ Constant.STORAGE_FOLDER + " directory");
				return null;
			}
		}
		return mediaStorageDir.getAbsolutePath();
	}
}
