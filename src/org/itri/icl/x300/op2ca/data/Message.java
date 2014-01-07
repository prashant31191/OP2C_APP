package org.itri.icl.x300.op2ca.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor(staticName="of", suppressConstructorProperties=true)
@DatabaseTable 
public class Message {
	@DatabaseField(generatedId=true) long _id;
	@DatabaseField @NonNull String from;
	@DatabaseField @NonNull String to;
	@DatabaseField @NonNull String content;
	@DatabaseField @NonNull Long time;
	@DatabaseField(canBeNull=false, foreign=true, foreignAutoRefresh=true) @NonNull Resource resource;
}