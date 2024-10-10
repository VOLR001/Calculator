package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calculator.ui.theme.CalculatorTheme

/**
*main method to begin calculator function
 * */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

/**
 * composable function to project the Calculator screen
 * Sets up the UI layout for displaying the current and previous entries as well as button inputs.
 */
@Composable
fun CalculatorScreen() {
    //variables for display and operation functions, start with id & name
    var previousEntry by remember { mutableStateOf("45881699") } // Initial display text set to "cwu"
    var currentTotal by remember { mutableStateOf("COLE MANCHESTER") } // Initial main display set to "C"

    Column(
        modifier = Modifier
            .fillMaxSize() //fill space and pad
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        //display for previous entry (above the main display)
        Text(
            text = previousEntry,
            modifier = Modifier
                //fit to width
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge
        )

        //display for the current total or error message
        Text(
            text = currentTotal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .weight(1f),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.displayMedium
        )

        //column for button layout and format
        Column {
            //buttons for each row, with specific colors
            ButtonRow(
                buttons = listOf("C", "(", ")", "/"),
               //create colors to assign the buttons
                colors = listOf(Color(0xFFFF337D), Color.Gray, Color.Gray, Color(0xFFFF8033))
            ) { handleButtonClick(it, previousEntry, currentTotal) { prev, total ->
                previousEntry = prev
                currentTotal = total
            } }
            ButtonRow(
                buttons = listOf("7", "8", "9", "*"),
                colors = listOf(Color.Gray, Color.Gray, Color.Gray, Color(0xFFFF8033))
            ) { handleButtonClick(it, previousEntry, currentTotal) { prev, total ->
                previousEntry = prev
                currentTotal = total
            } }
            ButtonRow(
                buttons = listOf("4", "5", "6", "+"),
                colors = listOf(Color.Gray, Color.Gray, Color.Gray, Color(0xFFFF8033))
            ) { handleButtonClick(it, previousEntry, currentTotal) { prev, total ->
                previousEntry = prev
                currentTotal = total
            } }
            ButtonRow(
                buttons = listOf("1", "2", "3", "-"),
                colors = listOf(Color.Gray, Color.Gray, Color.Gray, Color(0xFFFF8033))
            ) { handleButtonClick(it, previousEntry, currentTotal) { prev, total ->
                previousEntry = prev
                currentTotal = total
            } }
            ButtonRow(
                buttons = listOf("AC", "0", ".", "="),
                colors = listOf(Color(0xFF3377FF), Color(0xFFFF8033), Color.Gray, Color(0xFFFF8033))
            ) { handleButtonClick(it, previousEntry, currentTotal) { prev, total ->
                previousEntry = prev
                currentTotal = total
            } }
        }
    }
}

/**
 * handles button click events and manages the state for the calculator display and operations
 *
 * @param button The button that was clicked.
 * @param previousEntry The previous input entered by the user.
 * @param currentTotal The current total displayed on the calculator.
 * @param updateState A lambda function to update the state variables.
 */
fun handleButtonClick(
    button: String,
    previousEntry: String,
    currentTotal: String,
    updateState: (String, String) -> Unit
) {
    when (button) {
        //handles 'AC' where we clear all texts
        "AC" -> {
            updateState("", "0") // Reset to initial state
            return
        }
        //handle 'C' we clear last entry
        "C" -> {
            val newPrev = if (previousEntry.isNotEmpty()) previousEntry.dropLast(1) else ""
            updateState(newPrev, currentTotal)
            //update the state with current reading and past value
            return
        }
        //handles '='
        "=" -> {
            try {
                val result = evaluateExpression(previousEntry)
                updateState(result.toString(), result.toString())
            } catch (e: Exception) {
                updateState(previousEntry, "Error") // Show "Error" on exception
            }
            return
        }
        //here handle decimal point reading
        "." -> {
            if (!previousEntry.contains(".")) {
                updateState(previousEntry + ".", currentTotal)
            }
            return
        }
    }

    //check if the display needs resetting for the first input
    val newPrev = if (previousEntry == "45881699" || currentTotal == "COLE MANCHESTER") button else previousEntry + button
    updateState(newPrev, newPrev) // Update display
}

/**
 * evaluate an input as a string to handle the specific operation
 *
 * @param expression The mathematical expression as a string.
 * @return The evaluated result as a double
 */
fun evaluateExpression(expression: String): Double {
    val cleanedExpression = expression.replace(" ", "")
    //clear values, total and build string to hold new set of ints and strings
    var total = 0.0
    var currentNumber = StringBuilder()
    var operation = '+'
//find decimal pt
    for (char in cleanedExpression) {
        when {
            char.isDigit() || char == '.' -> {
                currentNumber.append(char)
            }
            char in "+-*/" -> {
                //operations are searched and applied to current total
                if (currentNumber.isNotEmpty()) {
                    total = performOperation(total, currentNumber.toString().toDouble(), operation)
                    currentNumber.clear()
                    //clear and reset total
                }
                operation = char
            }
        }
    }
    //as long as its not cleared, do stuff
    if (currentNumber.isNotEmpty()) {
        total = performOperation(total, currentNumber.toString().toDouble(), operation)
    }

    return total
}

/**
 * Perform the specific math operation based off button click
 *
 * @param total The current total.
 * @param number The next number to apply the operation to.
 * @param operation The operation to perform.
 * @return The result of the operation.
 */
fun performOperation(total: Double, number: Double, operation: Char): Double {
    return when (operation) {
        //possible operators
        '+' -> total + number
        '-' -> total - number
        '*' -> total * number
        '/' -> if (number != 0.0) total / number else Double.NaN
        //in case of division by 0
        //if
        else -> total
    }
}

/**
 * Composable function that creates a row of very cool calculator buttons.
 *
 * @param buttons The labels for each button in the row.
 * @param colors The background colors for each button.
 * @param onButtonClick The function to call when a button is clicked.
 */
@Composable
fun ButtonRow(buttons: List<String>, colors: List<Color>, onButtonClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
        //fill void spaces
    ) {
        buttons.forEachIndexed { index, button ->
            Button(
                onClick = { onButtonClick(button) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .background(colors[index], if (button == "C") CircleShape else RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(containerColor = colors[index]),
                shape = if (button == "C") CircleShape else RoundedCornerShape(50)
                //circle and round buttons to fit format
            ) {
                Text(text = button, color = Color.White)
            }
        }
    }
}