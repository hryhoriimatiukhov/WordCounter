# Word Counter Android Application

An Android application built with Kotlin and Jetpack Compose (Material 3) that analyzes user-entered text to count sentences, words, punctuation marks, and numbers.

## Features

- **Text Analysis Logic**:
  - **Sentences**: Counts sentences terminating with `.`, `!`, or `?`. Consecutive sentence-ending marks (e.g., `?!`, `...`) are treated as a single sentence delimiter.
  - **Words**: Counts lexical words separated by whitespace while ignoring surrounding punctuation (e.g., `"Hello, world!"` = 2 words).
  - **Numbers**: Counts each numeric token as one number regardless of how many digits it contains (e.g., `2026` = 1 number, `42` = 1 number).
  - **Punctuation Marks**: Counts each punctuation character in the input text (`.`, `,`, `!`, `?`, `:`, `;`, `-`, `(`, `)`, `[`, `]`, `"`, `'`, etc.), excluding spaces, letters, and digits.
- **Dedicated Reusable Engine**: `TextMetricsCalculator` is a pure Kotlin class with zero Android UI dependencies, making it 100% reusable and unit-testable.
- **Session Calculation History**:
  - Tracks all calculations performed during the current application runtime in a vertically scrollable list.
  - Newest calculations appear at the top.
  - Display format: `"Original text" => sentences, words, punctuation marks, numbers`.
  - Automatically resets when the app is restarted.
  - Includes a "Clear History" button to manually clear current session history.
- **Test Sentence Generator**:
  - "Generate Test Sentence" button replaces the input field with a curated realistic sentence mixing words, punctuation, and numbers.
  - Generated sentences can be freely edited before calculation and are only recorded in history after pressing "Calculate Metrics".
- **Input Validation & Feedback**:
  - Empty or whitespace-only submissions are blocked with an inline field error and a Snackbar notification.
  - Notifications provide feedback for successful calculations, sentence generation, and clearing history.
- **Localization**: All user-facing strings are stored in `res/values/strings.xml`.

## Project Structure

```
WordCounter/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/wordcounter/
│   │   │   │   ├── calculator/
│   │   │   │   │   └── TextMetricsCalculator.kt   # Core text analysis engine
│   │   │   │   ├── data/
│   │   │   │   │   └── TestSentenceProvider.kt    # Predefined curated test sentences
│   │   │   │   ├── model/
│   │   │   │   │   ├── HistoryEntry.kt            # Session history model
│   │   │   │   │   └── TextMetrics.kt             # Metrics data model
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── HistoryItemCard.kt     # History list item widget
│   │   │   │   │   │   └── MetricCards.kt         # 4-metric badge grid
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   └── Type.kt
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   └── WordCounterViewModel.kt# UI state, validation, history
│   │   │   │   │   └── WordCounterScreen.kt       # Jetpack Compose UI
│   │   │   │   └── MainActivity.kt                # App entry point
│   │   │   ├── res/
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml
│   │   │   │       ├── strings.xml                # Centralized string resources
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/com/example/wordcounter/
│   │           └── TextMetricsCalculatorTest.kt   # Complete unit test suite
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Opening and Running in Android Studio

1. Open **Android Studio**.
2. Select **Open** and navigate to:
   `C:\Users\gmdmi\.gemini\antigravity\scratch\WordCounter`
3. Allow Gradle to sync dependencies.
4. Select an emulator or connected device and press **Run** (Shift+F10).

## Running Unit Tests

Run unit tests via command line or in Android Studio:
```bash
./gradlew test
```
Or right-click `TextMetricsCalculatorTest.kt` in Android Studio and select **Run 'TextMetricsCalculatorTest'**.
