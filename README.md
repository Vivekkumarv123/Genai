# GenAI

GenAI is a desktop chat application that integrates Java Swing for the UI and Google Gemini AI (via Node.js) for intelligent chat responses. It features user authentication, chat history, and a modern interface.

## Features
- User login and signup
- Chat with "Shinchan" (Gemini AI)
- Chat history sidebar
- Save/load chats to a Derby database
- Node.js backend for AI responses
- Modern Java Swing UI

## Project Structure
```
GenAI/
├── build.xml                # Build script
├── manifest.mf              # Manifest file
├── src/
│   └── genai/
│       ├── GenAI.java       # Main chat window
│       ├── LoginScreen.java # Login UI
│       ├── SignupScreen.java# Signup UI
│       ├── WelcomePage.java # Welcome screen
│       ├── genai_chat.js    # Node.js Gemini AI backend
│       └── package.json     # Node.js dependencies
├── build/                   # Compiled Java classes
├── nbproject/               # NetBeans project files
└── .gitignore               # Git ignore rules
```

## Requirements
- Java (JDK 8+)
- Node.js (v18+ recommended)
- NetBeans (optional, for development)
- Google Gemini API key
- Apache Derby (for chat history)

## Setup
1. **Clone the repository:**
   ```sh
   git clone https://github.com/Vivekkumarv123/Genai.git
   cd Genai
   ```
2. **Install Node.js dependencies:**
   ```sh
   cd src/genai
   npm install @google/generative-ai
   ```
3. **Configure your Gemini API key:**
   - Edit `genai_chat.js` and replace `<your_gemini_api_api>` with your actual API key.
4. **Set up Derby database:**
   - Ensure Apache Derby is running and the `GenData` database is created with a `ChatMessages` table.
5. **Build and run the Java application:**
   - Use NetBeans or run from terminal:
     ```sh
     javac -d build/classes src/genai/*.java
     java -cp build/classes;path/to/derbyclient.jar genai.WelcomePage
     ```

## Usage
- Launch the app, log in or sign up, and start chatting with "Shinchan".
- Previous chats are saved and can be accessed from the sidebar.

## License
This project is for educational purposes.
