#!/usr/bin/env python3
"""
KYF (Know Your Food) SQLite Database Generator
Generates nutrition_app.db containing:
- profiles: User profiles with body metrics, allergens, non-IgE conditions, pollen sensitivities, trace strictness
- products: Packaged goods with barcodes, Nutri-Scores, UK/EU traffic lights, ingredients, direct/trace allergens, healthy alternatives
- raw_foods: USDA Foundation + USDA SR Legacy + INDB 2024 produce & whole foods with detailed macros & micros
- plate: Active plate items for multi-profile aggregate nutrition and recipe suggestion
"""

import sqlite3
import json
import os

def create_database(db_path: str):
    os.makedirs(os.path.dirname(db_path), exist_ok=True)
    if os.path.exists(db_path):
        os.remove(db_path)

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # 1. Create tables per specification
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS profiles (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        avatar_path TEXT,
        age INTEGER NOT NULL,
        gender TEXT NOT NULL,
        weight REAL NOT NULL,
        height REAL NOT NULL,
        allergies_json TEXT NOT NULL
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS products (
        barcode TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        brand TEXT NOT NULL,
        category TEXT NOT NULL,
        nutri_score TEXT NOT NULL,
        sugars_100g REAL NOT NULL,
        fat_100g REAL NOT NULL,
        sat_fat_100g REAL NOT NULL,
        salt_100g REAL NOT NULL,
        protein_100g REAL NOT NULL DEFAULT 0.0,
        energy_kcal_100g REAL NOT NULL DEFAULT 0.0,
        fiber_100g REAL NOT NULL DEFAULT 0.0,
        ingredients_text TEXT NOT NULL,
        allergens_json TEXT NOT NULL,
        healthier_alternatives_json TEXT
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS raw_foods (
        fdc_id INTEGER PRIMARY KEY,
        name TEXT NOT NULL,
        category TEXT NOT NULL,
        serving_g REAL NOT NULL DEFAULT 100.0,
        protein REAL NOT NULL,
        carbs REAL NOT NULL,
        fat REAL NOT NULL,
        fiber REAL NOT NULL,
        iron REAL NOT NULL,
        vit_c REAL NOT NULL,
        energy_kcal REAL NOT NULL DEFAULT 0.0,
        nutrients_json TEXT NOT NULL,
        source TEXT NOT NULL
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS plate (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        profile_id INTEGER NOT NULL,
        food_id INTEGER NOT NULL,
        quantity_g REAL NOT NULL,
        FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
        FOREIGN KEY (food_id) REFERENCES raw_foods(fdc_id) ON DELETE CASCADE
    );
    """)

    # 2. Insert Default Profiles
    sample_profiles = [
        (
            1,
            "Alex Johnson",
            "avatar_male_1",
            28,
            "Male",
            72.5,
            178.0,
            json.dumps({
                "allergens": ["PEANUT", "TREE_NUTS", "SESAME"],
                "pollen_sensitivities": ["BIRCH"],
                "conditions": [],
                "strict_traces": True
            })
        ),
        (
            2,
            "Maya (Child)",
            "avatar_child_1",
            7,
            "Female",
            23.0,
            122.0,
            json.dumps({
                "allergens": ["MILK", "EGG"],
                "pollen_sensitivities": [],
                "conditions": ["FPIES"],
                "strict_traces": True
            })
        ),
        (
            3,
            "David Miller",
            "avatar_male_2",
            34,
            "Male",
            80.0,
            182.0,
            json.dumps({
                "allergens": ["WHEAT", "GLUTEN"],
                "pollen_sensitivities": [],
                "conditions": ["CELIAC"],
                "strict_traces": True
            })
        ),
        (
            4,
            "Sophie Chen",
            "avatar_female_1",
            26,
            "Female",
            58.0,
            165.0,
            json.dumps({
                "allergens": ["CRUSTACEANS", "MOLLUSCS"],
                "pollen_sensitivities": ["LATEX"],
                "conditions": ["LACTOSE_INTOLERANCE", "ALPHA_GAL"],
                "strict_traces": False
            })
        ),
        (
            5,
            "Rahul Sharma",
            "avatar_male_3",
            42,
            "Male",
            76.0,
            173.0,
            json.dumps({
                "allergens": ["MUSTARD", "SULPHITES", "SOYBEANS"],
                "pollen_sensitivities": ["MUGWORT"],
                "conditions": ["SULFITE_SENSITIVITY"],
                "strict_traces": False
            })
        )
    ]

    cursor.executemany("""
    INSERT INTO profiles (id, name, avatar_path, age, gender, weight, height, allergies_json)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?);
    """, sample_profiles)

    # Helper for allergen format with direct & traces
    def make_allergens(direct, traces=None):
        return json.dumps({
            "contains": direct or [],
            "may_contain": traces or []
        })

    # 3. Rich Products Dataset
    products = [
        # --- CEREALS & BREAKFAST ---
        (
            "0016000275287",
            "Cheerios Whole Grain Cereal",
            "General Mills",
            "Breakfast Cereals",
            "A",
            3.8, 4.3, 0.7, 1.25, 12.1, 376.0, 10.3,
            "Whole Grain Oats, Corn Starch, Sugar, Salt, Tripotassium Phosphate, Vitamin E.",
            make_allergens(["OATS", "GLUTEN"]),
            json.dumps(["0016000275287", "7622210449283"])
        ),
        (
            "5000168001019",
            "Kellogg's Frosties (Frosted Flakes)",
            "Kellogg's",
            "Breakfast Cereals",
            "D",
            37.0, 0.6, 0.1, 0.83, 4.5, 375.0, 2.0,
            "Maize, Sugar, Barley Malt Extract, Salt, Niacin, Iron, Vitamin B6, Riboflavin, Thiamin, Folic Acid, Vitamin D, Vitamin B12. May contain gluten from other cereals.",
            make_allergens(["BARLEY", "GLUTEN"], ["WHEAT"]),
            json.dumps(["0016000275287", "5010044000305"])
        ),
        (
            "5010044000305",
            "Alpen No Added Sugar Swiss Muesli",
            "Alpen",
            "Breakfast Cereals",
            "A",
            16.0, 5.8, 0.9, 0.28, 12.0, 370.0, 8.3,
            "Wholegrain Wheat Flakes, Wholegrain Rolled Oats, Raisins, Skimmed Milk Powder, Milk Whey Powder, Roasted Sliced Hazelnuts and Almonds.",
            make_allergens(["WHEAT", "OATS", "MILK", "TREE_NUTS", "HAZELNUT", "ALMOND", "GLUTEN"]),
            json.dumps(["0016000275287"])
        ),
        (
            "8901499008404",
            "Kellogg's Chocos Crunchy Bites",
            "Kellogg's",
            "Breakfast Cereals",
            "D",
            29.5, 3.2, 1.2, 0.75, 7.8, 388.0, 4.5,
            "Whole Wheat Flour (29%), Wheat Flour, Sugar, Cocoa Solids, Edible Vegetable Oil (Palmolein), Minerals, Malt Extract, Iodized Salt, Vitamins.",
            make_allergens(["WHEAT", "BARLEY", "GLUTEN", "SOYBEANS"], ["MILK", "TREE_NUTS"]),
            json.dumps(["0016000275287"])
        ),
        (
            "5010477300054",
            "Weetabix Original Whole Wheat Cereal",
            "Weetabix",
            "Breakfast Cereals",
            "A",
            4.2, 2.0, 0.6, 0.26, 12.0, 362.0, 10.0,
            "Wholegrain Wheat (95%), Malted Barley Extract, Sugar, Salt, Niacin, Iron, Riboflavin (B2), Thiamin (B1), Folic Acid.",
            make_allergens(["WHEAT", "BARLEY", "GLUTEN"]),
            json.dumps(["0016000275287"])
        ),

        # --- DAIRY & PLANT-BASED MILKS ---
        (
            "7394376616038",
            "Oatly Barista Edition Oat Drink",
            "Oatly",
            "Dairy & Alternatives",
            "B",
            3.4, 3.0, 0.3, 0.10, 1.0, 59.0, 0.8,
            "Oat base (water, oats 10%), rapeseed oil, dipotassium phosphate, calcium carbonate, calcium phosphates, iodised salt, vitamins (D2, riboflavin, B12).",
            make_allergens(["OATS", "GLUTEN"]),
            json.dumps(["7394376616038", "0052200004265"])
        ),
        (
            "0025293000988",
            "Silk Original Soy Milk",
            "Silk",
            "Dairy & Alternatives",
            "A",
            2.5, 1.8, 0.3, 0.20, 3.3, 45.0, 0.8,
            "Soymilk (Filtered Water, Soybeans), Cane Sugar, Vitamin and Mineral Blend (Calcium Carbonate, Vitamin A Palmitate, Vitamin D2, Riboflavin, Vitamin B12), Sea Salt, Natural Flavor, Gellan Gum.",
            make_allergens(["SOYBEANS"]),
            json.dumps(["7394376616038"])
        ),
        (
            "0052200004265",
            "Almond Breeze Unsweetened Original",
            "Blue Diamond",
            "Dairy & Alternatives",
            "A",
            0.0, 1.1, 0.1, 0.18, 0.5, 13.0, 0.4,
            "Almondmilk (Filtered Water, Almonds), Calcium Carbonate, Sea Salt, Potassium Citrate, Sunflower Lecithin, Gellan Gum, Vitamin A Palmitate, Vitamin D2, D-Alpha-Tocopherol (Natural Vitamin E).",
            make_allergens(["TREE_NUTS", "ALMOND"]),
            json.dumps(["7394376616038", "0025293000988"])
        ),
        (
            "5411188110835",
            "Alpro Soya Plain Unsweetened Yogurt",
            "Alpro",
            "Dairy & Alternatives",
            "A",
            0.0, 2.3, 0.4, 0.08, 4.0, 43.0, 1.0,
            "Soya base (water, hulled soya beans (10.7%)), tricalcium citrate, acidity regulators (citric acid), stabiliser (pectins), natural flavouring, sea salt, vitamins (B12, D2), yogurt cultures.",
            make_allergens(["SOYBEANS"]),
            json.dumps(["5411188110835"])
        ),
        (
            "8901262010058",
            "Amul Taaza Homogenised Toned Milk",
            "Amul",
            "Dairy & Alternatives",
            "B",
            4.7, 3.0, 1.9, 0.11, 3.1, 58.0, 0.0,
            "Toned Milk, Vitamin A, Vitamin D.",
            make_allergens(["MILK"]),
            json.dumps(["7394376616038"])
        ),
        (
            "5201051000939",
            "FAGE Total 0% Fat Greek Yogurt",
            "FAGE",
            "Dairy & Alternatives",
            "A",
            3.0, 0.0, 0.0, 0.10, 10.3, 54.0, 0.0,
            "Pasteurised Skimmed Milk, Live Active Yogurt Cultures (L. Bulgaricus, S. Thermophilus, L. Acidophilus, Bifidus, L. Casei).",
            make_allergens(["MILK"]),
            json.dumps(["5201051000939"])
        ),
        (
            "8906079970014",
            "Epigamia Greek Yogurt Natural (No Added Sugar)",
            "Epigamia",
            "Dairy & Alternatives",
            "A",
            3.5, 0.0, 0.0, 0.09, 8.0, 52.0, 0.0,
            "Pasteurized Double Toned Milk, Milk Solids, Permitted Starter Cultures.",
            make_allergens(["MILK"]),
            json.dumps(["8906079970014"])
        ),

        # --- SNACKS & CHIPS ---
        (
            "0028400064088",
            "Lay's Classic Potato Chips",
            "Lay's",
            "Snacks & Crisps",
            "D",
            0.4, 35.7, 5.4, 1.25, 6.5, 536.0, 3.4,
            "Potatoes, Vegetable Oil (Canola, Corn, Soybean, and/or Sunflower Oil), Salt.",
            make_allergens([], ["SOYBEANS"]),
            json.dumps(["8906010500125", "5060194080017"])
        ),
        (
            "0028400047685",
            "Doritos Nacho Cheese Flavored Tortilla Chips",
            "Doritos",
            "Snacks & Crisps",
            "D",
            2.3, 26.0, 4.1, 1.80, 7.1, 500.0, 3.6,
            "Corn, Vegetable Oil (Sunflower, Canola, and/or Corn Oil), Salt, Cheddar Cheese (Milk, Cheese Cultures, Salt, Enzymes), Whey, Monosodium Glutamate, Buttermilk, Romano Cheese, Whey Protein Concentrate, Onion Powder, Lactose, Spices, Artificial Color (Yellow 6, Yellow 5, Red 40), Citric Acid, Garlic Powder.",
            make_allergens(["MILK"]),
            json.dumps(["8906010500125", "5060194080017"])
        ),
        (
            "5060194080017",
            "Proper Chips Sea Salt Lentil Chips",
            "Proper",
            "Snacks & Crisps",
            "B",
            2.1, 14.0, 1.2, 0.95, 12.5, 430.0, 5.2,
            "Lentil Flour (42%), Potato Starch, Corn Flour, Sunflower Oil, Sea Salt.",
            make_allergens([], ["MILK", "SOYBEANS"]),
            json.dumps(["5060194080017", "8906010500125"])
        ),
        (
            "8906010500125",
            "Farmley Roasted Peri Peri Makhana (Foxnuts)",
            "Farmley",
            "Snacks & Crisps",
            "A",
            1.8, 8.5, 1.1, 0.65, 9.2, 380.0, 8.5,
            "Foxnuts (Makhana 80%), Olive Oil, Peri Peri Seasoning (Garlic, Onion, Chili, Oregano, Black Pepper, Salt, Citric Acid).",
            make_allergens([], []),
            json.dumps(["8906010500125", "5060194080017"])
        ),
        (
            "8901725181220",
            "Haldiram's Nagpur Bhujia Sev",
            "Haldiram's",
            "Snacks & Crisps",
            "E",
            1.2, 42.0, 14.5, 2.10, 13.5, 580.0, 4.0,
            "Tepary Bean Flour (Moth Dal Flour 43%), Edible Vegetable Oil (Cottonseed & Palmolein Oil), Chickpeas Flour (Besan 12%), Salt, Red Chilli Powder, Black Pepper, Clove Powder, Cardamom Powder, Ginger Powder. May contain traces of peanut, tree nuts, wheat, gluten, milk, soy, sesame.",
            make_allergens([], ["PEANUT", "TREE_NUTS", "WHEAT", "GLUTEN", "MILK", "SOYBEANS", "SESAME"]),
            json.dumps(["8906010500125"])
        ),

        # --- CHOCOLATES, SPREADS & CONFECTIONERY ---
        (
            "8000500179865",
            "Nutella Hazelnut Spread with Cocoa",
            "Ferrero",
            "Sweet Spreads & Chocolates",
            "E",
            56.3, 30.9, 10.6, 0.11, 6.3, 539.0, 3.0,
            "Sugar, Palm Oil, Hazelnuts (13%), Skimmed Milk Powder (8.7%), Fat-Reduced Cocoa (7.4%), Emulsifier: Lecithins (Soya), Vanillin.",
            make_allergens(["TREE_NUTS", "HAZELNUT", "MILK", "SOYBEANS"]),
            json.dumps(["7610400010872", "8437013894012"])
        ),
        (
            "5000159461122",
            "Snickers Milk Chocolate Candy Bar",
            "Mars",
            "Sweet Spreads & Chocolates",
            "E",
            51.8, 22.8, 8.5, 0.63, 8.6, 481.0, 2.3,
            "Sugar, Glucose Syrup, Peanuts, Skimmed Milk Powder, Cocoa Butter, Cocoa Mass, Sunflower Oil, Palm Fat, Lactose and Protein from Whey (from Milk), Whey Powder (from Milk), Milk Fat, Emulsifier (Soya Lecithin), Salt, Coconut Oil, Egg White Powder, Natural Vanilla Extract, Milk Protein. May contain hazelnuts.",
            make_allergens(["PEANUT", "MILK", "SOYBEANS", "EGG"], ["TREE_NUTS", "HAZELNUT"]),
            json.dumps(["7610400010872"])
        ),
        (
            "7610400010872",
            "Lindt Excellence 85% Cocoa Dark Chocolate",
            "Lindt",
            "Sweet Spreads & Chocolates",
            "D",
            12.0, 46.0, 27.0, 0.05, 11.0, 584.0, 11.0,
            "Cocoa Mass, Fat-Reduced Cocoa, Cocoa Butter, Demerara Sugar, Natural Bourbon Vanilla Beans. May contain nuts, milk, soy, sesame.",
            make_allergens([], ["TREE_NUTS", "MILK", "SOYBEANS", "SESAME"]),
            json.dumps(["7610400010872"])
        ),
        (
            "7622210449283",
            "Cadbury Dairy Milk Chocolate",
            "Cadbury",
            "Sweet Spreads & Chocolates",
            "E",
            56.0, 30.5, 18.5, 0.24, 7.3, 534.0, 2.1,
            "Milk, Sugar, Cocoa Butter, Cocoa Mass, Vegetable Fats (Palm, Shea), Emulsifiers (E442, E476), Flavourings. May contain nuts, wheat.",
            make_allergens(["MILK"], ["TREE_NUTS", "WHEAT", "GLUTEN"]),
            json.dumps(["7610400010872"])
        ),
        (
            "8901063012015",
            "Britannia Good Day Butter Cookies",
            "Britannia",
            "Sweet Spreads & Chocolates",
            "E",
            24.5, 23.5, 11.0, 0.70, 7.0, 502.0, 1.5,
            "Refined Wheat Flour (Maida 58%), Sugar, Edible Vegetable Oil (Palm Oil), Butter (2%), Invert Sugar Syrup, Raising Agents, Milk Solids, Salt, Emulsifiers (Soy Lecithin 322), Artificial Flavouring. May contain traces of nuts.",
            make_allergens(["WHEAT", "GLUTEN", "MILK", "SOYBEANS"], ["TREE_NUTS"]),
            json.dumps(["8906010500125"])
        ),

        # --- CONDIMENTS, SAUCES & OILS ---
        (
            "0013000006050",
            "Heinz Tomato Ketchup",
            "Heinz",
            "Condiments & Sauces",
            "D",
            22.8, 0.1, 0.0, 1.80, 1.2, 102.0, 0.3,
            "Tomato Concentrate from Red Ripe Tomatoes, Distilled Vinegar, High Fructose Corn Syrup, Corn Syrup, Salt, Spice, Onion Powder, Natural Flavoring.",
            make_allergens([], []),
            json.dumps(["0013000006050"])
        ),
        (
            "3088542500096",
            "Maille Dijon Originale Mustard",
            "Maille",
            "Condiments & Sauces",
            "C",
            2.0, 11.0, 0.8, 5.70, 7.0, 150.0, 3.2,
            "Water, Mustard Seeds (26%), Spirit Vinegar, Salt, Acid (Citric Acid), Preservative (Potassium Metabisulphite).",
            make_allergens(["MUSTARD", "SULPHITES"]),
            json.dumps([])
        ),
        (
            "0041390001004",
            "Kikkoman Naturally Brewed Soy Sauce",
            "Kikkoman",
            "Condiments & Sauces",
            "D",
            1.7, 0.0, 0.0, 14.9, 8.8, 53.0, 0.0,
            "Water, Soybeans, Wheat, Salt.",
            make_allergens(["SOYBEANS", "WHEAT", "GLUTEN"]),
            json.dumps([])
        ),
        (
            "0048001213485",
            "Hellmann's Real Mayonnaise",
            "Hellmann's",
            "Condiments & Sauces",
            "E",
            1.3, 75.0, 11.0, 1.20, 1.1, 680.0, 0.0,
            "Rapeseed oil (78%), water, pasteurised free range egg & egg yolk (7.9%), spirit vinegar, salt, sugar, sunflower oil, lemon juice concentrate, antioxidant (calcium disodium EDTA), flavourings, paprika extract.",
            make_allergens(["EGG"]),
            json.dumps([])
        ),
        (
            "8853933000115",
            "Flying Goose Sriracha Hot Chilli Sauce",
            "Flying Goose",
            "Condiments & Sauces",
            "D",
            24.0, 1.4, 0.3, 4.50, 1.8, 122.0, 1.5,
            "Chilli (61%), Sugar, Garlic, Salt, Water, Acidity Regulators (E260, E330), Flavour Enhancer (E621), Stabiliser (E415), Preservative (E202).",
            make_allergens([], ["MUSTARD", "SOYBEANS"]),
            json.dumps([])
        ),

        # --- BREADS & BAKERY ---
        (
            "5010061001088",
            "Warburtons Wholemeal Medium Sliced Bread",
            "Warburtons",
            "Bakery & Breads",
            "A",
            3.0, 2.5, 0.5, 0.98, 10.0, 218.0, 7.0,
            "Wholemeal Wheat Flour, Water, Yeast, Salt, Wheat Gluten, Soya Flour, Emulsifier: E472e, Preservative: Calcium Propionate, Ascorbic Acid (Vitamin C). May contain sesame.",
            make_allergens(["WHEAT", "GLUTEN", "SOYBEANS"], ["SESAME"]),
            json.dumps(["8008698002018"])
        ),
        (
            "8008698002018",
            "Schär Gluten Free White Bread (Mastro Panettiere)",
            "Schär",
            "Bakery & Breads",
            "B",
            2.7, 2.8, 0.4, 1.30, 3.5, 233.0, 6.0,
            "Water, Maize Starch, Rice Flour, Vegetable Fibre (Psyllium), Thickener: Hydroxypropyl Methyl Cellulose, Sunflower Oil, Soya Protein, Yeast, Salt, Sugar, Citrus Fibre. Certified Gluten Free.",
            make_allergens(["SOYBEANS"], ["SESAME", "MUSTARD"]),
            json.dumps(["8008698002018"])
        ),
        (
            "0072250011150",
            "Wonder Classic White Bread",
            "Wonder",
            "Bakery & Breads",
            "C",
            5.0, 2.0, 0.0, 1.35, 7.5, 260.0, 2.0,
            "Unbleached Enriched Flour (Wheat Flour, Malted Barley Flour, Niacin, Reduced Iron, Thiamin Mononitrate, Riboflavin, Folic Acid), Water, High Fructose Corn Syrup, Yeast, Soybean Oil, Salt, Wheat Gluten, Dough Conditioners, Calcium Propionate.",
            make_allergens(["WHEAT", "BARLEY", "GLUTEN", "SOYBEANS"]),
            json.dumps(["5010061001088", "8008698002018"])
        ),

        # --- BEVERAGES ---
        (
            "5449000000996",
            "Coca-Cola Original Taste",
            "Coca-Cola",
            "Beverages",
            "E",
            10.6, 0.0, 0.0, 0.00, 0.0, 42.0, 0.0,
            "Carbonated Water, Sugar, Colour (Caramel E150d), Phosphoric Acid, Natural Flavourings including Caffeine.",
            make_allergens([], []),
            json.dumps(["5449000131805", "5038862145672"])
        ),
        (
            "5449000131805",
            "Coca-Cola Zero Sugar",
            "Coca-Cola",
            "Beverages",
            "B",
            0.0, 0.0, 0.0, 0.02, 0.0, 0.3, 0.0,
            "Carbonated Water, Colour (Caramel E150d), Acid (Phosphoric Acid), Sweeteners (Aspartame, Acesulfame-K), Natural Flavourings including Caffeine, Acidity Regulator (Sodium Citrates). Contains phenylalanine.",
            make_allergens([], []),
            json.dumps(["5449000131805"])
        ),
        (
            "5038862145672",
            "Innocent Super Smoothie Invigorate",
            "Innocent",
            "Beverages",
            "B",
            10.2, 0.2, 0.0, 0.01, 0.7, 54.0, 1.4,
            "4 pressed apples (54%), 1 mashed banana, 20 pressed white grapes, a dash of kiwi puree (7%), cucumber juice (4%), spirulina extract, crushed flax seeds, vitamins (B1, B2, B3, B6, C, E).",
            make_allergens([], []),
            json.dumps(["5038862145672"])
        ),
        (
            "0048500001018",
            "Tropicana Pure Premium 100% Orange Juice",
            "Tropicana",
            "Beverages",
            "C",
            8.8, 0.0, 0.0, 0.01, 0.7, 43.0, 0.2,
            "100% Pure Squeezed Pasteurized Orange Juice.",
            make_allergens([], []),
            json.dumps(["5038862145672"])
        ),
        (
            "90162602",
            "Red Bull Energy Drink",
            "Red Bull",
            "Beverages",
            "E",
            11.0, 0.0, 0.0, 0.10, 0.0, 46.0, 0.0,
            "Water, Sucrose, Glucose, Acid (Citric Acid), Carbon Dioxide, Taurine (0.4%), Acidity Regulators (Sodium Carbonates, Magnesium Carbonates), Caffeine (0.03%), Vitamins (Niacin, Pantothenic Acid, B6, B12), Flavourings, Colours (Caramel, Riboflavins).",
            make_allergens([], []),
            json.dumps(["5449000131805"])
        ),

        # --- FROZEN & READY MEALS ---
        (
            "4001724819706",
            "Dr. Oetker Ristorante Pizza Mozzarella",
            "Dr. Oetker",
            "Ready Meals & Frozen",
            "C",
            3.0, 12.0, 4.8, 1.10, 8.4, 252.0, 2.3,
            "Wheat Flour, 24% Tomato Purée, 15% Mozzarella Cheese (Milk), 12% Cherry Tomatoes, Rapeseed Oil, Edam Cheese (Milk), Water, Yeast, Salt, Sugar, Basil, Spinach, Parsley, Garlic.",
            make_allergens(["WHEAT", "GLUTEN", "MILK"]),
            json.dumps(["0852629004505", "0042272005994"])
        ),
        (
            "5000116104052",
            "Birds Eye 10 Omega 3 Fish Fingers",
            "Birds Eye",
            "Ready Meals & Frozen",
            "B",
            0.6, 7.8, 0.8, 0.74, 13.0, 196.0, 1.1,
            "Alaska Pollock (Fish) (58%), Breadcrumb (Wheat Flour, Water, Yeast, Salt, Paprika, Turmeric), Rapeseed Oil, Wheat Flour, Water, Salt, White Pepper.",
            make_allergens(["FISH", "WHEAT", "GLUTEN"]),
            json.dumps([])
        ),
        (
            "0852629004505",
            "Beyond Meat Beyond Burger Plant-Based Patties",
            "Beyond Meat",
            "Ready Meals & Frozen",
            "C",
            0.0, 17.0, 5.0, 0.95, 17.0, 238.0, 2.0,
            "Water, Pea Protein* (16%), Canola Oil, Refined Coconut Oil, Rice Protein, Flavouring, Cocoa Butter, Dried Yeast, Mung Bean Protein, Methylcellulose, Potato Starch, Apple Extract, Pomegranate Extract, Salt, Potassium Chloride, Vinegar, Lemon Juice Concentrate, Sunflower Lecithin, Beetroot Juice Extract.",
            make_allergens([], []),
            json.dumps(["0852629004505"])
        ),
        (
            "0042272005994",
            "Amy's Kitchen Gluten Free Rice Mac & Cheese",
            "Amy's",
            "Ready Meals & Frozen",
            "C",
            2.0, 11.0, 6.0, 1.20, 8.0, 220.0, 1.0,
            "Organic Rice Pasta (Organic Rice Flour, Filtered Water), Filtered Water, Organic Lowfat Milk, Cheddar Cheese (Pasteurized Milk, Culture, Salt, Enzymes), Grade AA Butter (Cream, Salt), Organic Sweet Rice Flour, Sea Salt, Organic Annatto (Color).",
            make_allergens(["MILK"], ["SOYBEANS", "TREE_NUTS"]),
            json.dumps(["0042272005994"])
        ),

        # --- NOODLES & INSTANT FOODS ---
        (
            "8901058852442",
            "Maggi 2-Minute Masala Instant Noodles",
            "Nestle",
            "Ready Meals & Frozen",
            "D",
            2.2, 13.5, 6.2, 2.45, 8.0, 427.0, 3.5,
            "Noodles: Refined Wheat Flour (Maida), Palm Oil, Salt, Wheat Gluten, Mineral (Calcium Carbonate), Thickeners (508, 412). Tastemaker: Spices (Onion, Coriander, Turmeric, Red Chilli, Garlic, Cumin, Aniseed, Black Pepper, Fenugreek, Ginger, Clove, Nutmeg, Cardamom), Sugar, Salt, Hydrolysed Peanut Protein, Palm Oil, Flavour Enhancer (635). May contain milk, mustard, soy.",
            make_allergens(["WHEAT", "GLUTEN", "PEANUT"], ["MILK", "MUSTARD", "SOYBEANS"]),
            json.dumps(["8901725114143", "8906010500125"])
        ),
        (
            "8901725114143",
            "Haldiram's Minute Khana Punjabi Choley Ready-to-Eat",
            "Haldiram's",
            "Ready Meals & Frozen",
            "B",
            2.8, 6.5, 0.8, 1.20, 5.5, 140.0, 5.0,
            "Chickpeas (45%), Water, Tomatoes, Onions, Refined Sunflower Oil, Fresh Ginger, Fresh Garlic, Green Chilli, Salt, Spices (Coriander, Cumin, Red Chilli, Black Pepper, Cardamom, Clove, Cinnamon, Bay Leaves), Coriander Leaves, Fenugreek Leaves.",
            make_allergens([], ["PEANUT", "TREE_NUTS", "MILK", "SOYBEANS", "MUSTARD", "SESAME"]),
            json.dumps(["8901725114143"])
        ),

        # --- BABY FOOD & KIDS SNACKS ---
        (
            "0015000045181",
            "Gerber Organic 1st Foods Banana Puree",
            "Gerber",
            "Baby & Toddler Food",
            "A",
            12.0, 0.2, 0.0, 0.01, 1.0, 68.0, 1.8,
            "Organic Bananas, Organic Lemon Juice Concentrate, Vitamin C (Ascorbic Acid).",
            make_allergens([], []),
            json.dumps(["0015000045181"])
        ),
        (
            "8901058860263",
            "Nestle Cerelac Baby Cereal with Milk (Wheat-Apple)",
            "Nestle",
            "Baby & Toddler Food",
            "B",
            20.0, 10.0, 4.2, 0.35, 15.0, 415.0, 4.0,
            "Wheat Flour (49%), Milk Solids (35%), Sugar, Apple Puree (5%), Soyabean Oil, Minerals, Vitamins, Enzyme (Alpha Amylase).",
            make_allergens(["WHEAT", "GLUTEN", "MILK", "SOYBEANS"]),
            json.dumps(["0015000045181"])
        ),
        (
            "5060107330543",
            "Ella's Kitchen Organic The Red One Smoothie",
            "Ella's Kitchen",
            "Baby & Toddler Food",
            "A",
            10.8, 0.2, 0.0, 0.01, 0.6, 52.0, 1.2,
            "Organic Strawberries (37%), Organic Bananas (30%), Organic Apples (26%), Organic Raspberries (7%), Organic Lemon Juice Concentrate (a dash).",
            make_allergens([], []),
            json.dumps(["5060107330543", "0015000045181"])
        )
    ]

    cursor.executemany("""
    INSERT INTO products (
        barcode, name, brand, category, nutri_score,
        sugars_100g, fat_100g, sat_fat_100g, salt_100g,
        protein_100g, energy_kcal_100g, fiber_100g,
        ingredients_text, allergens_json, healthier_alternatives_json
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """, products)

    # 4. Rich Produce and Raw Foods Dataset (Combining USDA Foundation, SR Legacy & INDB 2024)
    raw_foods = [
        # --- FRUITS ---
        (1001, "Fresh Apple (with skin)", "Fruits", 100.0, 0.26, 13.81, 0.17, 2.4, 0.12, 4.6, 52.0,
         json.dumps({"vit_a_mcg": 3.0, "vit_e_mg": 0.18, "vit_k_mcg": 2.2, "calcium_mg": 6.0, "potassium_mg": 107.0, "magnesium_mg": 5.0, "iron_mg": 0.12, "vit_c_mg": 4.6, "folate_mcg": 3.0, "sodium_mg": 1.0, "allergenic_pollen_cross": ["BIRCH"], "notes": "Rich in pectin & quercetin; Birch cross-reactive (Mal d 1)."}), "USDA Foundation"),
        
        (1002, "Ripe Banana", "Fruits", 100.0, 1.09, 22.84, 0.33, 2.6, 0.26, 8.7, 89.0,
         json.dumps({"vit_a_mcg": 4.0, "vit_b6_mg": 0.37, "calcium_mg": 5.0, "potassium_mg": 358.0, "magnesium_mg": 27.0, "iron_mg": 0.26, "vit_c_mg": 8.7, "folate_mcg": 20.0, "sodium_mg": 1.0, "allergenic_pollen_cross": ["RAGWEED", "LATEX"], "notes": "High potassium; Latex-fruit cross-reactive (Mus a 1)."}), "USDA Foundation"),
        
        (1003, "Navel Orange", "Fruits", 100.0, 0.94, 11.75, 0.12, 2.4, 0.10, 53.2, 47.0,
         json.dumps({"vit_a_mcg": 11.0, "vit_e_mg": 0.18, "calcium_mg": 40.0, "potassium_mg": 181.0, "magnesium_mg": 10.0, "iron_mg": 0.10, "vit_c_mg": 53.2, "folate_mcg": 30.0, "sodium_mg": 0.0, "allergenic_pollen_cross": ["GRASS"], "notes": "Top Vitamin C source; Grass pollen cross-reactive."}), "USDA Foundation"),
        
        (1004, "Alphonso Mango", "Fruits", 100.0, 0.82, 14.98, 0.38, 1.6, 0.16, 36.4, 60.0,
         json.dumps({"vit_a_mcg": 54.0, "vit_e_mg": 0.90, "vit_k_mcg": 4.2, "calcium_mg": 11.0, "potassium_mg": 168.0, "magnesium_mg": 10.0, "iron_mg": 0.16, "vit_c_mg": 36.4, "folate_mcg": 43.0, "sodium_mg": 1.0, "allergenic_pollen_cross": [], "notes": "High Vitamin A & beta-carotene; prominent Indian king of fruits."}), "INDB 2024"),
        
        (1005, "Fresh Guava", "Fruits", 100.0, 2.55, 14.32, 0.95, 5.4, 0.26, 228.3, 68.0,
         json.dumps({"vit_a_mcg": 31.0, "lycopene_mcg": 5204.0, "calcium_mg": 18.0, "potassium_mg": 417.0, "magnesium_mg": 22.0, "iron_mg": 0.26, "vit_c_mg": 228.3, "folate_mcg": 49.0, "sodium_mg": 2.0, "allergenic_pollen_cross": [], "notes": "Ultra-rich Vitamin C (4x orange) & dietary fiber."}), "INDB 2024"),
        
        (1006, "Hass Avocado", "Fruits", 100.0, 2.00, 8.53, 14.66, 6.7, 0.55, 10.0, 160.0,
         json.dumps({"vit_a_mcg": 7.0, "vit_e_mg": 2.07, "vit_k_mcg": 21.0, "calcium_mg": 12.0, "potassium_mg": 485.0, "magnesium_mg": 29.0, "iron_mg": 0.55, "vit_c_mg": 10.0, "folate_mcg": 81.0, "sodium_mg": 7.0, "allergenic_pollen_cross": ["LATEX"], "notes": "Monounsaturated oleic acid; Latex-Fruit syndrome trigger."}), "USDA Foundation"),
        
        (1007, "Strawberries", "Fruits", 100.0, 0.67, 7.68, 0.30, 2.0, 0.41, 58.8, 32.0,
         json.dumps({"vit_a_mcg": 1.0, "vit_e_mg": 0.29, "vit_k_mcg": 2.2, "calcium_mg": 16.0, "potassium_mg": 153.0, "magnesium_mg": 13.0, "iron_mg": 0.41, "vit_c_mg": 58.8, "folate_mcg": 24.0, "sodium_mg": 1.0, "allergenic_pollen_cross": ["BIRCH"], "notes": "Low sugar, high antioxidant anthocyanins & Vit C."}), "USDA Foundation"),
        
        (1008, "Blueberries", "Fruits", 100.0, 0.74, 14.49, 0.33, 2.4, 0.28, 9.7, 57.0,
         json.dumps({"vit_a_mcg": 3.0, "vit_k_mcg": 19.3, "calcium_mg": 6.0, "potassium_mg": 77.0, "magnesium_mg": 6.0, "iron_mg": 0.28, "vit_c_mg": 9.7, "anthocyanins_mg": 163.0, "sodium_mg": 1.0, "allergenic_pollen_cross": [], "notes": "Potent polyphenol antioxidant profile."}), "USDA Foundation"),
        
        (1009, "Green Kiwi (Raw)", "Fruits", 100.0, 1.14, 14.66, 0.52, 3.0, 0.31, 92.7, 61.0,
         json.dumps({"vit_a_mcg": 4.0, "vit_e_mg": 1.46, "vit_k_mcg": 40.3, "calcium_mg": 34.0, "potassium_mg": 312.0, "magnesium_mg": 17.0, "iron_mg": 0.31, "vit_c_mg": 92.7, "folate_mcg": 25.0, "sodium_mg": 3.0, "allergenic_pollen_cross": ["BIRCH", "LATEX"], "notes": "Actinidin enzyme; Birch & Latex cross-reactive."}), "USDA Foundation"),
        
        (1010, "Papaya (Pawpaw)", "Fruits", 100.0, 0.47, 10.82, 0.26, 1.7, 0.25, 60.9, 43.0,
         json.dumps({"vit_a_mcg": 47.0, "calcium_mg": 20.0, "potassium_mg": 182.0, "magnesium_mg": 21.0, "iron_mg": 0.25, "vit_c_mg": 60.9, "folate_mcg": 37.0, "sodium_mg": 8.0, "allergenic_pollen_cross": ["LATEX"], "notes": "Contains digestive papain; beta-carotene rich."}), "INDB 2024"),
        
        (1011, "Watermelon", "Fruits", 100.0, 0.61, 7.55, 0.15, 0.4, 0.24, 8.1, 30.0,
         json.dumps({"vit_a_mcg": 28.0, "lycopene_mcg": 4532.0, "calcium_mg": 7.0, "potassium_mg": 112.0, "iron_mg": 0.24, "vit_c_mg": 8.1, "citrulline_mg": 250.0, "sodium_mg": 1.0, "allergenic_pollen_cross": ["RAGWEED", "GRASS"], "notes": "92% hydration, rich in lycopene & L-citrulline."}), "USDA Foundation"),
        
        (1012, "Pomegranate Arils", "Fruits", 100.0, 1.67, 18.70, 1.17, 4.0, 0.30, 10.2, 83.0,
         json.dumps({"vit_k_mcg": 16.4, "calcium_mg": 10.0, "potassium_mg": 236.0, "iron_mg": 0.30, "vit_c_mg": 10.2, "punicalagins_mg": 300.0, "folate_mcg": 38.0, "sodium_mg": 3.0, "allergenic_pollen_cross": [], "notes": "Super-antioxidant punicalagins & ellagitannins."}), "INDB 2024"),
        
        (1013, "Pineapple (Raw)", "Fruits", 100.0, 0.54, 13.12, 0.12, 1.4, 0.29, 47.8, 50.0,
         json.dumps({"manganese_mg": 0.93, "calcium_mg": 13.0, "potassium_mg": 109.0, "iron_mg": 0.29, "vit_c_mg": 47.8, "bromelain": True, "sodium_mg": 1.0, "allergenic_pollen_cross": [], "notes": "Bromelain proteolytic enzyme, high manganese."}), "USDA Foundation"),
        
        (1014, "Red Cherries (Sweet)", "Fruits", 100.0, 1.06, 16.01, 0.20, 2.1, 0.36, 7.0, 63.0,
         json.dumps({"potassium_mg": 222.0, "calcium_mg": 13.0, "iron_mg": 0.36, "vit_c_mg": 7.0, "melatonin_mcg": 13.5, "sodium_mg": 0.0, "allergenic_pollen_cross": ["BIRCH"], "notes": "Pru av 1 Birch pollen cross-reactive."}), "USDA SR Legacy"),
        
        (1015, "Custard Apple (Sitaphal)", "Fruits", 100.0, 2.06, 23.64, 0.29, 4.4, 0.60, 36.3, 94.0,
         json.dumps({"calcium_mg": 24.0, "potassium_mg": 247.0, "magnesium_mg": 21.0, "iron_mg": 0.60, "vit_c_mg": 36.3, "phosphorus_mg": 32.0, "sodium_mg": 9.0, "allergenic_pollen_cross": [], "notes": "Traditional Indian delicacy, high energy and natural sugars."}), "INDB 2024"),

        # --- VEGETABLES ---
        (2001, "Fresh Spinach (Palak)", "Vegetables", 100.0, 2.86, 3.63, 0.39, 2.2, 2.71, 28.1, 23.0,
         json.dumps({"vit_a_mcg": 469.0, "vit_k_mcg": 482.9, "folate_mcg": 194.0, "calcium_mg": 99.0, "potassium_mg": 558.0, "magnesium_mg": 79.0, "iron_mg": 2.71, "vit_c_mg": 28.1, "lutein_mcg": 12198.0, "sodium_mg": 79.0, "allergenic_pollen_cross": [], "notes": "Iron-rich, mega Vitamin K & folate."}), "USDA Foundation"),
        
        (2002, "Broccoli Florets", "Vegetables", 100.0, 2.82, 6.64, 0.37, 2.6, 0.73, 89.2, 34.0,
         json.dumps({"vit_a_mcg": 31.0, "vit_k_mcg": 101.6, "folate_mcg": 63.0, "calcium_mg": 47.0, "potassium_mg": 316.0, "iron_mg": 0.73, "vit_c_mg": 89.2, "sulforaphane_mg": 12.5, "sodium_mg": 33.0, "allergenic_pollen_cross": ["MUGWORT"], "notes": "High sulforaphane, glucosinolates & Vit C."}), "USDA Foundation"),
        
        (2003, "Raw Orange Carrot", "Vegetables", 100.0, 0.93, 9.58, 0.24, 2.8, 0.30, 5.9, 41.0,
         json.dumps({"vit_a_mcg": 835.0, "beta_carotene_mcg": 8285.0, "potassium_mg": 320.0, "calcium_mg": 33.0, "iron_mg": 0.30, "vit_c_mg": 5.9, "sodium_mg": 69.0, "allergenic_pollen_cross": ["BIRCH", "MUGWORT"], "notes": "Dau c 1 allergen; Birch & Mugwort cross-reactive."}), "USDA Foundation"),
        
        (2004, "Red Ripe Tomato", "Vegetables", 100.0, 0.88, 3.89, 0.20, 1.2, 0.27, 13.7, 18.0,
         json.dumps({"vit_a_mcg": 42.0, "lycopene_mcg": 2573.0, "potassium_mg": 237.0, "calcium_mg": 10.0, "iron_mg": 0.27, "vit_c_mg": 13.7, "sodium_mg": 5.0, "allergenic_pollen_cross": ["GRASS"], "notes": "High lycopene, Sola l 1 Grass cross-reactive."}), "USDA Foundation"),
        
        (2005, "Red Bell Pepper (Capsicum)", "Vegetables", 100.0, 0.99, 6.03, 0.30, 2.1, 0.43, 127.7, 31.0,
         json.dumps({"vit_a_mcg": 157.0, "vit_e_mg": 1.58, "potassium_mg": 211.0, "calcium_mg": 7.0, "iron_mg": 0.43, "vit_c_mg": 127.7, "sodium_mg": 4.0, "allergenic_pollen_cross": ["MUGWORT"], "notes": "Triple the Vitamin C of oranges; Mugwort cross-reactive."}), "USDA Foundation"),
        
        (2006, "Moringa Pods (Drumstick)", "Vegetables", 100.0, 2.10, 8.53, 0.20, 3.2, 0.36, 141.0, 37.0,
         json.dumps({"calcium_mg": 30.0, "potassium_mg": 259.0, "magnesium_mg": 45.0, "iron_mg": 0.36, "vit_c_mg": 141.0, "folate_mcg": 44.0, "sodium_mg": 9.0, "allergenic_pollen_cross": [], "notes": "Classic South Indian vegetable, massive Vitamin C & minerals."}), "INDB 2024"),
        
        (2007, "Moringa Leaves (Murungai)", "Vegetables", 100.0, 9.40, 8.28, 1.40, 2.0, 4.00, 51.7, 64.0,
         json.dumps({"vit_a_mcg": 378.0, "calcium_mg": 185.0, "potassium_mg": 337.0, "iron_mg": 4.00, "vit_c_mg": 51.7, "magnesium_mg": 42.0, "sodium_mg": 9.0, "allergenic_pollen_cross": [], "notes": "Superfood green leaf, ultra-high plant calcium & iron."}), "INDB 2024"),
        
        (2008, "Bitter Gourd (Karela)", "Vegetables", 100.0, 1.00, 3.70, 0.17, 2.8, 0.43, 84.0, 17.0,
         json.dumps({"calcium_mg": 19.0, "potassium_mg": 296.0, "iron_mg": 0.43, "vit_c_mg": 84.0, "charantin": True, "polypeptide_p": True, "sodium_mg": 5.0, "allergenic_pollen_cross": [], "notes": "Charantin & polypeptide-p for glycemic regulation."}), "INDB 2024"),
        
        (2009, "Bottle Gourd (Lauki / Doodhi)", "Vegetables", 100.0, 0.62, 3.39, 0.02, 0.5, 0.20, 10.1, 14.0,
         json.dumps({"potassium_mg": 150.0, "calcium_mg": 26.0, "magnesium_mg": 11.0, "iron_mg": 0.20, "vit_c_mg": 10.1, "sodium_mg": 2.0, "allergenic_pollen_cross": [], "notes": "Extremely low calorie, light cooling vegetable."}), "INDB 2024"),
        
        (2010, "Sweet Potato (Baked in skin)", "Vegetables", 100.0, 2.01, 20.71, 0.15, 3.3, 0.69, 19.6, 90.0,
         json.dumps({"vit_a_mcg": 961.0, "potassium_mg": 475.0, "calcium_mg": 38.0, "magnesium_mg": 27.0, "iron_mg": 0.69, "vit_c_mg": 19.6, "sodium_mg": 36.0, "allergenic_pollen_cross": [], "notes": "Complex carbs, huge beta-carotene reservoir."}), "USDA Foundation"),
        
        (2011, "Okra (Bhindi / Lady's Finger)", "Vegetables", 100.0, 1.93, 7.45, 0.19, 3.2, 0.62, 23.0, 33.0,
         json.dumps({"calcium_mg": 82.0, "potassium_mg": 299.0, "magnesium_mg": 57.0, "iron_mg": 0.62, "vit_c_mg": 23.0, "folate_mcg": 60.0, "vit_k_mcg": 31.3, "sodium_mg": 7.0, "allergenic_pollen_cross": [], "notes": "Mucilage soluble fiber for cholesterol and gut health."}), "INDB 2024"),
        
        (2012, "Fenugreek Leaves (Fresh Methi)", "Vegetables", 100.0, 4.40, 6.00, 0.90, 1.1, 1.93, 52.0, 49.0,
         json.dumps({"calcium_mg": 395.0, "potassium_mg": 51.0, "iron_mg": 1.93, "vit_c_mg": 52.0, "folate_mcg": 84.0, "sodium_mg": 19.0, "allergenic_pollen_cross": [], "notes": "Exceptionally high calcium and therapeutic phytonutrients."}), "INDB 2024"),
        
        (2013, "Eggplant (Brinjal / Aubergine)", "Vegetables", 100.0, 0.98, 5.88, 0.18, 3.0, 0.23, 2.2, 25.0,
         json.dumps({"potassium_mg": 229.0, "nasunin_anthocyanin": True, "calcium_mg": 9.0, "iron_mg": 0.23, "vit_c_mg": 2.2, "sodium_mg": 2.0, "allergenic_pollen_cross": [], "notes": "Contains nasunin in peel; low calorie."}), "USDA Foundation"),
        
        (2014, "Cauliflower Florets", "Vegetables", 100.0, 1.92, 4.97, 0.28, 2.0, 0.42, 48.2, 25.0,
         json.dumps({"vit_c_mg": 48.2, "vit_k_mcg": 15.5, "folate_mcg": 57.0, "calcium_mg": 22.0, "potassium_mg": 299.0, "iron_mg": 0.42, "sodium_mg": 30.0, "allergenic_pollen_cross": ["MUGWORT"], "notes": "Glucosinolates, versatile low-carb grain substitute."}), "USDA Foundation"),
        
        (2015, "Fresh Cucumber (with peel)", "Vegetables", 100.0, 0.65, 3.63, 0.11, 0.5, 0.28, 2.8, 15.0,
         json.dumps({"potassium_mg": 147.0, "vit_k_mcg": 16.4, "calcium_mg": 16.0, "iron_mg": 0.28, "vit_c_mg": 2.8, "water_g": 95.2, "sodium_mg": 2.0, "allergenic_pollen_cross": ["RAGWEED"], "notes": "High hydration; Ragweed pollen cross-reactive."}), "USDA Foundation"),
        
        (2016, "Fresh Celery Stalks", "Vegetables", 100.0, 0.69, 2.97, 0.17, 1.6, 0.20, 3.1, 14.0,
         json.dumps({"sodium_mg": 80.0, "potassium_mg": 260.0, "calcium_mg": 40.0, "iron_mg": 0.20, "vit_c_mg": 3.1, "vit_k_mcg": 29.3, "allergenic_pollen_cross": ["BIRCH", "MUGWORT"], "notes": "Major EU Allergen (Api g 1); Birch & Mugwort cross-reactive."}), "USDA Foundation"),

        # --- LEGUMES & DALS ---
        (3001, "Chickpeas / Garbanzo (Boiled)", "Legumes", 100.0, 8.86, 27.42, 2.59, 7.6, 2.89, 1.3, 164.0,
         json.dumps({"folate_mcg": 172.0, "calcium_mg": 49.0, "potassium_mg": 291.0, "magnesium_mg": 48.0, "iron_mg": 2.89, "vit_c_mg": 1.3, "zinc_mg": 1.53, "sodium_mg": 7.0, "allergenic_pollen_cross": [], "notes": "High plant protein and complex prebiotic fiber."}), "USDA Foundation"),
        
        (3002, "Red Lentils / Masoor Dal (Cooked)", "Legumes", 100.0, 9.02, 20.13, 0.38, 7.9, 3.33, 1.5, 116.0,
         json.dumps({"folate_mcg": 181.0, "potassium_mg": 369.0, "phosphorus_mg": 180.0, "iron_mg": 3.33, "vit_c_mg": 1.5, "calcium_mg": 19.0, "sodium_mg": 2.0, "allergenic_pollen_cross": [], "notes": "Top iron & folate powerhouse in Indian cuisine."}), "INDB 2024"),
        
        (3003, "Yellow Moong Dal (Split, Cooked)", "Legumes", 100.0, 7.03, 19.15, 0.40, 7.6, 1.40, 1.0, 105.0,
         json.dumps({"calcium_mg": 27.0, "potassium_mg": 266.0, "folate_mcg": 159.0, "magnesium_mg": 48.0, "iron_mg": 1.40, "vit_c_mg": 1.0, "sodium_mg": 4.0, "allergenic_pollen_cross": [], "notes": "Lightest, most easily digestible legume in Ayurveda."}), "INDB 2024"),
        
        (3004, "Kidney Beans / Rajma (Cooked)", "Legumes", 100.0, 8.67, 22.80, 0.50, 6.4, 2.94, 1.2, 127.0,
         json.dumps({"folate_mcg": 130.0, "potassium_mg": 405.0, "magnesium_mg": 45.0, "iron_mg": 2.94, "vit_c_mg": 1.2, "calcium_mg": 35.0, "sodium_mg": 2.0, "allergenic_pollen_cross": [], "notes": "Slow-release carbs, anthocyanin seed coat."}), "INDB 2024"),
        
        (3005, "Edamame (Boiled Soybeans)", "Legumes", 100.0, 11.91, 8.91, 5.20, 5.2, 2.27, 6.1, 121.0,
         json.dumps({"isoflavones_mg": 35.0, "calcium_mg": 63.0, "potassium_mg": 436.0, "magnesium_mg": 64.0, "iron_mg": 2.27, "vit_c_mg": 6.1, "sodium_mg": 6.0, "allergenic_pollen_cross": ["BIRCH"], "notes": "Complete protein source. MAJOR ALLERGEN (Soybean)."}), "USDA Foundation"),

        # --- NUTS & SEEDS ---
        (4001, "Raw Almonds (Unsalted)", "Nuts & Seeds", 100.0, 21.15, 21.55, 49.93, 12.5, 3.71, 0.0, 579.0,
         json.dumps({"vit_e_mg": 25.63, "calcium_mg": 269.0, "magnesium_mg": 270.0, "potassium_mg": 733.0, "iron_mg": 3.71, "vit_c_mg": 0.0, "sodium_mg": 1.0, "allergenic_pollen_cross": ["BIRCH"], "notes": "Mega Vitamin E & calcium. MAJOR TREE NUT ALLERGEN."}), "USDA Foundation"),
        
        (4002, "English Walnuts", "Nuts & Seeds", 100.0, 15.23, 13.71, 65.21, 6.7, 2.91, 1.3, 654.0,
         json.dumps({"omega3_ala_g": 9.08, "potassium_mg": 441.0, "magnesium_mg": 158.0, "copper_mg": 1.59, "calcium_mg": 98.0, "iron_mg": 2.91, "vit_c_mg": 1.3, "sodium_mg": 2.0, "allergenic_pollen_cross": ["BIRCH"], "notes": "Highest plant Omega-3 ALA of any nut. MAJOR TREE NUT ALLERGEN."}), "USDA Foundation"),
        
        (4003, "Chia Seeds (Dried)", "Nuts & Seeds", 100.0, 16.54, 42.12, 30.74, 34.4, 7.72, 1.6, 486.0,
         json.dumps({"omega3_ala_g": 17.83, "calcium_mg": 631.0, "magnesium_mg": 335.0, "phosphorus_mg": 860.0, "iron_mg": 7.72, "vit_c_mg": 1.6, "sodium_mg": 16.0, "allergenic_pollen_cross": [], "notes": "34g fiber/100g and extreme calcium density."}), "USDA Foundation"),
        
        (4004, "Roasted Pumpkin Seeds (Pepitas)", "Nuts & Seeds", 100.0, 29.84, 14.71, 49.05, 6.0, 8.82, 1.9, 559.0,
         json.dumps({"zinc_mg": 7.81, "magnesium_mg": 592.0, "potassium_mg": 809.0, "calcium_mg": 46.0, "iron_mg": 8.82, "vit_c_mg": 1.9, "sodium_mg": 7.0, "allergenic_pollen_cross": [], "notes": "Phenomenal zinc, magnesium and plant iron."}), "USDA Foundation"),

        # --- WHOLE GRAINS ---
        (5001, "Cooked Quinoa", "Grains", 100.0, 4.40, 21.30, 1.92, 2.8, 1.49, 0.0, 120.0,
         json.dumps({"folate_mcg": 42.0, "magnesium_mg": 64.0, "phosphorus_mg": 152.0, "potassium_mg": 172.0, "calcium_mg": 17.0, "iron_mg": 1.49, "vit_c_mg": 0.0, "sodium_mg": 7.0, "allergenic_pollen_cross": [], "notes": "Naturally gluten-free complete protein grain."}), "USDA Foundation"),
        
        (5002, "Rolled Oats (Dry)", "Grains", 100.0, 13.15, 67.70, 6.52, 10.1, 4.25, 0.0, 379.0,
         json.dumps({"beta_glucan_g": 4.5, "thiamin_mg": 0.46, "magnesium_mg": 138.0, "calcium_mg": 52.0, "iron_mg": 4.25, "vit_c_mg": 0.0, "sodium_mg": 2.0, "allergenic_pollen_cross": [], "notes": "Beta-glucan reduces LDL cholesterol. FPIES trigger in sensitive infants."}), "USDA Foundation"),

        # --- NON-VEG / PROTEINS ---
        (6001, "Chicken Breast (Boneless, Skinless, Raw)", "Poultry", 100.0, 22.50, 0.00, 2.62, 0.0, 0.74, 1.2, 120.0,
         json.dumps({"niacin_mg": 11.2, "vit_b6_mg": 0.81, "phosphorus_mg": 228.0, "potassium_mg": 334.0, "calcium_mg": 11.0, "iron_mg": 0.74, "vit_c_mg": 1.2, "sodium_mg": 65.0, "allergenic_pollen_cross": [], "notes": "Lean complete animal protein. Free from Alpha-gal."}), "USDA Foundation"),
        
        (6002, "Wild Atlantic Salmon (Raw)", "Fish & Seafood", 100.0, 20.42, 0.00, 13.42, 0.0, 0.80, 3.9, 208.0,
         json.dumps({"epa_dha_omega3_g": 2.15, "vit_d_mcg": 11.0, "vit_b12_mcg": 3.18, "selenium_mcg": 36.5, "calcium_mg": 9.0, "iron_mg": 0.80, "vit_c_mg": 3.9, "sodium_mg": 59.0, "allergenic_pollen_cross": [], "notes": "MAJOR ALLERGEN (Fish - parvalbumin). Rich EPA/DHA Omega-3."}), "USDA Foundation"),
        
        (6003, "Pastured Whole Chicken Egg (Raw)", "Eggs & Dairy", 100.0, 12.56, 0.72, 9.51, 0.0, 1.75, 0.0, 143.0,
         json.dumps({"choline_mg": 293.8, "vit_a_mcg": 160.0, "vit_d_mcg": 2.0, "lutein_zeaxanthin_mcg": 503.0, "calcium_mg": 56.0, "iron_mg": 1.75, "vit_c_mg": 0.0, "sodium_mg": 142.0, "allergenic_pollen_cross": [], "notes": "MAJOR ALLERGEN (Egg - ovomucoid / ovalbumin). Top choline source."}), "USDA Foundation"),
        
        (6004, "Raw Shrimp (Crustacean)", "Fish & Seafood", 100.0, 20.10, 0.20, 0.51, 0.0, 0.52, 0.0, 85.0,
         json.dumps({"selenium_mcg": 29.6, "astaxanthin_mg": 1.2, "potassium_mg": 259.0, "calcium_mg": 64.0, "iron_mg": 0.52, "vit_c_mg": 0.0, "sodium_mg": 119.0, "allergenic_pollen_cross": [], "notes": "MAJOR ALLERGEN (Crustacean shellfish - tropomyosin)."}), "USDA Foundation"),
        
        (6005, "Beef Tenderloin (Grass-fed, Raw)", "Meat", 100.0, 21.80, 0.00, 6.20, 0.0, 2.65, 0.0, 143.0,
         json.dumps({"vit_b12_mcg": 2.1, "zinc_mg": 4.5, "calcium_mg": 18.0, "iron_mg": 2.65, "vit_c_mg": 0.0, "sodium_mg": 55.0, "alpha_gal_present": True, "allergenic_pollen_cross": [], "notes": "Mammalian meat. CONTAINS ALPHA-GAL (Galactose-alpha-1,3-galactose)."}), "USDA SR Legacy")
    ]

    cursor.executemany("""
    INSERT INTO raw_foods (
        fdc_id, name, category, serving_g, protein, carbs, fat, fiber, iron, vit_c, energy_kcal, nutrients_json, source
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """, raw_foods)

    # 5. Insert Sample Plate items for Profile 1
    sample_plate = [
        (1, 1, 1001, 150.0), # 150g Apple
        (2, 1, 2001, 80.0),  # 80g Spinach
        (3, 1, 3001, 120.0), # 120g Chickpeas
        (4, 1, 4003, 15.0)   # 15g Chia seeds
    ]

    cursor.executemany("""
    INSERT INTO plate (id, profile_id, food_id, quantity_g)
    VALUES (?, ?, ?, ?);
    """, sample_plate)

    conn.commit()
    conn.close()
    print(f"Successfully generated nutrition_app.db at: {db_path}")

if __name__ == "__main__":
    target_path = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "databases", "nutrition_app.db")
    create_database(target_path)
