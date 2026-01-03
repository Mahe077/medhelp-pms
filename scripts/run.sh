#!/usr/bin/env bash

# MedHelp PMS - Multi-Terminal Development Runner
# ---------------------------------------------------------------
# Launches Backend and Frontend in separate macOS Terminal windows.

# Get the absolute path to the project root
PROJECT_ROOT=$(cd "$(dirname "$0")/.." && pwd)
BACKEND_DIR="$PROJECT_ROOT/backend"
FRONTEND_DIR="$PROJECT_ROOT/frontend"

echo "🚀 Launching MedHelp PMS Development Environment..."

# Function to run a command in a new macOS Terminal window
run_in_new_terminal() {
    local title=$1
    local dir=$2
    local cmd=$3
    
    osascript - "$title" "$dir" "$cmd" <<'EOF'
on run argv
    set terminalTitle to item 1 of argv
    set workingDir to item 2 of argv
    set shellCommand to item 3 of argv
    tell application "Terminal"
        set scriptCmd to "cd " & quoted form of workingDir & " && echo -n -e \"\\033]0;\"" & quoted form of terminalTitle & "\"\\007\" && " & shellCommand
        do script scriptCmd
        activate
    end tell
end run
EOF
}

# 1. Start the Backend
echo "📦 Starting Spring Boot Backend..."
run_in_new_terminal "PMS Backend (Spring Boot)" "$BACKEND_DIR" "if [ -f './mvnw' ]; then ./mvnw spring-boot:run; else mvn spring-boot:run; fi"

# 2. Start the Frontend
echo "🎨 Starting Next.js Frontend..."
run_in_new_terminal "PMS Frontend (Next.js)" "$FRONTEND_DIR" "npm run dev"

echo "✅ Both services have been launched in separate terminals."
