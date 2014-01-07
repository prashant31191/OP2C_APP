package org.itri.icl.x300.op2ca.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.itri.icl.x300.op2ca.data.TYPE.*;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor(staticName="of", suppressConstructorProperties=true)
@DatabaseTable
public class Device implements Parcelable {
	@DatabaseField(generatedId=true) long _id;
//	@DatabaseField @NonNull String uri;
	@DatabaseField @NonNull String text;
	@ForeignCollectionField Collection<Function> function = new ArrayList<Function>();
	
	
	int index = 0;
	public Function next() {
		if (index == function.size()) {
			index = 0;
		}
		return function.toArray(new Function[0])[index++];
	}
	public void add(Function function) {
		if (!this.function.contains(function)) {
			function.setDevice(this);
			this.function.add(function);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_id ^ (_id >>> 32));
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
		Device other = (Device) obj;
		if (_id != other._id)
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
		dest.writeString(text);
		dest.writeList(new ArrayList<Function>(function));
	}
	
	public Device(Parcel p) {
		_id = p.readLong();
		text = p.readString();
		p.readList(new ArrayList<Function>(function), Function.class.getClassLoader());
	}
	public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
		public Device createFromParcel(Parcel p) {
			return new Device(p);
		}

		public Device[] newArray(int size) {
			return new Device[size];
		}
	};
	
}
