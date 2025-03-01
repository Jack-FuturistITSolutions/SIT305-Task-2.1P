package com.example.task21p;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Instantiate instance variables
    private EditText inputValue;
    private TextView resultText;
    private Spinner sourceUnitSpinner;
    private Spinner destinationUnitSpinner;

    @Override
    /**
     * Initial instance creation to load content to the view of the app.
     *
     * @param savedInstanceState | The saved instance state to restore any previous UI/UX state.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialise the UI components as variables to manipulate
        sourceUnitSpinner = findViewById(R.id.unitspinner);
        destinationUnitSpinner = findViewById(R.id.destspinner);
        inputValue = findViewById(R.id.inputValue);
        resultText = findViewById(R.id.resultText);

        // Instantiate button for conversion
        Button convertButton = findViewById(R.id.convertButton);

        /**
         * Define the possible units of measurement for conversion with type and category,
         * then split them into two LinkedHashMaps to use exclusively across each spinner.
        */
        Map<String, String> sourceUnits = new LinkedHashMap<>();
        sourceUnits.put("Centimetres", "Distance");
        sourceUnits.put("Metres", "Distance");
        sourceUnits.put("Kilometres", "Distance");
        sourceUnits.put("Milligrams", "Weight");
        sourceUnits.put("Grams", "Weight");
        sourceUnits.put("Kilograms", "Weight");
        sourceUnits.put("Celsius", "Temperature");

        Map<String, String> destUnits = new LinkedHashMap<>();
        destUnits.put("Inches", "Distance");
        destUnits.put("Feet", "Distance");
        destUnits.put("Yards", "Distance");
        destUnits.put("Miles", "Distance");
        destUnits.put("Ounces", "Weight");
        destUnits.put("Pounds", "Weight");
        destUnits.put("Tonnes", "Weight");
        destUnits.put("Fahrenheit", "Temperature");
        destUnits.put("Kelvin", "Temperature");

        // Extract unit names for the Spinner
        List<String> sourceUnitNames = new ArrayList<>(sourceUnits.keySet());
        List<String> destUnitNames = new ArrayList<>(destUnits.keySet());

        // Create ArrayAdapters to provide dropdown names for the spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sourceUnitNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, destUnitNames);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to each spinner to make units available as dropdown options
        sourceUnitSpinner.setAdapter(adapter);
        destinationUnitSpinner.setAdapter(adapter2);

        // Listen for button activity
        convertButton.setOnClickListener(v -> conversionFunction(sourceUnits, destUnits));
    }

    /**
     * Top-level function for handling conversion operations.
     * Gathers data on sourceUnits and destUnits Maps for use in other functions.
     * Parses the user's input as a double to reduce errors.
     * Handles errors and provides visual feedback if conversion is not possible.
     *
     * @param sourceUnits | LinkedHashMap containing source units of measurement.
     * @param destUnits | LinkedHashMap containing destination units of measurement.
     */
    private void conversionFunction(Map<String, String> sourceUnits, Map<String, String> destUnits) {
        String sourceUnit = sourceUnitSpinner.getSelectedItem().toString();
        String destUnit = destinationUnitSpinner.getSelectedItem().toString();
        String inputText = inputValue.getText().toString();

        // Check that the input is not empty, early exit if so and prompt user
        if (inputText.isEmpty()) {
            resultText.setText("Please enter a value to perform conversion.");
            return;
        }

        // Parse the input text out as a double
        double inputDouble = Double.parseDouble(inputText);

        // Compare the categories of possible selections to determine if they are convertible
        boolean conversionPossible = checkUnits(sourceUnit, destUnit, sourceUnits, destUnits);

        // Handle errors if the conversion is not possible
        if (conversionPossible) {
            double convertedValue = convertUnits(inputDouble, sourceUnit, destUnit);

            // Show a Toast message indicating that the conversion was successful
            Toast.makeText(this, "Conversion successful!", Toast.LENGTH_SHORT).show();

            // Display the conversion result
            resultText.setText(String.format("%.4f %s", convertedValue, destUnit));
        }
        else {
            resultText.setText("The conversion between " + sourceUnit + " and " + destUnit + " is not possible. Please reselect.");
        }
    }

    /**
     * Specifically handles whether source and destination units are compatible for conversion, based on
     * value/category in the key-value pairs of the LinkedHashMaps above.
     *
     * @param sourceUnit | String of the unit of measurement from the source units.
     * @param destUnit | String of the unit of measurement from the destination units.
     * @param sourceUnits | LinkedHashMap containing source units of measurement.
     * @param destUnits | LinkedHashMap containing destination units of measurement.
     *
     * @return boolean | Return whether the categories are equivalent
     */
    private boolean checkUnits(String sourceUnit, String destUnit, Map<String, String> sourceUnits, Map<String, String> destUnits) {

        // Define the category variables which extract the
        String sourceUnitCategory = sourceUnits.get(sourceUnit);
        String destUnitCategory = destUnits.get(destUnit);

        // Ensure both units exist and belong to the same category, return a boolean value for error handling
        return (sourceUnitCategory != null && destUnitCategory != null) && sourceUnitCategory.equals(destUnitCategory);
    }

    /**
     * Apply conversion arithmetic based on preset conversion factors.
     *
     * @param value | Input value as a double generated by the user.
     * @param sourceUnit | String of the unit of measurement from the source units.
     * @param destinationUnit | String of the unit of measurement from the destination units.
     *
     * @return double | Return the answer
     */
    private double convertUnits(double value, String sourceUnit, String destinationUnit) {

        /* Declare the conversion factors */

        // Distance Conversion
        double cmToInches = 0.393701, inchesToCm = 2.54;
        double mToInches = 39.3701, inchesToM = 0.0254;
        double kmToInches = 39370.1, inchesToKm = 0.0000254;

        double cmToFoot = 0.0328084, footToCm = 30.48;
        double mToFoot = 3.28084, footToM = 0.3048;
        double kmToFoot = 3280.84, footToKm = 0.0003048;

        double cmToYard = 0.0109361, yardToCm = 91.44;
        double mToYard = 1.09361, yardToM = 0.9144;
        double kmToYard = 1093.61, yardToKm = 0.0009144;

        double cmToMi = 0.0000062137, miToCm = 160934;
        double mToMi = 0.000621371, miToM = 1609.34;
        double kmToMi = 0.621371, miToKm = 1.60934;

        // Weight Conversion
        double mgToOz = 0.000035274, ozToMg = 28349.5;
        double gToOz = 0.035274, ozToG = 28.3495;
        double kgToOz = 35.274, ozToKg = 0.0283495;

        double mgToLb = 0.00000220462, LbToMg = 453592;
        double gToLb = 0.00220462, LbToG = 453.592;
        double kgToLb = 2.20462, LbToKg = 0.453592;

        double mgToT = 0.000000001, tToMg = 1000000000;
        double gToT = 0.000001, tToG = 1000000;
        double kgToT = 0.001, tToKg = 1000;

        // Handle conversions based on selections made by user
        switch (sourceUnit) {
            // Length conversions
            case "Centimetres":
                switch (destinationUnit) {
                    case "Inches": return value * cmToInches;
                    case "Feet": return value * cmToFoot;
                    case "Yards": return value * cmToYard;
                    case "Miles": return value * cmToMi;
                }
                break;
            case "Metres":
                switch (destinationUnit) {
                    case "Inches": return value * mToInches;
                    case "Feet": return value * mToFoot;
                    case "Yards": return value * mToYard;
                    case "Miles": return value * mToMi;
                }
                break;
            case "Kilometres":
                switch (destinationUnit) {
                    case "Inches": return value * kmToInches;
                    case "Feet": return value * kmToFoot;
                    case "Yards": return value * kmToYard;
                    case "Miles": return value * kmToMi;
                }
                break;
            case "Inches":
                switch (destinationUnit) {
                    case "Centimetres": return value * inchesToCm;
                    case "Metres": return value * inchesToM;
                    case "Kilometres": return value * inchesToKm;
                }
                break;
            case "Feet":
                switch (destinationUnit) {
                    case "Centimetres": return value * footToCm;
                    case "Metres": return value * footToM;
                    case "Kilometres": return value * footToKm;
                }
                break;
            case "Yards":
                switch (destinationUnit) {
                    case "Centimetres": return value * yardToCm;
                    case "Metres": return value * yardToM;
                    case "Kilometres": return value * yardToKm;
                }
                break;
            case "Miles":
                switch (destinationUnit) {
                    case "Centimetres": return value * miToCm;
                    case "Metres": return value * miToM;
                    case "Kilometres": return value * miToKm;
                }
                break;

            // Weight conversions
            case "Milligrams":
                switch (destinationUnit) {
                    case "Ounces": return value * mgToOz;
                    case "Pounds": return value * mgToLb;
                    case "Tonnes": return value * mgToT;
                }
                break;
            case "Grams":
                switch (destinationUnit) {
                    case "Ounces": return value * gToOz;
                    case "Pounds": return value * gToLb;
                    case "Tonnes": return value * gToT;
                }
                break;
            case "Kilograms":
                switch (destinationUnit) {
                    case "Ounces": return value * kgToOz;
                    case "Pounds": return value * kgToLb;
                    case "Tonnes": return value * kgToT;
                }
                break;
            case "Ounces":
                switch (destinationUnit) {
                    case "Milligrams": return value * ozToMg;
                    case "Grams": return value * ozToG;
                    case "Kilograms": return value * ozToKg;
                }
                break;
            case "Pounds":
                switch (destinationUnit) {
                    case "Milligrams": return value * LbToMg;
                    case "Grams": return value * LbToG;
                    case "Kilograms": return value * LbToKg;
                }
                break;
            case "Tonnes":
                switch (destinationUnit) {
                    case "Milligrams": return value * tToMg;
                    case "Grams": return value * tToG;
                    case "Kilograms": return value * tToKg;
                }
                break;

            // Temperature conversions (calling functions)
            case "Celsius":
                switch (destinationUnit) {
                    case "Fahrenheit": return celsToFahr(value);
                    case "Kelvin": return celsToKelv(value);
                }
                break;
            case "Fahrenheit":
                switch (destinationUnit) {
                    case "Celsius": return fahrToCels(value);
                    case "Kelvin": return fahrToKelv(value);
                }
                break;
            case "Kelvin":
                switch (destinationUnit) {
                    case "Celsius": return kelvToCels(value);
                    case "Fahrenheit": return kelvToFahr(value);
                }
                break;
        }

        // If no conversion was found, return the original value
        return value;
    }

    /**
     * Converts Celsius to Fahrenheit.
     *
     * @param c | The Celsius value to convert.
     * @return double | The converted Fahrenheit value.
     */
    double celsToFahr(double c) { return (c * 9/5) + 32; }

    /**
     * Converts Fahrenheit to Celsius.
     *
     * @param f | The Fahrenheit value to convert.
     * @return double | The converted Celsius value.
     */
    double fahrToCels(double f) { return (f - 32) * 5/9; }

    /**
     * Converts Celsius to Kelvin.
     *
     * @param c | The Celsius value to convert.
     * @return double | The converted Kelvin value.
     */
    double celsToKelv(double c) { return c + 273.15; }

    /**
     * Converts Kelvin to Celsius.
     *
     * @param k | The Kelvin value to convert.
     *
     * @return double | The converted Celsius value.
     */
    double kelvToCels(double k) { return k - 273.15; }

    /**
     * Converts Fahrenheit to Kelvin.
     *
     * @param f | The Fahrenheit value to convert.
     *
     * @return double | The converted Kelvin value.
     */
    double fahrToKelv(double f) { return (f - 32) * 5/9 + 273.15; }

    /**
     * Converts Kelvin to Fahrenheit.
     *
     * @param k | The Kelvin value to convert.
     *
     * @return double | The converted Fahrenheit value.
     */
    double kelvToFahr(double k) { return (k - 273.15) * 9/5 + 32; }
}