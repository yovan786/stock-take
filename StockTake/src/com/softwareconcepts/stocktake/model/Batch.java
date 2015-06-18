package com.softwareconcepts.stocktake.model;

import java.util.List;

public class Batch {
	private long id;
	private List<BatchLine> batchLinesList;
	private Warehouse warehouse;
	private List<String> fileNamesList;

	public Batch() {
		super();
	}

	public Batch(long id, List<BatchLine> batchLinesList, Warehouse warehouse,
			List<String> fileNamesList) {
		super();
		this.id = id;
		this.batchLinesList = batchLinesList;
		this.warehouse = warehouse;
		this.fileNamesList = fileNamesList;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<BatchLine> getBatchLinesList() {
		return batchLinesList;
	}

	public void setBatchLinesList(List<BatchLine> batchLinesList) {
		this.batchLinesList = batchLinesList;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public List<String> getFileNamesList() {
		return fileNamesList;
	}

	public void setFileNamesList(List<String> fileNamesList) {
		this.fileNamesList = fileNamesList;
	}

	@Override
	public String toString() {
		return "Batch [id=" + id + ", batchLinesList=" + batchLinesList
				+ ", warehouse=" + warehouse + ", fileNamesList="
				+ fileNamesList + "]";
	}

}
