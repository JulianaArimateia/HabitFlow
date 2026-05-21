package com.habitflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.habitflow.ui.theme.*

fun categoryColor(categoria: String): Color = when (categoria) {
    "Saúde"        -> CatSaude
    "Produtividade" -> CatProd
    "Bem-estar"    -> CatBemEstar
    "Hidratação"   -> CatHidrat
    "Leitura"      -> CatLeitura
    "Sono"         -> CatSono
    "Academia"     -> CatAcademia
    else           -> CatOutro
}

fun categoryIcon(categoria: String): ImageVector = when (categoria) {
    "Saúde"        -> Icons.Default.Favorite
    "Produtividade" -> Icons.Default.Bolt
    "Bem-estar"    -> Icons.Default.SelfImprovement
    "Hidratação"   -> Icons.Default.WaterDrop
    "Leitura"      -> Icons.Default.MenuBook
    "Sono"         -> Icons.Default.Bedtime
    "Academia"     -> Icons.Default.FitnessCenter
    else           -> Icons.Default.Star
}
