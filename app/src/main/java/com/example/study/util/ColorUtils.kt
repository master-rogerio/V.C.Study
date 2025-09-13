package com.example.study.util

import android.graphics.Color
import java.util.Random

object ColorUtils {
    private val random = Random()
    
    // Cores predefinidas para garantir uma boa aparência
    private val predefinedColors = listOf(
        Color.parseColor("#FF5722"), // Deep Orange
        Color.parseColor("#2196F3"), // Blue
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#9C27B0"), // Purple
        Color.parseColor("#FF9800"), // Orange
        Color.parseColor("#009688"), // Teal
        Color.parseColor("#E91E63"), // Pink
        Color.parseColor("#3F51B5"), // Indigo
        Color.parseColor("#795548"), // Brown
        Color.parseColor("#607D8B")  // Blue Grey
    )
    
    /**
     * Gera uma cor consistente baseada em uma string.
     * A mesma string sempre gerará a mesma cor.
     */
    fun getColorFromString(input: String): Int {
        // Se a string estiver vazia, retorna uma cor aleatória
        if (input.isEmpty()) {
            return predefinedColors[random.nextInt(predefinedColors.size)]
        }
        
        // Usa o hashCode da string para selecionar uma cor predefinida
        val index = Math.abs(input.hashCode()) % predefinedColors.size
        return predefinedColors[index]
    }
    
    /**
     * Retorna uma versão mais clara da cor para usar como fundo
     */
    fun getLighterColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = hsv[1] * 0.3f // Reduz a saturação para 30%
        hsv[2] = Math.min(hsv[2] * 1.5f, 1.0f) // Aumenta o brilho em 50%, mas não mais que 100%
        return Color.HSVToColor(hsv)
    }
} 