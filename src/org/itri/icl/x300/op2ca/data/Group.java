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
public class Group {
	@DatabaseField(generatedId=true) long id;
	@DatabaseField @NonNull String grpName;
	@DatabaseField(canBeNull = false, foreign = true) Phone phone;
}
