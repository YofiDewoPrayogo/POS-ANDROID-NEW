package com.yofidewo.pos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yofidewo.pos.ui.PosViewModel

@Composable
fun LoginScreen(viewModel: PosViewModel, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2B4D), // Dark Navy Blue
                        Color(0xFF0F172A)  // Slate 900
                    )
                )
            )
    ) {
        // Decorative ambient glow circles in background
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color(0xFFFF6600).copy(alpha = 0.2f))
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .clip(CircleShape)
                .background(Color(0xFF2563EB).copy(alpha = 0.2f))
        )

        // Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo WarungKu POS Resmi
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.yofidewo.pos.R.drawable.app_warungku_logo),
                        contentDescription = "Logo WarungKu POS",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "WarungKu",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFF6600)
                    )
                    Text(
                        text = "Sistem Kasir",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email / Username Kasir") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFF6600)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text("PIN / Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF6600)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.login(
                                email = email.trim(),
                                pin = pin.trim(),
                                onSuccess = { onLoginSuccess() },
                                onError = {
                                    Toast.makeText(context, "Email atau PIN salah", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600))
                    ) {
                        Text("Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    var showOutletSetup by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { showOutletSetup = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Setup / Gabung Kode Outlet (Cloud)", fontSize = 13.sp)
                    }

                    if (showOutletSetup) {
                        androidx.compose.ui.window.Dialog(onDismissRequest = { showOutletSetup = false }) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            ) {
                                com.yofidewo.pos.ui.screens.OutletSetupScreen(
                                    viewModel = viewModel,
                                    onComplete = { showOutletSetup = false }
                                )
                            }
                        }
                    }

                    TextButton(onClick = { Toast.makeText(context, "Hubungi Admin untuk reset password", Toast.LENGTH_SHORT).show() }) {
                        Text("Lupa Password?", fontSize = 12.sp, color = Color(0xFFFF6600))
                    }
                }
            }


            // Clean, elegant watermark text at the bottom
            Text(
                text = "generate by Yofi Dewo Prayogo",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
