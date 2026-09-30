package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiRecipeService
import com.example.data.local.AppDatabase
import com.example.data.local.KitchenIngredientEntity
import com.example.data.model.DefaultIngredients
import com.example.data.model.Ingredient
import com.example.data.model.Recipe
import com.example.data.repository.KitchenRepository
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

sealed class Screen {
    data object Home : Screen()
    data object Scan : Screen()
    data object FusionLab : Screen()
    data class RecipeDetail(val recipe: Recipe) : Screen()
    data class CookingMode(val recipe: Recipe, val stepIndex: Int = 0) : Screen()
    data object Kitchen : Screen()
    data object Saved : Screen()
    data object Profile : Screen()
}

class FoodAlchemistViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val kitchenRepo = KitchenRepository(db.foodAlchemistDao())
    private val recipeRepo = RecipeRepository(db.foodAlchemistDao())
    private val geminiService = GeminiRecipeService()

    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    // Kitchen Ingredients Flow
    val kitchenIngredients: StateFlow<List<KitchenIngredientEntity>> = kitchenRepo.allIngredients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved Recipes Flow
    val savedRecipes: StateFlow<List<Recipe>> = recipeRepo.savedRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Featured Recipes on Home
    private val _madeForYouRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val madeForYouRecipes: StateFlow<List<Recipe>> = _madeForYouRecipes.asStateFlow()

    // Fusion Lab State
    private val _selectedFusionIngredients = MutableStateFlow<List<Ingredient>>(
        listOf(
            Ingredient("potato", "Potato", "🥔", "Produce", "2 medium"),
            Ingredient("tomato", "Tomato", "🍅", "Produce", "2 medium"),
            Ingredient("onion", "Onion", "🧅", "Produce", "1 medium"),
            Ingredient("egg", "Egg", "🥚", "Dairy & Eggs", "3 eggs")
        )
    )
    val selectedFusionIngredients: StateFlow<List<Ingredient>> = _selectedFusionIngredients.asStateFlow()

    private val _selectedGoal = MutableStateFlow("Unique")
    val selectedGoal: StateFlow<String> = _selectedGoal.asStateFlow()

    private val _isInventing = MutableStateFlow(false)
    val isInventing: StateFlow<Boolean> = _isInventing.asStateFlow()

    // Stock-based generated ideas
    private val _stockMealIdeas = MutableStateFlow<List<Recipe>>(emptyList())
    val stockMealIdeas: StateFlow<List<Recipe>> = _stockMealIdeas.asStateFlow()

    private val _isGeneratingStockIdeas = MutableStateFlow(false)
    val isGeneratingStockIdeas: StateFlow<Boolean> = _isGeneratingStockIdeas.asStateFlow()

    // Scan State
    private val _scannedIngredients = MutableStateFlow<List<Ingredient>>(
        listOf(
            Ingredient("potato", "Potato", "🥔", "Produce", "2 medium"),
            Ingredient("tomato", "Tomato", "🍅", "Produce", "2 medium"),
            Ingredient("onion", "Onion", "🧅", "Produce", "1 medium"),
            Ingredient("egg", "Egg", "🥚", "Dairy & Eggs", "2 eggs")
        )
    )
    val scannedIngredients: StateFlow<List<Ingredient>> = _scannedIngredients.asStateFlow()

    // Cooking Mode State
    private val _activeCookingStep = MutableStateFlow(0)
    val activeCookingStep: StateFlow<Int> = _activeCookingStep.asStateFlow()

    private val _isVoiceModeActive = MutableStateFlow(false)
    val isVoiceModeActive: StateFlow<Boolean> = _isVoiceModeActive.asStateFlow()

    private val _isTtsSpeaking = MutableStateFlow(false)
    val isTtsSpeaking: StateFlow<Boolean> = _isTtsSpeaking.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // Servings Scaler on Recipe Detail
    private val _currentRecipeServings = MutableStateFlow(2)
    val currentRecipeServings: StateFlow<Int> = _currentRecipeServings.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            kitchenRepo.initializeDefaultPantryIfEmpty()
            loadInitialRecipes()
        }
        initTts(application)
    }

    private fun initTts(application: Application) {
        textToSpeech = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                isTtsInitialized = true
            }
        }
    }

    private suspend fun loadInitialRecipes() {
        val defaultStock = listOf(
            Ingredient("potato", "Potato", "🥔"),
            Ingredient("tomato", "Tomato", "🍅"),
            Ingredient("onion", "Onion", "🧅"),
            Ingredient("egg", "Egg", "🥚")
        )
        val initialList = geminiService.generateMealIdeas(defaultStock)
        _madeForYouRecipes.value = initialList
        _stockMealIdeas.value = initialList
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
            if (screen is Screen.RecipeDetail) {
                _currentRecipeServings.value = screen.recipe.servings
            }
            if (screen is Screen.CookingMode) {
                _activeCookingStep.value = screen.stepIndex
            }
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            val previous = screenBackStack.removeAt(screenBackStack.size - 1)
            _currentScreen.value = previous
            stopTts()
            return true
        }
        if (_currentScreen.value != Screen.Home) {
            _currentScreen.value = Screen.Home
            stopTts()
            return true
        }
        return false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectGoal(goal: String) {
        _selectedGoal.value = goal
    }

    fun toggleFusionIngredient(ingredient: Ingredient) {
        val current = _selectedFusionIngredients.value.toMutableList()
        val index = current.indexOfFirst { it.name.equals(ingredient.name, ignoreCase = true) }
        if (index >= 0) {
            current.removeAt(index)
        } else {
            current.add(ingredient)
        }
        _selectedFusionIngredients.value = current
    }

    fun removeFusionIngredient(name: String) {
        val current = _selectedFusionIngredients.value.toMutableList()
        current.removeAll { it.name.equals(name, ignoreCase = true) }
        _selectedFusionIngredients.value = current
    }

    fun addFusionIngredient(ingredient: Ingredient) {
        if (_selectedFusionIngredients.value.none { it.name.equals(ingredient.name, ignoreCase = true) }) {
            _selectedFusionIngredients.value = _selectedFusionIngredients.value + ingredient
        }
    }

    // Scan Actions
    fun removeScannedIngredient(name: String) {
        _scannedIngredients.value = _scannedIngredients.value.filterNot { it.name.equals(name, ignoreCase = true) }
    }

    fun addScannedIngredient(name: String, emoji: String) {
        _scannedIngredients.value = _scannedIngredients.value + Ingredient(
            id = UUID.randomUUID().toString(),
            name = name,
            emoji = emoji,
            category = "Produce"
        )
    }

    fun useScannedIngredients() {
        _selectedFusionIngredients.value = _scannedIngredients.value
        navigateTo(Screen.FusionLab)
    }

    // AI Alchemy: Invent Recipe
    fun inventRecipe(onSuccess: (Recipe) -> Unit = {}) {
        viewModelScope.launch {
            _isInventing.value = true
            val ingredients = _selectedFusionIngredients.value.ifEmpty {
                listOf(
                    Ingredient("potato", "Potato", "🥔"),
                    Ingredient("tomato", "Tomato", "🍅"),
                    Ingredient("onion", "Onion", "🧅"),
                    Ingredient("egg", "Egg", "🥚")
                )
            }
            val result = geminiService.inventRecipe(ingredients, _selectedGoal.value)
            _isInventing.value = false
            result.onSuccess { recipe ->
                _madeForYouRecipes.value = listOf(recipe) + _madeForYouRecipes.value.filter { it.title != recipe.title }
                navigateTo(Screen.RecipeDetail(recipe))
                onSuccess(recipe)
            }.onFailure {
                Log.e("FoodAlchemist", "Failed to invent recipe: ${it.message}")
            }
        }
    }

    // Feature explicitly requested: Button to generate new meal ideas based on available stock
    fun generateMealIdeasFromStock() {
        viewModelScope.launch {
            _isGeneratingStockIdeas.value = true
            val stock = kitchenIngredients.value.map {
                Ingredient(it.id, it.name, it.emoji, it.category, it.quantity)
            }.ifEmpty {
                DefaultIngredients.commonIngredients.take(5)
            }
            val ideas = geminiService.generateMealIdeas(stock)
            _stockMealIdeas.value = ideas
            _isGeneratingStockIdeas.value = false
        }
    }

    // Recipe Detail Actions
    fun adjustServings(delta: Int) {
        _currentRecipeServings.value = (_currentRecipeServings.value + delta).coerceIn(1, 12)
    }

    fun toggleSaveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val isAlreadySaved = recipeRepo.isSaved(recipe.id)
            if (isAlreadySaved) {
                recipeRepo.removeRecipe(recipe.id)
            } else {
                recipeRepo.saveRecipe(recipe.copy(isFavorite = true))
            }
        }
    }

    // Kitchen Inventory Actions
    fun addKitchenItem(name: String, emoji: String, category: String, quantity: String) {
        viewModelScope.launch {
            val entity = KitchenIngredientEntity(
                id = UUID.randomUUID().toString(),
                name = name.trim().replaceFirstChar { it.uppercase() },
                emoji = emoji.ifBlank { "🥘" },
                category = category,
                quantity = quantity.ifBlank { "1 item" }
            )
            kitchenRepo.addIngredient(entity)
        }
    }

    fun removeKitchenItem(id: String) {
        viewModelScope.launch {
            kitchenRepo.removeIngredient(id)
        }
    }

    // Cooking Mode & Voice Assistant
    fun setCookingStep(index: Int, totalSteps: Int, recipe: Recipe) {
        val next = index.coerceIn(0, totalSteps - 1)
        _activeCookingStep.value = next
        if (_isVoiceModeActive.value) {
            val step = recipe.steps.getOrNull(next)
            if (step != null) {
                speakStep("Step ${step.stepNumber}: ${step.title}. ${step.instruction}")
            }
        }
        val step = recipe.steps.getOrNull(next)
        if (step?.durationMinutes != null && step.durationMinutes > 0) {
            setTimer(step.durationMinutes * 60)
        }
    }

    fun toggleVoiceMode(recipe: Recipe) {
        val newVoiceState = !_isVoiceModeActive.value
        _isVoiceModeActive.value = newVoiceState
        if (newVoiceState) {
            val step = recipe.steps.getOrNull(_activeCookingStep.value)
            if (step != null) {
                speakStep("Voice Cooking Mode Active. Step ${step.stepNumber}: ${step.title}. ${step.instruction}")
            }
        } else {
            stopTts()
        }
    }

    fun speakStep(text: String) {
        if (isTtsInitialized && textToSpeech != null) {
            _isTtsSpeaking.value = true
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "step_tts")
            viewModelScope.launch {
                delay(3000)
                _isTtsSpeaking.value = false
            }
        }
    }

    fun stopTts() {
        textToSpeech?.stop()
        _isTtsSpeaking.value = false
    }

    fun setTimer(seconds: Int) {
        timerJob?.cancel()
        _timerSeconds.value = seconds
        _isTimerRunning.value = false
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            timerJob?.cancel()
            _isTimerRunning.value = false
        } else {
            if (_timerSeconds.value <= 0) return
            _isTimerRunning.value = true
            timerJob = viewModelScope.launch {
                while (_timerSeconds.value > 0 && _isTimerRunning.value) {
                    delay(1000)
                    _timerSeconds.value -= 1
                }
                _isTimerRunning.value = false
                speakStep("Timer finished! Your step is complete.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        timerJob?.cancel()
    }
}
