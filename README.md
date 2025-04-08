# 🖋️ book-scribe

## 🚀 Overview  
**book-scribe** is a dedicated ETL (Extract, Transform, Load) microservice within the Book Metadata Management System. 
It takes raw book metadata from MongoDB, enriches it using scraping and AI (GPT), transforms it into SQL-compatible format, 
and stores it in the MySQL database managed by `book-worm-api`.

This service ensures that imported metadata is complete, clean, and standardized before entering the final SQL store.

---

## 🧱 Tech Stack  
- **Language:** Java 17  
- **Framework:** Spring Boot  
- **Databases:** MongoDB (input) + MySQL (output)  
- **Scraping:** Apache HttpClient + Jsoup  
- **AI Integration:** OpenAI API (GPT-4)  
- **Persistence:** Spring Data JPA + Spring Data MongoDB  
- **Containerization:** Docker (optional)

---

## 📦 Features  

✅ Search book metadata in `book-harvest` (MongoDB) using:
- Title
- Author (first/last)
- Editors
- Publication year range

✅ Automatically **select the best match** based on custom heuristics  
✅ Scrape `infoLink` and `canonicalVolumeLink` for missing data (editors, translators, etc.)  
✅ (Optional) Enrich metadata using **GPT** (name normalization, category suggestions, etc.)  
✅ Transform enriched data into SQL-ready format  
✅ Save to `book-worm` MySQL DB or present to user for review  
✅ REST API to trigger search, enrich, preview, and import workflows

---

## 🧩 Part of: Book Metadata Management System

This service is part of a modular microservices ecosystem:

| Service          | Responsibility                                        |
|------------------|--------------------------------------------------------|
| `book-worm-api`  | Finalized book metadata storage in MySQL              |
| `book-bridge-api`| Imports raw book metadata from Google Books → MongoDB |
| `book-scribe`    | Enriches + transforms + imports MongoDB → MySQL       |
| `control-panel` *(planned)* | Orchestrates flows & exposes user interface |

---

## 🔧 Getting Started

```bash
# Clone the repository
git clone https://github.com/Lasha-Less/book-scribe.git
cd book-scribe

# Run with Maven
mvn spring-boot:run
