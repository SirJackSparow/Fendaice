---
name: Main Games
description: Creates a drag-and-drop math game for all ages on Android using Jetpack Compose and Firebase Firestore for questions.
  Use this skill when the user asks to create, build, add, or implement a math game, number game, drag and drop math quiz, or educational math app on Android.
  Questions are fetched from Firestore in real time.
  UI uses Jetpack Compose drag gestures.
  Covers all math topics (addition, subtraction, multiplication, division, fractions, algebra) with age-based difficulty levels.
license: Complete terms in LICENSE.txt
---

## Goal

Create a complete drag-and-drop math game for all ages using KMM.
Shared game logic lives in commonMain. Android UI uses Jetpack Compose.
iOS UI uses SwiftUI. Follow each step in order.


## Step

### Step1: Verify Firebase setup
Check Firestore is enabled and google-services.json is present.
See [firestore-setup.md](firestore-setup.md).

### Step2: Add Depedencies if needed
Add depedencies in this project.
See [depedencies.md](depedencies.md)

### Step3: Implement Firestore Structure
Check Firestore structure in this project.
See [firestore-structure.md](firestore-structure.md)]

### Step4: Make Data Layer
Create repository and model classes to fetch questions from Firestore.
See [data-layer.md](data-layer.md)]
 
### Step5: Create View Model
Create MathGameViewModel to manage game state and Firestore data.
See [viewmodel-model.md](viewmodel-model.md)]

### Step6: Create UI
CCreate drag-and-drop game screen using Compose drag gestures.
See [game-ui.md](game-ui.md)]

### Step7: Score
Implement score tracking, stars, and level progression.
See [score.md](score.md)]

### Step 8: Validate and troubleshoot
Build, run, and verify drag-and-drop + Firestore fetch works.
See [validation.md](validation.md)]