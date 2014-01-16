package org.itri.icl.x300.op2ca.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.data.Message;

import data.Comments.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@Log
public class MessageAdapter extends ArrayAdapter<Comment> implements
		OnClickListener {
	SimpleDateFormat mFmt = new SimpleDateFormat("MM/dd, hh:mm");
	LayoutInflater vi;
	public MessageAdapter() {
		super(App.getCtx(), R.layout.op2c_item_msg_right, R.id.txtMessage);
		vi = (LayoutInflater) App.getCtx().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = super.getView(position, convertView, parent);
//		Message item = getItem(position);
//		log.warning("holder " + item.getContent() + " " + item.getFrom() + " " + item.getTo());
//		TextView txtPeople = (TextView) view.findViewById(R.id.txtPeople);
//		TextView txtMesage = (TextView) view.findViewById(R.id.txtMessage);
//		TextView txtTime = (TextView) view.findViewById(R.id.txtTime);
//
//		txtPeople.setText(App.getNameCache(item.getFrom()));
//		txtMesage.setText(item.getContent());
//		txtTime.setText(mFmt.format(item.getTime()));
//		return view;
//	}

	
//	public int getItemViewType(int pos) {
//		
//	}
//	
//	@Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        int type = getItemViewType(position);
//        System.out.println("getView " + position + " " + convertView + " type = " + type);
//        if (convertView == null) {
//            holder = new ViewHolder();
//            switch (type) {
//                case TYPE_ITEM:
//                    convertView = mInflater.inflate(R.layout.item1, null);
//                    holder.textView = (TextView)convertView.findViewById(R.id.text);
//                    break;
//                case TYPE_SEPARATOR:
//                    convertView = mInflater.inflate(R.layout.item2, null);
//                    holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
//                    break;
//            }
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder)convertView.getTag();
//        }
//        holder.textView.setText(mData.get(position));
//        return convertView;
//    }
	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// first get the animal from our data model
		Comment item = getItem(position);
		log.warning("holder2 " + item.getContent() + " " + item.getUserID() + " " + item.getOpInfo().getOpID());
		// if we have an image so we setup an the view for an image row
		ViewHolder holder;
		View view = convertView;
		if (view == null) {
			if ("SELF".equalsIgnoreCase(item.getUserID())) {
				log.warning("holder1 right");
				view = (RelativeLayout) vi.inflate(R.layout.op2c_item_msg_right, null);
			} else {
				log.warning("holder1 left");
				view = (RelativeLayout) vi.inflate(R.layout.op2c_item_msg_left, null);
			}
			holder = new ViewHolder((ImageView) view.findViewById(R.id.imgPeople),
					(TextView) view.findViewById(R.id.txtMessage),
					(TextView) view.findViewById(R.id.txtPeople),
					(TextView) view.findViewById(R.id.txtTime));
			view.setTag(holder);
		} else {
			// get the holder so we can set the image
			holder = (ViewHolder) view.getTag();
		}

//		holder.imgPeople.setImageResource(item.getImageId());
		holder.txtMesage.setText(item.getContent());
//		if ("SELF".equalsIgnoreCase(item.getUserID()))
//			holder.txtPeople.setText(App.getNameCache(item.getTo()));
//		else 
			holder.txtPeople.setText(App.getNameCache(item.getUserID()));
		holder.txtDate.setText(mFmt.format(item.getTime()));
		return view;
	}
	
	@RequiredArgsConstructor(staticName="of", suppressConstructorProperties=true)
	static class ViewHolder {
		@NonNull ImageView imgPeople;
		@NonNull TextView txtMesage;
		@NonNull TextView txtPeople;
		@NonNull TextView txtDate;
	}

	@Override
	public void onClick(View v) {
	}

}
