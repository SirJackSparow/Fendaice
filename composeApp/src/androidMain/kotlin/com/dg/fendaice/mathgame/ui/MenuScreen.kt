package com.dg.fendaice.mathgame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*

@Composable
fun MenuScreen(
    onCategorySelected: (ageGroup: String, topic: String) -> Unit
) {
    val categories = listOf(
        CategoryItem("Kids Easy", "KIDS_EASY", "ADDITION", Icons.Rounded.EmojiEmotions, Color(0xFFFFB300)),
        CategoryItem("Kids Medium", "KIDS_MEDIUM", "ADDITION", Icons.Rounded.AutoStories, Color(0xFFFB8C00)),
        CategoryItem("Kids Hard", "KIDS_HARD", "MULTIPLICATION", Icons.Rounded.Calculate, Color(0xFFF4511E)),
        CategoryItem("Teen Easy", "TEEN_EASY", "FRACTIONS", Icons.AutoMirrored.Rounded.MenuBook, Color(0xFF7CB342)),
        CategoryItem("Teen Hard", "TEEN_HARD", "ALGEBRA", Icons.Rounded.Functions, Color(0xFF43A047)),
        CategoryItem("Adult", "ADULT", "MIXED", Icons.Rounded.Psychology, Color(0xFF1E88E5))
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://lottie.host/8c0938aa-5e57-4498-8f7b-6d6eb713972c/eXsV4UNukX.json"))
            LottieAnimation(
                composition, 
                iterations = LottieConstants.IterateForever, 
                modifier = Modifier.size(screenHeight * 0.18f).padding(top = 16.dp)
            )

            Text(
                text = "Math Master",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = (screenWidth.value * 0.1f).sp
                )
            )
            
            Text(
                text = "Choose Your Challenge",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = (screenWidth.value * 0.045f).sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category) {
                        onCategorySelected(category.ageGroup, category.topic)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(32.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(18.dp),
                color = category.color,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = category.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = category.topic.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

data class CategoryItem(
    val name: String,
    val ageGroup: String,
    val topic: String,
    val icon: ImageVector,
    val color: Color
)
