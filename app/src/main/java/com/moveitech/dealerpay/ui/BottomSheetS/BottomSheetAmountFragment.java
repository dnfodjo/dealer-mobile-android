package com.moveitech.dealerpay.ui.BottomSheetS;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.moveitech.dealerpay.R;

public class BottomSheetAmountFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private View view;
    private TextInputLayout mainLyt;
    private TextInputEditText userNameEdit;
    private AppCompatButton updateBtn;
    private String editVal;

    public OnBottomSheetClick onBottomSheetClick;

    public interface OnBottomSheetClick{

        void updateClicked(String editVal);

    }

    public void setOnBottomSheetClick(OnBottomSheetClick onBottomSheetClick) {
        this.onBottomSheetClick = onBottomSheetClick;
    }

    public BottomSheetAmountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);

        mainLyt = view.findViewById(R.id.amount_text_layout);
        userNameEdit = view.findViewById(R.id.amount);
        updateBtn = view.findViewById(R.id.go_btn);

        updateBtn.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.go_btn:

                editVal = userNameEdit.getText().toString().trim();

                if (onBottomSheetClick != null){

                    onBottomSheetClick.updateClicked(editVal);
                }



                break;

            default:

                break;
        }
    }

}
