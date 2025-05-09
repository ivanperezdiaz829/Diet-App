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
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
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
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.GlobalData
import com.example.diet_app.model.Goal
import com.example.diet_app.model.Screen
import com.example.diet_app.model.getGoalInt
import com.example.diet_app.model.getSexInt
import com.example.diet_app.screenActivities.*
import com.example.diet_app.screenActivities.components.navigateAndClearStack
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Forzar íconos oscuros en la barra de estado
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            var foodViewModel = FoodViewModel()
            var userViewModel = UserViewModel()
            userViewModel.updateUser(id = 17)
            var dietViewModel = DietViewModel()

            DietApp(LocalContext.current, userViewModel, foodViewModel, dietViewModel)

        }
    }
}


@Composable
fun DietPlanScreen() {
    val context = LocalContext.current
    var responseText by remember { mutableStateOf("Cargando...") }

    // Llamar solo una vez al obtener la composición
    LaunchedEffect(Unit) {
        getUserDietPlansCompletePro(16, context) { jsonResponse ->
            responseText = jsonResponse // Actualizamos el estado
        }
    }

    // Mostrar el contenido en pantalla
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Respuesta del servidor:")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = responseText)
    }

    Log.d("DietPlanScreen", "Response text: $responseText")

}

@Composable
fun DietViewModelScreen(dietViewModel: DietViewModel) {
    // Mostrar el contenido en pantalla
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Respuesta del servidor:")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = dietViewModel.getDiet().diets[0].getDiet().foods[0].name)
    }
}

@Composable
fun Plates() {
    val context = LocalContext.current
    var responseText by remember { mutableStateOf("Cargando...") }

    // Llamar solo una vez al componer
    LaunchedEffect(Unit) {
        getUserPlatesPro(11, context) { jsonResponse ->
            responseText = jsonResponse // Actualizamos el estado
        }
    }

    // Mostrar el contenido en pantalla
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Platos del usuario:")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = responseText)
    }
}

@Composable
fun PlatesWithNullUserIdScreen() {
    val context = LocalContext.current
    var responseText by remember { mutableStateOf("Cargando...") }

    // Call the API once during composition
    LaunchedEffect(Unit) {
        getAllPlatesWhereUserIdIsNull(context) { result ->
            responseText = result.fold(
                onSuccess = { plates ->
                    if (plates.isEmpty()) {
                        "No se encontraron platos sin usuario asignado."
                    } else {
                        plates.joinToString("\n") { plate ->
                            "Plato: ${plate.name}, Calorías: ${plate.calories} kcal, " +
                                    "Vegano: ${if (plate.vegan == 1) "Sí" else "No"}, " +
                                    "Vegetariano: ${if (plate.vegetarian == 1) "Sí" else "No"}"
                        }
                    }
                },
                onFailure = { error ->
                    "Error al obtener platos: ${error.message}"
                }
            )
        }
    }

    // Display the content on screen
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Platos sin usuario asignado:")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = responseText)
    }
}

@Composable
fun PlatesForUserOrNullScreen(userId: Int = 11) {
    val context = LocalContext.current
    var responseText by remember { mutableStateOf("Cargando...") }

    // Call the API once during composition
    LaunchedEffect(Unit) {
        getAllPlatesWhereUserIdIsEitherUsersOrNull(userId, context) { result ->
            responseText = result.fold(
                onSuccess = { plates ->
                    if (plates.isEmpty()) {
                        "No se encontraron platos para el usuario $userId o sin usuario asignado."
                    } else {
                        plates.joinToString("\n") { plate ->
                            "Plato: ${plate.name}, Calorías: ${plate.calories} kcal, " +
                                    "Usuario: ${plate.user_id.ifEmpty { "Ninguno" }}, " +
                                    "Vegano: ${if (plate.vegan == 1) "Sí" else "No"}"
                        }
                    }
                },
                onFailure = { error ->
                    "Error al obtener platos: ${error.message}"
                }
            )
        }
    }

    // Display the content on screen
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Platos del usuario $userId o sin usuario asignado:")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = responseText)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DietApp(applicationContext: Context, userViewModel: UserViewModel, newFood: FoodViewModel, dietViewModel: DietViewModel) {
    // Usamos NavController para manejar la navegación
    val navController = rememberNavController()
    var dietJson by remember { mutableStateOf<String?>(null) }
    var dietViewModels by remember { mutableStateOf<MutableList<DietViewModel>>(mutableListOf()) }

    // Configuración de la navegación entre pantallas
    NavHost(navController = navController, startDestination = Screen.Meals.route) {

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
                Log.d("CurrentUser", userViewModel.getUser().toString())
                userViewModel.updateUser(goal = it)
                GlobalData.login(userViewModel)
                createUser(
                    email = userViewModel.getUser().email,
                    password = userViewModel.getUser().password,
                    physicalActivity = 4,
                    sex = getSexInt(userViewModel.getUser().sex),
                    birthday = userViewModel.getUser().age,
                    height = userViewModel.getUser().height,
                    weight = userViewModel.getUser().currentWeight.toInt(),
                    context = applicationContext,
                    goal = getGoalInt(userViewModel.getUser().goal),
                    onResult = {},
                )
                navController.navigate(Screen.Home.route)
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

            var showDiets by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                getUserDietPlansCompletePro(userViewModel.getUser().id, applicationContext) { jsonResponse ->
                    dietJson = jsonResponse
                    // Solo actualizamos los ViewModels cuando tengamos el JSON válido
                    if (jsonResponse.isNotEmpty()) {
                        val response = deserializeDietInformation(jsonResponse)
                        dietViewModels = response.toDietViewModels() // Ahora recibe una lista
                        showDiets = true
                    }
                }
            }

            // Muestra la pantalla solo cuando tengamos datos
            if (showDiets) {
                DietPlansScreen(
                    navController = navController,
                    diets = dietViewModels // Pasamos la lista completa
                )
            } else {
                LoadingScreen()
            }

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
                applicationContext,
                userViewModel,
                onLoginSuccess = {
                    userViewModel.updateUser(
                        name = it.getUser().name,
                        email = it.getUser().email,
                        password = it.getUser().password,
                        id = it.getUser().id,
                        age = it.getUser().age,
                        sex = it.getUser().sex,
                        height = it.getUser().height,
                        currentWeight = it.getUser().currentWeight,
                        goal = it.getUser().goal,)
                    GlobalData.login(userViewModel)
                    printUserInfo(userViewModel)
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
            ChangePasswordScreen(
                navController,
                userViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    navController.navigateAndClearStack(Screen.Welcome.route)
                })
        }

        composable(
            route = Screen.PhsysicalData.route,
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
            UpdatePhysicalDataScreen(
                navController = navController,
                userViewModel = userViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    updateUserPhysicalData(userViewModel.getUser().id.toInt(), it, applicationContext, onResult = {}, onError = {})
                    navController.navigateAndClearStack(Screen.Welcome.route)
                }
            )
        }


        composable(route = Screen.FoodList.route,
        ) {
            FoodListViewScreen(navController, GlobalData.foodList)
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
                userViewModel.getUser().foodList.add(newFood)
                GlobalData.foodList.add(newFood)
                var foodList = GlobalData.foodList
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
                    navController.navigate(Screen.DietInterface.createRoute(3.toString()))
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
            SettingsScreen(
                navController,
                userViewModel,
                onLogout = {
                    // userViewModel = UserViewModel()
                    navController.navigateAndClearStack(Screen.Welcome.route)
                }
            )
        }

        // Así debe quedar tu composable (copia exactamente esto)
        composable(route = Screen.DietInterface.route,
            arguments = listOf(
                navArgument("dietId") {
                    type = NavType.StringType // o IntType si usas números
                    nullable = false // Cambia a true si puede ser nulo
                }
            )
        ) { entry ->
            val dietId = entry.arguments?.getString("dietId") ?: ""
            var diet = getDietViewModelId(dietViewModels, dietId)

            if (diet != null) {
                DietInterface(
                    dietViewModel = diet,
                    navController = navController,// Pasa el ID a tu pantalla
                )
            } else {
                // Muestra un indicador de carga mientras esperamos
                LoadingScreen()
            }
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
                userId = userViewModel.getUser().id,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    /*
                    val planJson = getPrefs(applicationContext, "name", 0, 0, 7)
                    val foodList = parseFoodsFromJson(planJson)
                    Log.d("FoodList", foodList.toString())
                    navController.navigate(Screen.Home.route)
                    */
                    create_diet_with_inputs(it, applicationContext, onResult = {})
                    navController.navigate(Screen.Meals.route)

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

/*
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
*/

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
        " Email: ${userViewModel.getUser().email},\n" +
        " Contraseña: ${userViewModel.getUser().password},\n" +
        " Edad: ${userViewModel.getUser().age},\n" +
        " Sexo: ${userViewModel.getUser().sex},\n" +
        " Altura: ${userViewModel.getUser().height},\n" +
        " Peso: ${userViewModel.getUser().currentWeight},\n" +
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

fun printAllFoodIds(dietDays: List<DietDayViewModel>, tag: String = "FoodIds") {
    dietDays.forEachIndexed { index, dayViewModel ->
        val foodIds = dayViewModel.getDiet().foodsId.joinToString(", ")
        Log.d(tag, "Día ${index + 1} - IDs de comidas: [$foodIds]")
    }
}

fun getDiet(dietId: String): DietViewModel {
    var foodViewModel1 = FoodViewModel()
    foodViewModel1.updateFood(
        name = "Avena con frutas",
        calories = 300.0,
        protein = 10.0,
        fats = 5.0,
        sugar = 10.0,
        salt = 1.0,
        carbohydrates = 50.0,
        price = 10.0,
        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
        foodTypes = setOf(FoodType.PLATO_LIGERO)
    )
    var foodViewModel3 = FoodViewModel()
    foodViewModel3.updateFood(
        name = "Jugo de naranja",
        calories = 70.0,
        protein = 0.0,
        fats = 0.0,
        sugar = 15.0,
        salt = 0.2,
        carbohydrates = 20.0,
        price = 3.0,
        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
        foodTypes = setOf(FoodType.BEBIDA)
    )
    var foodViewModel2 = FoodViewModel()
    foodViewModel2.updateFood(
        name = "Tortilla de vegetales",
        calories = 250.0,
        protein = 8.0,
        fats = 3.0,
        sugar = 8.0,
        salt = 0.5,
        carbohydrates = 40.0,
        price = 8.0,
        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
        foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
    )
    var foodViewModel4 = FoodViewModel()
    foodViewModel4.updateFood(
        name = "Ensalada mixta",
        calories = 150.0,
        protein = 5.0,
        fats = 2.0,
        sugar = 5.0,
        salt = 0.3,
        carbohydrates = 30.0,
        price = 6.0,
        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
        foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
    )
    var foodViewModel5 = FoodViewModel()
    foodViewModel5.updateFood(
        name = "Agua con gas",
        calories = 0.0,
        protein = 0.0,
        fats = 0.0,
        sugar = 0.0,
        salt = 0.0,
        carbohydrates = 0.0,
        price = 1.0,
        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
        foodTypes = setOf(FoodType.BEBIDA)
    )
    var foodViewModel6 = FoodViewModel()
    foodViewModel6.updateFood(
        name = "Pechuga de pavo al horno",
        calories = 300.0,
        protein = 20.0,
        fats = 10.0,
        sugar = 5.0,
        salt = 2.0,
        carbohydrates = 0.0,
        price = 15.0,
        foodVariants = setOf(),
        foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
    )
    var foodViewModel7 = FoodViewModel()
    foodViewModel7.updateFood(
        name = "Smoothie de mango",
        calories = 100.0,
        protein = 1.0,
        fats = 0.0,
        sugar = 20.0,
        salt = 0.5,
        carbohydrates = 20.0,
        price = 5.0,
        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
        foodTypes = setOf(FoodType.BEBIDA)
    )
    var foodListDiet1 = listOf(foodViewModel1,foodViewModel3, foodViewModel2, foodViewModel4, foodViewModel5, foodViewModel6, foodViewModel7)

    // Desayuno ligero
    val breakfastViewModel = FoodViewModel().apply {
        updateFood(
            name = "Yogur griego con granola",
            calories = 280.0,
            protein = 15.0,  // Alto en proteína por el yogur griego
            fats = 8.0,
            sugar = 12.0,
            salt = 0.3,
            carbohydrates = 35.0,
            price = 12.0,
            foodVariants = setOf(FoodVariant.VEGETARIAN),
            foodTypes = setOf(FoodType.PLATO_LIGERO)
        )
    }

// Bebida matutina
    val greenTeaViewModel = FoodViewModel().apply {
        updateFood(
            name = "Té verde matcha",
            calories = 5.0,
            protein = 0.5,
            fats = 0.0,
            sugar = 0.0,
            salt = 0.0,
            carbohydrates = 1.0,
            price = 4.0,
            foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
            foodTypes = setOf(FoodType.BEBIDA)
        )
    }

// Plato principal almuerzo
    val salmonViewModel = FoodViewModel().apply {
        updateFood(
            name = "Salmón a la parrilla",
            calories = 350.0,
            protein = 25.0,  // Rico en proteínas
            fats = 18.0,    // Grasas saludables omega-3
            sugar = 2.0,
            salt = 1.2,
            carbohydrates = 5.0,
            price = 18.0,
            foodVariants = setOf(),
            foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
        )
    }

// Acompañamiento
    val quinoaViewModel = FoodViewModel().apply {
        updateFood(
            name = "Ensalada de quinoa",
            calories = 220.0,
            protein = 8.0,
            fats = 6.0,
            sugar = 3.0,
            salt = 0.4,
            carbohydrates = 35.0,
            price = 9.0,
            foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN, FoodVariant.CELIAC),
            foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
        )
    }

// Bebida refrescante
    val lemonadeViewModel = FoodViewModel().apply {
        updateFood(
            name = "Limonada natural",
            calories = 90.0,
            protein = 0.3,
            fats = 0.1,
            sugar = 22.0,
            salt = 0.1,
            carbohydrates = 24.0,
            price = 5.0,
            foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
            foodTypes = setOf(FoodType.BEBIDA)
        )
    }

// Cena principal
    val chickenViewModel = FoodViewModel().apply {
        updateFood(
            name = "Pollo al curry",
            calories = 320.0,
            protein = 28.0,
            fats = 12.0,
            sugar = 8.0,    // Por el curry y coco
            salt = 1.5,
            carbohydrates = 20.0,
            price = 16.0,
            foodVariants = setOf(),
            foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
        )
    }

// Bebida nocturna
    val chamomileTeaViewModel = FoodViewModel().apply {
        updateFood(
            name = "Infusión de manzanilla",
            calories = 2.0,
            protein = 0.0,
            fats = 0.0,
            sugar = 0.0,
            salt = 0.0,
            carbohydrates = 0.5,
            price = 3.0,
            foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
            foodTypes = setOf(FoodType.BEBIDA)
        )
    }

    var foodListDiet2 = listOf(breakfastViewModel, greenTeaViewModel, salmonViewModel, quinoaViewModel, lemonadeViewModel, chickenViewModel, chamomileTeaViewModel)

    var dayDietViewModel = DietDayViewModel()
    var dayDietViewModel2 = DietDayViewModel()

    dayDietViewModel.updateDietDay(foods = foodListDiet1)
    dayDietViewModel2.updateDietDay(foods = foodListDiet2)

    var dayDietLists = listOf(dayDietViewModel, dayDietViewModel2)
    var dietViewModel = DietViewModel()

    val mediterraneanDiet = listOf(
        // Desayuno
        FoodViewModel().apply {
            updateFood(
                name = "Tostada con tomate y aceite",
                calories = 250.0, protein = 6.0, fats = 10.0, sugar = 4.0,
                salt = 0.5, carbohydrates = 30.0, price = 8.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN, ),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },

        // Bebida mañana
        FoodViewModel().apply {
            updateFood(
                name = "Café con leche desnatada",
                calories = 50.0, protein = 4.0, fats = 1.0, sugar = 3.0,
                salt = 0.1, carbohydrates = 6.0, price = 3.5,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Almuerzo principal
        FoodViewModel().apply {
            updateFood(
                name = "Merluza a la romana",
                calories = 280.0, protein = 30.0, fats = 12.0, sugar = 1.0,
                salt = 1.0, carbohydrates = 15.0, price = 14.0,
                foodVariants = setOf(),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Acompañamiento
        FoodViewModel().apply {
            updateFood(
                name = "Escalivada de verduras",
                calories = 120.0, protein = 3.0, fats = 7.0, sugar = 8.0,
                salt = 0.3, carbohydrates = 12.0, price = 7.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },

        // Bebida tarde
        FoodViewModel().apply {
            updateFood(
                name = "Agua de Valencia light",
                calories = 80.0, protein = 0.5, fats = 0.0, sugar = 10.0,
                salt = 0.0, carbohydrates = 12.0, price = 6.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Cena
        FoodViewModel().apply {
            updateFood(
                name = "Solomillo de cerdo",
                calories = 320.0, protein = 25.0, fats = 15.0, sugar = 12.0,
                salt = 1.2, carbohydrates = 20.0, price = 16.0,
                foodVariants = setOf(),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Infusión noche
        FoodViewModel().apply {
            updateFood(
                name = "Té rooibos",
                calories = 2.0, protein = 0.0, fats = 0.0, sugar = 0.0,
                salt = 0.0, carbohydrates = 0.5, price = 3.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        }
    )

    val vegetarianDiet = listOf(
        // Desayuno
        FoodViewModel().apply {
            updateFood(
                name = "Porridge de avena y chía",
                calories = 300.0, protein = 12.0, fats = 8.0, sugar = 10.0,
                salt = 0.2, carbohydrates = 45.0, price = 9.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },

        // Bebida mañana
        FoodViewModel().apply {
            updateFood(
                name = "Zumo verde detox",
                calories = 70.0, protein = 2.0, fats = 0.5, sugar = 12.0,
                salt = 0.1, carbohydrates = 15.0, price = 5.5,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Almuerzo principal
        FoodViewModel().apply {
            updateFood(
                name = "Curry de garbanzos",
                calories = 350.0, protein = 18.0, fats = 10.0, sugar = 8.0,
                salt = 1.0, carbohydrates = 40.0, price = 12.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Acompañamiento
        FoodViewModel().apply {
            updateFood(
                name = "Arroz basmati integral",
                calories = 200.0, protein = 5.0, fats = 1.0, sugar = 0.5,
                salt = 0.3, carbohydrates = 45.0, price = 6.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN, FoodVariant.CELIAC),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },

        // Bebida tarde
        FoodViewModel().apply {
            updateFood(
                name = "Leche dorada (cúrcuma)",
                calories = 90.0, protein = 3.0, fats = 4.0, sugar = 6.0,
                salt = 0.2, carbohydrates = 10.0, price = 5.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Cena
        FoodViewModel().apply {
            updateFood(
                name = "Tarta de espinacas y tofu",
                calories = 280.0, protein = 20.0, fats = 12.0, sugar = 5.0,
                salt = 1.0, carbohydrates = 25.0, price = 14.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Infusión noche
        FoodViewModel().apply {
            updateFood(
                name = "Té de jengibre",
                calories = 5.0, protein = 0.1, fats = 0.0, sugar = 1.0,
                salt = 0.0, carbohydrates = 1.0, price = 3.5,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        }
    )

    val highProteinDiet = listOf(
        // Desayuno
        FoodViewModel().apply {
            updateFood(
                name = "Revuelto de espinacas",
                calories = 220.0, protein = 25.0, fats = 8.0, sugar = 2.0,
                salt = 0.8, carbohydrates = 10.0, price = 11.0,
                foodVariants = setOf(),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },

        // Bebida mañana
        FoodViewModel().apply {
            updateFood(
                name = "Batido de proteína whey",
                calories = 120.0, protein = 24.0, fats = 1.0, sugar = 3.0,
                salt = 0.3, carbohydrates = 5.0, price = 7.0,
                foodVariants = setOf(),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Almuerzo principal
        FoodViewModel().apply {
            updateFood(
                name = "Pechuga de pavo braseada",
                calories = 300.0, protein = 35.0, fats = 10.0, sugar = 2.0,
                salt = 1.2, carbohydrates = 5.0, price = 15.0,
                foodVariants = setOf(),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Acompañamiento
        FoodViewModel().apply {
            updateFood(
                name = "Puré de coliflor",
                calories = 80.0, protein = 4.0, fats = 3.0, sugar = 5.0,
                salt = 0.4, carbohydrates = 10.0, price = 6.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },

        // Bebida tarde
        FoodViewModel().apply {
            updateFood(
                name = "Bebida isotónica casera",
                calories = 40.0, protein = 1.0, fats = 0.0, sugar = 8.0,
                salt = 0.2, carbohydrates = 10.0, price = 4.0,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Cena
        FoodViewModel().apply {
            updateFood(
                name = "Salmón con espárragos",
                calories = 350.0, protein = 30.0, fats = 20.0, sugar = 3.0,
                salt = 1.0, carbohydrates = 8.0, price = 18.0,
                foodVariants = setOf(),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Infusión noche
        FoodViewModel().apply {
            updateFood(
                name = "Té relajante muscular",
                calories = 2.0, protein = 0.0, fats = 0.0, sugar = 0.0,
                salt = 0.0, carbohydrates = 0.5, price = 3.5,
                foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        }
    )

    val veganDiet = listOf(
        // Desayuno
        FoodViewModel().apply {
            updateFood(
                name = "Pudín de chía",
                calories = 280.0, protein = 10.0, fats = 12.0, sugar = 15.0,
                salt = 0.1, carbohydrates = 30.0, price = 10.0,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },

        // Bebida mañana
        FoodViewModel().apply {
            updateFood(
                name = "Café con leche de avena",
                calories = 60.0, protein = 2.0, fats = 2.0, sugar = 5.0,
                salt = 0.1, carbohydrates = 8.0, price = 4.5,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Almuerzo principal
        FoodViewModel().apply {
            updateFood(
                name = "Buddha bowl proteico",
                calories = 400.0, protein = 22.0, fats = 15.0, sugar = 10.0,
                salt = 0.8, carbohydrates = 45.0, price = 14.0,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Acompañamiento
        FoodViewModel().apply {
            updateFood(
                name = "Verduras al wok",
                calories = 120.0, protein = 4.0, fats = 5.0, sugar = 8.0,
                salt = 0.3, carbohydrates = 15.0, price = 7.0,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },

        // Bebida tarde
        FoodViewModel().apply {
            updateFood(
                name = "Zumo de remolacha y manzana",
                calories = 100.0, protein = 2.0, fats = 0.5, sugar = 18.0,
                salt = 0.2, carbohydrates = 22.0, price = 6.0,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },

        // Cena
        FoodViewModel().apply {
            updateFood(
                name = "Hamburguesa de lentejas",
                calories = 320.0, protein = 18.0, fats = 10.0, sugar = 5.0,
                salt = 1.0, carbohydrates = 35.0, price = 13.0,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },

        // Infusión noche
        FoodViewModel().apply {
            updateFood(
                name = "Leche de almendras caliente",
                calories = 50.0, protein = 2.0, fats = 3.0, sugar = 3.0,
                salt = 0.1, carbohydrates = 4.0, price = 4.0,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        }
    )
    var dietDayViewModel3 = DietDayViewModel().apply {
        updateDietDay(foods = mediterraneanDiet)
    }
    var dietDayViewModel4 = DietDayViewModel().apply {
        updateDietDay(foods = vegetarianDiet)
    }
    var dietDayViewModel5 = DietDayViewModel().apply {
        updateDietDay(foods = highProteinDiet)
    }
    var dietDayViewModel6 = DietDayViewModel().apply {
        updateDietDay(foods = veganDiet)
    }
    var dayDietLists2 = listOf(dietDayViewModel3, dietDayViewModel4, dietDayViewModel5, dietDayViewModel6)

    val diet1 = listOf(
        // Desayuno
        FoodViewModel().apply {
            updateFood(
                name = "Tostadas con tomate y aceite de oliva",
                calories = 280.0,
                protein = 6.0,
                fats = 12.0,
                carbohydrates = 35.0,
                price = 5.50,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },
        // Media Mañana
        FoodViewModel().apply {
            updateFood(
                name = "Yogur griego con miel y nueces",
                calories = 220.0,
                protein = 10.0,
                fats = 15.0,
                carbohydrates = 12.0,
                price = 4.80,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },
        // Almuerzo
        FoodViewModel().apply {
            updateFood(
                name = "Paella de mariscos",
                calories = 450.0,
                protein = 25.0,
                fats = 18.0,
                carbohydrates = 50.0,
                price = 14.90,
                foodVariants = setOf(FoodVariant.REGULAR),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },
        // Merienda
        FoodViewModel().apply {
            updateFood(
                name = "Gazpacho andaluz",
                calories = 150.0,
                protein = 2.0,
                fats = 8.0,
                carbohydrates = 18.0,
                price = 6.20,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },
        // Cena
        FoodViewModel().apply {
            updateFood(
                name = "Dorada al horno con verduras",
                calories = 320.0,
                protein = 30.0,
                fats = 15.0,
                carbohydrates = 10.0,
                price = 12.50,
                foodVariants = setOf(FoodVariant.REGULAR),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },
        // Snack 1
        FoodViewModel().apply {
            updateFood(
                name = "Aceitunas aliñadas",
                calories = 180.0,
                protein = 1.0,
                fats = 16.0,
                carbohydrates = 5.0,
                price = 3.80,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },
        // Snack 2
        FoodViewModel().apply {
            updateFood(
                name = "Vino tinto (copa)",
                calories = 125.0,
                protein = 0.0,
                fats = 0.0,
                carbohydrates = 4.0,
                price = 7.00,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        }
    )
    val diet2 = listOf(
        // Desayuno
        FoodViewModel().apply {
            updateFood(
                name = "Revuelto de claras con espinacas",
                calories = 250.0,
                protein = 28.0,
                fats = 10.0,
                carbohydrates = 8.0,
                price = 6.80,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },
        // Media Mañana
        FoodViewModel().apply {
            updateFood(
                name = "Batido de proteína con leche de almendras",
                calories = 200.0,
                protein = 25.0,
                fats = 5.0,
                carbohydrates = 15.0,
                price = 8.50,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },
        // Almuerzo
        FoodViewModel().apply {
            updateFood(
                name = "Solomillo de ternera con brócoli",
                calories = 380.0,
                protein = 40.0,
                fats = 22.0,
                carbohydrates = 12.0,
                price = 16.90,
                foodVariants = setOf(FoodVariant.HALAL),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },
        // Merienda
        FoodViewModel().apply {
            updateFood(
                name = "Queso fresco con nueces",
                calories = 210.0,
                protein = 15.0,
                fats = 16.0,
                carbohydrates = 4.0,
                price = 5.20,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },
        // Cena
        FoodViewModel().apply {
            updateFood(
                name = "Merluza a la plancha con espárragos",
                calories = 290.0,
                protein = 35.0,
                fats = 12.0,
                carbohydrates = 5.0,
                price = 13.40,
                foodVariants = setOf(FoodVariant.REGULAR),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },
        // Snack 1
        FoodViewModel().apply {
            updateFood(
                name = "Jamón serrano (50g)",
                calories = 160.0,
                protein = 18.0,
                fats = 10.0,
                carbohydrates = 0.0,
                price = 6.30,
                foodVariants = setOf(FoodVariant.REGULAR),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },
        // Snack 2
        FoodViewModel().apply {
            updateFood(
                name = "Yogur proteico",
                calories = 120.0,
                protein = 20.0,
                fats = 2.0,
                carbohydrates = 6.0,
                price = 4.90,
                foodVariants = setOf(FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        }
    )
    val diet3 = listOf(
        // Desayuno
        FoodViewModel().apply {
            updateFood(
                name = "Porridge de avena con leche de soja",
                calories = 300.0,
                protein = 12.0,
                fats = 8.0,
                carbohydrates = 45.0,
                price = 5.70,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            )
        },
        // Media Mañana
        FoodViewModel().apply {
            updateFood(
                name = "Smoothie verde (espinaca, plátano y chía)",
                calories = 180.0,
                protein = 5.0,
                fats = 4.0,
                carbohydrates = 30.0,
                price = 6.40,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        },
        // Almuerzo
        FoodViewModel().apply {
            updateFood(
                name = "Curry de garbanzos y espinacas",
                calories = 350.0,
                protein = 15.0,
                fats = 12.0,
                carbohydrates = 40.0,
                price = 11.80,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },
        // Merienda
        FoodViewModel().apply {
            updateFood(
                name = "Hummus con zanahorias",
                calories = 200.0,
                protein = 6.0,
                fats = 10.0,
                carbohydrates = 20.0,
                price = 4.90,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },
        // Cena
        FoodViewModel().apply {
            updateFood(
                name = "Tofu al wok con verduras",
                calories = 280.0,
                protein = 20.0,
                fats = 15.0,
                carbohydrates = 15.0,
                price = 10.60,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
            )
        },
        // Snack 1
        FoodViewModel().apply {
            updateFood(
                name = "Frutos secos tostados",
                calories = 250.0,
                protein = 8.0,
                fats = 20.0,
                carbohydrates = 10.0,
                price = 5.50,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
            )
        },
        // Snack 2
        FoodViewModel().apply {
            updateFood(
                name = "Leche dorada (cúrcuma)",
                calories = 150.0,
                protein = 3.0,
                fats = 8.0,
                carbohydrates = 15.0,
                price = 4.20,
                foodVariants = setOf(FoodVariant.VEGAN),
                foodTypes = setOf(FoodType.BEBIDA)
            )
        }
    )
    var dietDayViewModel7 = DietDayViewModel().apply {
        updateDietDay(foods = diet3)
    }
    var dietDayViewModel8 = DietDayViewModel().apply {
        updateDietDay(foods = diet2)
    }
    var dietDayViewModel9 = DietDayViewModel().apply {
        updateDietDay(foods = diet1)

    }
    var diets3 = listOf(dietDayViewModel7, dietDayViewModel8, dietDayViewModel9)

    if (dietId == "4") {
        dietViewModel.updateDiet(name = "Plan Usuario 7", duration = 2, dietId = dietId, goal = Goal.MANTENERSE, diets = dayDietLists)

    } else if (dietId == "3"){
        dietViewModel.updateDiet(name = "Nueva dieta", duration = 3, dietId = dietId, goal = Goal.GANAR_PESO, diets = diets3)
    } else {
        dietViewModel.updateDiet(name = "Plan Usuario 7 2", duration = 4, dietId = dietId, goal = Goal.GANAR_PESO, diets = dayDietLists2)
    }
    return dietViewModel
}

fun getDietViewModelId(dietViewModelList: List<DietViewModel>, dietId: String): DietViewModel? {
    return dietViewModelList.find { it.getDiet().dietId == dietId }
}