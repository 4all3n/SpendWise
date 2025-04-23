package com.fallen.spenwise.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fallen.spenwise.R
import com.fallen.spenwise.ui.components.BottomNavigationBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.fallen.spenwise.utils.PreferenceManager
import androidx.navigation.NavController
import com.fallen.spenwise.data.UserRepository
import com.fallen.spenwise.data.DatabaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToTransactions: () -> Unit = {},
    onBudgetClick: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(3) }
    var darkMode by remember { mutableStateOf(true) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val isEmailPasswordUser = remember {
        currentUser?.providerData?.any { 
            it.providerId == EmailAuthProvider.PROVIDER_ID 
        } ?: false
    }

    // Check if user is signed in with Google
    val isGoogleUser = remember {
        currentUser?.providerData?.any { 
            it.providerId == GoogleAuthProvider.PROVIDER_ID 
        } ?: false
    }

    // Get user's profile photo URL
    val profilePhotoUrl = remember {
        if (isGoogleUser) {
            currentUser?.photoUrl?.toString()
        } else {
            null
        }
    }

    // State for delete all dialog
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    val userRepository = remember { UserRepository(context) }
    val currentUserId = remember { userRepository.getCurrentUserId() }
    val dbHelper = remember { DatabaseHelper(context) }

    // Function to handle complete sign out
    fun handleSignOut() {
        try {
            // Clear saved credentials
            val preferenceManager = PreferenceManager(context)
            preferenceManager.clearCredentials()
            preferenceManager.setRememberMe(false)

        // Sign out from Firebase
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            // If signed in with Google, revoke access
            if (currentUser?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true) {
                GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
                ).revokeAccess()
            }
            
            // Sign out from Firebase
            auth.signOut()

        // Navigate to welcome screen
        onSignOut()

            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error signing out: ${e.message}", Toast.LENGTH_SHORT).show()
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
        // Decorative Background Elements
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = (-50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 200.dp, y = 200.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFB06AB3).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Profile",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Manage your account settings",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF282C35),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile Image with Edit Button
                            Box(
                                modifier = Modifier.size(72.dp)
                            ) {
                                // Profile Image
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (profilePhotoUrl == null)
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF8B5CF6),
                                                    Color(0xFFB06AB3)
                                                )
                                            )
                                            else
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Transparent
                                                    )
                                                )
                                        )
                                ) {
                                    if (profilePhotoUrl != null) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(profilePhotoUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Profile Picture",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                        )
                                    } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Picture",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .align(Alignment.Center)
                                    )
                                    }
                                }
                                
                                // Only show edit button for non-Google users
                                if (!isGoogleUser) {
                                Surface(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .clickable { /* Handle edit profile picture */ },
                                    shape = CircleShape,
                                    color = Color(0xFF8B5CF6),
                                    tonalElevation = 4.dp
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile Picture",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(6.dp)
                                    )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = currentUser?.displayName ?: "User",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = currentUser?.email ?: "",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Personal Information Section
                    Text(
                        text = "Personal Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    SettingsCard(
                        title = "Name",
                        value = currentUser?.displayName ?: "User",
                        onClick = { /* Handle name edit */ }
                    )

                    SettingsCard(
                        title = "Email",
                        value = currentUser?.email ?: "",
                        onClick = { /* Handle email edit */ }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Security Section
                    Text(
                        text = "Security",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Change Password Option (always visible but conditionally enabled)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isEmailPasswordUser) {
                                    onNavigateToChangePassword()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Password change is not available for Google Sign-In users",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                        color = Color(0xFF282C35),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF8B5CF6).copy(alpha = if (isEmailPasswordUser) 0.2f else 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_lock),
                                        contentDescription = null,
                                        tint = Color(0xFF8B5CF6).copy(alpha = if (isEmailPasswordUser) 1f else 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Change Password",
                                    fontSize = 16.sp,
                                    color = if (isEmailPasswordUser) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = if (isEmailPasswordUser) 0.5f else 0.3f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Preferences Section
                    Text(
                        text = "Preferences",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Delete All Data Option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteAllDialog = true },
                        color = Color(0xFF282C35),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delete),
                                        contentDescription = "Delete All",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Delete All Data",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = "Delete All",
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dark Mode Toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF282C35),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dark Mode",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Switch(
                                checked = darkMode,
                                onCheckedChange = { darkMode = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF8B5CF6),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign Out Button
                    Button(
                        onClick = { handleSignOut() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF282C35)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Sign Out",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)  // Match the background color
        ) {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { newTab ->
                    if (newTab != selectedTab) {
                        selectedTab = newTab
                        when (newTab) {
                            0 -> onNavigateBack()  // Dashboard
                            1 -> onNavigateToTransactions()  // Transactions
                            2 -> onBudgetClick()
                            // 3 is Settings, already here
                        }
                    }
                }
            )
        }
    }

    // Delete All Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Delete All Data") },
            text = { Text("Are you sure you want to delete all your transactions and budgets? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (currentUserId != null) {
                            val success = dbHelper.deleteAllUserData(currentUserId)
                            if (success) {
                                Toast.makeText(context, "All data deleted successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to delete data", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showDeleteAllDialog = false
                    }
                ) {
                    Text("Delete All", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    value: String? = null,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        color = Color(0xFF282C35),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color.White
                )
                if (value != null) {
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
} 