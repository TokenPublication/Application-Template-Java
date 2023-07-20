# Application Template Java MVVM

# Application Template

[![Android](https://img.shields.io/badge/Android-SDK%2021%2B-green.svg)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg)](https://developer.android.com/topic/architecture?hl=tr)
[![Room](https://img.shields.io/badge/Room-2.4.0-blueviolet.svg)](https://developer.android.com/training/data-storage/room)
[![Rx Java 2](https://img.shields.io/badge/RxJava2-2.2.21-blueviolet.svg)](hhttps://reactivex.io/)
[![LiveData](https://img.shields.io/badge/LiveData-2.3.1-green.svg)](https://developer.android.com/topic/libraries/architecture/livedata)
[![RecyclerView](https://img.shields.io/badge/RecyclerView-1.3.0-brightgreen.svg)](https://developer.android.com/jetpack/androidx/releases/recyclerview)
[![Dagger Hilt](https://img.shields.io/badge/Dagger%20Hilt-2.38.1-red.svg)](https://dagger.dev/hilt/)

Application Template is an example Android app implemented in Java, 
adhering to the MVVM (Model-View-ViewModel) architecture. It serves as a foundation 
for developers working on a Banking Application. The app incorporates several standard 
Android development libraries and technologies commonly utilized in Android app development.

## Features

- This application provides functionality for Sale, Void, Refund, and Batch Close operations on a POS device.
- It enables printing a slip after each of the mentioned operations.
- The app stores all operations in a local database using Room technology.
- Users can utilize Postman to make requests for performing the operations.
- The application includes illustrative use cases of specific library functions demonstrated below the example menu.
- Additionally, it showcases the implementation of CardService Library for card reading and emv Configuration.
- Furthermore, it demonstrates the process of uploading parameters for a bin table, allowed operations, supported AIDs, and configuration files.

## Prerequisites

- Android SDK 26 or higher.
- Gradle 7.0.4 or higher

## Dependencies

The application includes the following dependencies:

- Rx Java 2: A reactive programming library that enables asynchronous and event-based programming in Android applications.
- Android Room: Provides an abstraction layer over SQLite to handle database operations.
- Android LiveData: A data holder class that allows observing changes in data across the application.
- Android RecyclerView: A powerful UI component for displaying large datasets.
- Dagger Hilt: A dependency injection framework for managing dependencies and enhancing testability.

## Usage

1. Clone the repository:
2. Sync Gradle settings with [Gradle Assistant](https://developer.android.com/build/agp-upgrade-assistant)
3. Run the Template

