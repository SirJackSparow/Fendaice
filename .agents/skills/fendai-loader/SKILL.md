# Fendai Model Loader Skill

This skill provides instructions on how to interact with the dynamic model loading system in the `fendai` module.

## Core Components
- **`@Prompt(val prompt: String)`**: Annotation used to mark classes and define their API namespace.
- **`DefineLoadModelImpl`**: The implementation that manages registration and invocation of routes.

## Usage Rules for AI Agents

### 1. Registering Classes
Before calling any function, the class must be registered.
```kotlin
val loader = DefineLoadModelImpl()
loader.promptRegister(YourAnnotatedClass::class)
```

### 2. Route Pattern
Routes are strings in the format: `"{promptValue}/{functionName}"`.
- Example: If a class has `@Prompt("llm")` and a function `generate()`, the route is `"llm/generate"`.

### 3. Calling Functions
Use the `call` method with the route string and any required arguments.
```kotlin
val result = loader.call("llm/generate", "Hello AI")
```

### 4. Implementation Details
- The system uses reflection (`kotlin-reflect`).
- Functions must be members of a class annotated with `@Prompt`.
- Classes must have a no-argument constructor for `createInstance()`.
