package org.itri.icl.x300.op2ca.data.ext;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import android.os.Parcel;
import android.os.Parcelable;
import data.Capability;
import data.Resources.Resource;

@Getter @Setter @RequiredArgsConstructor(suppressConstructorProperties=true)
public class CapabilityArg implements Parcelable {
	@NonNull Long id;
	@NonNull String type;
	@NonNull String text;
	@NonNull ResourceArg resource;

	@Override
	public int describeContents() {return 0;}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(type);
		dest.writeString(text);
		dest.writeParcelable(resource, flags);
	}
	
	
	public CapabilityArg(Parcel p) {
		id = p.readLong();
		type = p.readString();
		text = p.readString();
		resource = p.readParcelable(Resource.class.getClassLoader());
	}
	public static final Parcelable.Creator<CapabilityArg> CREATOR = new Parcelable.Creator<CapabilityArg>() {
		public CapabilityArg createFromParcel(Parcel p) {
			return new CapabilityArg(p);
		}
		public CapabilityArg[] newArray(int size) {
			return new CapabilityArg[size];
		}
	};
}
