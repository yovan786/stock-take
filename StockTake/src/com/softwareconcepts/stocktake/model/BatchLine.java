package com.softwareconcepts.stocktake.model;

public class BatchLine {
	private long id;
	private Long item;
	private Long quantity;
	private String rackNo;
	private String uniId;
	private long batchId;

	public BatchLine() {
		super();
	}

	public BatchLine(String uniId) {
		super();
		this.uniId = uniId;
	}

	public BatchLine(Long item, Long quantity) {
		super();
		this.item = item;
		this.quantity = quantity;
	}

	public BatchLine(String uniId, Long item, Long quantity, String rackNo, long batchId) {
		super();
		this.uniId = uniId;
		this.item = item;
		this.quantity = quantity;
		this.rackNo = rackNo;
		this.batchId = batchId;
	}

	public BatchLine(long id, Long item, Long quantity, String rackNo,
			long batchId) {
		super();
		this.id = id;
		this.item = item;
		this.quantity = quantity;
		this.rackNo = rackNo;
		this.batchId = batchId;
	}

	public BatchLine(long id, Long item, Long quantity, String rackNo,
			String uniId) {
		super();
		this.id = id;
		this.item = item;
		this.quantity = quantity;
		this.rackNo = rackNo;
		this.uniId = uniId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getItem() {
		return item;
	}

	public void setItem(Long item) {
		this.item = item;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getRackNo() {
		return rackNo;
	}

	public void setRackNo(String rackNo) {
		this.rackNo = rackNo;
	}

	public String getUniId() {
		return uniId;
	}

	public void setUniId(String uniId) {
		this.uniId = uniId;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	@Override
	public boolean equals(Object o) {
		boolean isEqual = false;
		if (uniId != null) {
			if (o instanceof BatchLine) {
				isEqual = uniId.equals(((BatchLine) o).getUniId());
			}
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = hashCode * 37 + this.uniId.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		return "BatchLine [id=" + id + ", item=" + item + ", quantity="
				+ quantity + ", rackNo=" + rackNo + ", uniId=" + uniId
				+ ", batchId=" + batchId + "]";
	}

}
