# MultiPattern Scanner

A console-based DSA-3 project that scans text, TXT files, and PDF documents for multiple keywords using the Aho-Corasick algorithm.

## Features

- Manual text input
- TXT file scanning
- PDF text extraction
- Multiple pattern input
- Trie-based pattern storage
- Failure links built using BFS
- Aho-Corasick multi-pattern matching
- Match counts and 0-based positions
- Basic performance measurements

## Requirements

- JDK 17 or newer
- Maven 3.8+
- VS Code
- Extension Pack for Java (recommended)

## Run in VS Code

Open the project folder in VS Code and open a terminal.

First verify:

```bash
java -version
mvn -version
```

Then run:

```bash
mvn compile
mvn exec:java
```

## PDF usage

Choose:

```text
3. Load PDF file
```

Then enter the full path, for example:

```text
C:\Users\YourName\Documents\sample.pdf
```

PDFBox extracts the text from the PDF and the Aho-Corasick engine scans that extracted text.

## Important limitation

This version extracts **text-based PDFs**. A scanned PDF that contains only images will not automatically produce searchable text because OCR is not included yet.

## Algorithm

The application:

1. Reads the text/document.
2. Accepts multiple patterns.
3. Inserts all patterns into a Trie.
4. Builds failure links using BFS.
5. Scans the document text once using the Aho-Corasick automaton.
6. Reports each matching pattern and its starting positions.

## Complexity

Let:

- `N` = total length of all patterns
- `M` = length of the document text
- `Z` = number of reported matches

Trie construction is `O(N)`. Failure-link construction processes the automaton using BFS. Searching is `O(M + Z)` after construction when output reporting is included in the bound.

The automaton requires `O(N)` space apart from output/match storage.

## Project

**Project:** MultiPattern Scanner  
**Course:** Data Structures and Algorithms - 3 (25CS2103E)  
**Team:** 6
