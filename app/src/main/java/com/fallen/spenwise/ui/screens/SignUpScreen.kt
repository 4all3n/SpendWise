package com.fallen.spenwise.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.util.Log

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
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

    // Function to handle sign up
    fun handleSignUp() {
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            showError = true
            return
        }
        
        if (!termsAccepted) {
            errorMessage = "Please accept the terms and conditions"
            showError = true
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // Update user profile with full name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()

                authResult.user?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {
                        Log.d("SignUpScreen", "Account created successfully")
                        onSignUpSuccess()
                    }
                    ?.addOnFailureListener { e ->
                        errorMessage = e.message ?: "Failed to update profile"
                        showError = true
                    }
            }
            .addOnFailureListener { e ->
                errorMessage = e.message ?: "Sign up failed"
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
                    painter = painterResource(id = R.drawable.ic_person_add),
                    contentDescription = "Create Account",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create Account Text
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Join SpendWise to track your expenses",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Form Fields
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Full Name Field
                Column {
                    Text(
                        text = "Full Name",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your full name") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Name",
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
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }

                // Email Field
                Column {
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
                }

                // Password Fields
                Column {
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
                        placeholder = { Text("Create password") },
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
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }

                Column {
                    Text(
                        text = "Confirm Password",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Confirm password") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = "Confirm Password",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    id = if (isConfirmPasswordVisible) R.drawable.ic_visibility_on
                                    else R.drawable.ic_visibility_off
                                ),
                                contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.clickable { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                            )
                        },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None
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
                }

                // Terms and Conditions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF8D5CF5),
                            uncheckedColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = "I accept the Terms and Conditions",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { termsAccepted = !termsAccepted }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create Account Button
            Button(
                onClick = { handleSignUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8D5CF5)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Create Account",
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

            // Google Sign Up Button
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

            Spacer(modifier = Modifier.height(24.dp))

            // Already have an account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign In",
                    color = Color(0xFF8D5CF5),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
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