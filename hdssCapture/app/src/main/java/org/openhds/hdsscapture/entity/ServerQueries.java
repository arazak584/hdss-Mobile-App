package org.openhds.hdsscapture.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;



@Entity(tableName = "serverqueries")
public class ServerQueries {

	@Expose
	@NotNull
	@PrimaryKey
	public String id;

	@Expose
	public String compno;

	@Expose
	public String householdId;

	@Expose
	public String permId;

	@Expose
	public String fullName;

	@Expose
	public String entity;

	@Expose
	public String error;

	@Expose
	public String fw;

	@Expose
	public String fwname;

	public ServerQueries() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompno() {
		return compno;
	}

	public void setCompno(String compno) {
		this.compno = compno;
	}

	public String getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(String householdId) {
		this.householdId = householdId;
	}

	public String getPermId() {
		return permId;
	}

	public void setPermId(String permId) {
		this.permId = permId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getFw() {
		return fw;
	}

	public void setFw(String fw) {
		this.fw = fw;
	}

	public String getFwname() {
		return fwname;
	}

	public void setFwname(String fwname) {
		this.fwname = fwname;
	}
}
