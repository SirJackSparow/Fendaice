# Validation — Android

## Build
```bash
./gradlew :composeApp:assembleDebug
android run --apks=composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

## Firestore checklist
- [ ] Questions load from Firestore (check Logcat for errors)
- [ ] Loading indicator shows while fetching
- [ ] Error message shows if no questions found

## Drag-and-drop checklist
- [ ] Tile scales up while being dragged
- [ ] Drop zone highlights when tile hovers over it
- [ ] Correct tile dropped → green flash → next question
- [ ] Wrong tile dropped → shake animation → tile bounces back
- [ ] Tile released outside drop zone → bounces back with no penalty

## Game flow checklist
- [ ] Age selection → topic selection → game → score screen
- [ ] Score updates correctly after each question
- [ ] Round ends after 10 questions
- [ ] Stars calculated correctly
- [ ] Progress saved after round completes
- [ ] "Next Level" fetches harder questions from Firestore