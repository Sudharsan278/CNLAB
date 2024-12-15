package MCQ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MCQServer {
	
	static class Question {
        String questionText;
        String[] options;
        int correctOptionIndex;

        public Question(String questionText, String[] options, int correctOptionIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
        }
    }

    private static List<Question> questions = new ArrayList<>();

    public static void main(String[] args) {
        // Simulate questions and answers
        questions.add(new Question("What is the capital of France?", new String[]{"Berlin", "Madrid", "Paris", "Rome"}, 2));
        questions.add(new Question("Which planet is known as the Red Planet?", new String[]{"Earth", "Mars", "Venus", "Jupiter"}, 1));
        questions.add(new Question("What is the largest ocean on Earth?", new String[]{"Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"}, 3));
        questions.add(new Question("Who wrote the play 'Romeo and Juliet'?", new String[]{"Shakespeare", "Dickens", "Austen", "Twain"}, 0));

        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for client connection...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    int score = 0;
                    // Send questions and evaluate answers
                    for (int i = 0; i < questions.size(); i++) {
                        Question question = questions.get(i);

                        // Send question and options
                        out.println("Question " + (i + 1) + ": " + question.questionText);
                        for (int j = 0; j < question.options.length; j++) {
                            out.println((j + 1) + ". " + question.options[j]);
                        }

                        // Receive answer from client
                        String answer = in.readLine();
                        int userAnswer = Integer.parseInt(answer) - 1;

                        // Check answer
                        if (userAnswer == question.correctOptionIndex) {
                            score++;
                        }
                    }

                    // Send the score to the client
                    out.println("Your final score is: " + score + " out of " + questions.size());
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

}
