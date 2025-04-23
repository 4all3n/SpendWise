package com.fallen.spenwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.fallen.spenwise.navigation.NavGraph
import com.fallen.spenwise.ui.theme.SpendWiseTheme
import com.fallen.spenwise.utils.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import com.fallen.spenwise.navigation.Screen
import kotlinx.coroutines.delay
import com.fallen.spenwise.data.UserRepository

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize user in local database if needed
        val userRepository = UserRepository(this)
        userRepository.initializeUserIfNeeded()
        
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Check for Google Sign-In result
        intent?.getParcelableExtra<GoogleSignInAccount>("google_sign_in_result")?.let { account ->
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("MainActivity", "signInWithCredential:success")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                    }
                }
        }

        setContent {
            SpendWiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val preferenceManager = remember { PreferenceManager(context) }
                    var initialDestination by remember { mutableStateOf(Screen.Welcome.route) }
                    var isCheckingAuth by remember { mutableStateOf(true) }

                    // Check auth state and set initial destination
                    LaunchedEffect(Unit) {
                        // Add a small delay to ensure Firebase is initialized
                        delay(100)
                        
                        if (auth.currentUser != null) {
                            initialDestination = Screen.Dashboard.route
                            isCheckingAuth = false
                        } else if (preferenceManager.getRememberMe()) {
                            val savedEmail = preferenceManager.getEmail()
                            val savedPassword = preferenceManager.getPassword()
                            
                            if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                                auth.signInWithEmailAndPassword(savedEmail, savedPassword)
                                    .addOnSuccessListener {
                                        initialDestination = Screen.Dashboard.route
                                        isCheckingAuth = false
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("MainActivity", "Auto-login failed", e)
                                        preferenceManager.clearCredentials()
                                        isCheckingAuth = false
                                    }
                            } else {
                                isCheckingAuth = false
                            }
                        } else {
                            isCheckingAuth = false
                        }
                    }

                    // Add navigation state observer
                    navController.addOnDestinationChangedListener { controller, destination, arguments ->
                        Log.d("Navigation", "Current destination: ${destination.route}")
                    }

                    // Handle back press
                    BackHandler {
                        if (!navController.popBackStack()) {
                            finish()
                        }
                    }
                    
                    if (!isCheckingAuth) {
                        NavGraph(
                            navController = navController,
                            initialDestination = initialDestination
                        )
                    }
                }
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("MainActivity", "Google sign in failed", e)
            }
        }
    }
    
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                // Update user profile with Google name if not set
                result.user?.let { user ->
                    if (user.displayName.isNullOrEmpty() && !account.displayName.isNullOrEmpty()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(account.displayName)
                            .build()
                        
                        user.updateProfile(profileUpdates)
                            .addOnSuccessListener {
                                Log.d("GoogleSignIn", "User profile updated with name: ${account.displayName}")
                            }
                    }
                }
                
                Log.d("GoogleSignIn", "signInWithCredential:success")
                // Navigate to dashboard
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("GoogleSignIn", "signInWithCredential:failure", e)
                // TODO: Show error message to user
            }
    }
    
    companion object {
        private const val RC_SIGN_IN = 9001
    }
}