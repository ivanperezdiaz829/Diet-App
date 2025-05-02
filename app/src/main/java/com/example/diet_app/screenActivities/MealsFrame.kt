import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diet_app.R
import com.example.diet_app.screenActivities.components.DietViewScreen
import com.example.diet_app.screenActivities.components.ToolBox
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.viewModel.DietViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        )
                {
                    // Título
                    Text(
                        text = "Meal plan",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                                // Pestañas
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = GrayGreen, // Color principal para las pestañas
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 4.dp,
                                color = GrayGreen // Color del indicador
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Current",
                                color = if (selectedTab == 1) GrayGreen else Color.Gray) },
                            icon = {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = "Current Plan",
                                    tint = if (selectedTab == 1) GrayGreen else Color.Gray
                                )
                            }
                        )

                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Other",
                                color = if (selectedTab == 1) GrayGreen else Color.Gray) },
                            icon = {
                                Icon(
                                    Icons.Filled.List,
                                    contentDescription = "Other Plans",
                                    tint = if (selectedTab == 1) GrayGreen else Color.Gray
                                )
                            }
                        )
                    }

                    // Contenido dinámico
                    when (selectedTab) {
                        0 -> CurrentMealPlanContent()
                        1 -> OtherMealPlanContent(navController)
                    }
                }
        ToolBox(navController)
    }
}

@Composable
fun CurrentMealPlanContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You don't have any current plan",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp))

        Button(
            onClick = { /* Navegación a pantalla de creación de dieta */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Generate",
                    modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Your Plan",
                    style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun OtherMealPlanContent(
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*
        Icon(
            Icons.Filled.Info,
            contentDescription = "Info",
            tint = GrayGreen,
            modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Other meal plans will appear here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
            */
        DietViewScreen(onClick = {  }, diet = DietViewModel(), image = R.drawable.healthy_icon)
        DietViewScreen(onClick = {  }, diet = DietViewModel(), image = R.drawable.healthy_icon)
        DietViewScreen(onClick = {  }, diet = DietViewModel(), image = R.drawable.healthy_icon)
    }
}