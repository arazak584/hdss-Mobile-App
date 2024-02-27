package org.openhds.hdsscapture.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.util.Date;


@Entity
public class LogBook {

	@Expose
	@NotNull
	@PrimaryKey
	public String id;

	@Expose
	public String fw_name;

	@Expose
	public Date date;

	@Expose
	public Integer complete;

	public LogBook() {
	}
}
