package com.fallen.spenwise.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.fallen.spenwise.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val slideIn by animateFloatAsState(
        targetValue = if (isVisible) 0f else 100f,
        animationSpec = tween(500),
        label = "slideIn"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(700),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Get current activity context
    val context = LocalContext.current
    val activity = context as? Activity
    val preferenceManager = remember { PreferenceManager(context) }
    
    // Load saved credentials if Remember Me was enabled
    LaunchedEffect(Unit) {
        if (preferenceManager.getRememberMe()) {
            email = preferenceManager.getEmail() ?: ""
            password = preferenceManager.getPassword() ?: ""
            rememberMe = true
        }
    }
    
    // Configure Google Sign In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID")
            .requestEmail()
            .build()
    }
    
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }
    
    // Function to handle Google Sign In
    fun signInWithGoogle() {
        activity?.let {
            val signInIntent = googleSignInClient.signInIntent
            it.startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // Function to handle email/password login
    fun handleLogin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                if (rememberMe) {
                    preferenceManager.saveCredentials(email, password)
                } else {
                    preferenceManager.clearCredentials()
                }
                Log.d("LoginScreen", "Login successful")
                onLoginSuccess()
            }
            .addOnFailureListener { e ->
                errorMessage = e.message ?: "Login failed"
                showError = true
            }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B1E27),
                        Color(0xFF232731)
                    )
                )
            )
    ) {
        // Background decorative elements
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF8D5CF5).copy(alpha = 0.1f),
                            Color(0xFF8D5CF5).copy(alpha = 0.0f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 200.dp, y = 400.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFB06AB3).copy(alpha = 0.1f),
                            Color(0xFFB06AB3).copy(alpha = 0.0f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .graphicsLayer {
                    translationY = slideIn
                    this.alpha = alpha
                }
        ) {
            // Back Button
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Logo with gradient background
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8D5CF5),
                                Color(0xFFB06AB3),
                                Color(0xFFE96D71)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Login",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Text
            Text(
                text = "Welcome Back!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Sign in to continue to SpendWise",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email Field
            Text(
                text = "Email",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your email") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedBorderColor = Color(0xFF8D5CF5),
                    unfocusedContainerColor = Color(0xFF2A2F3C),
                    focusedContainerColor = Color(0xFF2A2F3C),
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password Field
            Text(
                text = "Password",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your password") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Password",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.ic_visibility_on
                            else R.drawable.ic_visibility_off
                        ),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                    )
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None
                                    else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedBorderColor = Color(0xFF8D5CF5),
                    unfocusedContainerColor = Color(0xFF2A2F3C),
                    focusedContainerColor = Color(0xFF2A2F3C),
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Remember me and Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF8D5CF5),
                            uncheckedColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = "Remember me",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
                
                Text(
                    text = "Forgot Password?",
                    color = Color(0xFF8D5CF5),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* TODO: Handle forgot password */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign In Button
            Button(
                onClick = { handleLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8D5CF5)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Or continue with
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.2f)
                )
                Text(
                    text = "  Or continue with  ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.2f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign In Button
            OutlinedButton(
                onClick = { signInWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Don't have an account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign Up",
                    color = Color(0xFF8D5CF5),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }

        // Error Snackbar
        if (showError && errorMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = Color(0xFF2A2F3C),
                contentColor = Color.White,
                action = {
                    TextButton(
                        onClick = { showError = false }
                    ) {
                        Text(
                            text = "Dismiss",
                            color = Color(0xFF8D5CF5)
                        )
                    }
                }
            ) {
                Text(errorMessage!!)
            }
        }
    }
}

private const val RC_SIGN_IN = 9001 