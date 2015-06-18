package com.softwareconcepts.stocktake.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StockTakeOpenHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;

	public static final String DATABASE_NAME = "stockTake.db";
	public static final String TABLE_BATCH = "batch";
	public static final String TABLE_BATCH_LINE = "batch_line";
	public static final String KEY_ID = "id";
	public static final String COLUMN_WAREHOUSE = "warehouse";
	public static final String COLUMN_BATCH_ID = "batch_id";
	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_RACK_NUMBER = "rack_number";
	public static final String COLUMN_UNIID = "uniid";

	private static final String CREATE_TABLE_BATCH = "CREATE TABLE "
			+ TABLE_BATCH + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_WAREHOUSE + " TEXT NOT NULL)";
	
	private static final String CREATE_TABLE_BATCH_LINE = "CREATE TABLE "
			+ TABLE_BATCH_LINE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_BATCH_ID + " INTEGER NOT NULL,"
			+ COLUMN_ITEM + " LONG NOT NULL," + COLUMN_QUANTITY
			+ " TEXT NOT NULL," + COLUMN_RACK_NUMBER + " TEXT, "
			+ COLUMN_UNIID + " TEXT NOT NULL)";

	public StockTakeOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_BATCH);
		db.execSQL(CREATE_TABLE_BATCH_LINE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATCH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATCH_LINE);
		onCreate(db);
	}

}
