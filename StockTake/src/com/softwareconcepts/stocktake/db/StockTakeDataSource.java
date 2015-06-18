package com.softwareconcepts.stocktake.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.softwareconcepts.stocktake.model.Batch;
import com.softwareconcepts.stocktake.model.BatchLine;
import com.softwareconcepts.stocktake.model.Warehouse;

public class StockTakeDataSource {
	private static final String LOGTAG = "StockTakeDataSource";
	private SQLiteOpenHelper dbHelper;
	private SQLiteDatabase database;

	private static final String[] allBatchColumns = {
			StockTakeOpenHelper.KEY_ID, StockTakeOpenHelper.COLUMN_WAREHOUSE };

	private static final String[] allBatchLinesColumns = {
			StockTakeOpenHelper.KEY_ID, StockTakeOpenHelper.COLUMN_BATCH_ID,
			StockTakeOpenHelper.COLUMN_ITEM,
			StockTakeOpenHelper.COLUMN_QUANTITY,
			StockTakeOpenHelper.COLUMN_RACK_NUMBER,
			StockTakeOpenHelper.COLUMN_UNIID };

	public StockTakeDataSource(Context context) {
		dbHelper = new StockTakeOpenHelper(context);
	}

	public void open() {
		Log.i(LOGTAG, "Database connection opens.");
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		Log.i(LOGTAG, "Database connection closes.");
		dbHelper.close();
	}

	public long createBatch(Batch batch) {
		ContentValues values = new ContentValues();
		values.put(StockTakeOpenHelper.COLUMN_WAREHOUSE, batch.getWarehouse()
				.getName());
		long batchId = database.insert(StockTakeOpenHelper.TABLE_BATCH, null,
				values);
		batch.setId(batchId);
		return batchId;

	}

	public long createBatchLine(BatchLine batchLine) {
		ContentValues values = new ContentValues();
		values.put(StockTakeOpenHelper.COLUMN_BATCH_ID, batchLine.getBatchId());
		values.put(StockTakeOpenHelper.COLUMN_ITEM, batchLine.getItem());
		values.put(StockTakeOpenHelper.COLUMN_QUANTITY, batchLine.getQuantity());
		values.put(StockTakeOpenHelper.COLUMN_RACK_NUMBER,
				batchLine.getRackNo());
		values.put(StockTakeOpenHelper.COLUMN_UNIID, batchLine.getUniId());
		long batchLineId = database.insert(
				StockTakeOpenHelper.TABLE_BATCH_LINE, null, values);
		batchLine.setId(batchLineId);
		return batchLineId;
	}

	public Batch getBatch() {
		Batch batch = null;
		Cursor cursor = database.query(StockTakeOpenHelper.TABLE_BATCH,
				allBatchColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				batch = new Batch();
				batch.setId(cursor.getLong(cursor
						.getColumnIndex(StockTakeOpenHelper.KEY_ID)));
				batch.setWarehouse(new Warehouse(cursor.getString(cursor
						.getColumnIndex(StockTakeOpenHelper.COLUMN_WAREHOUSE))));
				break;
			}
		}
		return batch;
	}

	public List<BatchLine> getAllBatchLines() {
		List<BatchLine> batchLinesList = new ArrayList<BatchLine>();
		Cursor cursor = database.query(StockTakeOpenHelper.TABLE_BATCH_LINE,
				allBatchLinesColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				BatchLine batchLine = new BatchLine();
				batchLine.setId(cursor.getLong(cursor
						.getColumnIndex(StockTakeOpenHelper.KEY_ID)));
				batchLine.setItem(cursor.getLong(cursor
						.getColumnIndex(StockTakeOpenHelper.COLUMN_ITEM)));
				batchLine.setQuantity(cursor.getLong(cursor
						.getColumnIndex(StockTakeOpenHelper.COLUMN_QUANTITY)));
				batchLine
						.setRackNo(cursor.getString(cursor
								.getColumnIndex(StockTakeOpenHelper.COLUMN_RACK_NUMBER)));
				batchLine.setUniId(cursor.getString(cursor
						.getColumnIndex(StockTakeOpenHelper.COLUMN_UNIID)));
				batchLinesList.add(batchLine);
			}
		}
		return batchLinesList;
	}

	public void deleteAllData() {
		database.delete(StockTakeOpenHelper.TABLE_BATCH_LINE, null, null);
		database.delete(StockTakeOpenHelper.TABLE_BATCH, null, null);
	}

	public int deleteBatchLine(BatchLine batchLine) {
		int rowcount = database.delete(StockTakeOpenHelper.TABLE_BATCH_LINE,
				StockTakeOpenHelper.COLUMN_UNIID + " = ?",
				new String[] { String.valueOf(batchLine.getUniId()) });
		return rowcount;
	}

}
