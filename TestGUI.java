package huffmanproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import static javafx.application.Application.launch;

public class TestGUI extends Application {

    private Huffman manageData; // For Huffman encoding and tree management

    @Override
    public void start(Stage primaryStage) {
        // Get screen dimensions
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        // GUI Components
        Label inputLabel = new Label("Enter text to compress:");
        inputLabel.setFont(new Font("Arial", 24)); // Increased font size
        inputLabel.setTextFill(Color.DARKBLUE);

        TextArea inputTextArea = new TextArea();
        inputTextArea.setPromptText("Type your text here...");
        inputTextArea.setStyle("-fx-font-size: 18px; -fx-border-color: #007ACC;"); // Increased font size

        Button compressButton = new Button("Compress");
        compressButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;"); // Increased font size

        // TableView to display the tree nodes (character, frequency, and code)
        TableView<TreeNode> treeNodesTable = new TableView<>();
        TableColumn<TreeNode, String> nodeCharColumn = new TableColumn<>("Character");
        nodeCharColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getCharacter())));

        TableColumn<TreeNode, Integer> nodeFreqColumn = new TableColumn<>("Frequency");
        nodeFreqColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getFreq()));

        TableColumn<TreeNode, String> nodeCodeColumn = new TableColumn<>("Code");
        nodeCodeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCode()));

        treeNodesTable.getColumns().addAll(nodeCharColumn, nodeFreqColumn, nodeCodeColumn);

        // Labels for displaying compressed message and compression ratio
        Label compressedLabel = new Label("Compressed Message:");
        compressedLabel.setFont(new Font("Arial", 18)); // Increased font size
        TextArea compressedTextArea = new TextArea();
        compressedTextArea.setEditable(false);
        compressedTextArea.setStyle("-fx-font-size: 18px; -fx-border-color: #007ACC;"); // Increased font size

        Label compressionRatioLabel = new Label("Compression Ratio:");
        compressionRatioLabel.setFont(new Font("Arial", 18)); // Increased font size
        Label compressionRatioValue = new Label();
        compressionRatioValue.setFont(new Font("Arial", 18)); // Increased font size

        Button viewTreeButton = new Button("View Huffman Tree");
        viewTreeButton.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;"); // Increased font size

        // Event handlers
        compressButton.setOnAction(event -> {
            String inputText = inputTextArea.getText();
            if (!inputText.isEmpty()) {
                // Build Huffman tree and generate codes using ManageData
                manageData = new Huffman(inputText);

                // Ensure manageData.getCodes() returns a Map<Character, String>
                Map<Character, String> huffmanCodes = manageData.getCodesMap();
                if (huffmanCodes != null) {
                    // Display the compressed message
                    compressedTextArea.setText(manageData.getCompressedData());
                    // Display the compression ratio
                    compressionRatioValue.setText(String.format("%.2f%%", manageData.getCompressionPercentage()));

                    // Get the tree nodes and add them to the table
                    List<TreeNode> treeNodes = collectTreeNodes(manageData.getHuffmanTree());
                    treeNodesTable.getItems().setAll(treeNodes);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to generate Huffman codes.");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter some text to compress.");
                alert.show();
            }
        });

        viewTreeButton.setOnAction(event -> {
            if (manageData != null && manageData.getHuffmanTree() != null) {
                Stage treeStage = new Stage();
                Pane treePane = new Pane();
                treePane.setStyle("-fx-border-color: #ccc; -fx-background-color: #e0f7fa; -fx-padding: 10px;");
                treePane.setPrefSize(screenWidth * 0.8, screenHeight * 0.8);
                visualizeTree(treePane, manageData.getHuffmanTree());

                Scene treeScene = new Scene(treePane, screenWidth * 0.8, screenHeight * 0.8);
                treeStage.setTitle("Huffman Tree Visualization");
                treeStage.setScene(treeScene);
                treeStage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please compress text first to view the tree.");
                alert.show();
            }
        });

        // Layout Setup
        VBox inputBox = new VBox(20, inputLabel, inputTextArea, compressButton, viewTreeButton);
        inputBox.setPadding(new Insets(20));
        inputBox.setPrefWidth(screenWidth * 0.3);

        VBox resultBox = new VBox(20, compressedLabel, compressedTextArea, compressionRatioLabel, compressionRatioValue, new Label("Tree Nodes:"), treeNodesTable);
        resultBox.setPadding(new Insets(20));
        resultBox.setPrefWidth(screenWidth * 0.6);

        BorderPane mainContent = new BorderPane();
        mainContent.setLeft(inputBox);
        mainContent.setCenter(resultBox);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(mainContent, screenWidth, screenHeight);

        primaryStage.setTitle("Huffman Compression Tool");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void visualizeTree(Pane treePane, TreeNode root) {
        treePane.getChildren().clear();
        drawTree(treePane, root, treePane.getPrefWidth() / 2, 50, treePane.getPrefWidth() / 4);
    }

    private void drawTree(Pane pane, TreeNode node, double x, double y, double offset) {
        if (node == null) {
            return;
        }

        Circle circle = new Circle(x, y, 30);
        circle.setFill(Color.LIGHTCORAL);
        circle.setStroke(Color.BLACK);

        String textContent = node.getCharacter() == '\0' ? String.valueOf(node.getFreq()) : node.getCharacter() + "\n" + node.getFreq();
        Text text = new Text(x - 10, y + 5, textContent);
        text.setFont(new Font("Arial", 18)); // Increased font size

        pane.getChildren().addAll(circle, text);

        if (node.getLeft() != null) {
            Line line = new Line(x, y, x - offset, y + 100);
            line.setStroke(Color.DARKGRAY);
            pane.getChildren().add(line);
            drawTree(pane, node.getLeft(), x - offset, y + 100, offset / 2);
        }

        if (node.getRight() != null) {
            Line line = new Line(x, y, x + offset, y + 100);
            line.setStroke(Color.DARKGRAY);
            pane.getChildren().add(line);
            drawTree(pane, node.getRight(), x + offset, y + 100, offset / 2);
        }
    }

    private List<TreeNode> collectTreeNodes(TreeNode root) {
        List<TreeNode> nodes = new ArrayList<>();
        collectNodesRecursive(root, nodes);
        return nodes;
    }

    private void collectNodesRecursive(TreeNode node, List<TreeNode> nodes) {
        if (node == null) {
            return;
        }

        if (node.getCharacter() != '\0') {
            nodes.add(node);
        }

        collectNodesRecursive(node.getLeft(), nodes);
        collectNodesRecursive(node.getRight(), nodes);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
