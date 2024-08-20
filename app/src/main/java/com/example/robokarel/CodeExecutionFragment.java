package com.example.robokarel;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CodeExecutionFragment extends Fragment {

    private View leftRect;
    private View rightRect;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_execution, container, false);

        leftRect = view.findViewById(R.id.leftRect);
        rightRect = view.findViewById(R.id.rightRect);
        ImageView backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> getFragmentManager().popBackStack());

        // Execute the code if provided
        Bundle arguments = getArguments();
        if (arguments != null) {
            String code = arguments.getString("code", "");
            runCommands(code);
        }

        return view;
    }

    private void runCommands(String code) {
        String[] lines = code.split(";");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            handler.postDelayed(() -> executeCommand(line), i * 5000L);
        }
    }

    private void executeCommand(String command) {
        switch (command) {
            case "forward()":
                leftRect.setBackgroundColor(Color.WHITE);
                rightRect.setBackgroundColor(Color.WHITE);
                break;
            case "left()":
                leftRect.setBackgroundColor(Color.WHITE);
                rightRect.setBackgroundColor(Color.BLACK);
                break;
            case "right()":
                leftRect.setBackgroundColor(Color.BLACK);
                rightRect.setBackgroundColor(Color.WHITE);
                break;
            case "stop()":
                leftRect.setBackgroundColor(Color.BLACK);
                rightRect.setBackgroundColor(Color.BLACK);
                break;
        }
    }
}
