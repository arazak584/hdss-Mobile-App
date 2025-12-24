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
}
