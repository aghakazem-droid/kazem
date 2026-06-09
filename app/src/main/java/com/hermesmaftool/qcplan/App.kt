package com.hermesmaftool.qcplan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

private val Navy = Color(0xFF12345B)
private val Blue = Color(0xFF2D76C3)
private val Green = Color(0xFF1F8C4D)
private val Orange = Color(0xFFE8891A)
private val Purple = Color(0xFF6A3FB0)
private val Red = Color(0xFFC43B3B)
private val SoftBg = Color(0xFFF4F7FB)
private val CardBg = Color(0xFFFFFFFF)
private val Muted = Color(0xFF6C7A8D)
private val TextDark = Color(0xFF223042)

enum class AppLang { EN, FA }

data class LText(val en: String, val fa: String) {
    fun value(lang: AppLang): String = if (lang == AppLang.EN) en else fa
}

enum class AppScreen(val icon: String, val title: LText) {
    DASHBOARD("📊", LText("Dashboard", "داشبورد")),
    RAW_MATERIAL("🧶", LText("Raw Material", "مواد اولیه")),
    PICKLING("🧪", LText("Pickling / Phosphating", "اسیدشویی / فسفاته")),
    SPHEROIDIZE("🔥", LText("Spheroidizing", "اسفروئیدایز")),
    FINAL_INSPECTION("📦", LText("Final Inspection", "بازرسی نهایی")),
    MTC("📜", LText("MTC", "MTC")),
}

data class StageItem(
    val number: Int,
    val icon: String,
    val color: Color,
    val title: LText,
    val subtitle: LText,
)

private val stages = listOf(
    StageItem(1, "🧶", Blue, LText("Wire Rod Inspection", "بازرسی مفتول ورودی"), LText("Chemistry + surface + traceability", "شیمی + سطح + ردیابی")),
    StageItem(2, "🧪", Green, LText("Chemical Cleaning", "شستشوی شیمیایی"), LText("Pickling, phosphating, boraxing", "اسیدشویی، فسفاته، بوراکس")),
    StageItem(3, "⚙️", Blue, LText("Primary Drawing", "کشش اولیه"), LText("Wire tolerance and surface control", "تلرانس سیم و کنترل سطح")),
    StageItem(4, "🔥", Orange, LText("Spheroidize Annealing", "اسفروئیدایز آنیلینگ"), LText("Heat treatment + decarb + structure", "عملیات حرارتی + دکربوره + ریزساختار")),
    StageItem(5, "🛠️", Blue, LText("Final Drawing", "کشش نهایی"), LText("Final diameter and lubricant film", "قطر نهایی و فیلم روانکار")),
    StageItem(6, "📦", Green, LText("Final Inspection", "بازرسی نهایی"), LText("Mechanical + dimensional + packing", "آزمون‌ها + ابعاد + بسته‌بندی")),
    StageItem(7, "📜", Purple, LText("MTC Release", "صدور MTC"), LText("Certificate + tag + release", "گواهی + تگ + آزادسازی")),
)

@Composable
fun HermesQcApp() {
    var lang by rememberSaveable { mutableStateOf(AppLang.EN) }
    var screen by rememberSaveable { mutableStateOf(AppScreen.DASHBOARD) }

    val direction = if (lang == AppLang.EN) LayoutDirection.Ltr else LayoutDirection.Rtl
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        HermesQcRoot(lang = lang, screen = screen, onLanguageChange = { lang = it }, onScreenChange = { screen = it })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HermesQcRoot(
    lang: AppLang,
    screen: AppScreen,
    onLanguageChange: (AppLang) -> Unit,
    onScreenChange: (AppScreen) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(lang)
                AppScreen.entries.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text("${item.icon}  ${item.title.value(lang)}", fontWeight = FontWeight.Bold) },
                        selected = item == screen,
                        onClick = {
                            onScreenChange(item)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Blue.copy(alpha = 0.12f),
                            selectedTextColor = Blue,
                            unselectedTextColor = TextDark,
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (lang == AppLang.EN) {
                                "Hermes QC Plan"
                            } else {
                                "برنامه کنترل کیفیت هرمس"
                            },
                            fontWeight = FontWeight.Black,
                            color = Navy,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Navy)
                        }
                    },
                    actions = {
                        TextButton(onClick = { onLanguageChange(AppLang.EN) }) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = if (lang == AppLang.EN) Blue else Muted)
                            Spacer(Modifier.width(4.dp))
                            Text("EN", color = if (lang == AppLang.EN) Blue else Muted, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { onLanguageChange(AppLang.FA) }) {
                            Text("FA", color = if (lang == AppLang.FA) Orange else Muted, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = SoftBg,
        ) { padding ->
            when (screen) {
                AppScreen.DASHBOARD -> DashboardScreen(Modifier.padding(padding), lang)
                AppScreen.RAW_MATERIAL -> RawMaterialScreen(Modifier.padding(padding), lang)
                AppScreen.PICKLING -> PicklingScreen(Modifier.padding(padding), lang)
                AppScreen.SPHEROIDIZE -> SpheroidizingScreen(Modifier.padding(padding), lang)
                AppScreen.FINAL_INSPECTION -> FinalInspectionScreen(Modifier.padding(padding), lang)
                AppScreen.MTC -> MtcScreen(Modifier.padding(padding), lang)
            }
        }
    }
}

@Composable
fun DrawerHeader(lang: AppLang) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(if (lang == AppLang.EN) "Hermes Maftool" else "هرمس مفتول", fontWeight = FontWeight.Black, color = TextDark)
                    Text(
                        if (lang == AppLang.EN) "Full bilingual QC application" else "اپلیکیشن دوزبانه کامل QC",
                        color = Muted,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "ASTM F2282-03 + ISO 9717 + BS EN 10263-1",
                color = Orange,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun DashboardScreen(modifier: Modifier = Modifier, lang: AppLang) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { IntroCard(lang) }
        item { StageFlowCard(lang) }
        item {
            QuickSummaryCard(
                title = LText("Standards included", "استانداردهای پوشش‌داده‌شده"),
                color = Blue,
                lang = lang,
                chips = listOf(
                    LText("ASTM F2282", "ASTM F2282"),
                    LText("ISO 9717", "ISO 9717"),
                    LText("BS EN 10263-1", "BS EN 10263-1"),
                    LText("Hermes internal boards", "بردهای داخلی هرمس"),
                )
            )
        }
        item {
            QuickSummaryCard(
                title = LText("CTQ focus", "تمرکز CTQ"),
                color = Green,
                lang = lang,
                chips = listOf(
                    LText("Dimensional", "ابعادی"),
                    LText("Mechanical", "مکانیکی"),
                    LText("Metallurgical", "متالوژیکی"),
                    LText("Surface", "سطحی"),
                    LText("Traceability", "ردیابی"),
                )
            )
        }
        item {
            InfoCard(
                title = LText("Decision logic", "منطق تصمیم‌گیری"),
                color = Purple,
                lang = lang,
                items = listOf(
                    LText("PASS", "PASS") to LText("Full conformity to standards + company process gates", "انطباق کامل با استانداردها + گیت‌های فرآیندی شرکت"),
                    LText("HOLD / REJECT", "HOLD / REJECT") to LText("Any nonconforming chemistry, surface, decarb, test result or traceability", "هرگونه عدم انطباق در شیمی، سطح، دکربوره، نتیجه آزمون یا ردیابی"),
                )
            )
        }
        item { BottomInfo(lang) }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
fun RawMaterialScreen(modifier: Modifier = Modifier, lang: AppLang) {
    DetailScreen(
        modifier = modifier,
        lang = lang,
        title = LText("Raw Material / Wire Rod", "مواد اولیه / وایرراد"),
        subtitle = LText("ASTM F2282 + BS EN 10263-1 + internal wire rod QC board", "ASTM F2282 + BS EN 10263-1 + برد داخلی QC وایرراد"),
        color = Blue,
        sections = listOf(
            DetailSection(
                LText("Applicable grades", "گریدهای قابل استفاده"),
                listOf(
                    LText("1010 / 10B21 / 10B38", "1010 / 10B21 / 10B38")
                )
            ),
            DetailSection(
                LText("ASTM F2282 key controls", "کنترل‌های کلیدی ASTM F2282"),
                listOf(
                    LText("Chemistry by grade", "ترکیب شیمیایی بر اساس گرید"),
                    LText("Residual limits", "حدود عناصر باقیمانده"),
                    LText("Rod tolerance", "تلرانس راد"),
                    LText("Surface defect limit", "حد عیب سطحی"),
                )
            ),
            DetailSection(
                LText("Internal wire rod QC board", "برد داخلی کنترل وایرراد"),
                listOf(
                    LText("Size / diameter ovality / diameter tolerance / surface quality", "سایز / اوالیتی قطر / تلرانس قطر / کیفیت سطح"),
                    LText("Upsetting / tensile / elongation / constriction", "آپست / کشش / ازدیاد طول / کاهش سطح مقطع"),
                    LText("Microstructure / macrostructure / inclusion rating / grain size / decarb depth", "ریزساختار / درشت‌ساختار / درجه‌بندی آخال / اندازه دانه / عمق دکربوره"),
                )
            ),
            DetailSection(
                LText("BS EN 10263-1 add-on", "تکمیل با BS EN 10263-1"),
                listOf(
                    LText("No cracks after upsetting = evidence of surface soundness", "عدم وجود ترک بعد از آپست = نشانه سلامت سطح"),
                    LText("Tensile test reference: EN ISO 6892-1", "مرجع تست کشش: EN ISO 6892-1"),
                    LText("Hardness reference: EN ISO 6508-1", "مرجع سختی: EN ISO 6508-1"),
                )
            ),
        )
    )
}

@Composable
fun PicklingScreen(modifier: Modifier = Modifier, lang: AppLang) {
    DetailScreen(
        modifier = modifier,
        lang = lang,
        title = LText("Chemical Cleaning / Coating", "شستشوی شیمیایی / پوشش‌دهی"),
        subtitle = LText("Internal plant parameters + ISO 9717 + ASTM coating logic", "پارامترهای داخلی کارخانه + ISO 9717 + منطق پوشش در ASTM"),
        color = Green,
        sections = listOf(
            DetailSection(
                LText("Internal process checkpoints", "نقاط کنترل داخلی فرآیند"),
                listOf(
                    LText("Pickling → Acidity", "Pickling → اسیدیته"),
                    LText("Water tank → pH", "Water tank → pH"),
                    LText("Boraxing → Density", "Boraxing → دانسیته"),
                    LText("Phosphating → Total acid / Free acid / Calcium ion / Iron ion / Fisher point / Temperature", "Phosphating → Total acid / Free acid / Calcium ion / Iron ion / Fisher point / Temperature"),
                )
            ),
            DetailSection(
                LText("ISO 9717 highlights", "نکات مهم ISO 9717"),
                listOf(
                    LText("Phosphate conversion coating assists cold forming and modifies friction", "پوشش تبدیلی فسفاته به شکل‌دهی سرد کمک می‌کند و اصطکاک را تنظیم می‌کند"),
                    LText("Guidance coating mass for steel wire drawing: 5–15 g/m²", "راهنمای جرم پوشش برای کشش سیم فولادی: 5 تا 15 g/m²"),
                    LText("Guidance coating mass for cold heading / cold extrusion: 5–20 g/m²", "راهنمای جرم پوشش برای cold heading / cold extrusion: 5 تا 20 g/m²"),
                    LText("Untreated phosphate coating alone is not enough for corrosion protection", "پوشش فسفاته بدون پس‌تیمار به‌تنهایی برای حفاظت خوردگی کافی نیست"),
                )
            ),
            DetailSection(
                LText("ASTM coating side", "سمت ASTM در پوشش"),
                listOf(
                    LText("Pickle + lime dip", "اسیدشویی + آهک"),
                    LText("Zn phosphate + lime", "فسفاته + آهک"),
                    LText("Zn phosphate + lubricant", "فسفاته + لوبریکنت"),
                    LText("Alternate polymer by agreement", "پوشش پلیمری جایگزین با توافق"),
                )
            ),
        )
    )
}

@Composable
fun SpheroidizingScreen(modifier: Modifier = Modifier, lang: AppLang) {
    DetailScreen(
        modifier = modifier,
        lang = lang,
        title = LText("Spheroidizing / Forming Readiness", "اسفروئیدایز / آمادگی برای فرم‌دهی"),
        subtitle = LText("Heat treatment, grain, spheroidization and decarburization", "عملیات حرارتی، اندازه دانه، اسفروئیدایز و دکربوره"),
        color = Orange,
        sections = listOf(
            DetailSection(
                LText("Heat treatment / structure", "عملیات حرارتی / ریزساختار"),
                listOf(
                    LText("Minimum spheroidization: G2 / L2", "حداقل اسفروئیدایز: G2 / L2"),
                    LText("Optimum: ≥ 90% spheroidization", "حالت بهینه: اسفروئیدایز ≥ 90%"),
                    LText("Grain size: Fine > 5 / Coarse 1–5", "اندازه دانه: ریز > 5 / درشت 1 تا 5"),
                )
            ),
            DetailSection(
                LText("Mechanical snapshot", "خلاصه خواص مکانیکی"),
                listOf(
                    LText("1010 → Spheroidized max TS 379 MPa", "1010 → حداکثر استحکام در Spheroidized برابر 379 MPa"),
                    LText("10B21 → 490 MPa", "10B21 → 490 MPa"),
                    LText("10B38 → 565 MPa", "10B38 → 565 MPa"),
                )
            ),
            DetailSection(
                LText("Decarburization", "دکربوره"),
                listOf(
                    LText("Method: ASTM E1077", "روش: ASTM E1077"),
                    LText("Applies to killed steels with C > 0.15%", "برای فولادهای killed با C > 0.15% اعمال می‌شود"),
                    LText("Control logic: Free Ferrite + TAAD + Worst Location", "منطق کنترل: Free Ferrite + TAAD + Worst Location"),
                )
            ),
        )
    )
}

@Composable
fun FinalInspectionScreen(modifier: Modifier = Modifier, lang: AppLang) {
    DetailScreen(
        modifier = modifier,
        lang = lang,
        title = LText("Final Inspection / Packaging", "بازرسی نهایی / بسته‌بندی"),
        subtitle = LText("Mechanical, dimensional, surface and packaging release", "آزادسازی مکانیکی، ابعادی، سطحی و بسته‌بندی"),
        color = Green,
        sections = listOf(
            DetailSection(
                LText("Final inspection checklist", "چک‌لیست بازرسی نهایی"),
                listOf(
                    LText("Maximum tensile strength", "حداکثر استحکام کششی"),
                    LText("Reduction of area", "کاهش سطح مقطع"),
                    LText("Grain size / metallography", "اندازه دانه / متالوگرافی"),
                    LText("Decarburization", "دکربوره"),
                    LText("Dimensional check", "کنترل ابعادی"),
                    LText("Surface / coating integrity", "سلامت سطح / پوشش"),
                )
            ),
            DetailSection(
                LText("Sampling", "نمونه‌برداری"),
                listOf(
                    LText("Mechanical tests: 20% random coils/bundles", "آزمون‌های مکانیکی: 20% کویل‌ها / باندل‌های تصادفی"),
                    LText("Minimum 2 tests per lot", "حداقل 2 تست در هر lot"),
                    LText("R/A not applicable below 0.092 in. wire", "R/A برای سیم‌های زیر 0.092 in قابل اعمال نیست"),
                )
            ),
            DetailSection(
                LText("Packaging / loading", "بسته‌بندی / بارگیری"),
                listOf(
                    LText("Supplier / grade / heat no. / diameter", "تأمین‌کننده / گرید / Heat No. / قطر"),
                    LText("Packaging method per PO", "روش بسته‌بندی بر اساس PO"),
                    LText("QC sign-off before dispatch", "تأیید QC قبل از ارسال"),
                )
            ),
        )
    )
}

@Composable
fun MtcScreen(modifier: Modifier = Modifier, lang: AppLang) {
    DetailScreen(
        modifier = modifier,
        lang = lang,
        title = LText("MTC / Release", "MTC / آزادسازی"),
        subtitle = LText("Traceability, mandatory tag, certificate content and final decision", "ردیابی، تگ اجباری، محتوای گواهی و تصمیم نهایی"),
        color = Purple,
        sections = listOf(
            DetailSection(
                LText("Certificate / test report", "گواهی / گزارش آزمون"),
                listOf(
                    LText("Chemical analysis / heat analysis", "آنالیز شیمیایی / Heat analysis"),
                    LText("Mechanical properties", "خواص مکانیکی"),
                    LText("Grain size if required", "اندازه دانه در صورت نیاز"),
                    LText("Decarburization data if checked", "نتایج دکربوره در صورت کنترل"),
                    LText("Conformance to ASTM F2282", "اعلام انطباق با ASTM F2282"),
                )
            ),
            DetailSection(
                LText("Mandatory tag", "تگ اجباری"),
                listOf(
                    LText("Supplier / trademark", "نام / علامت تأمین‌کننده"),
                    LText("Steel grade", "گرید فولاد"),
                    LText("Heat number / traceable code", "Heat number / کد ردیابی"),
                    LText("Diameter", "قطر"),
                    LText("Mill order", "Mill order"),
                )
            ),
            DetailSection(
                LText("Decision rule", "قانون تصمیم"),
                listOf(
                    LText("PASS = full conformity to standards + company gates", "PASS = انطباق کامل با استانداردها + گیت‌های شرکت"),
                    LText("HOLD / REJECT = any traceability or technical gap", "HOLD / REJECT = هر شکاف فنی یا ردیابی"),
                )
            ),
        )
    )
}

@Composable
fun IntroCard(lang: AppLang) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 12.dp, end = 12.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.White, Color(0xFFF8FAFD))))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo2),
                    contentDescription = null,
                    modifier = Modifier.size(74.dp).clip(RoundedCornerShape(18.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(if (lang == AppLang.EN) "Hermes Maftool" else "هرمس مفتول", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextDark)
                    Text(if (lang == AppLang.EN) "Professional bilingual QC APK source" else "سورس نسخه حرفه‌ای APK دوزبانه", color = Muted, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (lang == AppLang.EN) "Full Information Version" else "نسخه کامل اطلاعاتی",
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                color = Navy,
            )
            Text(
                text = "ASTM F2282-03 + ISO 9717 + BS EN 10263-1",
                color = Orange,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = if (lang == AppLang.EN) {
                    "Multi-page native Android application with navigation, separate QC screens, bilingual content and offline access."
                } else {
                    "اپلیکیشن نیتیو اندروید چندصفحه‌ای با منوی ناوبری، صفحات جداگانه QC، محتوای دوزبانه و دسترسی آفلاین."
                },
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun StageFlowCard(lang: AppLang) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                if (lang == AppLang.EN) "7-Stage Process Flow" else "فلو 7 مرحله‌ای فرآیند",
                color = Muted,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(stages) { stage -> StageCard(stage, lang) }
            }
        }
    }
}

@Composable
fun QuickSummaryCard(title: LText, color: Color, lang: AppLang, chips: List<LText>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color)
                    .padding(12.dp)
            ) {
                Text(title.value(lang), color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
            Column(Modifier.padding(12.dp)) {
                ChipCloud(chips, color, lang)
            }
        }
    }
}

@Composable
fun InfoCard(title: LText, color: Color, lang: AppLang, items: List<Pair<LText, LText>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color)
                    .padding(12.dp)
            ) {
                Text(title.value(lang), color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
            Column(Modifier.padding(12.dp)) {
                KeyValueList(lang, items)
            }
        }
    }
}

@Composable
fun BottomInfo(lang: AppLang) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF26303D)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = if (lang == AppLang.EN) "Why this version?" else "چرا این نسخه؟",
                color = Color(0xFFFFD18A),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(10.dp))
            FooterLine(if (lang == AppLang.EN) "Multi-page professional structure" else "ساختار حرفه‌ای چندصفحه‌ای", Color(0xFF88D3A0))
            FooterLine(if (lang == AppLang.EN) "Bilingual English / فارسی" else "دوزبانه فارسی / انگلیسی", Color(0xFF9FC3EB))
            FooterLine(if (lang == AppLang.EN) "Offline industrial reference app" else "اپ مرجع صنعتی آفلاین", Color(0xFFF1C57D))
        }
    }
}

@Composable
fun FooterLine(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color(0xFFD7DDE8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    lang: AppLang,
    title: LText,
    subtitle: LText,
    color: Color,
    sections: List<DetailSection>,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(title.value(lang), fontWeight = FontWeight.Black, fontSize = 24.sp, color = color)
                Text(subtitle.value(lang), color = Muted, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        sections.forEach { section ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(section.title.value(lang), color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(10.dp))
                    section.points.forEachIndexed { index, point ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = if (index % 2 == 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (index % 2 == 0) color else Orange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = point.value(lang),
                                color = TextDark,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (index != section.points.lastIndex) {
                            Spacer(Modifier.height(8.dp))
                            Divider(color = Color(0xFFE8EDF3))
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

data class DetailSection(val title: LText, val points: List<LText>)
