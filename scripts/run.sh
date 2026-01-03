#!/usr/bin/env bash
set -e

# Script to run Spring Boot backend and Next.js frontend for local development
# ---------------------------------------------------------------
# Usage: ./run.sh
# ---------------------------------------------------------------

# Start the backend (Spring Boot) in the background
cd ../backend
if [ -f "./mvnw" ]; then
  echo "Starting Spring Boot via Maven Wrapper..."
  ./mvnw spring-boot:run &
else
  echo "Starting Spring Boot via Maven..."
  mvn spring-boot:run &
fi
BACKEND_PID=$!

# Give the backend a moment to start up (adjust if needed)
echo "Waiting for backend to become ready..."
sleep 10

# Start the frontend (Next.js) in the foreground
cd ../frontend
echo "Installing frontend dependencies..."
npm install
echo "Starting Next.js development server..."
npm run dev

# When the script exits (e.g., Ctrl+C), ensure the backend is stopped
trap "kill $BACKEND_PID" EXIT
