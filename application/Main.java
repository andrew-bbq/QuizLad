package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

  HashMap<String, ArrayList<Question>> questionList = new HashMap<String, ArrayList<Question>>();
  JSONHandler importExport = new JSONHandler();
  Quiz quizObject = new Quiz();
  ListView<String> list = new ListView<String>();

  final FileChooser fc = new FileChooser();

  @Override
  public void start(Stage primaryStage) {
    try {
      // Creation of buttons and boxes
      Media sound = new Media(new File("open.mp3").toURI().toString());
      MediaPlayer mediaPlayer = new MediaPlayer(sound);
      mediaPlayer.play();
      primaryStage.setTitle("Quiz Generator");
      Scene scene = home(primaryStage);
      showStage(primaryStage, scene);
    } catch (Exception e) {
      e.printStackTrace();
    }

    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      public void handle(WindowEvent exitApp) {
        Alert alert = new Alert(AlertType.INFORMATION,
            "You're about to exit, do you want to save your questions to a JSON?", ButtonType.YES,
            ButtonType.NO);
        alert.setHeaderText("You're about to exit");
        alert.setContentText("Do you want to save your questions to a JSON?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {

        }
        if (result.get() == ButtonType.YES) {
          try {
            File file = fc.showSaveDialog(primaryStage);
            importExport.generateJSON(questionList, file.getAbsolutePath().toString());
          } catch (Exception ex) {

          }
        } else if (result.get() == ButtonType.NO) {

        }
      }
    });

  }

  public void showStage(Stage primaryStage, Scene scene) {
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  public Scene home(Stage primaryStage) {

    int size = 0;
    for (String key : questionList.keySet()) {
      size += questionList.get(key).size();
    }

    // Creation of buttons and boxes
    VBox vungryBox = new VBox();
    HBox hungryBox1 = new HBox();
    HBox hungryBox2 = new HBox();
    HBox hungryBox3 = new HBox();

    Text topicLabel = new Text("Topics ");
    Button takeQuiz = new Button("Take Quiz");
    Button addNewQuestion = new Button("Add New Question");
    Button importBooks = new Button("Import JSON");
    Button export = new Button("Export");

    Text placeHolderNum = new Text("Number of Questions: " + size);

    // event handling
    EventHandler<MouseEvent> AddQuestionSceneEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        showStage(primaryStage, addQuestion(primaryStage));
      }
    };

    EventHandler<MouseEvent> MakeQuizSceneEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        showStage(primaryStage, makeQuiz(primaryStage));
      }
    };

    EventHandler<MouseEvent> ExportQuestions = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        try {
          File file = fc.showSaveDialog(primaryStage);
          importExport.generateJSON(questionList, file.getAbsolutePath().toString());
        } catch (Exception ex) {

        }
      }
    };

    EventHandler<MouseEvent> ImportJSONEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        try {
          File file = fc.showOpenDialog(primaryStage);
          questionList =
              importExport.addQuestions(questionList, file.getCanonicalPath().toString());
          showStage(primaryStage, home(primaryStage));
        } catch (Exception ex) {
          
        }
      }
    };

    // event buttons
    addNewQuestion.addEventFilter(MouseEvent.MOUSE_CLICKED, AddQuestionSceneEvent);
    takeQuiz.addEventFilter(MouseEvent.MOUSE_CLICKED, MakeQuizSceneEvent);
    export.addEventFilter(MouseEvent.MOUSE_CLICKED, ExportQuestions);
    importBooks.addEventFilter(MouseEvent.MOUSE_CLICKED, ImportJSONEvent);

    // Create comboBox with possible topics
    ObservableList<String> topicList = FXCollections.observableArrayList(questionList.keySet());
    Collections.sort(topicList);
    ComboBox<String> topics = new ComboBox<String>();
    topics.setItems(topicList);
    if (!topicList.isEmpty())
      topics.getSelectionModel().selectFirst();

    // Display questions for default topic
    ArrayList<Question> topicQuestions = questionList.get(topics.getValue());
    ObservableList<String> questionTitles = FXCollections.observableArrayList();
    if (topicQuestions != null) {
      for (Question q : topicQuestions) {
        questionTitles.add(q.getQuestionTitle());
      }
    }
    list = new ListView<String>(questionTitles);

    // Make a listener attached to the comboBox
    topics.valueProperty().addListener(new ChangeListener<Object>() {
      // Modify the changed method to update all this garbage
      public void changed(ObservableValue<?> obv, Object oldVal, Object newVal) {
        ArrayList<Question> topicQuestions = questionList.get(newVal);
        ObservableList<String> questionTitles = FXCollections.observableArrayList();
        if (topicQuestions != null) {
          for (Question q : topicQuestions) {
            questionTitles.add(q.getQuestionTitle());

          }
          // Update ListView field
          list.setItems(questionTitles);
        }
      }
    });

    takeQuiz.getStyleClass().addAll("custom-button", "basic-text");
    addNewQuestion.getStyleClass().addAll("custom-button", "basic-text");
    importBooks.getStyleClass().addAll("custom-button", "basic-text");
    export.getStyleClass().addAll("custom-button", "basic-text");
    topics.getStyleClass().addAll("custom-combo", "basic-text");
    placeHolderNum.getStyleClass().addAll("basic-text");
    placeHolderNum.setFont(Font.font("Letter Gothic"));
    placeHolderNum.setFill(Color.rgb(13, 61, 137));
    list.getStyleClass().addAll("custom-list", "basic-text");
    topicLabel.getStyleClass().addAll("basic-text", "text-padding-top");
    topicLabel.setFill(Color.rgb(13, 61, 137));
    HBox.setMargin(topicLabel, new Insets(8, 0, 0, 0));
    HBox.setMargin(importBooks, new Insets(0, 10, 0, 0));

    hungryBox1.setPadding(new Insets(10, 10, 0, 10));
    hungryBox2.setPadding(new Insets(10, 10, 0, 10));
    hungryBox3.setPadding(new Insets(10, 10, 10, 10));

    topics.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    final Pane spacer = new Pane();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    final Pane spacer2 = new Pane();
    HBox.setHgrow(spacer2, Priority.ALWAYS);
    spacer.setMinSize(10, 1);
    takeQuiz.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

    // Add all objects to scene and display
    hungryBox1.getChildren().addAll(topicLabel, topics, spacer, takeQuiz);
    hungryBox2.getChildren().addAll(addNewQuestion, spacer2, importBooks, export);

    hungryBox3.getChildren().addAll(placeHolderNum);
    vungryBox.getChildren().addAll(hungryBox1, hungryBox2, hungryBox3, list);
    Scene scene = new Scene(vungryBox, 800, 600);
    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    return scene;
  }

  public Scene addQuestion(Stage primaryStage) {

    String imagePath = "";
    ScrollPane scroller = new ScrollPane();

    VBox root = new VBox();
    HBox header = new HBox();
    HBox topic = new HBox();
    HBox top = new HBox();
    VBox center = new VBox();
    HBox bot = new HBox();

    Button back = new Button("Back to Home");
    Text title = new Text("New Question");
    Text topicLabel = new Text("Topic: ");
    TextField topicText = new TextField();
    Text questionLabel = new Text("Question: ");
    TextField questionTitle = new TextField();
    Text optionsLabel = new Text("Choices: ");
    ArrayList<HBox> options = new ArrayList<HBox>();
    Button addOption = new Button("+Add New Choice");
    Button removeOption = new Button("-Remove Choice");
    Text imageLabel = new Text("Photo (Optional): ");
    Button addImage = new Button("Choose File");
    Text fileName = new Text();
    Button finish = new Button("Add Question");

    final ToggleGroup optionSelector = new ToggleGroup();
    VBox radioButtons = new VBox();
    RadioButton optionI = new RadioButton();
    optionI.setToggleGroup(optionSelector);
    optionI.setSelected(true);
    HBox.setMargin(optionI, new Insets(10, 0, 0, 0));
    TextField optionIText = new TextField();
    optionIText.getStyleClass().addAll("custom-textfield", "basic-text");
    HBox.setMargin(optionIText, new Insets(0, 0, 5, 0));
    HBox toAdd = new HBox();
    toAdd.getChildren().addAll(optionI, optionIText);
    options.add(toAdd);
    radioButtons.getChildren().addAll(toAdd);
    HBox.setMargin(radioButtons, new Insets(0, 0, 0, 20));

    // CSS
    HBox.setMargin(back, new Insets(8, 0, 0, 0));
    back.getStyleClass().addAll("custom-button", "basic-text");
    title.getStyleClass().addAll("header-text");
    title.setFill(Color.rgb(13, 61, 137));
    HBox.setMargin(title, new Insets(0, 0, 0, 100));
    topicLabel.getStyleClass().addAll("basic-text");
    topicLabel.setFill(Color.rgb(13, 61, 137));
    HBox.setMargin(topicLabel, new Insets(5, 0, 0, 0));
    topicText.getStyleClass().addAll("custom-textfield", "basic-text");
    questionLabel.getStyleClass().addAll("basic-text");
    questionLabel.setFill(Color.rgb(13, 61, 137));
    HBox.setMargin(questionLabel, new Insets(5, 0, 0, 0));
    questionTitle.setMaxWidth(300);
    questionTitle.getStyleClass().addAll("custom-textfield", "basic-text");
    optionsLabel.getStyleClass().addAll("basic-text");
    optionsLabel.setFill(Color.rgb(13, 61, 137));
    addOption.getStyleClass().addAll("custom-button", "basic-text");
    HBox.setMargin(removeOption, new Insets(0, 0, 0, 8));
    removeOption.getStyleClass().addAll("custom-button", "basic-text");
    imageLabel.getStyleClass().addAll("basic-text");
    imageLabel.setFill(Color.rgb(13, 61, 137));
    HBox.setMargin(imageLabel, new Insets(6, 0, 0, 0));
    addImage.getStyleClass().addAll("custom-button", "basic-text");
    finish.getStyleClass().addAll("custom-button", "basic-text");
    HBox.setMargin(fileName, new Insets(10, 0, 0, 10));

    // event methods
    EventHandler<MouseEvent> AddQuestionEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        RadioButton optionI = new RadioButton();
        optionI.setToggleGroup(optionSelector);
        HBox.setMargin(optionI, new Insets(10, 0, 0, 0));
        TextField optionIText = new TextField();
        optionIText.getStyleClass().addAll("basic-text", "custom-textfield");
        HBox.setMargin(optionIText, new Insets(0, 0, 5, 0));
        HBox toAdd = new HBox();
        toAdd.getChildren().addAll(optionI, optionIText);
        options.add(toAdd);
        radioButtons.getChildren().addAll(toAdd);
      }
    };

    EventHandler<MouseEvent> RemoveOptionEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        if (options.size() != 1) {
          radioButtons.getChildren().remove(options.size() - 1);
          options.remove(options.size() - 1);
        }
      }
    };

    EventHandler<MouseEvent> BackToHomeEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        showStage(primaryStage, home(primaryStage));
      }
    };

    EventHandler<MouseEvent> AddImageEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        try {
          File file = fc.showOpenDialog(primaryStage);
          if (file != null) {
            try {
              fileName.setText(file.getCanonicalPath().toString());
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        } catch (Exception ex) {
          // don't do anything
        }
      }
    };

    EventHandler<MouseEvent> Finalize = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        int correctIndex = -1;
        Alert failure = new Alert(AlertType.ERROR);
        failure.setHeaderText("Invalid Question Parameters");
        if (topicText.getText() == null || topicText.getText().trim().isEmpty()) {
          failure.setContentText("You have typed in an invalid topic");
        }
        if (questionTitle.getText() == null || questionTitle.getText().trim().isEmpty()) {
          failure.setContentText("You have typed in an invalid question title");
        }
        for (int i = 0; i < radioButtons.getChildren().size(); i++) {
          if (((TextField) ((HBox) radioButtons.getChildren().get(i)).getChildren().get(1))
              .getText() == null
              || ((TextField) ((HBox) radioButtons.getChildren().get(i)).getChildren().get(1))
                  .getText().trim().isEmpty()) {
            failure.setContentText("You have typed in an invalid question choice");
            break;
          }
          if (((RadioButton) ((HBox) radioButtons.getChildren().get(i)).getChildren().get(0))
              .isSelected() == true) {
            correctIndex = i;
          }
        }
        if (correctIndex == -1) {
          failure.setContentText("You must select a correct answer");
        }
        if (failure.getContentText().equals("")) {
          String questionT = questionTitle.getText().toString();
          String topic = topicText.getText().toString().toLowerCase();
          ArrayList<String> choices = new ArrayList<String>();
          for (int i = 0; i < radioButtons.getChildren().size(); i++) {
            choices
                .add(((TextField) ((HBox) radioButtons.getChildren().get(i)).getChildren().get(1))
                    .getText().toString());
          }
          String imagePath = fileName.getText().toString();
          Question toAdd = new Question(questionT, choices, correctIndex, imagePath);
          if (questionList.keySet().contains(topic)) {
            questionList.get(topic).add(toAdd);
          } else {
            questionList.put(topic, new ArrayList<Question>());
            questionList.get(topic).add(toAdd);
          }
          showStage(primaryStage, home(primaryStage));
        } else {
          failure.showAndWait();
        }
      }
    };

    // Event Handling
    addOption.addEventFilter(MouseEvent.MOUSE_CLICKED, AddQuestionEvent);
    back.addEventFilter(MouseEvent.MOUSE_CLICKED, BackToHomeEvent);
    addImage.addEventFilter(MouseEvent.MOUSE_CLICKED, AddImageEvent);
    removeOption.addEventFilter(MouseEvent.MOUSE_CLICKED, RemoveOptionEvent);
    finish.addEventFilter(MouseEvent.MOUSE_CLICKED, Finalize);

    // spacing
    header.getChildren().addAll(back, title);
    header.setPadding(new Insets(0, 0, 20, 0));
    topic.getChildren().addAll(topicLabel, topicText);
    topic.setPadding(new Insets(0, 0, 20, 0));
    top.getChildren().addAll(questionLabel, questionTitle);
    top.setPadding(new Insets(0, 0, 20, 0));
    center.getChildren().addAll(optionsLabel, radioButtons, new HBox(addOption, removeOption));
    center.setPadding(new Insets(0, 0, 20, 0));
    bot.getChildren().addAll(imageLabel, addImage, fileName);
    bot.setPadding(new Insets(0, 0, 40, 0));

    root.getChildren().addAll(header, topic, top, center, bot, finish);
    root.setPadding(new Insets(0, 0, 0, 20));

    // scroll bar policies
    scroller.setContent(root);
    scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
    scroller.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

    Scene scene = new Scene(scroller, 800, 600);

    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    return scene;
  }

  public Scene finalScore(Stage primaryStage) {

    // Get values from quiz
    double numQuestions = quizObject.getNumQuestions();
    double numAnswered = quizObject.getIndex();
    double numCorrect = quizObject.getCorrect();
    double percentCorrect = (numCorrect / numQuestions) * 100;

    // Creation of buttons and boxes
    VBox vungryBox = new VBox();
    HBox hungryBox = new HBox();
    Text finalScore = new Text("Final Score");
    Text score = new Text((Math.round(percentCorrect * 100) / 100) + "%");
    Text questionsAnswered = new Text("You answered " + (int) numAnswered + " questions");
    Text correctAnswers = new Text("You got " + (int) numCorrect + " answers correct");
    Button tryNewQuiz = new Button("Try New Quiz");
    Button returnHome = new Button("Return Home");

    // CSS
    tryNewQuiz.getStyleClass().addAll("custom-button", "basic-text");
    returnHome.getStyleClass().addAll("custom-button", "basic-text");
    finalScore.getStyleClass().addAll("header-text");
    finalScore.setFont(Font.font("Letter Gothic"));
    finalScore.setFill(Color.rgb(13, 61, 137));
    score.getStyleClass().addAll("header-text");
    score.setFont(Font.font("Letter Gothic"));
    score.setFill(Color.rgb(137, 13, 37));
    questionsAnswered.getStyleClass().addAll("basic-text");
    questionsAnswered.setFont(Font.font("Letter Gothic"));
    questionsAnswered.setFill(Color.rgb(13, 61, 137));
    correctAnswers.getStyleClass().addAll("basic-text");
    correctAnswers.setFont(Font.font("Letter Gothic"));
    correctAnswers.setFill(Color.rgb(13, 61, 137));
    final Pane spacer = new Pane();
    spacer.setMinSize(10, 1);

    // Align center
    vungryBox.setAlignment(Pos.CENTER);
    hungryBox.setAlignment(Pos.CENTER);
    hungryBox.setPadding(new Insets(10, 10, 10, 10));

    // event handlers
    EventHandler<MouseEvent> GoHome = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        showStage(primaryStage, home(primaryStage));
      }
    };

    EventHandler<MouseEvent> NewQuiz = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        showStage(primaryStage, makeQuiz(primaryStage));
      }
    };

    // event buttons
    tryNewQuiz.addEventFilter(MouseEvent.MOUSE_CLICKED, NewQuiz);
    returnHome.addEventFilter(MouseEvent.MOUSE_CLICKED, GoHome);

    // Add all objects to scene and display
    hungryBox.getChildren().addAll(tryNewQuiz, spacer, returnHome);
    vungryBox.getChildren().addAll(finalScore, score, questionsAnswered, correctAnswers, hungryBox);
    Scene scene = new Scene(vungryBox, 800, 600);
    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    return scene;
  }

  public Scene makeQuiz(Stage primaryStage) {

    VBox root = new VBox();
    HBox header = new HBox();
    HBox numQuestions = new HBox();
    VBox topicList = new VBox();

    Button back = new Button("Back to Home");
    Text titleText = new Text("Take Quiz");
    Text numQuestionsLabel = new Text("How many questions?");
    TextField insertNum = new TextField();
    Text topicLabel = new Text("Topics: ");
    Button finish = new Button("Generate");

    header.getChildren().addAll(back, titleText);
    numQuestions.getChildren().addAll(numQuestionsLabel, insertNum);
    topicList.getChildren().addAll(topicLabel);

    for (String topic : questionList.keySet()) {
      CheckBox addMe = new CheckBox(topic);
      HBox.setMargin(addMe, new Insets(5, 0, 0, 0));
      addMe.getStyleClass().addAll("basic-text");
      addMe.setTextFill(Color.rgb(13, 61, 137));
      topicList.getChildren().add(addMe);
    }

    // event handlers
    EventHandler<MouseEvent> BackToHomeEvent = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        showStage(primaryStage, home(primaryStage));
      }
    };

    EventHandler<MouseEvent> Generate = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        Alert fail = new Alert(AlertType.ERROR);
        fail.setHeaderText("Invalid Information");
        ArrayList<String> selectedTopics = new ArrayList<String>();
        for (int i = 1; i < topicList.getChildren().size(); i++) {
          if (((CheckBox) topicList.getChildren().get(i)).isSelected()) {
            selectedTopics.add(((CheckBox) topicList.getChildren().get(i)).getText());
          }
        }
        if (selectedTopics.size() == 0) {
          fail.setContentText("No topics have been selected");
        }
        try {
          if (fail.getContentText().equals("")) {
            int numQuestions = Integer.parseInt(insertNum.getText());
            GenerateQuiz generation = new GenerateQuiz(numQuestions, selectedTopics);
            quizObject = generation.generate(questionList);
            showStage(primaryStage, quiz(primaryStage, quizObject.getQuestion()));
          } else {
            fail.showAndWait();
          }
        } catch (Exception exception) {
          fail.setContentText("A valid number wasn't typed in");
          fail.showAndWait();
        }
      }
    };

    // event calls
    back.addEventFilter(MouseEvent.MOUSE_CLICKED, BackToHomeEvent);
    finish.addEventFilter(MouseEvent.MOUSE_CLICKED, Generate);

    // CSS
    back.getStyleClass().addAll("custom-button", "basic-text");
    HBox.setMargin(back, new Insets(8, 0, 0, 0));
    titleText.getStyleClass().addAll("header-text");
    titleText.setFill(Color.rgb(13, 61, 137));
    HBox.setMargin(titleText, new Insets(0, 0, 0, 150));
    numQuestionsLabel.getStyleClass().addAll("basic-text");
    numQuestionsLabel.setFill(Color.rgb(13, 61, 137));
    HBox.setMargin(numQuestionsLabel, new Insets(3, 5, 0, 0));

    insertNum.getStyleClass().addAll("custom-textfield");
    insertNum.setMaxWidth(50);
    topicLabel.getStyleClass().addAll("basic-text");
    topicLabel.setFill(Color.rgb(13, 61, 137));
    finish.getStyleClass().addAll("custom-button", "basic-text");

    header.setPadding(new Insets(10, 0, 10, 0));
    numQuestions.setPadding(new Insets(0, 0, 10, 0));
    topicList.setPadding(new Insets(0, 0, 20, 0));

    root.getChildren().addAll(header, numQuestions, topicList, finish);
    root.setPadding(new Insets(0, 0, 0, 20));

    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    return scene;
  }

  public Scene quiz(Stage primaryStage, Question test) {

    VBox root = new VBox();
    HBox questionHeader = new HBox();
    HBox questionTitleBox = new HBox();
    HBox questionContent = new HBox();
    VBox questionOptions = new VBox();
    HBox submission = new HBox();

    Button exitQuiz = new Button("Exit Quiz");
    Text questionHead = new Text("Question ");
    Text questionNum = new Text("" + quizObject.getIndex() + " of " + quizObject.getNumQuestions());
    Text questionTitle = new Text(test.getQuestionTitle());
    ImageView image = new ImageView();
    try {
      Image check = new Image("file:" + test.getImage(), 250, 250, true, true);
      image.setImage(check);
    } catch (Exception e) {
      // don't populate the image
    }
    Button next = new Button("Submit");
    Button nextQuestion = new Button("Next");

    final ToggleGroup optionSelector = new ToggleGroup();

    for (String option : test.getOptions()) {
      RadioButton optionI = new RadioButton(option);
      optionI.setToggleGroup(optionSelector);
      HBox.setMargin(optionI, new Insets(3, 0, 0, 0));
      optionI.getStyleClass().addAll("basic-text");
      optionI.setTextFill(Color.rgb(13, 61, 137));
      questionOptions.getChildren().addAll(optionI);
    }

    root.getChildren().addAll(exitQuiz, questionHeader, questionTitleBox, questionContent,
        submission);
    questionHeader.getChildren().addAll(questionHead, questionNum);
    questionTitleBox.getChildren().addAll(questionTitle);
    questionContent.getChildren().addAll(image, questionOptions);
    submission.getChildren().addAll(next);

    // CSS
    root.setPadding(new Insets(10, 0, 0, 10));
    questionHeader.setAlignment(Pos.CENTER);
    questionTitleBox.setAlignment(Pos.CENTER);
    questionTitleBox.setPadding(new Insets(0, 0, 30, 0));
    exitQuiz.getStyleClass().addAll("custom-button", "basic-text");
    next.getStyleClass().addAll("custom-button", "basic-text");
    submission.setAlignment(Pos.BOTTOM_RIGHT);
    submission.setPadding(new Insets(0, 50, 50, 0));
    questionHead.getStyleClass().addAll("header-text");
    questionHead.setFill(Color.rgb(13, 61, 137));
    questionNum.getStyleClass().addAll("header-text");
    questionNum.setFill(Color.rgb(13, 61, 137));
    questionTitle.getStyleClass().addAll("basic-text");
    questionTitle.setFill(Color.rgb(13, 61, 137));
    nextQuestion.getStyleClass().addAll("basic-text", "custom-button");
    HBox.setMargin(image, new Insets(0, 30, 0, 0));
    image.setPreserveRatio(true);

    // event handlers
    EventHandler<MouseEvent> CheckQuestion = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        boolean somethingSelected = true;
        submission.getChildren().remove(next);
        for (int i = 0; i < questionOptions.getChildren().size(); i++) {
          if (((RadioButton) questionOptions.getChildren().get(i)).isSelected()) {
            if (i == test.getCorrect()) {
              ((RadioButton) questionOptions.getChildren().get(i)).setText(
                  ((RadioButton) questionOptions.getChildren().get(i)).getText() + " - :D Correct");
              ((RadioButton) questionOptions.getChildren().get(i))
                  .setTextFill(Color.rgb(0, 128, 0));
              quizObject.incCorrect();
              somethingSelected = false;
            } else if (i != test.getCorrect()) {
              ((RadioButton) questionOptions.getChildren().get(i))
                  .setText(((RadioButton) questionOptions.getChildren().get(i)).getText()
                      + " - X Incorrect");

              ((RadioButton) questionOptions.getChildren().get(i))
                  .setTextFill(Color.rgb(128, 0, 0));

              ((RadioButton) questionOptions.getChildren().get(test.getCorrect())).setText(
                  ((RadioButton) questionOptions.getChildren().get(test.getCorrect())).getText()
                      + " - This is the correct answer");
              ((RadioButton) questionOptions.getChildren().get(test.getCorrect()))
                  .setTextFill(Color.rgb(0, 128, 0));
              somethingSelected = false;
            }
          }
        }
        if (somethingSelected) {
          ((RadioButton) questionOptions.getChildren().get(test.getCorrect())).setText(
              ((RadioButton) questionOptions.getChildren().get(test.getCorrect())).getText()
                  + " - This is the correct answer");
          ((RadioButton) questionOptions.getChildren().get(test.getCorrect()))
              .setTextFill(Color.rgb(0, 128, 0));
        }
        submission.getChildren().add(nextQuestion);
      }
    };

    EventHandler<MouseEvent> ExitQuiz = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        showStage(primaryStage, finalScore(primaryStage));
      }
    };

    EventHandler<MouseEvent> NextQuestion = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        try {
          Question next = quizObject.getQuestion();
          showStage(primaryStage, quiz(primaryStage, next));
        } catch (IndexOutOfBoundsException exception) {
          showStage(primaryStage, finalScore(primaryStage));
        }
      }
    };

    // button events
    next.addEventFilter(MouseEvent.MOUSE_CLICKED, CheckQuestion);
    exitQuiz.addEventFilter(MouseEvent.MOUSE_CLICKED, ExitQuiz);
    nextQuestion.addEventFilter(MouseEvent.MOUSE_CLICKED, NextQuestion);

    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    questionTitle.wrappingWidthProperty().bind(scene.widthProperty());
    return scene;
  }
}
