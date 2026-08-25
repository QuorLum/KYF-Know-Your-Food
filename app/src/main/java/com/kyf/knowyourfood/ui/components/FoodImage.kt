package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kyf.knowyourfood.ui.theme.*

object FoodImageHelper {

    /**
     * Maps product information to high-resolution product imagery from OpenFoodFacts CDN
     * and curated high-quality food photography.
     */
    fun getProductImageUrl(barcode: String, name: String, brand: String, category: String): String {
        val lowerName = name.lowercase()
        val lowerBrand = brand.lowercase()
        val lowerCat = category.lowercase()

        // 1. Direct Known Brand / Product Matches
        return when {
            lowerName.contains("nutella") || lowerBrand.contains("ferrero") ->
                "https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?w=500&q=80"
            lowerName.contains("coca-cola") || lowerName.contains("coke") || lowerBrand.contains("coca-cola") ->
                "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500&q=80"
            lowerName.contains("cheerios") || (lowerName.contains("oat") && lowerName.contains("cereal")) ->
                "https://images.unsplash.com/photo-1521483451569-e33803c0330c?w=500&q=80"
            lowerName.contains("doritos") || lowerName.contains("tortilla chips") || lowerName.contains("nacho") ->
                "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=500&q=80"
            lowerName.contains("oreo") || lowerName.contains("cookie") || lowerName.contains("biscuit") ->
                "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&q=80"
            lowerName.contains("greek yogurt") || lowerName.contains("yoghurt") ->
                "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500&q=80"
            lowerName.contains("milk") || lowerBrand.contains("alpro") || lowerBrand.contains("oatly") ->
                "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&q=80"
            lowerName.contains("heinz") || lowerName.contains("ketchup") || lowerName.contains("tomato sauce") ->
                "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500&q=80"
            lowerName.contains("chocolate") || lowerName.contains("dark chocolate") || lowerBrand.contains("lindt") ->
                "https://images.unsplash.com/photo-1511381939415-e44015466834?w=500&q=80"
            lowerName.contains("peanut butter") || lowerName.contains("skippy") || lowerName.contains("jif") ->
                "https://images.unsplash.com/photo-1568899307518-9938138ba566?w=500&q=80"
            lowerName.contains("pasta") || lowerName.contains("spaghetti") || lowerBrand.contains("barilla") ->
                "https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=500&q=80"
            lowerName.contains("bread") || lowerName.contains("whole wheat") || lowerName.contains("sourdough") ->
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&q=80"
            lowerName.contains("granola") || lowerName.contains("muesli") ->
                "https://images.unsplash.com/photo-1517093707567-99e52718e244?w=500&q=80"
            lowerName.contains("juice") || lowerName.contains("smoothie") ->
                "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=500&q=80"
            lowerName.contains("coffee") || lowerName.contains("espresso") ->
                "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&q=80"
            lowerName.contains("tea") || lowerName.contains("green tea") ->
                "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&q=80"
            lowerName.contains("tofu") || lowerName.contains("tempeh") || lowerName.contains("plant-based") ->
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80"
            lowerName.contains("cheese") || lowerName.contains("cheddar") || lowerName.contains("mozzarella") ->
                "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=500&q=80"
            lowerName.contains("oil") || lowerName.contains("olive oil") ->
                "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&q=80"

            // 2. OpenFoodFacts Barcode CDN Pattern (Standard EAN-13 structure)
            barcode.length >= 12 ->
                "https://images.openfoodfacts.org/images/products/${formatBarcodePath(barcode)}/front_en.400.jpg"

            // 3. Category Fallbacks
            lowerCat.contains("beverage") || lowerCat.contains("drink") ->
                "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500&q=80"
            lowerCat.contains("cereal") || lowerCat.contains("breakfast") ->
                "https://images.unsplash.com/photo-1521483451569-e33803c0330c?w=500&q=80"
            lowerCat.contains("dairy") ->
                "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&q=80"
            lowerCat.contains("snack") || lowerCat.contains("chips") ->
                "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=500&q=80"
            lowerCat.contains("sweet") || lowerCat.contains("confectionery") ->
                "https://images.unsplash.com/photo-1511381939415-e44015466834?w=500&q=80"
            else ->
                "https://images.unsplash.com/photo-1506617420156-8e4536971650?w=500&q=80"
        }
    }

    /**
     * Maps whole foods & produce to high-quality Unsplash food photos.
     */
    fun getProduceImageUrl(name: String, category: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("apple") -> "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=500&q=80"
            lower.contains("banana") -> "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=500&q=80"
            lower.contains("orange") || lower.contains("mandarin") -> "https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=500&q=80"
            lower.contains("mango") -> "https://images.unsplash.com/photo-1553279768-865429fa0078?w=500&q=80"
            lower.contains("strawberry") -> "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=500&q=80"
            lower.contains("blueberry") -> "https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=500&q=80"
            lower.contains("grape") -> "https://images.unsplash.com/photo-1537640538966-79f369143f8f?w=500&q=80"
            lower.contains("lemon") || lower.contains("lime") -> "https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?w=500&q=80"
            lower.contains("avocado") -> "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=500&q=80"
            lower.contains("papaya") -> "https://images.unsplash.com/photo-1517282009859-f000ec3b26fe?w=500&q=80"
            lower.contains("guava") -> "https://images.unsplash.com/photo-1536511135898-3f5f3e5e6fb4?w=500&q=80"
            lower.contains("pomegranate") -> "https://images.unsplash.com/photo-1541344999736-83eca872f240?w=500&q=80"
            lower.contains("watermelon") -> "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=500&q=80"
            lower.contains("pineapple") -> "https://images.unsplash.com/photo-1550258987-190a2d41a8ba?w=500&q=80"
            lower.contains("kiwi") -> "https://images.unsplash.com/photo-1518492104633-130d0cc84637?w=500&q=80"

            lower.contains("spinach") || lower.contains("palak") -> "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=500&q=80"
            lower.contains("broccoli") -> "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=500&q=80"
            lower.contains("carrot") || lower.contains("gajar") -> "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=500&q=80"
            lower.contains("tomato") -> "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500&q=80"
            lower.contains("potato") || lower.contains("aloo") -> "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=500&q=80"
            lower.contains("sweet potato") -> "https://images.unsplash.com/photo-1596097635121-14b63b7a0c19?w=500&q=80"
            lower.contains("onion") -> "https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?w=500&q=80"
            lower.contains("garlic") -> "https://images.unsplash.com/photo-1540148426945-6cf22a6b2383?w=500&q=80"
            lower.contains("bell pepper") || lower.contains("capsicum") -> "https://images.unsplash.com/photo-1563565375-f3fdfdbefa83?w=500&q=80"
            lower.contains("cucumber") -> "https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=500&q=80"
            lower.contains("cauliflower") || lower.contains("gobi") -> "https://images.unsplash.com/photo-1568584711075-3d021a7c3ca3?w=500&q=80"
            lower.contains("cabbage") -> "https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=500&q=80"
            lower.contains("mushroom") -> "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500&q=80"
            lower.contains("kale") || lower.contains("lettuce") -> "https://images.unsplash.com/photo-1524179091875-bf99a9a6fa57?w=500&q=80"
            lower.contains("beetroot") || lower.contains("beet") -> "https://images.unsplash.com/photo-1593105544559-ecb03bf76f82?w=500&q=80"
            lower.contains("ginger") -> "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=500&q=80"

            lower.contains("lentil") || lower.contains("dal") || lower.contains("moong") || lower.contains("masoor") ->
                "https://images.unsplash.com/photo-1585996746976-a010d293d1e7?w=500&q=80"
            lower.contains("chickpea") || lower.contains("chana") || lower.contains("garbanzo") ->
                "https://images.unsplash.com/photo-1515543237350-b3eea1ec8082?w=500&q=80"
            lower.contains("kidney bean") || lower.contains("rajma") || lower.contains("black bean") ->
                "https://images.unsplash.com/photo-1551462147-ff29053bfc14?w=500&q=80"
            lower.contains("edamame") || lower.contains("soybean") ->
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80"
            lower.contains("pea") || lower.contains("matar") ->
                "https://images.unsplash.com/photo-1587735243615-c03f25aaff15?w=500&q=80"

            lower.contains("almond") || lower.contains("badam") -> "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=500&q=80"
            lower.contains("walnut") || lower.contains("akhrot") -> "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500&q=80"
            lower.contains("cashew") || lower.contains("kaju") -> "https://images.unsplash.com/photo-1509912760195-55e1003e872e?w=500&q=80"
            lower.contains("pistachio") || lower.contains("pista") -> "https://images.unsplash.com/photo-1525607551316-4a8e16d1f9ba?w=500&q=80"
            lower.contains("peanut") || lower.contains("groundnut") -> "https://images.unsplash.com/photo-1568899307518-9938138ba566?w=500&q=80"
            lower.contains("chia seed") -> "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500&q=80"
            lower.contains("flax seed") || lower.contains("flaxseed") -> "https://images.unsplash.com/photo-1607672632458-9eb56696346b?w=500&q=80"
            lower.contains("pumpkin seed") -> "https://images.unsplash.com/photo-1597362925123-77861d3fbac7?w=500&q=80"
            lower.contains("sunflower seed") -> "https://images.unsplash.com/photo-1597362925123-77861d3fbac7?w=500&q=80"
            lower.contains("sesame") || lower.contains("til") -> "https://images.unsplash.com/photo-1597362925123-77861d3fbac7?w=500&q=80"

            lower.contains("oat") || lower.contains("oatmeal") -> "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=500&q=80"
            lower.contains("rice") || lower.contains("brown rice") || lower.contains("basmati") -> "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&q=80"
            lower.contains("quinoa") -> "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&q=80"
            lower.contains("millet") || lower.contains("ragi") || lower.contains("bajra") || lower.contains("jowar") ->
                "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=500&q=80"

            category.contains("Fruits", ignoreCase = true) -> "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=500&q=80"
            category.contains("Vegetables", ignoreCase = true) -> "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=500&q=80"
            category.contains("Legumes", ignoreCase = true) -> "https://images.unsplash.com/photo-1585996746976-a010d293d1e7?w=500&q=80"
            category.contains("Nuts", ignoreCase = true) -> "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=500&q=80"
            category.contains("Grains", ignoreCase = true) -> "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=500&q=80"
            else -> "https://images.unsplash.com/photo-1490818387583-1baba5e638af?w=500&q=80"
        }
    }

    fun getProduceEmoji(name: String, category: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("apple") -> "🍎"
            lower.contains("banana") -> "🍌"
            lower.contains("orange") || lower.contains("mandarin") -> "🍊"
            lower.contains("mango") -> "🥭"
            lower.contains("strawberry") -> "🍓"
            lower.contains("blueberry") -> "🫐"
            lower.contains("grape") -> "🍇"
            lower.contains("lemon") || lower.contains("lime") -> "🍋"
            lower.contains("avocado") -> "🥑"
            lower.contains("watermelon") -> "🍉"
            lower.contains("pineapple") -> "🍍"
            lower.contains("kiwi") -> "🥝"
            lower.contains("peach") -> "🍑"
            lower.contains("cherry") -> "🍒"
            lower.contains("spinach") || lower.contains("kale") || lower.contains("lettuce") -> "🥬"
            lower.contains("broccoli") -> "🥦"
            lower.contains("carrot") -> "🥕"
            lower.contains("tomato") -> "🍅"
            lower.contains("potato") -> "🥔"
            lower.contains("onion") || lower.contains("garlic") -> "🧅"
            lower.contains("pepper") || lower.contains("chili") -> "🌶️"
            lower.contains("cucumber") -> "🥒"
            lower.contains("corn") -> "🌽"
            lower.contains("mushroom") -> "🍄"
            lower.contains("lentil") || lower.contains("bean") || lower.contains("chickpea") || lower.contains("dal") -> "🫘"
            lower.contains("almond") || lower.contains("walnut") || lower.contains("nut") -> "🥜"
            lower.contains("rice") || lower.contains("oat") || lower.contains("grain") || lower.contains("wheat") -> "🌾"
            lower.contains("egg") -> "🥚"
            lower.contains("milk") || lower.contains("yogurt") || lower.contains("cheese") -> "🥛"
            lower.contains("fish") || lower.contains("salmon") || lower.contains("tuna") -> "🐟"
            lower.contains("chicken") || lower.contains("poultry") -> "🍗"
            category.contains("Fruits", ignoreCase = true) -> "🍎"
            category.contains("Vegetables", ignoreCase = true) -> "🥦"
            category.contains("Legumes", ignoreCase = true) -> "🫘"
            category.contains("Nuts", ignoreCase = true) -> "🥜"
            category.contains("Grains", ignoreCase = true) -> "🌾"
            else -> "🥗"
        }
    }

    fun getProductEmoji(category: String, name: String): String {
        val lower = (category + " " + name).lowercase()
        return when {
            lower.contains("beverage") || lower.contains("drink") || lower.contains("cola") -> "🥤"
            lower.contains("juice") -> "🧃"
            lower.contains("coffee") -> "☕"
            lower.contains("tea") -> "🍵"
            lower.contains("chocolate") || lower.contains("candy") || lower.contains("sweet") -> "🍫"
            lower.contains("cookie") || lower.contains("biscuit") -> "🍪"
            lower.contains("cereal") || lower.contains("oats") -> "🥣"
            lower.contains("snack") || lower.contains("chips") || lower.contains("crisps") -> "🍿"
            lower.contains("dairy") || lower.contains("milk") || lower.contains("yogurt") -> "🥛"
            lower.contains("cheese") -> "🧀"
            lower.contains("bread") || lower.contains("bakery") -> "🍞"
            lower.contains("pasta") || lower.contains("noodles") -> "🍝"
            lower.contains("sauce") || lower.contains("ketchup") -> "🥫"
            else -> "📦"
        }
    }

    private fun formatBarcodePath(barcode: String): String {
        return if (barcode.length >= 13) {
            "${barcode.substring(0, 3)}/${barcode.substring(3, 6)}/${barcode.substring(6, 9)}/${barcode.substring(9)}"
        } else {
            barcode
        }
    }
}

@Composable
fun FoodImageThumbnail(
    imageUrl: String,
    fallbackEmoji: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    contentDescription: String? = null
) {
    var isError by remember(imageUrl) { mutableStateOf(false) }
    var isLoading by remember(imageUrl) { mutableStateOf(true) }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Slate800, Slate850)
                )
            )
            .border(1.dp, GlassBorderDark, shape),
        contentAlignment = Alignment.Center
    ) {
        if (!isError && imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                onLoading = { isLoading = true },
                onSuccess = { isLoading = false; isError = false },
                onError = { isLoading = false; isError = true },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isLoading && !isError) {
            // Subtle loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fallbackEmoji,
                    fontSize = (size.value * 0.45f).sp
                )
            }
        } else if (isError || imageUrl.isBlank()) {
            // Fallback Graphic Badge
            Text(
                text = fallbackEmoji,
                fontSize = (size.value * 0.5f).sp
            )
        }
    }
}

@Composable
fun FoodImageBanner(
    imageUrl: String,
    fallbackEmoji: String,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    contentDescription: String? = null
) {
    var isError by remember(imageUrl) { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Slate800, Slate900)
                )
            )
            .border(1.dp, GlassBorderDark, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!isError && imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                onError = { isError = true },
                modifier = Modifier.fillMaxSize()
            )

            // Scrim gradient for readability over image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Slate950.copy(alpha = 0.7f))
                        )
                    )
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = fallbackEmoji, fontSize = 54.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nutrition Guard",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400
                )
            }
        }
    }
}
