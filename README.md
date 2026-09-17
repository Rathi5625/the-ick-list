# The Ick List 💅💸

> *"Your bank account called. It wants to know why you're like this."*

**The Ick List** is a viral-ready, brutally honest personal finance audit web application designed for Gen-Z and millennials. Traditional budgeting apps give you sterile pie charts and scolding notifications that get ignored. The Ick List takes your CSV bank statements, feeds them into **Amazon Bedrock (Nova Lite)**, and roasts your worst spending habits like a funny, brutally honest best friend.

---

## 🎯 The Problem & The Solution

- **The Problem**: People avoid looking at their bank statements because finance apps are boring, guilt-tripping, and overwhelming.
- **The Solution**: Radical candor powered by Generative AI. We transform sterile transaction spreadsheets into culturally resonant, hilarious "icks" that people actually want to read, laugh at, and share with friends.

---

## ⚡ Core Features

- **Pill Glassmorphic Navigation & Claymorphic UI**: 
  - A floating, frosted glass navbar with automatic light/dark mode toggling.
  - 95% claymorphic UI styling with tactile 3D drop-shadows and pill-shaped badges across cards, forms, and buttons.
  - Zero-photo auto-generated user avatars using deterministic initials and a locked, cohesive 6-color palette (`#20212B`, `#FED7A5`, `#9E6752`, `#534145`, `#2D4354`, `#73766A`).
- **CSV Transaction Parser & Ingestion**:
  - Drag-and-drop file upload with format validation for `Date`, `Amount`, `Merchant`, and `Category`.
  - Batch chunking and persistence to Amazon DynamoDB using batch write operations.
- **AI Financial Roasting via Amazon Bedrock**:
  - Analyzes itemized statements across **5 fixed categories**:
    1. 🌙 **Late-Night Spending** (e.g. 2:00 AM delivery spirals)
    2. 👯 **Duplicate Purchases** (double-tapped games, repeat coffee)
    3. 💳 **Subscription Creep** (unused gym memberships, forgotten apps)
    4. 🛍️ **Impulse Category Spikes** (sudden manic spending binges)
    5. 🍸 **Weekend Overspending** (weekday monk vs. weekend chaos)
  - Strict 3-tier severity rating: `Mild Ick`, `Medium Ick`, and `Wildly Unserious`.
- **Chronological Audit Trail (Roast History)**:
  - Multi-batch statement tracking.
  - Automatically groups past roasts by upload session with date/time headers, ensuring new statements never overwrite previous roasts.
  - Custom illustrated empty state for fresh accounts.

---

## 🛠️ Architecture & Tech Stack

```
   ┌─────────────────────────────────────────────────────────┐
   │             React 18 + Vite (Vanilla CSS)               │
   │      Claymorphism Cards + Floating Glass Pill Navbar    │
   └───────────────────────────┬─────────────────────────────┘
                               │ HTTP / JSON (JWT Auth)
                               ▼
   ┌─────────────────────────────────────────────────────────┐
   │             Spring Boot 3.3.4 (Java 21)                 │
   │               Spring Security + JJWT                    │
   └───────────────┬─────────────────────────┬───────────────┘
                   │                         │
                   ▼                         ▼
   ┌───────────────────────────┐ ┌───────────────────────────┐
   │      Amazon DynamoDB      │ │   Amazon Bedrock (Nova)   │
   │ Users / Txns / Roasts     │ │ Generative Roast Engine   │
   └───────────────────────────┘ └───────────────────────────┘
```

- **Frontend**: React 18, Vite, Lucide React, Vanilla CSS with custom Claymorphic & Glassmorphic design tokens.
- **Backend**: Java 21, Spring Boot 3.3.4, Spring Security, BCrypt password hashing, JJWT.
- **Cloud & AI (AWS SDK v2)**:
  - **Amazon Bedrock**: `amazon.nova-lite-v1:0` for generative AI text roast synthesis.
  - **Amazon DynamoDB**: Scalable NoSQL persistence for `Users`, `Transactions`, and `Roasts` tables.
  - **Amazon SES**: Transactional welcome email service.

---

## 🚀 Running Locally

### 1. Prerequisites
- **Java JDK 21** (e.g., Eclipse Temurin 21)
- **Node.js v20+** and `npm`
- **Apache Maven 3.9+**
- **AWS CLI v2** (optional, for inspecting local DynamoDB tables)

### 2. Start DynamoDB Local
For local development without cloud costs, run DynamoDB Local on port `8000`:
```powershell
# From project root
java "-Djava.library.path=./tools/dynamodb-local/DynamoDBLocal_lib" -jar tools/dynamodb-local/DynamoDBLocal.jar -sharedDb -inMemory -port 8000
```
Create the local database tables:
```powershell
powershell -ExecutionPolicy Bypass -File "scripts\create-tables.ps1" -EndpointUrl "http://localhost:8000"
```

### 3. Start the Spring Boot Backend
```powershell
cd backend
mvn spring-boot:run
```
The backend will launch on `http://localhost:8080`.

### 4. Start the Vite Frontend
```powershell
cd frontend
npm install
npm run dev
```
Open **`http://localhost:5173`** in your browser.

---

## 🧪 Automated Testing Scripts

PowerShell automated testing scripts are included in `/scripts`:
```powershell
# Test Auth (Signup with Avatar, Login, JWT verification)
powershell -ExecutionPolicy Bypass -File "scripts\test-auth.ps1"

# Test CSV Upload & Validation
powershell -ExecutionPolicy Bypass -File "scripts\test-upload.ps1"

# Test Roast Generation (Prompt Building, Bedrock Stub, DynamoDB Persistence)
powershell -ExecutionPolicy Bypass -File "scripts\test-roasts.ps1"

# Test Multi-Batch Roast History (Empty State, Multi-upload Grouping, No Overwrite)
powershell -ExecutionPolicy Bypass -File "scripts\test-history.ps1"
```

A ready-to-use [`sample-transactions.csv`](sample-transactions.csv) file is included to test upload and roast generation out of the box.

---

## ☁️ Live AWS Status & Bedrock Switch

> **Note**: Live AWS production deployment and Bedrock model invocation are currently configured in test/stubbed mode while AWS account identity verification completes (~9 hours remaining).
>
> The Amazon Bedrock client invocation code is 100% written and integrated. Switching from realistic stubbed roasts to live Amazon Bedrock Nova requires only a **single-line configuration change**:
>
> In `backend/src/main/resources/application.properties`:
> ```properties
> # Change from false to true
> aws.bedrock.enabled=true
> ```
> *(Or set environment variable `$env:AWS_BEDROCK_ENABLED = "true"`).*

---

## 📄 License
MIT License. Built for hackathon showcase.
