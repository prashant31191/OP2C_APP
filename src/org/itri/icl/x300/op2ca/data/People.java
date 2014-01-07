package org.itri.icl.x300.op2ca.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Lists;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor(staticName="of", suppressConstructorProperties=true)
@DatabaseTable 
public class People implements Parcelable {
	@NonNull @DatabaseField(id=true) String lookupKey;
	@NonNull @DatabaseField(unique=true) long _id;
	@NonNull @DatabaseField String displayName;
	@DatabaseField long syncTime;
	@ForeignCollectionField Collection<Phone> phone = new ArrayList<Phone>();
	
	public void add(Phone phone) {
		if (!this.phone.contains(phone)) {
			phone.setPeople(this);
			this.phone.add(phone);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lookupKey == null) ? 0 : lookupKey.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		People other = (People) obj;
		if (lookupKey == null) {
			if (other.lookupKey != null)
				return false;
		} else if (!lookupKey.equals(other.lookupKey))
			return false;
		return true;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(lookupKey);
		dest.writeLong(_id);
		dest.writeString(displayName);
	}
	
	public People(Parcel p) {
		lookupKey = p.readString();
		_id = p.readLong();
		displayName = p.readString();
	}
	public static final Parcelable.Creator<People> CREATOR = new Parcelable.Creator<People>() {
		public People createFromParcel(Parcel p) {
			return new People(p);
		}

		public People[] newArray(int size) {
			return new People[size];
		}
	};
	
}
