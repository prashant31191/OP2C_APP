package org.itri.icl.x300.op2ca.data.ext;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import android.os.Parcel;
import android.os.Parcelable;

@Getter @Setter @RequiredArgsConstructor(suppressConstructorProperties=true)
public class ContactArg implements Parcelable {

	@NonNull String userID;
	@NonNull String displayName;
    
    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userID);
		dest.writeString(displayName);
	}

	public ContactArg(Parcel p) {
		userID = p.readString();
		displayName = p.readString();
	}
	public static final Parcelable.Creator<ContactArg> CREATOR = new Parcelable.Creator<ContactArg>() {
		public ContactArg createFromParcel(Parcel p) {
			return new ContactArg(p);
		}
		public ContactArg[] newArray(int size) {
			return new ContactArg[size];
		}
	};
}
