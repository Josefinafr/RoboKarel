package com.example.robokarel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CodeInputFragment extends Fragment {

    private EditText codeInput;
    private TextView errorLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_input, container, false);

        codeInput = view.findViewById(R.id.codeInput);
        errorLabel = view.findViewById(R.id.errorLabel);
        Button playButton = view.findViewById(R.id.playButton);
        ImageView arrowButton = view.findViewById(R.id.arrowButton);

        playButton.setOnClickListener(v -> executeCode());
        arrowButton.setOnClickListener(v -> goToExecutionFragment());

        return view;
    }

    private void executeCode() {
        String code = codeInput.getText().toString().trim();
        errorLabel.setText("");

        // Validate the code
        if (!validateCode(code)) {
            errorLabel.setText("Syntax error in the code.");
            return;
        }

        // Pass the code to the execution fragment
        com.example.robokarel.CodeExecutionFragment fragment = new com.example.robokarel.CodeExecutionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        fragment.setArguments(bundle);

        // Load the execution fragment
        ((com.example.robokarel.MainActivity) getActivity()).loadFragment(fragment);
    }

    private boolean validateCode(String code) {
        // Basic validation logic (ensure all statements end with ;)
        String[] lines = code.split(";");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !(line.equals("begin {") || line.equals("end") || line.contains("}"))) {
                if (!line.endsWith("()")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void goToExecutionFragment() {
        ((MainActivity) getActivity()).loadFragment(new CodeExecutionFragment());
    }
}
