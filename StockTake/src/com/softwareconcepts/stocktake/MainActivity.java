package com.softwareconcepts.stocktake;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.softwareconcepts.stocktake.db.StockTakeDataSource;
import com.softwareconcepts.stocktake.model.Batch;
import com.softwareconcepts.stocktake.model.BatchLine;
import com.softwareconcepts.stocktake.model.Warehouse;
import com.softwareconcepts.stocktake.util.Constant;
import com.softwareconcepts.stocktake.util.FileUtil;
import com.softwareconcepts.stocktake.util.Utility;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	private static final int MAX_ITEM_CHARACTER = 13;
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			Constant.DATE_FORMAT, Locale.ENGLISH);
	private Batch batch;
	private List<BatchLine> batchLinesList;
	private BatchLine batchLine;
	private Warehouse warehouse;
	private List<String> fileNamesList;
	private String deviceIMEI = "UNKNOWN";

	private Button slideHandleButton;
	private SlidingDrawer slidingDrawer;
	private TextView warehouseTextView;
	private TextView rackNoHeaderTextView;
	private EditText itemEditText;
	private EditText qtyEditText;
	private EditText rackNoEditText;
	private Button batchLineDoneButton;
	private ScrollView batchLinesScrollView;
	private ImageView barcodeImageView;

	private TableLayout batchLinesTableLayout;
	private TextView currentIdTextView;
	private TextView currentItemTextView;
	private TextView currentQtyTextView;
	private TextView currentRackNoTextView;
	private ImageView currentDeleteImageView;

	private SharedPreferences sharedPreferences;
	private final String WAREHOUSE_NAME = "WAREHOUSE_NAME";

	private boolean isRackNoVisible;
	private SharedPreferences settingsPreference;

	public static final int REQUEST_CODE = 1;

	private StockTakeDataSource dataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sharedPreferences = getPreferences(MODE_PRIVATE);
		setSettings();

		dataSource = new StockTakeDataSource(this);
		dataSource.open();

		slideHandleButton = (Button) findViewById(R.id.slideHandleButton);
		slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		itemEditText = (EditText) findViewById(R.id.item_edit_text);
		warehouseTextView = (TextView) findViewById(R.id.warehouse_text_view);
		rackNoHeaderTextView = (TextView) findViewById(R.id.rackNo_header_text_view);
		qtyEditText = (EditText) findViewById(R.id.qty_edit_text);
		rackNoEditText = (EditText) findViewById(R.id.rack_edit_text);
		barcodeImageView = (ImageView) findViewById(R.id.barcode_imageView);
		batchLineDoneButton = (Button) findViewById(R.id.batch_line_done_button);
		batchLinesScrollView = (ScrollView) findViewById(R.id.batch_lines_scroll_view);
		batchLinesTableLayout = (TableLayout) findViewById(R.id.batch_lines_table_layout);

		itemEditText.setHint(Utility
				.surroundWithParentheses(getString(R.string.item)));
		itemEditText.addTextChangedListener(textWatcher);
		itemEditText.setOnFocusChangeListener(itemOnFocusChangeListener);
		qtyEditText.setHint(Utility
				.surroundWithParentheses(getString(R.string.qty)));
		rackNoEditText.setHint(Utility
				.surroundWithParentheses(getString(R.string.rack_no)));
		rackNoEditText.setOnClickListener(clearRackTextOnclickListener);
		batchLineDoneButton.setOnClickListener(batchLineDoneOnclickListener);
		qtyEditText
		.setOnEditorActionListener(batchLineDoneEditorActionListener);
		rackNoEditText
		.setOnEditorActionListener(batchLineDoneEditorActionListener);
		slidingDrawer.setOnDrawerOpenListener(drawerOpenListener);
		slidingDrawer.setOnDrawerCloseListener(drawerCloseListener);
		barcodeImageView.setOnClickListener(barcodeOnclickListener);
		deviceIMEI = getDeviceImie();
		getBatchData();
	}

	
	OnClickListener barcodeOnclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			IntentIntegrator scanIntegrator = new IntentIntegrator(
					MainActivity.this);
			scanIntegrator.initiateScan();
		}
	};
	OnClickListener clearRackTextOnclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			rackNoEditText.setText("");
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		setSettings();
	}

	private void getBatchData() {
		batch = dataSource.getBatch();
		if (!isRackNoVisible) {
			rackNoEditText.setVisibility(View.INVISIBLE);
		} else {
			rackNoEditText.setVisibility(View.VISIBLE);
		}
		if (batch != null) {
			warehouse = batch.getWarehouse();
			warehouseTextView.setText(getString(R.string.warehouse) + ": "
					+ batch.getWarehouse().getName());
			if (batchLinesList == null) {
				batchLinesList = dataSource.getAllBatchLines();
				batch.setBatchLinesList(batchLinesList);
				for (BatchLine batchLine : batchLinesList) {
					showBatchLineRow(batchLine);
				}
			}
		} else {
			getWarehouse();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataSource.close();
	}

	private void setSettings() {
		if (dataSource == null) {
			dataSource = new StockTakeDataSource(MainActivity.this);
		}
		dataSource.open();
		settingsPreference = PreferenceManager
				.getDefaultSharedPreferences(this);
		isRackNoVisible = settingsPreference.getBoolean("checkbox", true);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			itemEditText.setText(scanContent);
		} else if(requestCode == REQUEST_CODE){
			showCenterToast(getString(R.string.restart_app));
		}else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	OnFocusChangeListener itemOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				if (itemEditText.getText().toString().length() != MAX_ITEM_CHARACTER) {
					itemEditText
					.setError(getString(R.string.enter_item_value_max));
				}
			}
		}
	};

	private final TextWatcher textWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		public void afterTextChanged(Editable s) {
			if (itemEditText.getText().toString().length() == MAX_ITEM_CHARACTER) {
				qtyEditText.requestFocus();
			}
		}
	};

	OnEditorActionListener batchLineDoneEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				batchLineDoneButton.performClick();
			}
			return false;
		}
	};

	OnClickListener batchLineDoneOnclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String itemValue = itemEditText.getText().toString();
			String qtyValue = qtyEditText.getText().toString();
			String rackNoValue = rackNoEditText.getText().toString();
			if (batch == null || batch.getWarehouse() == null
					|| Utility.isNull(batch.getWarehouse().getName())) {
				batch = new Batch();
				batch.setWarehouse(warehouse);
				dataSource.createBatch(batch);
			}

			if (Utility.isEmpty(batchLinesList)) {
				batchLinesList = new ArrayList<BatchLine>();
			}

			batch.setBatchLinesList(batchLinesList);

			if ("".equals(itemValue) || itemValue.length() < MAX_ITEM_CHARACTER) {
				itemEditText.setError(getString(R.string.enter_item_value));
			} else if ("".equals(qtyValue)) {
				qtyEditText.setError(getString(R.string.enter_qty_value));
			} else {
				long item = Long.parseLong(itemValue);
				long qty = Long.parseLong(qtyValue);
				// TODO
				addBatchLineRow(item, qty, rackNoValue, batch);
			}
		}
	};

	OnDrawerOpenListener drawerOpenListener = new OnDrawerOpenListener() {
		@Override
		public void onDrawerOpened() {
			slideHandleButton.setBackgroundResource(R.drawable.openarrow);
		}
	};

	OnDrawerCloseListener drawerCloseListener = new OnDrawerCloseListener() {
		@Override
		public void onDrawerClosed() {
			slideHandleButton.setBackgroundResource(R.drawable.closearrow);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_email_csv:
			createAndEmailCsv();
			return true;
		case R.id.action_delete_all:
			createNewBatch();
			return true;
		case R.id.action_details:
			getDetails();
			return true;
		case R.id.menu_settings:
			setPreference();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setPreference() {
		startActivityForResult(new Intent(MainActivity.this, SetPreference.class), REQUEST_CODE);
	}
	

	private void getDetails() {
		int totalNoOfItems = 0;
		if (!Utility.isEmpty(batchLinesList)) {
			for (BatchLine batchLine : batchLinesList) {
				totalNoOfItems += batchLine.getQuantity();
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(false)
		.setTitle(getString(R.string.no_of_items))
		.setIcon(R.drawable.ic_action_info)
		.setMessage(
				getString(R.string.total_no_of_items_is) + " "
						+ totalNoOfItems)
						.setNegativeButton(getString(R.string.close),
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void createAndEmailCsv() {
		hideKeyboard();
		if (batch != null) {
			if (batchLinesList == null) {
				showCenterToast(getString(R.string.select_warehouse));
			} else if (batchLinesList.size() == 0) {
				showCenterToast(getString(R.string.no_valid_items));
			} else if (warehouse == null || Utility.isNull(warehouse.getName())) {
				showCenterToast(getString(R.string.select_warehouse));
			} else {
				confirmEmail();
			}
		} else {
			showCenterToast(getString(R.string.select_warehouse));
		}
	}

	private void sendEmail(List<String> fileNamesList) {
		List<Uri> urisList = new ArrayList<Uri>();
		for (String fileName : fileNamesList) {
			Uri uri = Uri.parse("file://" + fileName);
			urisList.add(uri);
		}
		final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(
				android.content.Intent.EXTRA_SUBJECT,
				getString(R.string.mail_subject)
				.replace("%WAREHOUSE%", warehouse.getName())
				.replace("%DEVICEIMEI%", deviceIMEI)
				.replace("%DATEEXPORT%", sdf.format(new Date())));
		if (urisList != null) {
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
					(ArrayList<? extends Parcelable>) urisList);
		}
		emailIntent.putExtra(
				android.content.Intent.EXTRA_TEXT,
				getString(R.string.mail_main_message)
				.replace("%DATEEXPORT%", sdf.format(new Date()))
				.replace("%WAREHOUSE%", warehouse.getName())
				.replace("%DEVICEIMEI%", deviceIMEI)
				.replace("%NUMOFRECORDS%",
						String.valueOf(batchLinesList.size())));
		this.startActivity(Intent.createChooser(emailIntent,
				getString(R.string.sending_email)));
	}

	private void resetBatch() {
		warehouse = new Warehouse();
		batchLinesList = new ArrayList<BatchLine>();
		batch = new Batch();
		reinitializeEntry(itemEditText);

		List<TableRow> tableRowsList = new ArrayList<TableRow>();

		if (batchLinesTableLayout.getChildCount() > 2) {
			for (int i = 2; i < batchLinesTableLayout.getChildCount(); i++) {
				View child = batchLinesTableLayout.getChildAt(i);
				if (child instanceof TableRow) {
					tableRowsList.add((TableRow) child);
				}
			}
		}

		if (batchLinesTableLayout.getChildCount() > 2) {
			ViewGroup container = ((ViewGroup) batchLinesTableLayout);
			for (TableRow tableRow : tableRowsList) {
				container.removeView(tableRow);
			}
		}

		batchLinesScrollView.setVisibility(View.INVISIBLE);
	}

	private void getWarehouse() {
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.warehouse_dialog);
		dialog.setTitle(R.string.enter_warehouse);
		resetBatch();
		final EditText warehouseEditText = (EditText) dialog
				.findViewById(R.id.warehouse_edit_text);
		warehouseEditText.setText(sharedPreferences.getString(WAREHOUSE_NAME,
				""));
		final Button warehouseDoneButton = (Button) dialog
				.findViewById(R.id.warehouse_done_button);

		warehouseEditText.setOnClickListener(clearEditTextOnclickListener);
		warehouseEditText
		.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					warehouseDoneButton.performClick();
				}
				return false;
			}
		});
		warehouseDoneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String warehouseName = warehouseEditText.getText().toString();
				if ("".equals(warehouseName)) {
					warehouseEditText
					.setError(getString(R.string.enter_warehouse));
				} else {
					storeWarehouseInPreferences(warehouseName);
					dialog.dismiss();
					warehouse.setName(warehouseName);
					slidingDrawer.open();
					hideKeyboard();
					warehouseTextView.setText(getString(R.string.warehouse)
							+ ": " + warehouseName);
					Toast toast = Toast.makeText(MainActivity.this,
							R.string.enter_batch_lines, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});
		dialog.show();
	}

	OnClickListener clearEditTextOnclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v instanceof EditText) {
				((EditText) v).setText("");
			}
		}
	};

	private void storeWarehouseInPreferences(String warehouseName) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(WAREHOUSE_NAME, warehouseName);
		editor.commit();
	}

	private void hideKeyboard() {
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	protected void addBatchLineRow(long item, long qty, String rackNo,
			Batch batch) {
		if (Utility.isNull(rackNo)) {
			rackNo = "N/A";
		}
		batchLine = new BatchLine(Utility.getUniqueToken(), item, qty, rackNo,
				batch.getId());
		dataSource.createBatchLine(batchLine);
		batchLinesList.add(batchLine);
		reinitializeEntry(itemEditText);
		showBatchLineRow(batchLine);
	}

	OnClickListener deleteRowOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			confirmDelete(v);
		}
	};

	private void showBatchLineRow(BatchLine batchLine) {
		if (batchLinesScrollView.getVisibility() == View.INVISIBLE) {
			batchLinesScrollView.setVisibility(View.VISIBLE);
		}

		float[] rackDimensions = { 0.5f, 0.2f, 0.2f, 0.1f };

		final Context context = MainActivity.this;
		TableRow batchLineTableRow = new TableRow(context);
		currentIdTextView = new TextView(context);
		currentItemTextView = new TextView(context);
		currentQtyTextView = new TextView(context);
		// TODO
		currentRackNoTextView = new TextView(context);
		currentDeleteImageView = new ImageView(context);

		currentItemTextView.setGravity(Gravity.CENTER_VERTICAL);
		currentQtyTextView.setGravity(Gravity.CENTER_VERTICAL);
		// TODO
		currentRackNoTextView.setGravity(Gravity.CENTER_VERTICAL);
		currentIdTextView.setText(batchLine.getUniId());
		currentItemTextView.setText(String.valueOf(batchLine.getItem()));
		currentQtyTextView.setText(String.valueOf(batchLine.getQuantity()));
		// TODO
		currentRackNoTextView.setText(String.valueOf(batchLine.getRackNo()));
		currentDeleteImageView.setImageResource(R.drawable.trash);
		currentDeleteImageView.setOnClickListener(deleteRowOnClickListener);
		currentItemTextView.setTextColor(context.getResources().getColor(
				R.color.dark_blue));
		currentQtyTextView.setTextColor(context.getResources().getColor(
				R.color.dark_blue));
		// TODO
		currentRackNoTextView.setTextColor(context.getResources().getColor(
				R.color.dark_blue));
		currentItemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				Constant.BATCH_LINE_TEXT_SIZE);
		currentQtyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				Constant.BATCH_LINE_TEXT_SIZE);
		// TODO
		currentRackNoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				Constant.BATCH_LINE_TEXT_SIZE);
		// TODO
		currentQtyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				Constant.BATCH_LINE_TEXT_SIZE);

		batchLineTableRow.addView(currentIdTextView);
		currentIdTextView.setVisibility(View.INVISIBLE);
		currentIdTextView.setWidth(0);
		currentIdTextView.setHeight(0);
		batchLineTableRow.addView(currentItemTextView);
		batchLineTableRow.addView(currentQtyTextView);
		if (!isRackNoVisible) {
			// TODO
			rackNoHeaderTextView.setVisibility(View.INVISIBLE);
			rackNoHeaderTextView.setWidth(0);
			rackNoHeaderTextView.setHeight(0);
			currentRackNoTextView.setWidth(0);
			currentRackNoTextView.setHeight(0);
		}

		batchLineTableRow.addView(currentRackNoTextView);
		batchLineTableRow.addView(currentDeleteImageView);

		batchLinesTableLayout.addView(batchLineTableRow);

		TableRow.LayoutParams currentItemTextViewParams = (TableRow.LayoutParams) currentItemTextView
				.getLayoutParams();
		currentItemTextViewParams.weight = rackDimensions[0];
		currentItemTextView.setLayoutParams(currentItemTextViewParams);
		currentItemTextViewParams.gravity = Gravity.CENTER;

		TableRow.LayoutParams currentQtyTextViewParams = (TableRow.LayoutParams) currentQtyTextView
				.getLayoutParams();
		currentQtyTextViewParams.weight = rackDimensions[1];
		currentQtyTextView.setLayoutParams(currentQtyTextViewParams);
		currentQtyTextViewParams.gravity = Gravity.CENTER;

		// TODO
		TableRow.LayoutParams currentRackNoViewParams = (TableRow.LayoutParams) currentRackNoTextView
				.getLayoutParams();
		currentRackNoViewParams.weight = rackDimensions[2];
		currentRackNoTextView.setLayoutParams(currentRackNoViewParams);
		currentRackNoViewParams.gravity = Gravity.CENTER;

		TableRow.LayoutParams currentDeleteImageViewParams = (TableRow.LayoutParams) currentDeleteImageView
				.getLayoutParams();
		currentDeleteImageViewParams.weight = rackDimensions[3];
		currentDeleteImageView.setLayoutParams(currentDeleteImageViewParams);
		currentDeleteImageViewParams.gravity = Gravity.CENTER;

		TableLayout.LayoutParams batchLineTableRowLayoutParams = (TableLayout.LayoutParams) batchLineTableRow
				.getLayoutParams();
		batchLineTableRowLayoutParams.setMargins(Constant.BATCTH_TABLE_MARGIN,
				Constant.BATCTH_TABLE_MARGIN, Constant.BATCTH_TABLE_MARGIN,
				Constant.BATCTH_TABLE_MARGIN);
		batchLineTableRow.setLayoutParams(batchLineTableRowLayoutParams);
	}

	private void confirmDelete(final View v) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);
		alertDialog.setTitle(getString(R.string.confirm_delete));
		alertDialog.setMessage(getString(R.string.delete_confirm_message));
		alertDialog.setIcon(R.drawable.delete);
		alertDialog.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteBatchLine(v);
			}
		});
		alertDialog.setNegativeButton(getString(R.string.no),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}

	private void createNewBatch() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);
		alertDialog.setTitle(getString(R.string.create_new_batch));
		alertDialog.setMessage(getString(R.string.new_batch_confirm_message));
		alertDialog.setIcon(R.drawable.ic_action_delete);
		alertDialog.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					dataSource.deleteAllData();
					warehouseTextView.setText("");
					getWarehouse();
				} catch (Exception e) {
					e.printStackTrace();
					showCenterToast(getString(R.string.error_creating_csv));
				}
			}
		});
		alertDialog.setNegativeButton(getString(R.string.no),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}

	private void confirmEmail() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);
		alertDialog.setTitle(getString(R.string.confirm_email));
		alertDialog.setMessage(getString(R.string.email_confirm_message));
		alertDialog.setIcon(R.drawable.ic_email);
		alertDialog.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					fileNamesList = new ArrayList<String>();
					batch.setFileNamesList(fileNamesList);
					FileUtil.createCSVFiles(batch, fileNamesList,
							isRackNoVisible);
					sendEmail(fileNamesList);
					reinitializeItemAndQty();
				} catch (Exception e) {
					e.printStackTrace();
					showCenterToast(getString(R.string.error_creating_csv));
				}
			}
		});
		alertDialog.setNegativeButton(getString(R.string.no),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}

	private void deleteBatchLine(View v) {
		View row = (View) v.getParent();
		if (row instanceof TableRow) {
			View textView = ((TableRow) row).getChildAt(0);
			if (textView instanceof TextView) {
				batchLine = new BatchLine(((TextView) textView).getText()
						.toString());
				batchLinesList.remove(batchLine);
				dataSource.deleteBatchLine(batchLine);
			}
		}
		View tableLayout = (View) row.getParent();
		ViewGroup container = ((ViewGroup) tableLayout);
		container.removeView(row);
		if (batchLinesTableLayout.getChildCount() == 2) {
			batchLinesScrollView.setVisibility(View.INVISIBLE);
		}
		reinitializeEntry(itemEditText);
	}

	private void reinitializeEntry(View v) {
		reinitializeItemAndQty();
		if (v != null && v.requestFocus()) {
			showKeyboard(v);
		}
	}

	private void reinitializeItemAndQty() {
		itemEditText.setText("");
		qtyEditText.setText(getString(R.string.default_qty));
	}

	private void showKeyboard(View v) {
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	private void showCenterToast(String message) {
		Toast toast = Toast.makeText(MainActivity.this, message,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public String getDeviceImie() {
		TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return mngr.getDeviceId();
	}
}
