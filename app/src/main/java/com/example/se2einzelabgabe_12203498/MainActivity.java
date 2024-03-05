package com.example.se2einzelabgabe_12203498;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final String ip = "se2-submission.aau.at";
    private final int port = 20080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        clientSocket.setSoTimeout(10000);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void sendMatNrToServer(View view) {
        new Thread(() -> {
            try {
                startConnection(ip, port);

                final EditText matNrInput = findViewById(R.id.matNrInput);
                String matNrToSend = matNrInput.getText().toString();

                out.println(matNrToSend);

                String message = in.readLine();

                runOnUiThread(() -> {
                    TextView serverOutput = findViewById(R.id.serverOutput);
                    serverOutput.setText(message);
                });

                stopConnection();
            } catch (IOException ex) {
                runOnUiThread(() -> {
                    TextView serverOutput = findViewById(R.id.serverOutput);
                    serverOutput.setText(ex.getMessage());
                });
            }
        }).start();
    }


    @SuppressLint("SetTextI18n")
    public void berechneQuersumme(View view) {
        int summe = 0;

        try {
            final EditText matNrInput = findViewById(R.id.matNrInput);
            String matNrSting = matNrInput.getText().toString();
            int zahl = Integer.parseInt(matNrSting);

            while (zahl != 0) {
                summe = summe + (zahl % 10);
                zahl = zahl / 10;
            }

            String binaryString = Integer.toBinaryString(summe);

            TextView quersummeOutput = findViewById(R.id.quersummeOutput);
            quersummeOutput.setText("Quersumme von " + matNrSting + " ist " + summe + ". Binärzahl: " + binaryString);
        } catch (Exception ex) {
            TextView quersummeOutput = findViewById(R.id.quersummeOutput);
            quersummeOutput.setText("Ungültige Eingabe");
        }
    }
}