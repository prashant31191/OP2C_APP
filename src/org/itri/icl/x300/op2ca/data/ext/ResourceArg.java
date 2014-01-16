package org.itri.icl.x300.op2ca.data.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import android.os.Parcel;
import android.os.Parcelable;

@Getter @Setter @RequiredArgsConstructor(suppressConstructorProperties=true)
public class ResourceArg implements Parcelable {

	@NonNull String uri;
	@NonNull String displayName;
	Collection<CapabilityArg> capabilities;
	@NonNull String status; 

    int index = 0;
	public CapabilityArg next() {
		if (index == capabilities.size()) {
			index = 0;
		}
		return capabilities.toArray(new CapabilityArg[0])[index++];
	}
	public void add(CapabilityArg capArg) {
		if (!this.capabilities.contains(capArg)) {
			capArg.setResource(this);
			this.capabilities.add(capArg);
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		ResourceArg other = (ResourceArg) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uri);
		dest.writeString(displayName);
		dest.writeList(new ArrayList<CapabilityArg>(capabilities));
	}
	
	public ResourceArg(Parcel p) {
		uri = p.readString();
		displayName = p.readString();
		p.readList(new ArrayList<CapabilityArg>(capabilities), CapabilityArg.class.getClassLoader());
	}
	public static final Parcelable.Creator<ResourceArg> CREATOR = new Parcelable.Creator<ResourceArg>() {
		public ResourceArg createFromParcel(Parcel p) {
			return new ResourceArg(p);
		}

		public ResourceArg[] newArray(int size) {
			return new ResourceArg[size];
		}
	};

}
