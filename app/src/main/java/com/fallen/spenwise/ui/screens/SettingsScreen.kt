package com.fallen.spenwise.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.fallen.spenwise.ui.components.BottomNavigationBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    onNavigate: (Int) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(3) }
    var darkMode by remember { mutableStateOf(true) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    // Function to handle complete sign out
    fun handleSignOut() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()
        
        // Sign out from Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
            
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
        googleSignInClient.revokeAccess()

        // Navigate to welcome screen
        onSignOut()
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
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF8B5CF6),
                                                    Color(0xFFB06AB3)
                                                )
                                            )
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Picture",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                                
                                // Edit Button
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    SettingsCard(
                        title = "Change Password",
                        showArrow = true,
                        onClick = { /* Handle password change */ }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Preferences Section
                    Text(
                        text = "Preferences",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

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
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { newTab ->
                    if (newTab != selectedTab) {
                        selectedTab = newTab
                        onNavigate(newTab)
                    }
                }
            )
        }
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