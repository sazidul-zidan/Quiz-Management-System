import java.util.*;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

abstract class User {
    private String userId;
    private String username;
    private String password;

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void login() {
        System.out.println(username + " logged in.");
    }

    public void logout() {
        System.out.println(username + " logged out.");
    }
}

class Admin extends User {
    private String adminId;

    public Admin(String adminId, String username, String password) {
        super(UUID.randomUUID().toString(), username, password);
        this.adminId = adminId;
    }

    public String getAdminId() {
        return adminId;
    }

    public Quiz createQuiz(String title) {
        return new Quiz(UUID.randomUUID().toString(), title, "Description", 15);
    }

    public void addQuestion(Quiz quiz, Question question) {
        quiz.questions.add(question);
    }

    public void deleteQuiz(List<Quiz> quizzes, int index) {
        quizzes.get(index).isActive = false;
        quizzes.remove(index);
        System.out.println("🗑 Quiz Deleted.");
    }

    public void viewAllResults(List<QuizParticipants> takers) {
        for (QuizParticipants taker : takers) {
            for (Result result : taker.getResults()) {
                System.out.println("QuizParticipants: " + taker.getUsername() + " | ID: " + taker.getUserId() +
                        " | Quiz: " + result.quiz.title + " | Score: " + result.score);
            }
        }
    }
}

class QuizParticipants extends User {
    private List<Result> results = new ArrayList<>();

    public QuizParticipants(String userId, String username, String password) {
        super(userId, username, password);
    }

    public void takeQuiz(Quiz quiz, Scanner sc) {
        quiz.startQuiz();
        int score = 0;
        for (Question question : quiz.questions) {
            System.out.println(question.text);
            List<String> options = question.options;
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i));
            }
            System.out.print("Your answer ( Option ): ");
            int answer = sc.nextInt();
            sc.nextLine();
            if (question.checkAnswer(options.get(answer - 1))) {
                score += question.points;
            }
        }
        quiz.endQuiz();
        Result result = new Result(UUID.randomUUID().toString(), score, new Date(), quiz);
        results.add(result);
        System.out.println(" Quiz completed! Your score: " + score);
    }

    public List<Result> getResults() {
        return results;
    }
}

class Quiz {
    String quizId;
    String title;
    String description;
    int timeLimit;
    boolean isActive = true;
    List<Question> questions = new ArrayList<>();

    public Quiz(String quizId, String title, String description, int timeLimit) {
        this.quizId = quizId;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
    }

    public void startQuiz() {
        System.out.println("\nStarting Quiz: " + title);
    }

    public void endQuiz() {
        System.out.println("Ending Quiz: " + title);
    }
}

class Question {
    String questionId;
    String text;
    List<String> options;
    String correctAnswer;
    int points;

    public Question(String text, List<String> options, String correctAnswer, int points) {
        this.questionId = UUID.randomUUID().toString();
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    public boolean checkAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer);
    }
}

class Result {
    String resultId;
    int score;
    Date dateTaken;
    Quiz quiz;

    public Result(String resultId, int score, Date dateTaken, Quiz quiz) {
        this.resultId = resultId;
        this.score = score;
        this.dateTaken = dateTaken;
        this.quiz = quiz;
    }
}



public class QuizSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Quiz> quizzes = new ArrayList<>();
        List<QuizParticipants> takers = new ArrayList<>();
        Admin admin = new Admin("admin01", "admin", "admin123");

        while (true) {
            System.out.println("──────────────────────────────────────────────");
            System.out.println("│                                            │");
            System.out.println("│         ▒▒▒ ONLINE QUIZ SYSTEM ▒▒▒         │");
            System.out.println("│                                            │");
            System.out.println("──────────────────────────────────────────────");
            System.out.println("1. Admin Login\n2. User Login\n0. Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter Admin ID: ");
                String inputAdminId = sc.nextLine();
                System.out.print("Enter Username: ");
                String inputUsername = sc.nextLine();
                System.out.print("Enter Password: ");
                String inputPassword = sc.nextLine();

                if (inputAdminId.equals(admin.getAdminId()) &&
                        inputUsername.equals(admin.getUsername()) &&
                        inputPassword.equals(admin.getPassword())) {

                    admin.login();
                    while (true) {
                        System.out.println("\n#Admin Panel");
                        System.out.println("1. Create Quiz\n2. Delete Quiz\n3. View All Results\n0. Logout");
                        int adminChoice = sc.nextInt();
                        sc.nextLine();

                        if (adminChoice == 1) {
                            System.out.print("Enter quiz title: ");
                            String title = sc.nextLine();
                            Quiz quiz = admin.createQuiz(title);

                            System.out.print("Enter number of questions: ");
                            int questionCount = sc.nextInt();
                            sc.nextLine();

                            for (int i = 0; i < questionCount; i++) {
                                System.out.print("Enter question text for " + (i + 1) + ": ");
                                String text = sc.nextLine();

                                List<String> options = new ArrayList<>();
                                System.out.print("Enter number of options: ");
                                int optCount = sc.nextInt();
                                sc.nextLine();

                                for (int j = 0; j < optCount; j++) {
                                    System.out.print("Enter option " +(j+1)+": ");
                                    options.add(sc.nextLine());
                                }

                                System.out.print("Enter the number of the correct answer (1 to " + optCount + "): ");
                                int correctIndex = sc.nextInt();
                                sc.nextLine();
                                String correctAnswer = options.get(correctIndex - 1);

                                System.out.print("Enter points: ");
                                int points = sc.nextInt();
                                sc.nextLine();

                                Question question = new Question(text, options, correctAnswer, points);
                                admin.addQuestion(quiz, question);
                            }

                            quizzes.add(quiz);
                            System.out.println("Quiz created successfully.");
                        } else if (adminChoice == 2) {
                            if (!quizzes.isEmpty()) {
                                for (int i = 0; i < quizzes.size(); i++) {
                                    System.out.println((i + 1) + ". " + quizzes.get(i).title);
                                }
                                System.out.print("Enter quiz number to delete: ");
                                int index = sc.nextInt() - 1;
                                sc.nextLine();
                                admin.deleteQuiz(quizzes, index);
                            } else {
                                System.out.println("No quizzes to delete.");
                            }
                        } else if (adminChoice == 3) {
                            admin.viewAllResults(takers);
                        } else if (adminChoice == 0) {
                            admin.logout();
                            break;
                        }
                    }
                } else {
                    System.out.println("Invalid admin credentials.");
                }
            } else if (choice == 2) {
                System.out.print("Enter User ID: ");
                String userId = sc.nextLine();
                System.out.print("Enter Username: ");
                String username = sc.nextLine();
                System.out.print("Enter Password: ");
                String password = sc.nextLine();

                QuizParticipants taker = new QuizParticipants(userId, username, password);
                taker.login();
                takers.add(taker);

                while (true) {
                    System.out.println("\n#User Panel");
                    System.out.println("1. Take Quiz\n2. View Results\n0. Logout");
                    int userChoice = sc.nextInt();
                    sc.nextLine();

                    if (userChoice == 1) {
                        if (!quizzes.isEmpty()) {
                            for (int i = 0; i < quizzes.size(); i++) {
                                System.out.println((i + 1) + ". " + quizzes.get(i).title);
                            }
                            System.out.print("Enter quiz number to take: ");
                            int index = sc.nextInt() - 1;
                            sc.nextLine();
                            taker.takeQuiz(quizzes.get(index), sc);
                        } else {
                            System.out.println("No quizzes available.");
                        }
                    } else if (userChoice == 2) {
                        List<Result> results = taker.getResults();
                        if (!results.isEmpty()) {
                            for (Result result : results) {
                                System.out.println(result.quiz.title + " | Score: " + result.score);
                            }
                        } else {
                            System.out.println("No results available.");
                        }
                    } else if (userChoice == 0) {
                        taker.logout();
                        break;
                    }
                }
            } else if (choice == 0) {
                System.out.println("Exiting Quiz System.");
                break;
            }
        }
        sc.close();
    }
}
