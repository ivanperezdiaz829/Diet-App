package com.example.diet_app

import DietPlansScreen
import GenerateMealPlanWithInputsScreen
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diet_app.model.FoodModel
import com.example.diet_app.model.FoodType
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.GlobalData
import com.example.diet_app.model.Screen
import com.example.diet_app.model.getFoodIndexFromVariant
import com.example.diet_app.model.getFoodVariantFromIndex
import com.example.diet_app.model.getGoalInt
import com.example.diet_app.model.getSexInt
import com.example.diet_app.screenActivities.*
import com.example.diet_app.screenActivities.components.navigateAndClearStack
import com.example.diet_app.screenActivities.components.navigateSingleInStack
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel
import com.example.diet_app.viewModel.parseUserPlatesResponse
import kotlin.math.log


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
            var dietViewModel = DietViewModel()

            /*
            var foodModels = listOf(
                FoodModel(
                name = "Pollo a la parrilla",
                foodId = 1,
                protein = 27.0,
                fats = 5.0,
                sugar = 0.0,
                salt = 0.3,
                carbohydrates = 0.0,
                calories = 165.0,
                price = 5.0,
                foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                foodTypes = setOf(FoodType.PLATO_LIGERO)
            ),
                FoodModel(
                    name = "Ensalada César",
                    foodId = 2,
                    protein = 8.0,
                    fats = 15.0,
                    sugar = 3.0,
                    salt = 0.8,
                    carbohydrates = 10.0,
                    calories = 190.0,
                    price = 4.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Salmón al horno",
                    foodId = 3,
                    protein = 22.0,
                    fats = 13.0,
                    sugar = 0.0,
                    salt = 0.4,
                    carbohydrates = 0.0,
                    calories = 208.0,
                    price = 12.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Arroz integral",
                    foodId = 4,
                    protein = 7.0,
                    fats = 1.0,
                    sugar = 0.0,
                    salt = 0.02,
                    carbohydrates = 38.0,
                    calories = 180.0,
                    price = 2.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Huevos revueltos",
                    foodId = 5,
                    protein = 13.0,
                    fats = 11.0,
                    sugar = 1.0,
                    salt = 0.5,
                    carbohydrates = 1.0,
                    calories = 150.0,
                    price = 3.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Batido de frutas",
                    foodId = 6,
                    protein = 2.0,
                    fats = 1.0,
                    sugar = 25.0,
                    salt = 0.1,
                    carbohydrates = 35.0,
                    calories = 160.0,
                    price = 3.5,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Pasta al pesto",
                    foodId = 7,
                    protein = 12.0,
                    fats = 14.0,
                    sugar = 1.0,
                    salt = 0.8,
                    carbohydrates = 45.0,
                    calories = 350.0,
                    price = 6.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Pan integral",
                    foodId = 8,
                    protein = 4.0,
                    fats = 2.0,
                    sugar = 2.0,
                    salt = 0.5,
                    carbohydrates = 20.0,
                    calories = 110.0,
                    price = 1.5,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Yogur natural",
                    foodId = 9,
                    protein = 5.0,
                    fats = 3.0,
                    sugar = 10.0,
                    salt = 0.2,
                    carbohydrates = 15.0,
                    calories = 90.0,
                    price = 2.5,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Nueces",
                    foodId = 10,
                    protein = 15.0,
                    fats = 50.0,
                    sugar = 1.0,
                    salt = 0.02,
                    carbohydrates = 5.0,
                    calories = 600.0,
                    price = 7.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Tofu",
                    foodId = 11,
                    protein = 8.0,
                    fats = 5.0,
                    sugar = 0.0,
                    salt = 0.3,
                    carbohydrates = 2.0,
                    calories = 80.0,
                    price = 4.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Manzana",
                    foodId = 12,
                    protein = 0.5,
                    fats = 0.2,
                    sugar = 19.0,
                    salt = 0.0,
                    carbohydrates = 25.0,
                    calories = 95.0,
                    price = 1.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Papas al horno",
                    foodId = 13,
                    protein = 4.0,
                    fats = 2.0,
                    sugar = 0.0,
                    salt = 0.4,
                    carbohydrates = 35.0,
                    calories = 200.0,
                    price = 3.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                ),
                FoodModel(
                    name = "Leche descremada",
                    foodId = 14,
                    protein = 8.0,
                    fats = 0.5,
                    sugar = 12.0,
                    salt = 0.1,
                    carbohydrates = 12.0,
                    calories = 90.0,
                    price = 2.0,
                    foodVariants = setOf(FoodVariant.VEGAN, FoodVariant.CELIAC, FoodVariant.HALAL, FoodVariant.VEGETARIAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                )
            )
            val foodViewModels = foodModels.map { foodModel ->
                FoodViewModel().apply {
                    updateFood(
                        foodModel.name,
                        foodModel.foodId,
                        foodModel.protein,
                        foodModel.fats,
                        foodModel.sugar,
                        foodModel.salt,
                        foodModel.carbohydrates,
                        foodModel.calories,
                        foodModel.price,
                        foodModel.foodVariants,
                        foodModel.foodTypes
                    )
                    Log.d("DIET_APP", "FoodViewModel created: ${foodModel.name} (ID: ${foodModel.foodId})")
                }
            }

            // Crear los días de dieta
            val dietDay1 = DietDayViewModel()
            val dietDay2 = DietDayViewModel()

            dietDay1.updateDietDay(foods = foodViewModels.subList(0, 7).toMutableList())
            Log.d("DIET_APP", "Diet Day 1 plates: ${foodViewModels.subList(0, 7).size}")
            Log.d("DIET_APP", "Diet Day 1 initialized with 7 plates")

            dietDay2.updateDietDay(foods = foodViewModels.subList(7, 14).toMutableList())
            Log.d("DIET_APP", "Diet Day 2 plates: ${foodViewModels.subList(7, 14).size}")
            Log.d("DIET_APP", "Diet Day 2 initialized with 7 plates")


            // Configurar el usuario
            userViewModel.updateUser(11)
            Log.d("DIET_APP", "User updated with ID: ${userViewModel.getUser().id}")

            // Configurar la dieta completa
            dietViewModel.updateDiet(
                name = "Loko",
                userModel = userViewModel.getUser(),
                duration = 2,
                diets = listOf(dietDay1, dietDay2, DietDayViewModel(), DietDayViewModel(), DietDayViewModel(), DietDayViewModel(), DietDayViewModel()),
                foodVariant = FoodVariant.REGULAR
            )
            Log.d("DIET_APP", "Diet plan 'Loko' created with 7 days and user ID: ${userViewModel.getUser().id}")

            // Crear el plan de dieta
            createDietPlanFromPlates(
                dietViewModel.toDietPlanFromPlatesSelectedComplete(),
                applicationContext,
                {}
            )
            Log.d("DIET_APP", "Diet plan sent to createDietPlanFromPlates")
            */
            DietApp(LocalContext.current, userViewModel, foodViewModel, dietViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DietApp(applicationContext: Context, userViewModel: UserViewModel, newFood: FoodViewModel, dietViewModel: DietViewModel) {
    // Usamos NavController para manejar la navegación
    val navController = rememberNavController()
    var dietJson by remember { mutableStateOf<String?>(null) }
    var dietViewModels by remember { mutableStateOf<MutableList<DietViewModel>>(mutableListOf()) }
    var navigationVariable by remember { mutableStateOf(false) }
    var foodsDatabase by remember { mutableStateOf<MutableList<FoodViewModel>>(mutableListOf()) }
    var showDiets by remember { mutableStateOf(false) }
    var foodList by remember { mutableStateOf<MutableList<FoodViewModel>>(mutableListOf()) }
    var createDietPlan by remember { mutableStateOf(false) }

    getAllPlatesWhereUserIdIsNull(applicationContext) { result ->
        foodsDatabase = convertPlatesToFoodViewModels(result)
    }
    // Configuración de la navegación entre pantallas
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {

        composable(route = Screen.Home.route
        ) {HomePageFrame(navController, userViewModel)
        }

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
        ) {
            GoalSelectionScreen(
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
                        onResult = {
                            authenticateUser(
                                email = userViewModel.getUser().email,
                                password = userViewModel.getUser().password,
                                context = applicationContext,
                                userViewModel = userViewModel
                            ) { result ->
                                when {
                                    result.isSuccess -> {

                                        getUserDietPlansCompletePro(userViewModel.getUser().id, applicationContext) { jsonResponse ->
                                            dietJson = jsonResponse
                                            // Solo actualizamos los ViewModels cuando tengamos el JSON válido
                                            if (jsonResponse.isNotEmpty()) {
                                                val response = deserializeDietInformation(jsonResponse)
                                                dietViewModels = response.toDietViewModels() // Ahora recibe una lista
                                                showDiets = true
                                            }
                                        }

                                        getUserPlatesPro(userViewModel.getUser().id, applicationContext, {
                                            foodList = parseUserPlatesResponse(it).toMutableList()
                                        })


                                        Toast.makeText(
                                            applicationContext,
                                            "Usuario autenticado\n" +
                                                    "Bienvenido ${userViewModel.getUser().email}!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    result.isFailure -> {
                                        Toast.makeText(
                                            applicationContext,
                                            "Fallo de autenticación",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                    )

                    navController.navigate(Screen.Home.route)
                },
            )
        }

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
            // Muestra la pantalla solo cuando tengamos datos
            DietPlansScreen(
                navController = navController,
                diets = dietViewModels // Pasamos la lista completa
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
                onNext = {
                    navController.navigate(Screen.Login.route) }
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

                    getUserDietPlansCompletePro(userViewModel.getUser().id, applicationContext) { jsonResponse ->
                        dietJson = jsonResponse
                        // Solo actualizamos los ViewModels cuando tengamos el JSON válido
                        if (jsonResponse.isNotEmpty()) {
                            val response = deserializeDietInformation(jsonResponse)
                            dietViewModels = response.toDietViewModels() // Ahora recibe una lista
                            showDiets = true
                        }
                    }

                    getUserPlatesPro(userViewModel.getUser().id, applicationContext, {
                        foodList = parseUserPlatesResponse(it).toMutableList()
                        foodsDatabase.addAll(foodList)
                        foodsDatabase = (foodsDatabase).toSet().toMutableList()
                    })

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
                    navController.navigateAndClearStack(Screen.Welcome.route)
                }
            )
        }

        composable(route = Screen.FoodList.route,
        ) {
            FoodListViewScreen(navController, foodList)
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
            var addNewFood by remember { mutableStateOf(false) }
            NewFoodSummaryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    newFood.updateFood(name = it.getFood().name)
                    printFoodInfo(newFood)
                    navController.navigate(Screen.FoodDetail.route)
                    addNewFood = true
                    //llamar a la API para guardar comida
                },
                foodViewModel = newFood
            )

            if (addNewFood) {
                LaunchedEffect(Unit) {
                    createPlateFromViewModel(newFood, userViewModel.getUser().id.toString(), applicationContext, onResult = {

                    getUserPlatesPro(userViewModel.getUser().id, applicationContext, {
                        foodList = parseUserPlatesResponse(it).toMutableList()
                        foodsDatabase.addAll(foodList)
                        foodsDatabase = (foodsDatabase).toSet().toMutableList()
                    })

                    })
                    Toast.makeText(
                        applicationContext,
                        "Comida nueva agregada\n" +
                                "${newFood.getFood().name}!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
                        navController.navigate(Screen.PhsysicalData.route)
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
                    dietViewModel.updateDiet(foodVariant = getFoodVariantFromIndex(it))
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
                navigationVariable = navigationVariable,
                onNavigateBack = { navController.popBackStack() },
                onNextName = {
                    dietViewModel.updateDiet(duration = it)
                    navController.navigate(Screen.DietNameSelection.route)
                },
                onNextDiet = {
                    dietViewModel.updateDiet(duration = it)
                    val dietDays = List(7) {
                        DietDayViewModel()
                    }
                    dietViewModel.updateDiet(diets = dietDays)
                    navController.navigate(Screen.ChosenDiet.route)
                }
            )
        }

        composable(route = Screen.DietNameSelection.route,
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
            DietNameScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    dietViewModel.updateDiet(name = it)

                    if (createDietPlan) {
                        // llamada pa crear la dieta escogida por usuario
                        logDietDetails(dietViewModel)
                        createDietPlanFromPlates(
                            dietViewModel.toDietPlanFromPlatesSelectedComplete(),
                            applicationContext,
                            {
                                getUserDietPlansCompletePro(userViewModel.getUser().id, applicationContext) { jsonResponse ->
                                    dietJson = jsonResponse
                                    // Solo actualizamos los ViewModels cuando tengamos el JSON válido
                                    if (jsonResponse.isNotEmpty()) {
                                        val response = deserializeDietInformation(jsonResponse)
                                        dietViewModels = response.toDietViewModels() // Ahora recibe una lista
                                        showDiets = true
                                        navController.navigateAndClearStack(Screen.Home.route)
                                    }
                                }
                            })
                    } else {
                        val requirements = listOf(userViewModel.getUser().id, getFoodIndexFromVariant(dietViewModel.getDiet().foodVariant), dietViewModel.getDiet().duration, dietViewModel.getDiet().name)
                        create_diet_with_user_data(requirements, context = applicationContext, onResult = {
                            getUserDietPlansCompletePro(userViewModel.getUser().id, applicationContext) { jsonResponse ->
                                dietJson = jsonResponse
                                // Solo actualizamos los ViewModels cuando tengamos el JSON válido
                                if (jsonResponse.isNotEmpty()) {
                                    val response = deserializeDietInformation(jsonResponse)
                                    dietViewModels = response.toDietViewModels() // Ahora recibe una lista
                                    showDiets = true
                                    navController.navigateAndClearStack(Screen.Home.route)
                                }
                            }
                        })
                    }
                    createDietPlan = false
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
                onNavigateBack = {
                    navController.navigateAndClearStack(Screen.Home.route)
                },
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

        composable(route = Screen.ChosenDiet.route,
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
            ChosenDietInterface(
                context = applicationContext,
                dietViewModel = dietViewModel,
                navController = navController,// Pasa el ID a tu pantalla
                userViewModel = userViewModel,
                foodsDatabase = foodsDatabase,
                onNext = {
                    dietViewModel.updateDiet(diets = it.getDiet().diets)
                    Log.d("Diets: ", dietViewModel.getDiet().diets.toString())
                    // Crear el plan de dieta
                    createDietPlan = true
                    //logDietDetails(dietViewModel)
                    navController.navigateAndClearStack(Screen.DietNameSelection.route)
                }
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
            val diet = getDietViewModelId(dietViewModels, dietId)
            if (diet != null) {
                GraphicFrame(
                    navController = navController,
                    dietViewModel = diet
                )
            } else {
                Text("No se encontró la dieta con ID $dietId")
            }

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
                        navigationVariable = false
                        navController.navigate(Screen.GenerateMealPlanWithData.route)
                    } else if (it == DietGeneratorType.MANUAL_INPUT){
                        navigationVariable = false
                        navController.navigate(Screen.GenerateMealPlanWithInputs.route)
                    } else {
                        navigationVariable = true
                        navController.navigate(Screen.DietDurationSelection.route)
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
                    create_diet_with_inputs(it, applicationContext, onResult = {
                        getUserDietPlansCompletePro(userViewModel.getUser().id, applicationContext) { jsonResponse ->
                            dietJson = jsonResponse
                            // Solo actualizamos los ViewModels cuando tengamos el JSON válido
                            if (jsonResponse.isNotEmpty()) {
                                val response = deserializeDietInformation(jsonResponse)
                                dietViewModels = response.toDietViewModels() // Ahora recibe una lista
                                showDiets = true
                                navController.navigateAndClearStack(Screen.Meals.route)
                            }
                        }
                    })
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
            val lastPlanOnly = dietViewModels.lastOrNull()?.let { mutableListOf(it) } ?: mutableListOf()
            CalendarScreen(
                onSkip = { },
                onNavigateBack = { navController.popBackStack() },
                onNext = { },
                diets = lastPlanOnly
            )
        }

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

fun getDietViewModelId(dietViewModelList: List<DietViewModel>, dietId: String): DietViewModel? {
    return dietViewModelList.find { it.getDiet().dietId == dietId }
}

fun logDietDetails(dietViewModel: DietViewModel, tag: String = "DietLog") {
    val diet = dietViewModel.getDiet()

    // Información básica de la dieta
    Log.d(tag, "=== DIETA DETALLADA ===")
    Log.d(tag, "• Nombre: ${diet.name}")
    Log.d(tag, "• Duración: ${diet.duration} días")
    Log.d(tag, "• Fecha creación: ${diet.creationDate}")
    Log.d(tag, "• Variante: ${diet.foodVariant}")
    Log.d(tag, "• Objetivo: ${diet.goal}")
    Log.d(tag, "• ID: ${diet.dietId}")

    // Información de cada día
    Log.d(tag, "\nDÍAS (${diet.diets.size}):")
    diet.diets.forEachIndexed { index, dayViewModel ->
        val day = dayViewModel.getDiet()
        Log.d(tag, "\n  Día ${index + 1}:")
        Log.d(tag, "  - Variante: ${day.foodVariant}")
        Log.d(tag, "  - Objetivo: ${day.goal}")
        Log.d(tag, "  - ID Dieta padre: ${day.dietId}")

        // Comidas del día
        Log.d(tag, "  COMIDAS (${day.foods.size}):")
        day.foods.forEachIndexed { foodIndex, food ->
            Log.d(tag, "    ${foodIndex + 1}. ${food.name} (ID: ${food.foodId})")
            Log.d(tag, "      Calorías: ${food.getFood().calories}")
            Log.d(tag, "      Proteínas: ${food.getFood().protein}g")
            Log.d(tag, "      Carbos: ${food.getFood().carbohydrates}g")
            Log.d(tag, "      Grasas: ${food.getFood().fats}g")
        }
    }
}