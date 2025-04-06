package com.fallen.spenwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.fallen.spenwise.navigation.NavGraph
import com.fallen.spenwise.ui.theme.SpendWiseTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        setContent {
            SpendWiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Add navigation state observer
                    navController.addOnDestinationChangedListener { controller, destination, arguments ->
                        Log.d("Navigation", "Current destination: ${destination.route}")
                    }
                    
                    NavGraph(navController = navController)
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
                Log.w("GoogleSignIn", "Google sign in failed", e)
                // TODO: Show error message to user
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
                // TODO: Navigate to main screen
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