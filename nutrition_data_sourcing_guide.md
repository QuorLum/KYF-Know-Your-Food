# Sourcing Nutrition Data for Fruits, Vegetables & Non-Veg (Per 100g)

## 1. Primary global source: USDA FoodData Central (FDC)

The best single source for embedding into an app.

- **License:** Public domain (CC0) — free to use commercially, no attribution legally required (though citing it is good practice)
- **Coverage:** 300,000+ foods across 5 data types. For raw/whole foods, you want two of them:
  - **Foundation Foods** (~350 foods) — lab-analyzed, most detailed nutrient profiles, best accuracy
  - **SR Legacy** (~7,800 foods) — the classic USDA reference set, broader coverage of raw *and* cooked forms of fruits, vegetables, meats, poultry, fish, dairy, eggs
  - (Skip "Branded" and "Survey/FNDDS" data types — those are packaged products and dietary-survey composites, not raw ingredients)
- **Get access:**
  - Free API key: `fdc.nal.usda.gov/api-key-signup` (raises you from the throttled `DEMO_KEY` to 1,000 requests/hour)
  - **For embedding (what you actually want):** don't build on live API calls — go to the "Download Data" page on `fdc.nal.usda.gov` and pull the full **bulk CSV/JSON dataset**. This gives you the whole Foundation + SR Legacy set as files you can process once and bundle into your app, with zero runtime network dependency or rate limit.
- **Data shape:** each food has an FDC ID, a description, a food category, and a `foodNutrients[]` array — nutrient ID, name, amount, unit. Energy, macros, ~30 vitamins/minerals, full amino acid profile, and fatty acid breakdown are all present for Foundation Foods (up to 140+ possible nutrient fields, though not every field is populated for every food).
- **Quick API example** (for exploring before you commit to the bulk download):
```bash
curl "https://api.nal.usda.gov/fdc/v1/foods/search?query=spinach&dataType=Foundation,SR%20Legacy&api_key=YOUR_KEY"
```

## 2. For Indian-specific foods

USDA covers globally common produce well but is thin on Indian vegetables, dals, and regional items.

| Source | Coverage | License status |
|---|---|---|
| **IFCT 2017** (NIN/ICMR, Hyderabad) | 528 foods, 150+ nutrient components — the gold standard for Indian food data | Reproduction for personal/research use is fine; **storing or reproducing it electronically inside a product requires written permission from NIN** — email them before embedding |
| **Indian Nutrient Databank (INDB)**, 2024 | 1,095 individual foods + 1,014 common recipes, built from IFCT with gaps filled from UK/US data | Published as an explicitly open-access resource (Vijayakumar et al., 2024, *Current Developments in Nutrition*) — check current terms, but designed to solve exactly the problem IFCT's license creates |
| **FKG.in** (Ashoka University) | Research project combining IFCT/INDB/other sources with LLM-based gap-filling for Indian recipes | More experimental / research-stage, worth watching rather than relying on yet |

Practical move: use INDB (or get NIN's written permission for IFCT) as your India-specific layer on top of the USDA base.

## 3. Beyond USDA + India: FAO/INFOODS

FAO's INFOODS program aggregates national food composition tables from 100+ countries (IFCT itself is one of the tables it indexes). It's not a single downloadable unified dataset — more a directory pointing to each country's own table, each with its own license. Useful if you need a specific country's official values for a regional food, but not a one-stop bulk source.

## 4. Practical build approach

1. Pull the USDA bulk download (Foundation + SR Legacy) as your global base layer.
2. Layer in India-specific foods via INDB (or licensed IFCT) for what USDA doesn't cover well.
3. Normalize both into one schema: food name, category, veg/non-veg flag, per-100g values for whichever nutrients your app needs, plus `source` and `source_version` fields.
4. Bundle the result as a local SQLite/JSON asset in the app — matches your "embed directly" plan, and sidesteps both the live rate limit and any network dependency at runtime.
5. Decide explicitly whether each entry is raw or cooked (a "chicken breast" varies hugely between the two) — this is a data-modeling choice you'll need to make regardless of what the eventual app does with it.

## 5. One honest limit

No dataset — USDA, IFCT, INDB, or FAO combined — covers literally every fruit, vegetable, and animal product species on Earth. Thousands of wild or hyper-regional edible plants have simply never been lab-analyzed for nutrient content. What USDA + IFCT/INDB together give you is coverage of the commercially and nutritionally significant foods — low thousands of entries — which is genuinely what almost every commercial nutrition app in the world is built on. True total completeness isn't something any existing source can offer; comprehensive-at-the-common-and-regional-level is the realistic and achievable target.
