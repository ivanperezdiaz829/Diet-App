import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.R
import com.example.diet_app.model.GlobalData
import com.example.diet_app.model.Screen
import com.example.diet_app.viewModel.DietViewModel

@Composable
fun DietView(
    navController: NavController,
    dietViewModel: DietViewModel,
    imageResId: Int = R.drawable.healthy_icon, // Valor por defecto
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // Navegación simplificada
                GlobalData.mainDietUpdate(dietViewModel)
                navController.navigate(Screen.DietInterface.createRoute(dietViewModel.getDiet().dietId))
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Diet image",
                modifier = Modifier.size(64.dp)
            )

            Column {
                Text(
                    text = dietViewModel.getDiet().name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${dietViewModel.getDiet().duration} days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}