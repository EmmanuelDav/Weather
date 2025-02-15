# Weather App 🌦️
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)]

Weather App is a sleek and modern Android application that fetches real-time weather data using the [OpenWeatherMap API](https://openweathermap.org/api). It provides users with up-to-date weather information, including temperature, humidity, wind speed, and more, based on their location or a searched city. The app is designed to showcase best practices in modern Android development, clean architecture, and robust testing.

The codebase emphasizes the following key technologies and practices:
1. **[ViewBinding](https://developer.android.com/topic/libraries/view-binding)** - For type-safe view interaction.
2. **[Navigation Components](https://developer.android.com/guide/navigation/navigation-getting-started)** - Seamless navigation using Fragments.
3. **[LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData)** & **[ViewModel](https://developer.android.com/reference/android/arch/lifecycle/ViewModel)** - For lifecycle-aware data management.
4. **[Dagger 2.0](https://developer.android.com/training/dependency-injection/dagger-basics)** - Dependency injection without Hilt for modularity and testability.
5. **[Retrofit](https://square.github.io/retrofit/)** - For efficient API communication.
6. **[Room](https://developer.android.com/training/data-storage/room)** - Local data storage and caching.
7. **[Paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)** - For handling large datasets efficiently.
8. **[Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)** - Asynchronous programming made easy.
9. **[Material Design](https://material.io/develop/android)** - A modern and responsive UI.
10. **[Mockito](https://site.mockito.org/)** & **[JUnit](https://junit.org/junit5/)** - For unit and integration testing.
11. **[Timber](https://github.com/JakeWharton/timber)** - For streamlined logging.
12. **[Firebase Crashlytics](https://firebase.google.com/docs/crashlytics)** - For monitoring app stability.
13. **[Coil](https://coil-kt.github.io/coil/)** - For efficient image loading.
14. **[Shimmer](https://facebook.github.io/shimmer-android/)** - For smooth loading animations.


The goal of this project is to demonstrate how to build a high-quality Android app using modern tools and libraries while maintaining clean, testable, and maintainable code.

---
## Development Setup 🖥️

To build and run this project, you’ll need the latest version of **Android Studio** (preferably Arctic Fox or newer).

### API Key 🔑
To fetch weather data, you’ll need an API key from [OpenWeatherMap](https://openweathermap.org/api). Follow these steps:
1. Sign up on [OpenWeatherMap](https://openweathermap.org/api) and generate an API key.
2. Add the API key to your `local.properties` file:
   ```properties
   OPENWEATHER_API_KEY = <INSERT_YOUR_API_KEY>

3. Build the app 🎉


<h2 align="left">ScreenShots</h2>
<h4 align="start">
<img src="screenshots/screenshot1.jpeg" width="30%" vspace="10" hspace="10">
<img src="screenshots/screenshot2.jpeg" width="30%" vspace="10" hspace="10">
<img src="screenshots/screenshot3.jpeg" width="30%" vspace="10" hspace="10">
<img src="screenshots/screenshot4.jpeg" width="30%" vspace="10" hspace="10">
<img src="screenshots/screenshot5.jpeg" width="30%" vspace="10" hspace="10">
<img src="screenshots/screenshot6.jpeg" width="30%" vspace="10" hspace="10">
<br>

## Architecture 🏗️

The app uses MVVM [Model-View-ViewModel] architecture to ensure a clean separation of concerns, testability, and maintainability. Here’s a high-level overview:

Read more: 
- [Building Modern Android Apps with Architecture Guidelines](https://medium.com/@aky/building-modern-apps-using-the-android-architecture-guidelines-3238fff96f14)
- [Guide to app architecture](https://developer.android.com/jetpack/docs/guide)

![Architecture](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

## Author 👨‍💻
**Emmanuel Iyke**

- GitHub: [@Emmanueldav](https://github.com/emmanueldav)
- LinkedIn: [Emmanuel Iyke](https://linkedin.com/in/emmanueldav)
- Email: Emmanueldavis987@gmail.com

## License 📄
This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for details.