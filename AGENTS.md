# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Some SWE Experience developing software for NUS, CVWO
* IDE and level of expertise: VS Code, Cursor

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version: 25.0.3.fx-zulu

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Java Coding Standard

Apply these rules to all Java code. For topics not covered here, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Source: [SE Education Java Coding Standard](https://se-education.org/guides/conventions/java/intermediate.html)

### Naming

* Write package names entirely in lowercase, such as `todobuddy.ui`.
* Name classes and enums with nouns in `PascalCase`.
* Name variables in `camelCase`.
* Name methods with verbs in `camelCase`.
* Name constants in `SCREAMING_SNAKE_CASE`.
* Give related constants a common prefix.
* Treat acronyms as ordinary words within identifiers:

  * Use `exportHtmlSource()`, not `exportHTMLSource()`.
  * Use `openDvdPlayer()`, not `openDVDPlayer()`.
* Write all identifiers in English.
* Use descriptive names for variables with a large scope. Short names are acceptable only for small, obvious scopes.
* Use `i` for the first loop iterator. Reserve `j`, `k`, and subsequent letters for nested loops.
* Use plural names for collections and arrays, such as `points` and `values`.
* Name booleans so they read as predicates, preferably using prefixes such as `is`, `has`, `can`, `should`, or `was`.
* Name boolean setters in this form:

```java
void setFound(boolean isFound);
```

* Test method names may use:

```text
featureUnderTest_testScenario_expectedBehavior
```

The scenario and expected-behavior components may be omitted when unnecessary.

### Layout and Formatting

* Indent with four spaces. Never use tabs.
* Keep lines below 110 characters where practical. Never exceed 120 characters.
* Indent wrapped lines eight spaces beyond the parent line.
* When wrapping:

  * Break after a comma.
  * Break before an operator, including `.`, `&` in type bounds, and `|` in multi-catch clauses.
  * Keep a method or constructor name attached to its opening parenthesis.
  * Prefer breaks at higher syntactic levels.
  * Choose readability over an IDE formatter’s default output.
* Use K&R braces: place the opening brace on the same line as the declaration or control statement.
* Always use braces for conditional and loop bodies, including single-statement bodies.
* Put conditional bodies on separate lines.
* Format `else`, `catch`, and `finally` on the same line as the preceding closing brace:

```java
if (condition) {
    statements;
} else {
    statements;
}

try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
```

* Add `// Fallthrough` whenever a traditional `switch` case intentionally falls through.
* Surround operators with spaces.
* Add a space after Java keywords, commas, and semicolons in `for` statements.
* Surround ternary colons with spaces.
* Separate logical units within a block with one blank line.

### Packages, Imports, Types, and Variables

* Place every class in a package.
* Use one consistent import ordering throughout the project.
* Import classes explicitly. Do not use wildcard imports such as `java.util.*`.
* Attach array brackets to the type:

```java
int[] values;
```

* Declare variables in the smallest possible scope.
* Initialize variables where they are declared when a valid value is available.
* Do not assign a fabricated placeholder merely to initialize a variable.
* Do not expose mutable class variables as `public`.
* Public fields are acceptable only for constants or genuine behavior-free data classes.

### Comments and Javadoc

* Write comments in English using American spelling.
* Avoid slang and culturally local expressions.
* Add descriptive Javadoc to every class and public method, except:

  * Straightforward getters and setters.
  * Overrides for which the inherited Javadoc remains completely accurate.
  * Test classes and test methods.
* Begin Javadoc summaries with a third-person verb such as `Returns`, `Adds`, or `Sends`.
* Place `/**` on its own line.
* Align each `*` and include one space after it.
* Separate the description from Javadoc tags with a blank line.
* End parameter, return, and exception descriptions with punctuation.
* Do not place a blank line between Javadoc and the declaration it documents.
* Include either all useful `@param` tags or none; omit tags that merely repeat self-explanatory parameter names.
* Omit `@return` when the method returns nothing or the return value is already obvious.
* Use `{@inheritDoc}` when inherited documentation applies but requires additional detail.
* Indent comments to match the code they describe.

```java
/**
 * Returns the location for the specified position.
 *
 * @param position Position to evaluate.
 * @return Computed location.
 * @throws IllegalArgumentException If the position is invalid.
 */
public Location computeLocation(Position position) {
    // ...
}
```

## Git Conventions

Apply these rules to commit messages and branch names.

Source: [SE Education Git Conventions](https://se-education.org/guides/conventions/git.html)

### Commit Subject

Every commit must have a meaningful subject line that:

* Uses the imperative mood: `Add validation`, not `Added validation`.
* Begins with a capital letter.
* Does not end with a period.
* Targets 50 characters or fewer.
* Never exceeds 72 characters.
* Describes one coherent change.

An optional scope or category may precede the subject:

```text
Parser: Handle empty input
bug fix: Prevent duplicate entries
chore: Update release date
```

### Commit Body

Include a body for every non-trivial commit.

* Separate the subject and body with one blank line.
* Wrap body text at 72 characters.
* Separate paragraphs with blank lines.
* Use bullet points where they improve readability.
* Explain:

  1. The present situation.
  2. Why it needs to change.
  3. What the commit changes.
  4. Why this approach was chosen.
  5. Any other relevant context.
* Explain **what** changed and **why**, not implementation details already visible in the diff.
* Use present tense when describing the existing situation.
* Use imperative mood when describing the change.
* Avoid redundant qualifiers such as “currently” and “originally.”
* Avoid repeating information already captured in code comments.
* Provide enough context for a reviewer to assess the decision without reading the diff first.
* If the body becomes excessively long, split the work into smaller, logically independent commits.

Recommended structure:

```text
Add case-insensitive name matching

Name matching is case-sensitive.

Users cannot be expected to remember the exact capitalization of every
stored name.

Normalize both operands before comparing them. This preserves the existing
matching behavior while removing capitalization differences.
```

### Branch Names

* Use meaningful keywords in `kebab-case`:

```text
refactor-ui-tests
```

* When a branch corresponds to an issue, prefix it with the issue number:

```text
1234-ui-freeze-error
```
