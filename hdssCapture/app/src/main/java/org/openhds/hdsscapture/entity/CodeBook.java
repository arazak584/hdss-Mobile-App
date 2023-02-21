package org.openhds.hdsscapture.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;


@Entity
public class CodeBook {

	@Expose
	@NotNull
	@PrimaryKey
	public String id;

	@Expose
	public String codeFeature;

	@Expose
	public Integer codeValue;

	@Expose
	public String codeLabel;

	public CodeBook() {
	}
}
