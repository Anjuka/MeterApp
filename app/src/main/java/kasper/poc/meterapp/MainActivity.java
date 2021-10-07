package kasper.poc.meterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    MeterView meterView;
    EditText editTextNumber;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meterView = findViewById(R.id.meterView);
        editTextNumber = findViewById(R.id.editTextNumber);
        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meterView.setProgressVal(Integer.parseInt(editTextNumber.getText().toString()));
                meterView.setTempUnit("°C/m");
            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                meterView.setProgressVal(Integer.parseInt(editTextNumber.getText().toString()));
                meterView.setTempUnit("°F/m");
                return true;
            }
        });

    }
}