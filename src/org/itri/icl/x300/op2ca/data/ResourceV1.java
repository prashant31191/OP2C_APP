package org.itri.icl.x300.op2ca.data;

import java.util.ArrayList;
import java.util.Collection;

import org.itri.icl.x300.op2ca.utils.DateUtils;
import org.linphone.core.LinphoneCall.State;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor(staticName="of", suppressConstructorProperties=true)
@DatabaseTable
public class ResourceV1 {
	/** SPEC ShareInfo
	 * opID
	 * sender
	 * role
	 * shareType
	 * shareContent
	 * description
	 * num_linkes
	 * num_comments;
	 * num_online
	 * ticker
	 * createTime
	 * expireTime
	 */
	
	@DatabaseField(generatedId=true) long _id;
	@DatabaseField(unique=true) @NonNull long opID;
	@DatabaseField @NonNull String sender;
	@DatabaseField @NonNull String role;
	@DatabaseField @NonNull String description;
	@DatabaseField @NonNull String shareType;
	@DatabaseField @NonNull String shareContent;
	@DatabaseField @NonNull int num_likes;
	@DatabaseField @NonNull int num_comments;
	@DatabaseField @NonNull int num_online;
	@DatabaseField @NonNull int ticker;
	@DatabaseField @NonNull Long createTime;
	@DatabaseField @NonNull Long expireTime;
	@DatabaseField boolean read = false;
	State state = State.Idle;
	@ForeignCollectionField Collection<Message> message = new ArrayList<Message>();
	
	
//	@DatabaseField @NonNull String ownerId;
//	@DatabaseField @NonNull String ownerName;
//	@DatabaseField @NonNull String description;
//	@DatabaseField @NonNull String type;
//	@DatabaseField @NonNull int like;
//	@DatabaseField @NonNull long startDate;
//	@DatabaseField @NonNull long endDate;
//	@DatabaseField boolean read = false;
	
	public String fmtEndDate() {
		return DateUtils.zh_DateFmter(createTime);
	}
	
	public String fmtStartDate(long time) {
		return DateUtils.zh_DateFmter(expireTime);
	}

}