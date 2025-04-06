package com.fallen.spenwise.ui.screens

import android.app.Activity
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
import androidx.compose.ui.graphics.Color
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
    
    // Get current activity context
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Configure Google Sign In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID") // Replace with your web client ID from google-services.json
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

    // Validation functions
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
    
    fun validateInputs(): Boolean {
        return when {
            fullName.isBlank() -> {
                errorMessage = "Please enter your full name"
                false
            }
            email.isBlank() -> {
                errorMessage = "Please enter your email"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                errorMessage = "Please enter a valid email address"
                false
            }
            password.isBlank() -> {
                errorMessage = "Please enter a password"
                false
            }
            !isPasswordValid(password) -> {
                errorMessage = "Password must be at least 6 characters long"
                false
            }
            password != confirmPassword -> {
                errorMessage = "Passwords do not match"
                false
            }
            !termsAccepted -> {
                errorMessage = "Please accept the Terms and Privacy Policy"
                false
            }
            else -> {
                errorMessage = null
                true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

        // App Logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF8D5CF5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_person_add),
                contentDescription = "Create Account",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Create Account Text
        Column {
            Text(
                text = "Create Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Join SpendWise to track your expenses",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        // Form Fields in a scrollable column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
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
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
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
                    placeholder = { Text("Create password\n(min. 6 characters)") },
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
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
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
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )
            }

            // Terms and Privacy Policy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
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
                    text = "I agree to the ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Terms",
                    color = Color(0xFF8D5CF5),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* TODO: Show terms */ }
                )
                Text(
                    text = " and ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Privacy Policy",
                    color = Color(0xFF8D5CF5),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* TODO: Show privacy policy */ }
                )
            }

            // Error message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Bottom section with buttons and sign in text
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Create Account Button
            Button(
                onClick = {
                    Log.d("SignUpScreen", "Create Account button clicked")
                    if (validateInputs()) {
                        Log.d("SignUpScreen", "Starting Firebase createUserWithEmailAndPassword")
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                Log.d("SignUpScreen", "User created successfully")
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build()
                                
                                result.user?.updateProfile(profileUpdates)
                                    ?.addOnSuccessListener {
                                        Log.d("SignUpScreen", "User profile updated successfully")
                                        onSignUpSuccess()
                                    }
                                    ?.addOnFailureListener { e ->
                                        Log.e("SignUpScreen", "Failed to update user profile", e)
                                        errorMessage = e.message
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e("SignUpScreen", "Failed to create user", e)
                                errorMessage = e.message
                            }
                    }
                },
                enabled = fullName.isNotBlank() && email.isNotBlank() && 
                         password.isNotBlank() && confirmPassword.isNotBlank() && 
                         password == confirmPassword && termsAccepted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8D5CF5),
                    disabledContainerColor = Color(0xFF8D5CF5).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

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
                    color = Color(0xFFA78BFA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { 
                        Log.d("SignUpScreen", "Sign In text clicked")
                        onSignInClick() 
                    }
                )
            }
        }
    }
}

private const val RC_SIGN_IN = 9001 