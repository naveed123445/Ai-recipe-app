package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Ingredient
import com.example.data.model.Recipe
import com.example.data.model.RecipeIngredient
import com.example.data.model.RecipeStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiRecipeService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun inventRecipe(
        ingredients: List<Ingredient>,
        goal: String,
        dietary: String = "None"
    ): Result<Recipe> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        val ingredientNames = ingredients.joinToString(", ") { "${it.emoji} ${it.name} (${it.defaultAmount})" }

        // If no API key or placeholder, generate smart alchemy recipe locally
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiRecipeService", "Using curated alchemy generator (No custom API key injected)")
            return@withContext Result.success(generateCuratedAlchemyRecipe(ingredients, goal))
        }

        val prompt = """
            You are the master chef of "Food Alchemist", an AI that invents culinary masterpieces from whatever ingredients the user has on hand.
            
            Ingredients available:
            $ingredientNames
            
            Culinary Goal / Style: $goal
            Dietary Requirement: $dietary
            
            Invent a completely delicious, creative, and realistic recipe utilizing these ingredients (you may also include basic staples like salt, black pepper, cooking oil, water).
            
            Respond strictly in valid JSON matching this schema:
            {
              "title": "Creative Dish Name",
              "subtitle": "Short tagline describing why it works",
              "description": "2-3 sentences explaining the flavor profile and culinary magic.",
              "cookTimeMinutes": 20,
              "difficulty": "Easy",
              "servings": 2,
              "goalTag": "$goal",
              "ingredients": [
                { "name": "Potato", "amount": "2 medium diced", "emoji": "🥔", "isOwned": true },
                { "name": "Salt", "amount": "1/2 tsp", "emoji": "🧂", "isOwned": false }
              ],
              "steps": [
                {
                  "stepNumber": 1,
                  "title": "Prep vegetables",
                  "instruction": "Dice the potatoes and slice onions finely.",
                  "tip": "Keep potato cubes uniform for even cooking.",
                  "durationMinutes": 5
                }
              ],
              "smartSubstitutions": [
                "Don't have eggs? Use a silken tofu scramble or chickpea batter.",
                "Want extra crunch? Add toasted breadcrumbs or crushed seeds."
              ],
              "chefTips": [
                "Preheat your pan thoroughly before adding oil to prevent sticking.",
                "Let it rest for 2 minutes before serving."
              ],
              "calories": 380,
              "proteinGrams": 16,
              "carbsGrams": 42,
              "fatGrams": 14
            }
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        put("text", prompt)
                    }
                    val parts = JSONArray().apply { put(partObj) }
                    put(JSONObject().apply { put("parts", parts) })
                }
                put("contents", contents)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("GeminiRecipeService", "API call failed code ${response.code}, falling back to curated.")
                return@withContext Result.success(generateCuratedAlchemyRecipe(ingredients, goal))
            }

            val responseBody = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseBody)
            val candidates = jsonRoot.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val rawText = parts.getJSONObject(0).getString("text")

            val parsedRecipe = parseRecipeJson(rawText, goal, ingredients)
            Result.success(parsedRecipe)
        } catch (e: Exception) {
            Log.e("GeminiRecipeService", "Error calling Gemini API: ${e.message}", e)
            Result.success(generateCuratedAlchemyRecipe(ingredients, goal))
        }
    }

    suspend fun generateMealIdeas(
        ingredients: List<Ingredient>
    ): List<Recipe> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Recipe>()
        val ingredientNames = ingredients.map { it.name.lowercase() }

        // Idea 1: Spicy Pan Skillet
        list.add(
            Recipe(
                id = UUID.randomUUID().toString(),
                title = "Spicy Potato Tomato Egg Skillet",
                subtitle = "One-pan rustic alchemist skillet",
                description = "A flavorful and easy one-pan dish created by AI alchemy from your kitchen stock.",
                cookTimeMinutes = 18,
                difficulty = "Easy",
                servings = 2,
                imageDrawableName = "img_spicy_skillet",
                goalTag = "Spicy",
                ingredients = listOf(
                    RecipeIngredient("Potato", "2 medium, diced", "🥔", true),
                    RecipeIngredient("Tomato", "2 ripe, chopped", "🍅", true),
                    RecipeIngredient("Onion", "1 medium, sliced", "🧅", true),
                    RecipeIngredient("Egg", "3 large", "🥚", true),
                    RecipeIngredient("Salt & Chili", "To taste", "🌶️", true),
                    RecipeIngredient("Olive Oil", "1 tbsp", "🫒", true)
                ),
                steps = listOf(
                    RecipeStep(1, "Prep vegetables", "Dice the potatoes into 1/2 inch cubes and finely slice the onion.", "Uniform sizes cook evenly.", 4),
                    RecipeStep(2, "Sauté base aromatics", "Heat oil in a skillet, sauté onion until fragrant and golden translucent.", "Do not burn the onions.", 3),
                    RecipeStep(3, "Cook the vegetables", "Add potatoes and tomatoes. Cover and cook for 6-8 minutes until tender.", "Steam softens potatoes fast.", 7),
                    RecipeStep(4, "Drop the eggs", "Make small wells in the mixture and crack the eggs directly into the pan.", "Lower heat to medium-low.", 3),
                    RecipeStep(5, "Simmer & garnish", "Cover until egg whites are set but yolks stay runny. Garnish with chili flakes.", "Sprinkle pinch of salt on yolks.", 2)
                ),
                smartSubstitutions = listOf(
                    "No eggs? Use scrambled tofu or cooked chickpeas for great protein.",
                    "No fresh chili? Paprika or cayenne pepper adds wonderful smoky warmth."
                ),
                chefTips = listOf(
                    "Use a cast iron skillet for maximum heat retention and crispy edges.",
                    "Dip toasted crusty bread into the rich yolk and tomato reduction."
                ),
                calories = 360,
                proteinGrams = 18,
                carbsGrams = 34,
                fatGrams = 15,
                matchPercentage = calculateMatch(ingredientNames, listOf("potato", "tomato", "onion", "egg"))
            )
        )

        // Idea 2: Crispy Fritters
        list.add(
            Recipe(
                id = UUID.randomUUID().toString(),
                title = "Crispy Potato Onion Fritters",
                subtitle = "Golden crunchy alchemy patties",
                description = "Grated potatoes and sweet caramelised onions bound together into irresistible crispy golden disks.",
                cookTimeMinutes = 25,
                difficulty = "Medium",
                servings = 3,
                imageDrawableName = "img_potato_fritters",
                goalTag = "Comfort Food",
                ingredients = listOf(
                    RecipeIngredient("Potato", "3 medium, grated", "🥔", true),
                    RecipeIngredient("Onion", "1 large, thinly sliced", "🧅", true),
                    RecipeIngredient("Egg", "1 egg", "🥚", true),
                    RecipeIngredient("Flour / Starch", "2 tbsp", "🌾", true),
                    RecipeIngredient("Salt & Pepper", "1 tsp each", "🧂", true),
                    RecipeIngredient("Cooking Oil", "3 tbsp for pan-frying", "🫒", true)
                ),
                steps = listOf(
                    RecipeStep(1, "Grate & Squeeze", "Grate potatoes and squeeze out excess moisture thoroughly using a clean kitchen towel.", "Dry potatoes equal crispier fritters!", 6),
                    RecipeStep(2, "Mix batter", "In a bowl, combine squeezed potatoes, onions, beaten egg, salt, and flour until cohesive.", "Add pinch of garlic powder if you have it.", 4),
                    RecipeStep(3, "Pan fry golden", "Drop spoonfuls into hot shallow oil and flatten gently. Cook 4-5 minutes per side until deep golden brown.", "Don't overcrowd the pan.", 10),
                    RecipeStep(4, "Drain & Serve", "Drain on paper towels, sprinkle with flaky sea salt and serve warm.", "Serve with yogurt or spicy ketchup.", 3)
                ),
                smartSubstitutions = listOf(
                    "No flour? Cornstarch or ground oats work wonderfully.",
                    "Add grated cheese or chopped herbs directly into the mix."
                ),
                chefTips = listOf(
                    "Removing maximum water is the #1 secret to ultra-crispy fritters.",
                    "Keep the oil at 350°F (175°C) so fritters don't absorb excess oil."
                ),
                calories = 310,
                proteinGrams = 9,
                carbsGrams = 38,
                fatGrams = 14,
                matchPercentage = calculateMatch(ingredientNames, listOf("potato", "onion", "egg"))
            )
        )

        // Idea 3: Rustic Tortilla Espanola
        list.add(
            Recipe(
                id = UUID.randomUUID().toString(),
                title = "Spanish Potato Omelette (Tortilla)",
                subtitle = "Classic Mediterranean comfort",
                description = "Tender slow-poached potatoes and onions encased in soft, rich golden eggs.",
                cookTimeMinutes = 30,
                difficulty = "Easy",
                servings = 4,
                imageDrawableName = "img_spicy_skillet",
                goalTag = "Quick",
                ingredients = listOf(
                    RecipeIngredient("Potato", "3 medium, peeled and sliced", "🥔", true),
                    RecipeIngredient("Onion", "1 medium, diced", "🧅", true),
                    RecipeIngredient("Egg", "4 large eggs", "🥚", true),
                    RecipeIngredient("Olive Oil", "4 tbsp", "🫒", true),
                    RecipeIngredient("Sea Salt", "1/2 tsp", "🧂", true)
                ),
                steps = listOf(
                    RecipeStep(1, "Poach in oil", "Cook potato slices and onions in gentle warm oil until tender but not browned.", "Slow poaching makes them melt-in-your-mouth.", 12),
                    RecipeStep(2, "Rest with beaten eggs", "Beat eggs in a large bowl. Gently fold warm potatoes and onions into eggs; let stand 5 minutes.", "The eggs absorb the warmth.", 5),
                    RecipeStep(3, "Cook & flip", "Pour into skillet, cook on low until edges set, then flip with a plate and cook 2 minutes more.", "Keep the center juicy!", 8)
                ),
                smartSubstitutions = listOf(
                    "Add diced bell pepper or spinach for extra veggies."
                ),
                chefTips = listOf(
                    "Serve warm or at room temperature as tapas."
                ),
                calories = 290,
                proteinGrams = 14,
                carbsGrams = 26,
                fatGrams = 15,
                matchPercentage = calculateMatch(ingredientNames, listOf("potato", "onion", "egg"))
            )
        )

        list
    }

    private fun calculateMatch(have: List<String>, needed: List<String>): Int {
        if (needed.isEmpty()) return 100
        val matched = needed.count { need -> have.any { h -> h.contains(need) || need.contains(h) } }
        return ((matched.toFloat() / needed.size) * 100).toInt().coerceIn(40, 100)
    }

    private fun parseRecipeJson(raw: String, goal: String, availableIngredients: List<Ingredient>): Recipe {
        val clean = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val json = JSONObject(clean)

        val ingArray = json.optJSONArray("ingredients") ?: JSONArray()
        val recipeIngs = mutableListOf<RecipeIngredient>()
        for (i in 0 until ingArray.length()) {
            val obj = ingArray.getJSONObject(i)
            recipeIngs.add(
                RecipeIngredient(
                    name = obj.optString("name", "Ingredient"),
                    amount = obj.optString("amount", "As needed"),
                    emoji = obj.optString("emoji", "✨"),
                    isOwned = obj.optBoolean("isOwned", true)
                )
            )
        }

        val stepArray = json.optJSONArray("steps") ?: JSONArray()
        val steps = mutableListOf<RecipeStep>()
        for (i in 0 until stepArray.length()) {
            val obj = stepArray.getJSONObject(i)
            steps.add(
                RecipeStep(
                    stepNumber = obj.optInt("stepNumber", i + 1),
                    title = obj.optString("title", "Step ${i + 1}"),
                    instruction = obj.optString("instruction", ""),
                    tip = if (obj.has("tip")) obj.optString("tip") else null,
                    durationMinutes = if (obj.has("durationMinutes")) obj.optInt("durationMinutes") else null
                )
            )
        }

        val subArray = json.optJSONArray("smartSubstitutions") ?: JSONArray()
        val subs = mutableListOf<String>()
        for (i in 0 until subArray.length()) {
            subs.add(subArray.getString(i))
        }

        val tipArray = json.optJSONArray("chefTips") ?: JSONArray()
        val tips = mutableListOf<String>()
        for (i in 0 until tipArray.length()) {
            tips.add(tipArray.getString(i))
        }

        val img = if (goal.equals("Comfort Food", ignoreCase = true)) {
            "img_potato_fritters"
        } else {
            "img_spicy_skillet"
        }

        return Recipe(
            id = UUID.randomUUID().toString(),
            title = json.optString("title", "Alchemy Kitchen Creation"),
            subtitle = json.optString("subtitle", "Crafted with your ingredients"),
            description = json.optString("description", "A tasty dish uniquely crafted from what you have."),
            cookTimeMinutes = json.optInt("cookTimeMinutes", 20),
            difficulty = json.optString("difficulty", "Easy"),
            servings = json.optInt("servings", 2),
            imageDrawableName = img,
            goalTag = goal,
            ingredients = recipeIngs,
            steps = steps,
            smartSubstitutions = subs,
            chefTips = tips,
            calories = json.optInt("calories", 350),
            proteinGrams = json.optInt("proteinGrams", 15),
            carbsGrams = json.optInt("carbsGrams", 35),
            fatGrams = json.optInt("fatGrams", 12),
            isFavorite = false,
            matchPercentage = 100
        )
    }

    private fun generateCuratedAlchemyRecipe(
        ingredients: List<Ingredient>,
        goal: String
    ): Recipe {
        val names = ingredients.map { it.name.lowercase() }
        val hasPotato = names.any { it.contains("potato") }
        val hasTomato = names.any { it.contains("tomato") }
        val hasEgg = names.any { it.contains("egg") }
        val hasOnion = names.any { it.contains("onion") }
        val hasApple = names.any { it.contains("apple") }

        return when {
            goal.equals("Spicy", ignoreCase = true) -> {
                Recipe(
                    id = UUID.randomUUID().toString(),
                    title = "Spicy Potato Tomato Egg Skillet",
                    subtitle = "Sizzling Mediterranean skillet fusion",
                    description = "A flavorful and easy one-pan dish created by AI alchemy from your kitchen ingredients with a fiery kick.",
                    cookTimeMinutes = 18,
                    difficulty = "Easy",
                    servings = 2,
                    imageDrawableName = "img_spicy_skillet",
                    goalTag = "Spicy",
                    ingredients = listOf(
                        RecipeIngredient("Potato", "2 medium, diced into cubes", "🥔", true),
                        RecipeIngredient("Tomato", "2 medium, diced", "🍅", true),
                        RecipeIngredient("Onion", "1 medium, sliced", "🧅", true),
                        RecipeIngredient("Egg", "3 large eggs", "🥚", true),
                        RecipeIngredient("Chili Flakes / Paprika", "1 tsp", "🌶️", true),
                        RecipeIngredient("Salt & Pepper", "To taste", "🧂", true),
                        RecipeIngredient("Olive Oil", "1 tbsp", "🫒", true)
                    ),
                    steps = listOf(
                        RecipeStep(1, "Prep vegetables", "Wash and cut potatoes into uniform bite-sized cubes. Chop the tomatoes and slice onions.", "Even cubes cook in half the time.", 4),
                        RecipeStep(2, "Sauté aromatics", "Heat olive oil in a skillet over medium heat. Add sliced onions and cook until translucent and sweet.", "A pinch of salt draws out moisture.", 3),
                        RecipeStep(3, "Cook the vegetables", "Heat oil in a pan, add onions and cook until soft. Then add tomatoes and potatoes. Cook for 5–7 minutes until the potatoes are tender.", "Cover the pan with a lid to trap steam.", 6),
                        RecipeStep(4, "Create egg pockets", "Make 3 small nests in the softened vegetable mixture. Crack an egg into each pocket.", "Season yolks with a pinch of sea salt.", 2),
                        RecipeStep(5, "Simmer until set", "Cover skillet with lid on low heat for 3-4 minutes until egg whites are firm and yolks stay creamy.", "Watch carefully so yolks don't overcook.", 3),
                        RecipeStep(6, "Garnish & Serve", "Dust with red chili flakes and fresh cracked black pepper. Serve steaming hot directly from the skillet.", "Pairs wonderfully with toasted sourdough.", 1)
                    ),
                    smartSubstitutions = listOf(
                        "Don't have eggs? Sauté seasoned extra-firm tofu cubes for delicious protein.",
                        "No chili? Smoked paprika gives fantastic warmth without burning heat."
                    ),
                    chefTips = listOf(
                        "You can add green chilies or jalapeños for extra authentic heat.",
                        "Cast iron cookware yields the most delectable crispy potato crust."
                    ),
                    calories = 360,
                    proteinGrams = 18,
                    carbsGrams = 32,
                    fatGrams = 14,
                    matchPercentage = 100
                )
            }
            goal.equals("Comfort Food", ignoreCase = true) || !hasEgg -> {
                Recipe(
                    id = UUID.randomUUID().toString(),
                    title = "Crispy Potato Onion Fritters",
                    subtitle = "Golden pan-crisped alchemy cakes",
                    description = "Hand-shredded potatoes and sweet caramel onions fried to deep golden perfection with a fluffy tender center.",
                    cookTimeMinutes = 22,
                    difficulty = "Easy",
                    servings = 3,
                    imageDrawableName = "img_potato_fritters",
                    goalTag = "Comfort Food",
                    ingredients = listOf(
                        RecipeIngredient("Potato", "3 medium, peeled & coarsely grated", "🥔", true),
                        RecipeIngredient("Onion", "1 medium, finely minced", "🧅", true),
                        RecipeIngredient("Egg", "1 egg for binding", "🥚", true),
                        RecipeIngredient("Salt & Cracked Pepper", "1 tsp each", "🧂", true),
                        RecipeIngredient("Cooking Oil", "3 tbsp for pan crisping", "🫒", true)
                    ),
                    steps = listOf(
                        RecipeStep(1, "Grate & drain moisture", "Grate potatoes onto a clean dish towel. Gather edges and squeeze vigorously over the sink to remove excess starch water.", "Squeezing is the golden secret to maximum crispness!", 5),
                        RecipeStep(2, "Season & bind", "In a bowl, toss the dry grated potato with minced onion, beaten egg, salt, and pepper.", "Mix lightly with a fork so it stays airy.", 3),
                        RecipeStep(3, "Heat skillet", "Pour oil into non-stick pan over medium-high heat until shimmering.", "Test with a shred of potato—it should sizzle immediately.", 2),
                        RecipeStep(4, "Pan fry patties", "Spoon mounds into hot oil, flattening lightly with spatula. Fry 4 minutes each side until mahogany golden.", "Resist flipping early to let a crust form.", 8),
                        RecipeStep(5, "Finish & crunch", "Transfer to wire rack or paper towel. Sprinkle flaky salt while piping hot.", "Enjoy immediately while extra crisp.", 2)
                    ),
                    smartSubstitutions = listOf(
                        "No egg? 2 tbsp of potato starch or flour with water will bind beautifully.",
                        "Grate in some cheese or zucchini for flavor variety."
                    ),
                    chefTips = listOf(
                        "Never crowd the pan; cook in batches to maintain high oil temperature.",
                        "Serve with a cool garlic yogurt sauce or homemade apple chutney."
                    ),
                    calories = 295,
                    proteinGrams = 7,
                    carbsGrams = 36,
                    fatGrams = 13,
                    matchPercentage = 100
                )
            }
            hasApple -> {
                Recipe(
                    id = UUID.randomUUID().toString(),
                    title = "Apple-Chili Potato Bites",
                    subtitle = "Sweet & spicy alchemy fusion",
                    description = "Caramelized apples tossed with crispy roasted potatoes, finished with a warm chili-honey glaze.",
                    cookTimeMinutes = 22,
                    difficulty = "Easy",
                    servings = 2,
                    imageDrawableName = "img_potato_fritters",
                    goalTag = goal,
                    ingredients = listOf(
                        RecipeIngredient("Potato", "2 medium, diced small", "🥔", true),
                        RecipeIngredient("Apple", "1 crisp apple, diced", "🍎", true),
                        RecipeIngredient("Chili", "1/2 tsp flakes or fresh slices", "🌶️", true),
                        RecipeIngredient("Butter / Oil", "1 tbsp", "🧈", true),
                        RecipeIngredient("Honey / Sugar", "1 tsp", "🍯", true),
                        RecipeIngredient("Salt", "A pinch", "🧂", true)
                    ),
                    steps = listOf(
                        RecipeStep(1, "Crisp potatoes", "Sauté diced potatoes in butter/oil on medium heat for 10 minutes until golden and tender.", "Keep heat moderate so butter doesn't burn.", 10),
                        RecipeStep(2, "Add apples & chili", "Toss in the diced apples and chili flakes. Cook 4-5 minutes until apples caramelize slightly.", "Apples should soften but retain a slight bite.", 5),
                        RecipeStep(3, "Glaze & serve", "Drizzle honey and a pinch of flaky sea salt, tossing quickly over high heat for 1 minute.", "The honey glazes into an aromatic glossy coat.", 2)
                    ),
                    smartSubstitutions = listOf(
                        "Use pears instead of apples if desired.",
                        "Add diced bacon or pancetta for a savory smoky twist."
                    ),
                    chefTips = listOf(
                        "A tart green apple like Granny Smith balances the sweet chili glaze best."
                    ),
                    calories = 240,
                    proteinGrams = 4,
                    carbsGrams = 44,
                    fatGrams = 6,
                    matchPercentage = 100
                )
            }
            else -> {
                Recipe(
                    id = UUID.randomUUID().toString(),
                    title = "Golden Herb Potato & Egg Hash",
                    subtitle = "Nourishing rustic kitchen staple",
                    description = "Crisp caramelized potato cubes gently simmered with sweet onions and topped with velvety sunny eggs.",
                    cookTimeMinutes = 15,
                    difficulty = "Easy",
                    servings = 2,
                    imageDrawableName = "img_spicy_skillet",
                    goalTag = goal,
                    ingredients = listOf(
                        RecipeIngredient("Potato", "2 medium, cubed", "🥔", true),
                        RecipeIngredient("Onion", "1 small, diced", "🧅", true),
                        RecipeIngredient("Egg", "2 fresh eggs", "🥚", true),
                        RecipeIngredient("Olive Oil", "1 tbsp", "🫒", true),
                        RecipeIngredient("Sea Salt & Pepper", "To taste", "🧂", true)
                    ),
                    steps = listOf(
                        RecipeStep(1, "Cube & sauté", "Dice potatoes into 1/2 inch cubes. Heat skillet with oil and sauté potatoes 8 minutes.", "Stir occasionally to develop crust on all sides.", 8),
                        RecipeStep(2, "Sweeten with onions", "Add diced onions and sauté 4 minutes until golden and fragrant.", "Onion sugars caramelize on the potato surface.", 4),
                        RecipeStep(3, "Crown with eggs", "Make two wells, crack eggs in and cook 3 minutes covered until whites are firm.", "Turn off heat and let residual steam finish yolks.", 3)
                    ),
                    smartSubstitutions = listOf(
                        "Fold in chopped spinach, tomatoes, or leftover roast chicken."
                    ),
                    chefTips = listOf(
                        "Add a splash of water and cover for 2 minutes to cook potatoes through quickly."
                    ),
                    calories = 310,
                    proteinGrams = 14,
                    carbsGrams = 30,
                    fatGrams = 13,
                    matchPercentage = 100
                )
            }
        }
    }
}
