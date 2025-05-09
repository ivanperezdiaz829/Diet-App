import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.screenActivities.components.BackButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.diet_app.User
import com.example.diet_app.create_diet_with_inputs

@Composable
fun GenerateMealPlanWithInputsScreen(
    context: Context,
    userId: Int,
    onNavigateBack: () -> Unit,
    onNext: (List<Any>) -> Unit
) {
    var minCarbohydrates by remember { mutableStateOf("") }
    var maxSugar by remember { mutableStateOf("") }
    var minEnergy by remember { mutableStateOf("") }
    var maxEnergy by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxSalt by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedDiet by remember { mutableStateOf(FoodVariant.REGULAR) }
    var planName by remember { mutableStateOf("") }
    var formErrors by remember { mutableStateOf<List<FormError>>(emptyList()) }

    Column(modifier = Modifier
        .statusBarsPadding()
        .padding(horizontal = 16.dp)
        .padding(vertical = 16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        BackButton(onNavigateBack)

        Text("Planificador de Dieta", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 12.dp))

        if (formErrors.isNotEmpty()) {
            formErrors.forEach {
                Text(it.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 2.dp))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                NutritionInputField("Carbs mín.", minCarbohydrates) { minCarbohydrates = it }
                NutritionInputField("Azúcar máx.", maxSugar) { maxSugar = it }
                NutritionInputField("Energía mín.", minEnergy) { minEnergy = it }
                NutritionInputField("Energía máx.", maxEnergy) { maxEnergy = it }
            }

            Column(modifier = Modifier.weight(1f)) {
                NutritionInputField("Prot. mín.", minProtein) { minProtein = it }
                NutritionInputField("Sal máx.", maxSalt) { maxSalt = it }
                NutritionInputField("Grasa máx.", maxFat) { maxFat = it }
            }
        }

        GeneralInputField("Días", days) { days = it }
        GeneralInputField("Presupuesto", budget) { budget = it }
        GeneralInputField("Nombre plan", planName) { planName = it }
        DietPreferenceSwitchesCompact(selectedDiet) { selectedDiet = it }

        Button(
            onClick = {
                val result = validateInputs(
                    minCarbohydrates, maxSugar, minEnergy, maxEnergy,
                    minProtein, maxSalt, maxFat, days, budget, planName
                )
                formErrors = if (result.isValid) {
                    val dietType = when (selectedDiet) {
                        FoodVariant.REGULAR -> 1.0
                        FoodVariant.VEGAN -> 2.0
                        FoodVariant.VEGETARIAN -> 3.0
                        FoodVariant.CELIAC -> 4.0
                        FoodVariant.HALAL -> 5.0
                    }
                    val formData = listOf(
                        minCarbohydrates.toDouble(),
                        maxSugar.toDouble(),
                        minEnergy.toDouble(),
                        maxEnergy.toDouble(),
                        minProtein.toDouble(),
                        maxSalt.toDouble(),
                        maxFat.toDouble(),
                        days.toDouble(),
                        budget.toDouble(),
                        dietType,
                        planName,
                        userId
                    )
                    onNext(formData)
                    emptyList<FormError>()
                } else {
                    result.errors
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = Color.White
            )
        ) {
            Text("Generar", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun NutritionInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) onValueChange(it)
        },
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        textStyle = MaterialTheme.typography.bodySmall,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun GeneralInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (label != "Nombre plan") {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) onValueChange(it)
            } else {
                onValueChange(it)
            }
        },
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        textStyle = MaterialTheme.typography.bodySmall,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun DietPreferenceSwitchesCompact(selectedDiet: FoodVariant, onDietSelected: (FoodVariant) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Tipo de dieta", style = MaterialTheme.typography.bodyMedium)
        DietSwitchCompact("Normal", selectedDiet == FoodVariant.REGULAR) { onDietSelected(FoodVariant.REGULAR) }
        DietSwitchCompact("Vegana", selectedDiet == FoodVariant.VEGAN) { onDietSelected(FoodVariant.VEGAN) }
        DietSwitchCompact("Vegetariana", selectedDiet == FoodVariant.VEGETARIAN) { onDietSelected(FoodVariant.VEGETARIAN) }
        DietSwitchCompact("Celíaca", selectedDiet == FoodVariant.CELIAC) { onDietSelected(FoodVariant.CELIAC) }
        DietSwitchCompact("Halal", selectedDiet == FoodVariant.HALAL) { onDietSelected(FoodVariant.HALAL) }
    }
}
@Composable
private fun DietSwitchCompact(label: String, checked: Boolean, onCheckedChange: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Switch(
            checked = checked,
            onCheckedChange = { if (!checked) onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}

private fun validateInputs(
    minCarbs: String,
    maxSugar: String,
    minEnergy: String,
    maxEnergy: String,
    minProtein: String,
    maxSalt: String,
    maxFat: String,
    days: String,
    budget: String,
    planName: String
): ValidationResult {
    val errors = mutableListOf<FormError>()
    fun addError(field: String, message: String) = errors.add(FormError(field, message))

    if (minCarbs.isEmpty()) addError("minCarbohydrates", "Carbohidratos mínimos")
    if (maxSugar.isEmpty()) addError("maxSugar", "Azúcar máxima")
    if (minEnergy.isEmpty()) addError("minEnergy", "Energía mínima")
    if (maxEnergy.isEmpty()) addError("maxEnergy", "Energía máxima")
    if (minProtein.isEmpty()) addError("minProtein", "Proteína mínima")
    if (maxSalt.isEmpty()) addError("maxSalt", "Sal máxima")
    if (maxFat.isEmpty()) addError("maxFat", "Grasa máxima")
    if (days.isEmpty()) addError("days", "Días")
    if (budget.isEmpty()) addError("budget", "Presupuesto (€)")
    if (planName.isEmpty()) addError("planName", "Nombre del plan")
    if (errors.isNotEmpty()) return ValidationResult(false, errors)

    // Validar números
    if (minCarbs.toDoubleOrNull() == null) addError("minCarbohydrates", "Valor inválido")
    if (maxSugar.toDoubleOrNull() == null) addError("maxSugar", "Valor inválido")
    if (minEnergy.toDoubleOrNull() == null) addError("minEnergy", "Valor inválido")
    if (maxEnergy.toDoubleOrNull() == null) addError("maxEnergy", "Valor inválido")
    if (minProtein.toDoubleOrNull() == null) addError("minProtein", "Valor inválido")
    if (maxSalt.toDoubleOrNull() == null) addError("maxSalt", "Valor inválido")
    if (maxFat.toDoubleOrNull() == null) addError("maxFat", "Valor inválido")
    if (days.toIntOrNull() == null) addError("days", "Días inválidos")
    if (budget.toDoubleOrNull() == null) addError("budget", "Presupuesto inválido")

    if (errors.isNotEmpty()) return ValidationResult(false, errors)

    // Validar positivos
    if (minCarbs.toDouble() <= 0) addError("minCarbohydrates", "Debe ser positivo")
    if (maxSugar.toDouble() <= 0) addError("maxSugar", "Debe ser positivo")
    if (minEnergy.toDouble() <= 0) addError("minEnergy", "Debe ser positivo")
    if (maxEnergy.toDouble() <= 0) addError("maxEnergy", "Debe ser positivo")
    if (minProtein.toDouble() <= 0) addError("minProtein", "Debe ser positivo")
    if (maxSalt.toDouble() <= 0) addError("maxSalt", "Debe ser positivo")
    if (maxFat.toDouble() <= 0) addError("maxFat", "Debe ser positivo")
    if (days.toInt() <= 0) addError("days", "Debe ser al menos 1 día")
    if (budget.toDouble() <= 0) addError("budget", "Debe ser positivo")
    if (planName.length < 4) addError("planName", "Debe contener al menos 4 caracteres")
    if (minEnergy.toDouble() > maxEnergy.toDouble()) {
        addError("minEnergy", "No puede ser mayor que energía máxima")
        addError("maxEnergy", "No puede ser menor que energía mínima")
    }

    return ValidationResult(errors.isEmpty(), errors)
}

data class FormError(val field: String, val message: String)
data class ValidationResult(val isValid: Boolean, val errors: List<FormError>)

