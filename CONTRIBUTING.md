# Contributing

Thank you for improving Gridline Four. Keep changes focused on the existing local-game scope and preserve the separation between Android rendering and pure rules.

## Workflow

1. Create a focused branch from `main`.
2. Keep board and strategy rules in the `game` package without Android dependencies.
3. Publish immutable state through the ViewModels instead of storing game truth in views.
4. Add or update a JVM test for every rule change.
5. Run the complete verification command:

   ```bash
   ./gradlew --no-daemon clean test lint assembleDebug
   ```

6. Open a pull request that explains behavior, architecture impact, and verification.

## Style

- Target Java 17 and the repository's pinned Android toolchain.
- Prefer small value objects and typed outcomes over magic integers or view tags.
- Keep strings and dimensions in resources where Android owns them.
- Treat lint warnings as errors; do not suppress product issues to make CI green.
- Keep README visuals aligned, editable, and truthful to implemented behavior.
