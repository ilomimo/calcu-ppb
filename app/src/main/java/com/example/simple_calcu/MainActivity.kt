package com.example.simple_calcu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simple_calcu.ui.theme.SimplecalcuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimplecalcuTheme {
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

@Composable
fun CalculatorScreen() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    fun calculate() {
        try {
            val expr = input.replace("×", "*").replace("÷", "/")
            val eval = eval(expr)
            result = String.format("%.2f", eval)
        } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
            result = "Error"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = input,
            fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE3F2FD)) // biru sangat muda
                .padding(8.dp),
            textAlign = TextAlign.End,
            color = Color.Black
        )
        Text(
            text = result,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F8E9)) // hijau sangat muda
                .padding(8.dp),
            textAlign = TextAlign.End,
            color = Color(0xFF1B5E20)
        )
        Spacer(modifier = Modifier.height(16.dp))
        val buttons = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "=", "+"),
            listOf("C")
        )
        for (row in buttons) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (button in row) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(80.dp)
                            .background(
                                when (button) {
                                    "÷", "×", "-", "+" -> Color(0xFF90CAF9) // biru segar
                                    "=" -> Color(0xFF66BB6A) // hijau terang
                                    "C" -> Color(0xFFEF5350) // merah terang
                                    else -> Color(0xFFF5F5F5) // abu netral
                                }
                            )
                            .clickable {
                                when (button) {
                                    "=" -> calculate()
                                    "C" -> {
                                        input = ""
                                        result = ""
                                    }
                                    else -> input += button
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = button,
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

fun eval(expression: String): Double {
    return object {
        var pos = -1
        var ch = 0
        fun nextChar() {
            ch = if (++pos < expression.length) expression[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expression.length) throw RuntimeException("Unexpected: " + expression[pos])
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> x /= parseFactor()
                    else -> return x
                }
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = expression.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected: ${ch.toChar()}")
            }
            return x
        }
    }.parse()
}