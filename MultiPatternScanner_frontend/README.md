# MultiPattern Scanner Frontend

Simple working frontend for the DSA-3 project.

Input:
1. Paste/type text
2. Upload TXT
3. Upload PDF

Enter one search pattern per line and click Scan.

The browser implementation uses a Trie + BFS failure links + Aho-Corasick and displays every match and its 0-based position.

Run:
npm install
npm run dev

PDF support is for text-based PDFs; scanned image PDFs require OCR.
