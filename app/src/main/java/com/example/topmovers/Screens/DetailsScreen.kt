package com.example.topmovers.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Retrofit.CompanyInfo
import com.example.topmovers.Retrofit.TopMover
import com.example.topmovers.ViewModel.DetailsViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    ticker: String,
    price: String,
    changePercentage: String,
    onBackClicked: () -> Unit,
    changeAmount: String
)
 {
     val viewModel: DetailsViewModel = koinViewModel()


     LaunchedEffect(key1 = ticker) {
        viewModel.fetchStockDetails(ticker, "NTJBDU9U1JGKA613")
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
                actions = {
                    IconButton(onClick = { showDialog1 = true}) {
                        Icon(
                            imageVector = if (isStockInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Add to Watchlist",
                            tint = if (isStockInWatchlist) MaterialTheme.colorScheme.primary else LocalContentColor.current
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
                    viewModel = viewModel, // Pass the viewModel
                    ticker = ticker // Pass the ticker
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
                changePercentage = changePercentage
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
            Text(text = info.name ?: "N/A", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "${info.symbol} | ${info.assetType ?: "N/A"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
            if (isChartLoading) {
                CircularProgressIndicator()
            } else {
                Chart(
                    chart = lineChart(),
                    chartModelProducer = chartEntryModelProducer,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            timeRanges.forEach { range ->
                TextButton(
                    onClick = { viewModel.fetchChartData(ticker, range) },
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
    Column {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val range = (high - low).toFloat()
            val currentPositionPercent = if (range > 0) ((current - low).toFloat() / range) else 0.5f
            val offset = (this.maxWidth * currentPositionPercent).coerceIn(0.dp, this.maxWidth)

            Divider(modifier = Modifier.align(Alignment.CenterStart), thickness = 4.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            Column(modifier = Modifier.offset(x = offset - 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Current price: $$current", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("52-Week Low\n$${"%.2f".format(low)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text("52-Week High\n$${"%.2f".format(high)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray, textAlign = TextAlign.End)
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