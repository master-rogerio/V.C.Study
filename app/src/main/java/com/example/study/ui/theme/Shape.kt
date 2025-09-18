package com.example.study.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Modern Shape System for Study App - Material Design 3
val Shapes = Shapes(
    // Extra Small - For small components like chips, badges
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small - For buttons, cards
    small = RoundedCornerShape(8.dp),
    
    // Medium - For larger cards, dialogs
    medium = RoundedCornerShape(12.dp),
    
    // Large - For bottom sheets, large components
    large = RoundedCornerShape(16.dp),
    
    // Extra Large - For full-screen components
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes for specific components
object StudyShapes {
    val flashcardShape = RoundedCornerShape(16.dp)
    val buttonShape = RoundedCornerShape(12.dp)
    val fabShape = RoundedCornerShape(16.dp)
    val bottomSheetShape = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val dialogShape = RoundedCornerShape(20.dp)
    val chipShape = RoundedCornerShape(16.dp)
    val progressBarShape = RoundedCornerShape(8.dp)
}