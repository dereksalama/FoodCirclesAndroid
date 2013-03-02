package com.foodcircles.android.activity;

import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.foodcircles.android.R;

public class NewCircleDialog extends DialogFragment {

	private EditText mEditText;
	private Button mOK;
	private Button mCancel;
	private NewCircleListener mListener;

	private void setOnOkListener(NewCircleListener listener) {
		mListener = listener;
	}

	public interface NewCircleListener {
		void onOkClick(String name);
	}

	public NewCircleDialog(){}

	public static NewCircleDialog get(NewCircleListener listener) {
		NewCircleDialog f = new NewCircleDialog();
		f.setOnOkListener(listener);
		return f;
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_new_circle_dialog, container);
		getDialog().setTitle("Create Circle");
		mEditText = (EditText) v.findViewById(R.id.edit_text_new_circle);
		mOK = (Button) v.findViewById(R.id.button_new_circle_ok);
		mOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO: alert if empty?
				mListener.onOkClick(mEditText.getText().toString());
				dismiss();
			}
		});
		mCancel = (Button) v.findViewById(R.id.button_new_circle_cancel);
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return v;
	}

}
