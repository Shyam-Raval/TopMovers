<div align="center">
  <h1>TopMovers: A Modern Stock Tracking App</h1>
  <p>
    <strong>A sophisticated Android application for exploring the stock market, tracking top movers, and managing personalized watchlists. Built with Kotlin and Jetpack Compose.</strong>
  </p>
  <p>
    <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg" alt="Platform: Android">
    <img src="https://img.shields.io/badge/Language-Kotlin-blueviolet.svg" alt="Language: Kotlin">
    <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg" alt="UI: Jetpack Compose">
    <img src="https://img.shields.io/badge/Architecture-MVVM-orange.svg" alt="Architecture: MVVM">
  </p>
</div>

---

## 🚀 Project Overview

Welcome to **TopMovers**, a feature-rich Android application for a stocks broking platform. This app provides a seamless experience for users to explore the stock market, track top-gaining and top-losing stocks, manage personalized watchlists, and dive deep into detailed stock information complete with interactive price graphs.

Built entirely with **Kotlin** and leveraging the latest in Android development, this project showcases a robust, scalable architecture and a clean, modern UI designed with **Jetpack Compose**.

---

## ✨ Features

-   **Explore Screen**: The central hub of the app, displaying "Top Gainers" and "Top Losers" in a clean grid layout.
-   **Details Screen**: A comprehensive view with fundamental data, an interactive price chart (1D, 1W, 1M, 6M, 1Y), and key stats for any selected stock.
-   **Watchlist Management**: Easily create, manage, and view personalized watchlists. An elegant empty state guides new users.
-   **Advanced Search**: A powerful search function allows users to quickly find any stock by its ticker or company name, with results appearing in real-time.
-   **Dynamic Theming**: Instantly switch between a sleek light mode and an immersive dark mode from the main screen for a comfortable viewing experience. 🌗
-   **Full Lists**: A "View All" option navigates to a complete, scrollable list of all stocks within the "Top Gainers" and "Top Losers" categories.

---

## 🛠️ Architecture & Technologies

The application is built on a solid architectural foundation, ensuring maintainability, testability, and scalability.

### Architecture: MVVM

The app follows the highly-regarded **Model-View-ViewModel (MVVM)** pattern, creating a clear separation of concerns between the UI (View), business logic (ViewModel), and data layers (Model/Repository).

-   **UI Layer (`ui` package)**: Crafted entirely with **Jetpack Compose**, offering a reactive and declarative UI composed of screens that observe state from the ViewModels.
-   **ViewModel Layer (`ViewModel` package)**: Manages UI-related data, handles user interactions, and exposes UI state via Kotlin's `StateFlow`.
-   **Data Layer (`data` package)**: Utilizes the **Repository pattern** to abstract data sources (network and local database), providing a clean API for the ViewModels.

### Core Technologies

-   **Primary Language**: [Kotlin](https://kotlinlang.org/)
-   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
-   **Dependency Injection**: [Koin](https://insert-koin.io/) for its simplicity in managing application dependencies.
-   **Asynchronous Programming**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and [Flow](https://kotlinlang.org/docs/flow.html) for managing background threads and data streams.
-   **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for efficient communication with the Alpha Vantage API.
-   **JSON Parsing**: [Moshi](https://github.com/square/moshi) for fast serialization of API responses.
-   **Database & Caching**: [Room](https://developer.android.com/training/data-storage/room) for robust local data persistence and caching.
-   **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation) for handling in-app navigation.
-   **Charting**: [Vico](https://github.com/patrykandpatrick/vico), a modern charting library, for displaying beautiful line graphs.

---

## ⚡ Caching and Optimization

To deliver a fast and smooth user experience, TopMovers implements several key optimizations:

-   **Intelligent API Caching**: To minimize network requests and enhance performance, the app caches API responses using Room with expiration times (**30 mins for top movers**, **24 hours for company details**).
-   **Debounced Search**: Includes a **500ms debounce** mechanism on the search functionality to prevent excessive API calls while the user is typing.

---

## ⚙️ Setup Instructions

Follow these steps to get the project up and running on your local machine.

### 1. Get an API Key
You'll need a free API key from [**Alpha Vantage**](https://www.alphavantage.co/support/#api-key) to access the stock data endpoints.

### 2. Clone the Repository
```
git clone https://github.com/Shyam-Raval/TopMovers
```
### 3.  Add Your API Key

In the app/build.gradle.kts file, locate the buildConfigField line and replace the placeholder YOUR_API_KEY with your actual key.
```
In your app-level build.gradle.kts
buildConfigField("String", "ALPHA_VANTAGE_API_KEY", "\"YOUR_API_KEY\"")
```

### 4.  Build and Run
Open the project in Android Studio, allow Gradle to sync, and then build and run the app on an emulator or a physical device. Enjoy exploring the market!

## ⚡ App Demo Screenshots 
![WhatsApp Image 2025-09-08 at 23 04 45_b2ea6ea6](https://github.com/user-attachments/assets/8c1ed7ec-a4a9-4c6b-a8e4-c726ce68f570)
![WhatsApp Image 2025-09-08 at 23 04 22_958cbd35](https://github.com/user-attachments/assets/a58e8964-d0ae-43db-869f-cbeee010cd99)
![WhatsApp Image 2025-09-08 at 23 04 25_73f26ffb](https://github.com/user-attachments/assets/614e46ea-8e95-4e8d-aaee-f6b3fb1628d6)

