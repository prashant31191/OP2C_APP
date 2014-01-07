package org.itri.icl.x300.op2ca.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of", suppressConstructorProperties = true)
@DatabaseTable
public class Phone implements Parcelable {
	@DatabaseField(generatedId = true) long _id; 
	@DatabaseField @NonNull String number;
	@DatabaseField String domain;
	@DatabaseField long syncTime;
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true) People people;
	@ForeignCollectionField	ForeignCollection<Group> group;
//	boolean checked = false;

	@Override
	public String toString() {
		return people.getDisplayName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
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
		Phone other = (Phone) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString(number);
		dest.writeString(domain);
		dest.writeParcelable(people, flags);
//		dest.writeByte((byte) (checked ? 1 : 0)); 
	}

	public Phone(Parcel p) {
		_id = p.readLong();
		number = p.readString();
		domain = p.readString();
		people = p.readParcelable(People.class.getClassLoader());
//		checked = p.readByte() != 0;
	}
	public static final Parcelable.Creator<Phone> CREATOR = new Parcelable.Creator<Phone>() {
		public Phone createFromParcel(Parcel p) {
			return new Phone(p);
		}

		public Phone[] newArray(int size) {
			return new Phone[size];
		}
	};
}
