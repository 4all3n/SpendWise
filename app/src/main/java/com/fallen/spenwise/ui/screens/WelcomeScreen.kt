package com.fallen.spenwise.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.fallen.spenwise.ui.components.AnimatedLogo
import com.fallen.spenwise.ui.theme.SpendWiseTheme

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit = {},
    onSignInClick: () -> Unit = onGetStartedClick
) {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(500),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(700),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
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
                .padding(24.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Container with animation
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedLogo(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    isAnimated = isVisible
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // App Name with animation
            Text(
                text = "SpendWise",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { 
                    this.alpha = alpha 
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Description with animation
            Text(
                text = "Track your expenses smartly,\nsave wisely",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .graphicsLayer { 
                        this.alpha = alpha 
                    }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Get Started Button with animation
            Button(
                onClick = {
                    Log.d("WelcomeScreen", "Get Started button clicked")
                    onGetStartedClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer { 
                        this.alpha = alpha 
                        scaleX = scale
                        scaleY = scale
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8D5CF5)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Text with animation
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .graphicsLayer { 
                        this.alpha = alpha 
                    }
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Text(
                    text = "Sign In",
                    color = Color(0xFF8D5CF5),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { 
                        Log.d("WelcomeScreen", "Sign In text clicked")
                        onSignInClick()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    SpendWiseTheme {
        WelcomeScreen()
    }
} 