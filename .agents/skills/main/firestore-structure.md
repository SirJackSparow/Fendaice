# Firestore Question Structure

## Collection: `math_questions`

Each document represents one question.

### Document fields

| Field | Type | Example |
|---|---|---|
| `questionText` | String | `"3 + ? = 7"` |
| `answerTiles` | Array<String> | `["2", "4", "5", "7"]` |
| `correctAnswer` | String | `"4"` |
| `topic` | String | `"ADDITION"` |
| `ageGroup` | String | `"KIDS_MEDIUM"` |
| `level` | Number | `1` |

### Age groups (ageGroup field values)

- `KIDS_EASY` — ages 4–6, counting and addition 1–10
- `KIDS_MEDIUM` — ages 7–9, addition and subtraction 1–100
- `KIDS_HARD` — ages 10–12, multiplication and division
- `TEEN_EASY` — ages 13–15, fractions, decimals, percentages
- `TEEN_HARD` — ages 16–18, algebra and negative numbers
- `ADULT` — ages 18+, all topics mixed

### Topic field values

`ADDITION`, `SUBTRACTION`, `MULTIPLICATION`, `DIVISION`,
`FRACTIONS`, `DECIMALS`, `PERCENTAGES`, `ALGEBRA`, `MIXED`

### Example documents

```json
{
  "questionText": "3 + ? = 7",
  "answerTiles": ["2", "4", "5", "7"],
  "correctAnswer": "4",
  "topic": "ADDITION",
  "ageGroup": "KIDS_MEDIUM",
  "level": 1
}

{
  "questionText": "? × 6 = 42",
  "answerTiles": ["5", "6", "7", "8"],
  "correctAnswer": "7",
  "topic": "MULTIPLICATION",
  "ageGroup": "KIDS_HARD",
  "level": 2
}

{
  "questionText": "2x + 3 = 11 → x = ?",
  "answerTiles": ["2", "3", "4", "5"],
  "correctAnswer": "4",
  "topic": "ALGEBRA",
  "ageGroup": "TEEN_HARD",
  "level": 3
}
```

## Querying questions

Always query by both `ageGroup` and `topic`, limit to 10 per round:
```
math_questions
  where ageGroup == "KIDS_MEDIUM"
  where topic == "ADDITION"
  where level == 1
  limit 10
```

## Seeding questions

Tell the user to manually add questions in Firebase Console →
Firestore Database → math_questions → Add document.
Or provide a seed script if requested.
And please add all category data for the question.