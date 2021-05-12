package com.example.harang.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.harang.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment2 extends Fragment {

    public ProfessorFragment2(){
    }
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_professor2, container, false);

        Spinner spinner = view.findViewById(R.id.spinner);
        //TextView text_result = view.findViewById(R.id.text_result);

        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                text_result.setText(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        return view;
    }
}