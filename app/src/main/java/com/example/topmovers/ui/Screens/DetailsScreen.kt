package com.example.topmovers.ui.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.topmovers.data.model.CompanyInfo
import com.example.topmovers.data.model.TopMover
import com.example.topmovers.ViewModel.DetailsViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import org.koin.androidx.compose.koinViewModel
import com.example.topmovers.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    ticker: String,
    price: String,
    changePercentage: String,
    onBackClicked: () -> Unit,
    changeAmount: String
) {
    val viewModel: DetailsViewModel = koinViewModel()

    LaunchedEffect(key1 = ticker) {
        viewModel.fetchStockDetails(ticker, BuildConfig.ALPHA_VANTAGE_API_KEY)
    }

    val companyInfo = viewModel.companyInfo
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val allWatchlists by viewModel.allWatchlists.collectAsState()
    val isStockInWatchlist by viewModel.isStockInWatchlist(ticker).collectAsState()

    var showDialog1 by remember { mutableStateOf(false) }

    if (showDialog1) {
        AddToWatchlistDialog(
            existingWatchlists = allWatchlists,
            onDismiss = { showDialog1 = false },
            onConfirm = { watchlistId, newName ->
                val stock = TopMover(ticker, price, changeAmount, changePercentage, "")

                if (newName != null) {
                    viewModel.createNewWatchlistAndAddStock(newName, stock)
                } else if (watchlistId != null) {
                    viewModel.addStockToWatchlist(stock, watchlistId)
                }
                showDialog1 = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details Screen") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showDialog1 = true}) {
                        Icon(
                            imageVector = Icons.Default.Bookmark ,
                            contentDescription = "Add to Watchlist",
                            tint = if (isStockInWatchlist) Color.Yellow else Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                companyInfo != null -> DetailsContentNew(
                    info = companyInfo,
                    currentPrice = price,
                    changePercentage = changePercentage,
                    viewModel = viewModel,
                    ticker = ticker
                )
            }
        }
    }
}

@Composable
private fun DetailsContentNew(
    info: CompanyInfo,
    currentPrice: String,
    changePercentage: String,
    viewModel: DetailsViewModel,
    ticker: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            StockHeaderNew(
                info = info,
                currentPrice = currentPrice,
                changePercentage = changePercentage,
                ticker = ticker
            )
        }
        item {
            StockChartSection(viewModel = viewModel, ticker = ticker)
        }
        item { AboutSectionNew(info = info) }
        item {
            KeyStatsSectionNew(
                info = info,
                currentPrice = currentPrice
            )
        }
    }
}

@Composable
private fun StockHeaderNew(
    ticker: String,
    info: CompanyInfo,
    currentPrice: String,
    changePercentage: String
) {
    val isGain = !changePercentage.startsWith("-")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = "Company Logo",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = info.name ?: ticker, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "$ticker | ${info.assetType ?: "N/A"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "$$currentPrice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = changePercentage,
                style = MaterialTheme.typography.bodySmall,
                color = if (isGain) Color(0xFF00C853) else Color(0xFFD50000)
            )
        }
    }
}

@Composable
private fun StockChartSection(viewModel: DetailsViewModel, ticker: String) {
    val isChartLoading = viewModel.isChartLoading
    val chartData = viewModel.chartData
    val selectedTimeRange = viewModel.selectedTimeRange
    val chartErrorMessage = viewModel.chartErrorMessage

    val chartEntryModelProducer = ChartEntryModelProducer(
        chartData.mapIndexed { index, stockDataPoint ->
            FloatEntry(x = index.toFloat(), y = stockDataPoint.close.toFloat())
        }
    )

    val timeRanges = listOf("1D", "1W", "1M", "6M", "1Y")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isChartLoading -> {
                    CircularProgressIndicator()
                }
                chartErrorMessage != null -> {
                    Text(
                        text = chartErrorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartEntryModelProducer,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            timeRanges.forEach { range ->
                TextButton(
                    onClick = { viewModel.fetchChartData(ticker, range, BuildConfig.ALPHA_VANTAGE_API_KEY) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (selectedTimeRange == range) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Gray
                        }
                    )
                ) {
                    Text(
                        text = range,
                        fontWeight = if (selectedTimeRange == range) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun AboutSectionNew(info: CompanyInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "About ${info.name ?: ""}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = info.description ?: "No description available.",
            style = MaterialTheme.typography.bodyMedium.copy(
                lineBreak = LineBreak.Paragraph,
                hyphens = Hyphens.Auto
            )
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            info.industry?.let { if (it.isNotBlank()) Tag(text = "Industry: $it") }
            info.sector?.let { if (it.isNotBlank()) Tag(text = "Sector: $it") }
        }
    }
}

@Composable
private fun KeyStatsSectionNew(info: CompanyInfo, currentPrice: String) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        PriceRangeIndicator(
            low = info.week52Low?.toDoubleOrNull() ?: 0.0,
            high = info.week52High?.toDoubleOrNull() ?: 0.0,
            current = currentPrice.toDoubleOrNull() ?: 0.0
        )
        KeyStatsGrid(info = info)
    }
}


@Composable
private fun PriceRangeIndicator(low: Double, high: Double, current: Double) {
    val formattedLow = "%.2f".format(low)
    val formattedHigh = "%.2f".format(high)
    val formattedCurrent = "%.2f".format(current)

    Column(modifier = Modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp)
        ) {
            val range = high - low
            val currentPositionFraction = if (range > 0) {
                ((current - low) / range).toFloat()
            } else {
                0.5f
            }

            val indicatorOffset = (maxWidth * currentPositionFraction).coerceIn(12.dp, maxWidth - 12.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape)
                    .align(Alignment.Center)
            )

            Column(
                modifier = Modifier.offset(x = indicatorOffset - 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$$formattedCurrent",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .size(24.dp) // Larger size for better visibility.
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "52-Wk Low",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    "$$formattedLow",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "52-Wk High",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.End
                )
                Text(
                    "$$formattedHigh",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}


@Composable
private fun KeyStatsGrid(info: CompanyInfo) {
    val stats = listOf(
        "Market Cap" to info.marketCap,
        "P/E Ratio" to info.peRatio,
        "Beta" to info.beta,
        "Dividend Yield" to info.dividendYield,
        "Profit Margin" to info.profitMargin
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        stats.chunked(2).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { (label, value) ->
                    StatItem(label, value, Modifier.weight(1f))
                }
                if (rowItems.size < 2) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(
            text = if (value.isNullOrBlank() || value == "None") "N/A" else value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun Tag(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}