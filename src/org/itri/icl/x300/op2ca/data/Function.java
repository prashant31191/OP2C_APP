package org.itri.icl.x300.op2ca.data;

import java.util.ArrayList;
import java.util.Collection;

import org.itri.icl.x300.op2ca.data.TYPE.FUNC_TYPE;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.sun.research.ws.wadl.ParamStyle;

@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor(staticName="of", suppressConstructorProperties=true)
@DatabaseTable 
public class Function implements Parcelable {
	@DatabaseField(generatedId=true) long _id;
	@DatabaseField @NonNull FUNC_TYPE type;
	@DatabaseField @NonNull String text;
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh=true) Device device;
	
	
	@Override
	public String toString() {
		return text;
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
		Function other = (Function) obj;
		if (_id != other._id)
			return false;
		return true;
	}
	@Override
	public int describeContents() {return 0;}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString((type == null) ? "" : type.name());
		dest.writeString(text);
		dest.writeParcelable(device, flags);
	}
	
	
	public Function(Parcel p) {
		_id = p.readLong();
		try {
            type = FUNC_TYPE.valueOf(p.readString());
        } catch (IllegalArgumentException x) {
            type = null;
        }
		text = p.readString();
		device = p.readParcelable(Device.class.getClassLoader());
	}
	public static final Parcelable.Creator<Function> CREATOR = new Parcelable.Creator<Function>() {
		public Function createFromParcel(Parcel p) {
			return new Function(p);
		}

		public Function[] newArray(int size) {
			return new Function[size];
		}
	};

}
