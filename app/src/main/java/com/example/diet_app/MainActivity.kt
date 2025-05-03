package com.example.diet_app

import DietPlansScreen
import GenerateMealPlanWithInputsScreen
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diet_app.model.FoodType
import com.example.diet_app.model.GlobalData
import com.example.diet_app.model.Screen
import com.example.diet_app.screenActivities.*
import com.example.diet_app.screenActivities.components.navigateAndClearStack
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val foodViewModel: FoodViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Forzar íconos oscuros en la barra de estado
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val dietJson = """
{
  "dieta": [
    [
      {
        "name": "Manzana",
        "calories": 52,
        "carbohydrates": 14,
        "protein": 0.3,
        "fat": 0.2,
        "sugar": 10,
        "salt": 0,
        "price": 0.5
      },
      {
        "name": "Yogur",
        "calories": 59,
        "carbohydrates": 3.6,
        "protein": 10,
        "fat": 1.5,
        "sugar": 4.7,
        "salt": 0.1,
        "price": 0.8
      }
    ],
    [
      {
        "name": "Pasta",
        "calories": 131,
        "carbohydrates": 25,
        "protein": 5,
        "fat": 1.1,
        "sugar": 1,
        "salt": 0.3,
        "price": 1.2
      },
      {
        "name": "Ensalada",
        "calories": 33,
        "carbohydrates": 6,
        "protein": 1.5,
        "fat": 0.5,
        "sugar": 2,
        "salt": 0.2,
        "price": 1.0
      },
      {
        "name": "Agua",
        "calories": 0,
        "carbohydrates": 0,
        "protein": 0,
        "fat": 0,
        "sugar": 0,
        "salt": 0,
        "price": 0
      }
    ],
    [
      {
        "name": "Sopa",
        "calories": 80,
        "carbohydrates": 10,
        "protein": 3,
        "fat": 2,
        "sugar": 1,
        "salt": 0.4,
        "price": 1.0
      },
      {
        "name": "Pan integral",
        "calories": 69,
        "carbohydrates": 12,
        "protein": 2.5,
        "fat": 1.2,
        "sugar": 1,
        "salt": 0.3,
        "price": 0.6
      }
    ]
  ]
}
""".trimIndent()
        setContent {
            /*
            createUser(
                email = "frikazoA2@gmail.es",
                password = "superSeguro.123",
                physicalActivity = "STAY_HEALTHY",
                sex = 1,
                birthday = "1995-06-15",
                height = 168,
                weight = 60,
                context = LocalContext.current,
                onResult = {}
            )*/

            DietApp(LocalContext.current, userViewModel, foodViewModel)
            //getUserByEmail("Janesdoe@gmail.es", LocalContext.current, onResult = {})
            /*
            DietInterface(
                navController = rememberNavController(),
                dietViewModel = dietViewModel
            )
            */
            //CalendarScreen(onNavigateBack = { finish() }, onSkip = { finish() }, onNext = {})
            //GraphicFrame(dietJson, onNavigateBack = {finish()})
            //TargetWeightSelectionScreen(onNavigateBack = { finish() }, onSkip = { finish() }, onNext = {})
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DietApp(applicationContext: Context, userViewModel: UserViewModel, newFood: FoodViewModel) {
    // Usamos NavController para manejar la navegación
    val navController = rememberNavController()

    // Configuración de la navegación entre pantallas
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(route = Screen.Home.route
        ) {HomePageFrame(navController, userViewModel)}

        composable(route = Screen.Goal.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {GoalSelectionScreen(
            onSkip = { navController.navigate("welcome") },
            onNavigateBack = { navController.popBackStack() },
            onNext = {
                /*
                createUser(
                    email = userViewModel.getUser().email,
                    password = userViewModel.getUser().password,
                    physicalActivity = userViewModel.getUser().goal.toString(),
                    sex = 0,
                    birthday = "1995-06-15",
                    height = userViewModel.getUser().height,
                    weight = userViewModel.getUser().currentWeight.toInt(),
                    context = applicationContext,
                    onResult = {}
                )
                userViewModel.updateUser(goal = it)
                navController.navigate(Screen.Home.route)
                printUserInfo(userViewModel)
                */

            },
        )}

        composable(route = Screen.Sex.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            SexSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    userViewModel.updateUser(sex = it)
                    navController.navigate(Screen.Age.route)
                    printUserInfo(userViewModel)
                }
            )
        }

        composable(route = Screen.Age.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            AgeSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    userViewModel.updateUser(age = it)
                    navController.navigate(Screen.Height.route)
                    printUserInfo(userViewModel)
                } // O la siguiente pantalla que corresponda
            )
        }

        composable(route = Screen.Height.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            HeightSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    // userViewModel.updateUser(height = it)
                    navController.navigate(Screen.CurrentWeight.route)
                    userViewModel.updateUser(height = it)
                    printUserInfo(userViewModel)
                } // O la siguiente pantalla que corresponda
            )
        }

        composable(route = Screen.CurrentWeight.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            CurrentWeightSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    userViewModel.updateUser(currentWeight = it)
                    navController.navigate(Screen.Goal.route)
                    printUserInfo(userViewModel)
                } // O la siguiente pantalla que corresponda
            )
        }

        composable(route = Screen.Meals.route
        ) {

            DietPlansScreen(
                navController,
                diets = listOf(GlobalData.mainDiet)
            )
        }

        composable(route = Screen.Welcome.route,
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            InputDesign(
                onNext = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(
            route = Screen.Login.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            LoginScreen(
                userViewModel,
                onLoginSuccess = {
                    userViewModel.updateUser(name = it.getUser().name)
                    navController.navigate(Screen.Home.route)
                },
                onRegisterSuccess = {
                    userViewModel.updateUser(name = it.getUser().name)
                    navController.navigate(Screen.Sex.route)
                },
            )
        }

        composable(route = Screen.Password.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            ChangePasswordScreen(navController, userViewModel)
        }

        composable(route = Screen.FoodList.route,
        ) {
            FoodListViewScreen(navController, listOf(GlobalData.food1, GlobalData.food2, GlobalData.food3))
        }

        composable(route = Screen.AddFood.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            AddNewFoodScreen(navController, onNavigateBack = { navController.popBackStack() },
                onNext = {
                newFood.updateFood(
                    protein = it.getFood().protein,
                    fats = it.getFood().fats,
                    sugar = it.getFood().sugar,
                    salt = it.getFood().salt,
                    carbohydrates = it.getFood().carbohydrates,
                    calories = it.getFood().calories,
                    price = it.getFood().price,
                    foodVariants = it.getFood().foodVariants
                )
                printFoodInfo(newFood)
                navController.navigate(Screen.NewFoodType.route)
            })
        }

        composable(route = Screen.NewFoodType.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            FoodTypeSelectionScreen(navController,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    newFood.updateFood(foodTypes = it)
                    printFoodInfo(newFood)
                    navController.navigate(Screen.NewFoodSummary.route)
                }
            )
        }

        composable(route = Screen.NewFoodSummary.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            NewFoodSummaryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    newFood.updateFood(name = it.getFood().name)
                    printFoodInfo(newFood)
                    navController.navigate(Screen.FoodDetail.route)
                    //llamar a la API para guardar comida
                },
                foodViewModel = newFood
            )
        }

        composable(route = Screen.GenerateMealPlanWithData.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            GenerateMealPlanWithDataScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    if (it) {

                        navController.navigate(Screen.TypeOfDietSelection.route)
                    } else {
                        navController.navigate(Screen.Sex.route)
                    }
                },
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable(route = Screen.TypeOfDietSelection.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            DietSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    navController.navigate(Screen.DietDurationSelection.route)
                },
            )
        }

        composable(route = Screen.DietDurationSelection.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            DietDurationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    // llamada a la API para generar la dieta
                    var dietViewModel = GlobalData.mainDiet
                    navController.navigate(Screen.DietInterface.createRoute(dietViewModel.getDiet().dietId))
                    //navController.navigateAndClearStack(Screen.Home.route)
                },
            )
        }

        composable(route = Screen.FoodDetail.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            FoodDetailScreen(
                foodViewModel = newFood,
                onNavigateBack = { navController.navigateAndClearStack(Screen.Home.route) },

            )
        }

        composable(route = Screen.Settings.route
        ) {
            SettingsScreen(navController, userViewModel)
        }

        // Así debe quedar tu composable (copia exactamente esto)
        composable(
            route = Screen.DietInterface.route,
            arguments = listOf(
                navArgument("dietId") {
                    type = NavType.StringType // o IntType si usas números
                    nullable = false // Cambia a true si puede ser nulo
                }
            )
        ) { entry ->
            // Esto es clave: así se obtiene el argumento correctamente
            val dietId = entry.arguments?.getString("dietId") ?: ""
            var dietViewModel = getDietViewModelById(dietId)
            DietInterface(
                dietViewModel = dietViewModel,
                navController = navController,// Pasa el ID a tu pantalla
            )
        }

        composable(
            route = Screen.GraphicFrame.route,
            arguments = listOf(
                navArgument("dietId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            val dietId = entry.arguments?.getString("dietId") ?: ""
            GraphicFrame(
                navController = navController,
                dietId = dietId
            )
        }

        composable(route = Screen.Home.route
        ) {
            HomePageFrame(navController, userViewModel)
        }

        composable(route = Screen.SelectTypeOfDietGeneration.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            DietGeneratorSelectionScreen(
                navController = navController,
                userViewModel = userViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    if (it == DietGeneratorType.USER_DATA) {
                        navController.navigate(Screen.GenerateMealPlanWithData.route)
                    } else {
                        navController.navigate(Screen.GenerateMealPlanWithInputs.route)
                    }
                }
            )
        }

        composable(route = Screen.GenerateMealPlanWithInputs.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            GenerateMealPlanWithInputsScreen(
                context = applicationContext,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    val planJson = getPrefs(applicationContext, "name", 0, 0, 7)
                    val foodList = parseFoodsFromJson(planJson)
                    Log.d("FoodList", foodList.toString())
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        composable(route = Screen.Calendar.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            CalendarScreen(
                onSkip = { },
                onNavigateBack = { navController.popBackStack() },
                onNext = { }
            )
        }

    }
}

@Composable
fun WelcomeScreen(navController: NavController, userViewModel: UserViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8BC34A)), // Fondo verde
        contentAlignment = Alignment.Center
    ) {
        // Contenido principal de la pantalla
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Si el usuario está autenticado (currentUser no está vacío)
            var currentUser = userViewModel.getUser().name
            /*
            if (currentUser) {
                // Símbolo en la esquina superior izquierda y mensaje de bienvenida
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Margen superior
                ) {
                    // Mensaje "Bienvenido gloton" en el centro superior
                    Text(
                        text = "¡Bienvenido $currentUser!",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    // Símbolo en la esquina superior izquierda
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape)
                            .align(Alignment.TopStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓", // Símbolo
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Green)
                        )
                    }
                }
            } else {
                Text(
                    text = "¡Bienvenido a la aplicación de dieta!",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    modifier = Modifier.padding(16.dp)
                )
            }*/
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    // Navegar a la pantalla del formulario de la dieta
                    navController.navigate("diet_form")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Ir al formulario")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("basal_metabolism") }) {
                Text("Calcular Gasto Energético Basal")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("maintenance_calories") }) {
                Text("Calcular Calorías de Mantenimiento")
            }
        }
    }
}

@Composable
fun DietForm(userViewModel: UserViewModel, context: Context) {
    var maxCarbohydrates by remember { mutableStateOf("") }
    var maxSugar by remember { mutableStateOf("") }
    var minEnergy by remember { mutableStateOf("") }
    var maxEnergy by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxSalt by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Configurar valores nutricionales", style = MaterialTheme.typography.titleLarge)

        InputField(label = "Carbs máximas", value = maxCarbohydrates) { maxCarbohydrates = it }
        InputField(label = "Azúcar máximas", value = maxSugar) { maxSugar = it }
        InputField(label = "Energía mínimas", value = minEnergy) { minEnergy = it }
        InputField(label = "Energía máximas", value = maxEnergy) { maxEnergy = it }
        InputField(label = "Proteina mínimas", value = minProtein) { minProtein= it }
        InputField(label = "Sal máxima (g)", value = maxSalt) { maxSalt = it }
        InputField(label = "Grasa máxima (g)", value = maxFat) { maxFat = it }
        InputField(label = "Presupuesto", value = budget) { budget = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Convertir todos los valores a Double, ignorando los que no sean válidos
            val numericValues = listOf(maxCarbohydrates, maxSugar,
                minEnergy, maxEnergy, minProtein, maxSalt, maxFat, budget)
                .map { it.replace(",", ".") }  // Asegura el formato correcto de decimales
                .mapNotNull { it.toDoubleOrNull() }  // Convierte String a Double si es válido

            Log.d("DietForm", "Valores convertidos a Double: $numericValues")

            if (numericValues.size == 8) { // Asegurar que todos los valores sean numéricos
                sendDataToServer(numericValues, context) { response ->
                    result = response
                }
            } else {
                result = "Error: Ingresa valores numéricos válidos"
            }
        }) {
            Text("Generar dieta")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Resultado: $result", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun BasalMetabolismScreen(userViewModel: UserViewModel) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField("Peso (kg)", weight) { weight = it }
        InputField("Altura (cm)", height) { height = it }
        InputField("Edad (años)", age) { age = it }
        InputField("Género (M/F)", gender) { gender = it }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            result = calculateBasalMetabolicRate(weight, height, age, gender)
        }) {
            Text("Calcular")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = result)
    }
    // viewModel.updateUser(basalMetabolism = result)
}

fun calculateBasalMetabolicRate(weight: String, height: String, age: String, gender: String): String {
    val w = weight.toDoubleOrNull() ?: return "Peso no válido"
    val h = height.toDoubleOrNull() ?: return "Altura no válida"
    val a = age.toDoubleOrNull() ?: return "Edad no válida"

    return if (gender.equals("M", ignoreCase = true)) {
        val bmr = 88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)
        "Gasto Energético Basal: ${String.format("%.2f", bmr)} kcal/día"
    } else if (gender.equals("F", ignoreCase = true)) {
        val bmr = 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
        "Gasto Energético Basal: ${String.format("%.2f", bmr)} kcal/día"
    } else {
        "Género no válido"
    }
}

@Composable
fun MaintenanceCaloriesScreen(userViewModel: UserViewModel) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var physicalActivityLevel by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField("Peso (kg)", weight) { weight = it }
        InputField("Altura (cm)", height) { height = it }
        InputField("Edad (años)", age) { age = it }
        InputField("Género (M/F)", gender) { gender = it }
        InputField("Nivel de actividad física (1/2/3/4/5)", physicalActivityLevel) { physicalActivityLevel = it }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            result = calculateMaintenanceCalories(weight, height, age, gender, physicalActivityLevel)
        }) {
            Text("Calcular")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = result)
    }
    //viewModel.updateUser(maintenanceCalories = result)
}

fun calculateMaintenanceCalories(weight: String, height: String, age: String, gender: String, physicalActivityLevel: String): String {
    val w = weight.toDoubleOrNull() ?: return "Peso no válido"
    val h = height.toDoubleOrNull() ?: return "Altura no válida"
    val a = age.toDoubleOrNull() ?: return "Edad no válida"
    val pal = physicalActivityLevel.toDoubleOrNull() ?: return "Nivel de actividad física no válido"
    val physicalActivityCoefficients = arrayOf(1.2, 1.375,1.55, 1.725, 1.9)
    return if (gender.equals("M", ignoreCase = true)) {
        val mc = physicalActivityCoefficients[pal.toInt()] * (66 + (13.7 * w) + (5 * h) - (6.8 * a))
        "Calorías de mantenimiento: ${String.format("%.2f", mc)} kcal/día"
    } else if (gender.equals("F", ignoreCase = true)) {
        val mc = physicalActivityCoefficients[pal.toInt()] * (665 + (9.6 * w) + (1.8 * h) - (4.7 * a))
        "Calorías de mantenimiento: ${String.format("%.2f", mc)} kcal/día"
    } else {
        "Género no válido"
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

/*
fun sendDataToServer(values: List<Double>, onResult: (String) -> Unit) {
    val client = OkHttpClient()
    val url = "http://10.0.2.16:8000/calculate"

    val json = JSONObject()
    json.put("values", values)

    Log.d("DietForm", "Enviando valores al servidor: $values")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("DietForm", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    if (jsonResponse.has("error")) {
                        onResult("Error: ${jsonResponse.getString("error")}")
                    } else {
                        val breakfast = jsonResponse.getString("breakfast")
                        val lunch = jsonResponse.getJSONArray("lunch")
                        val dinner = jsonResponse.getJSONArray("dinner")

                        // Convierte JSONArray a String
                        val lunchList = (0 until lunch.length()).map { lunch.getString(it) }
                        val dinnerList = (0 until dinner.length()).map { dinner.getString(it) }

                        val resultString = """
                            🍳 **Desayuno:** $breakfast
                            
                            🥗 **Almuerzo:** 
                            - ${lunchList.joinToString("\n- ")}
                            
                            🍽 **Cena:** 
                            - ${dinnerList.joinToString("\n- ")}
                        """.trimIndent()

                        onResult(resultString)
                    }
                } catch (e: Exception) {
                    onResult("Error al procesar la respuesta")
                }
            }
        }
    })
}
*/

fun checkDatabaseConnection() {
    val db = FirebaseFirestore.getInstance()

    // Intenta realizar una consulta simple a la base de datos
    db.collection("testConnection")
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                Log.d("FirestoreConnection", "Conexión exitosa: la colección está vacía o no existe.")
            } else {
                Log.d("FirestoreConnection", "Conexión exitosa: datos encontrados en la colección.")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("FirestoreConnection", "Error al conectar con la base de datos: ${exception.message}")
        }
}

fun fetchAllUsers() {
    try {
        val db = FirebaseFirestore.getInstance()

        // Accede a la colección "users"
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FirestoreUsers", "No se encontraron usuarios en la base de datos.")
                } else {
                    for (document in documents) {
                        // Muestra los datos de cada documento (usuario)
                        Log.d(
                            "FirestoreUsers",
                            "Usuario ID: ${document.id}, Datos: ${document.data}"
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreUsers", "Error al obtener usuarios: ${exception.message}")
            }
    } catch (e: SecurityException) {
        Log.e("MyAppTag", "SecurityException: ${e.message}", e) // Log with the exception details
    } catch (e: Exception){
        Log.e("MyAppTag", "General exception", e) //Log general exception
    }
}

fun printUserInfo(userViewModel: UserViewModel) {
    Log.d("Resumen: ",
        "Nombre: ${userViewModel.getUser().name},\n" +
        " Edad: ${userViewModel.getUser().age},\n" +
        " Sexo: ${userViewModel.getUser().sex},\n" +
        " Altura: ${userViewModel.getUser().height}\n" +
        " Peso: ${userViewModel.getUser().currentWeight}\n" +
        " Objetivo de dieta: ${userViewModel.getUser().goal}"
    )
}

fun printFoodInfo(foodViewModel: FoodViewModel) {
    Log.d("Resumen: ",
        "Nombre: ${foodViewModel.getFood().name},\n" +
        "Protein: ${foodViewModel.getFood().protein}, \n" +
        "Fats: ${foodViewModel.getFood().fats}, \n" +
        "Sugar: ${foodViewModel.getFood().sugar}, \n" +
        "Salt: ${foodViewModel.getFood().salt}, \n" +
        "Carbohydrates: ${foodViewModel.getFood().carbohydrates}, \n" +
        "Calories: ${foodViewModel.getFood().calories}, \n" +
        "Price: ${foodViewModel.getFood().price}, \n" +
        "Food Variants: ${foodViewModel.getFood().foodVariants}, \n" +
        "Food Types: ${foodViewModel.getFood().foodTypes}"
    )
}

fun getDietViewModelById(id: String): DietViewModel {
    // Implementa tu lógica para obtener el ViewModel correcto
    // Esto puede venir de tu repositorio o ViewModel principal
    var foodViewModel = FoodViewModel()
    var foodViewModel2 = FoodViewModel()
    var foodViewModel3 = FoodViewModel()
    foodViewModel.updateFood(name = "Croissant", foodTypes = setOf(FoodType.LIGHT_MEAL))
    foodViewModel2.updateFood(name = "Rice", foodTypes = setOf(FoodType.MAIN_DISH))
    foodViewModel3.updateFood(name = "Sandwich", foodTypes = setOf(FoodType.LIGHT_MEAL, FoodType.SIDE_DISH))
    var dietViewModel = DietViewModel()
    var dietDayViewModel = DietDayViewModel()
    var dietDayViewModel2 = DietDayViewModel()
    dietDayViewModel.updateDietDay(foods = listOf(foodViewModel, foodViewModel2, foodViewModel3))
    dietDayViewModel2.updateDietDay(foods = listOf(foodViewModel3, foodViewModel2, foodViewModel))
    dietViewModel.updateDiet(name = "Dieta 1", duration = 2, diets = listOf(dietDayViewModel, dietDayViewModel2), dietId = "1")
    return dietViewModel
}