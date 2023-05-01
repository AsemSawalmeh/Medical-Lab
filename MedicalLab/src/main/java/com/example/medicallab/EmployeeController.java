package com.example.medicallab;

import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField searchBar;
    @FXML
    private ChoiceBox<String> filterChoices;
    @FXML
    private DatePicker filterDate;
    @FXML
    private TextField filterField;
    @FXML
    private TableView<Employee> table;
    @FXML
    private TableColumn<Employee, Integer> idColumn;
    @FXML
    private TableColumn<Employee, String> nameColumn;
    @FXML
    private TableColumn<Employee, String> genderColumn;
    @FXML
    private TableColumn<Employee, Integer> branchColumn;
    @FXML
    private TableColumn<Employee, String> shiftColumn;
    @FXML
    private TableColumn<Employee, LocalDate> birthDateColumn;
    @FXML
    private TableColumn<Employee, Hyperlink> contactColumn;
    @FXML
    private TableColumn<Employee, HBox> manipulateColumn;

    // add pane
    @FXML
    private TextField empName;
    @FXML
    private TextField empAddress;
    @FXML
    private ComboBox<String> yearPicker;
    @FXML
    private ComboBox<String> monthPicker;
    @FXML
    private ComboBox<String> dayPicker;
    @FXML
    private TextField phoneNumber;
    @FXML
    private ChoiceBox<String> shift;
    @FXML
    private TextField branchId;
    @FXML
    private ChoiceBox<String> jobDesc;
    @FXML
    private TextField salary;
    @FXML
    private DatePicker hireDate;
    @FXML
    private RadioButton male;
    @FXML
    private RadioButton female;
    @FXML
    private ToggleGroup genderGroup;
    @FXML
    private TitledPane addEmployeePane;
    @FXML
    private VBox labelBox;
    @FXML
    private VBox textBox;
    @FXML
    private ButtonBar buttonBar;

    // edit pane
    @FXML
    private AnchorPane editPane;
    @FXML
    private TextField editName;
    @FXML
    private TextField editShift;
    @FXML
    private Label editShiftLabel;
    @FXML
    private DatePicker editDate;
    @FXML
    private TextField editAddress;
    @FXML
    private TextField editSalary;
    @FXML
    private TextField editHireDate;
    @FXML
    private TextField editJobDesc;
    @FXML
    private TextField editBranch;
    @FXML
    private RadioButton editMale;
    @FXML
    private RadioButton editFemale;
    @FXML
    private ToggleGroup editGenderGroup;
    @FXML
    private Hyperlink editPatientLink;
    @FXML
    private Button update;

    private final ObservableList<Employee> dataList = FXCollections.observableArrayList();
    private final HashMap<String, String> primaryKey = new HashMap<>();
    private final HashMap<String, String> alteredFields = new HashMap<>();

    private int year, month;
    private Employee employee;
    private int phoneCount = 1, i = 4;
    private String gender;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Main.loadAllEmployees(dataList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        branchColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        shiftColumn.setCellValueFactory(new PropertyValueFactory<>("shift"));
        contactColumn.setCellFactory(col -> {
            Hyperlink showMoreLink = new Hyperlink("More Info");
            showMoreLink.setStyle("-fx-text-fill: #6666ff;");

            return new TableCell<>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        showMoreLink.setOnMouseClicked(e -> {
                            root.setPrefWidth(1840);
                            editPane.setVisible(true);
                            employee = getTableView().getItems().get(getIndex());
                            // fill the patient information
                            fillEmployeeData();
                            // set all fields in the edit page to non-editable
                            setAllFieldsEditable(false);

                            Transition transition = new Transition() {
                                {
                                    setCycleDuration(Duration.millis(300));
                                }

                                @Override
                                protected void interpolate(double v) {
                                    scrollPane.setHvalue(v);
                                }
                            };
                            transition.play();
                            transition.setOnFinished(e2 -> scrollPane.setPadding(new Insets(0, 0, 0, -950)));
                        });
                        setGraphic(showMoreLink);
                    }
                }
            };
        });
        FilteredList<Employee> filteredData = new FilteredList<>(dataList);
        searchBar.setOnKeyTyped(e -> filteredData.setPredicate(emp -> {
            if (searchBar.getText().isEmpty())
                return true;

            String lowerCaseFilter = searchBar.getText().toLowerCase();

            if (emp.getName().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (emp.getGender().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (emp.getAddress().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (String.valueOf(emp.getId()).contains(lowerCaseFilter))
                return true;
            else
                return false;
        }));
        SortedList<Employee> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }

    private void fillEmployeeData() {
        primaryKey.put("emp_id", String.valueOf(employee.getId()));

        if (employee.getGender().equalsIgnoreCase("male")) {
            editMale.setSelected(true);
            editFemale.setSelected(false);
        }
        else if (employee.getGender().equalsIgnoreCase("female")) {
            editFemale.setSelected(true);
            editMale.setSelected(false);
        }
        editName.setText(employee.getName());
        editShift.setText(employee.getShift());
        editShiftLabel.setText("show details here");
        editDate.setValue(employee.getBirthDate());
        editAddress.setText(employee.getAddress());
        editSalary.setText("show details here");
        editHireDate.setText(employee.getHireDate().toString());
        editJobDesc.setText(employee.getJobDesc());
        editBranch.setText(String.valueOf(employee.getBranchId()));
    }

    @FXML
    private void expandPane(MouseEvent e) {
        if (addEmployeePane.isExpanded() && scrollPane.getVvalue() < 0.5) {
            root.setPrefHeight(1300);
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.millis(300));
                }
                protected void interpolate(double frac) {
                    scrollPane.setVvalue(frac);
                }
            };
            transition.play();
        }
        else if (!addEmployeePane.isExpanded()){
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.millis(350));
                }
                protected void interpolate(double frac) {
                    scrollPane.setVvalue(1 - frac);
                }
            };
            transition.play();
            transition.setOnFinished(event2 -> root.setPrefHeight(630));
        }
    }

    @FXML
    private void addAnotherPhone(ActionEvent e) {
        Label label = new Label("Phone Number " + (++phoneCount) + ":");
        label.setStyle("-fx-font-size: 18px");
        HBox phoneBox = new HBox();
        Button remove = new Button("del");
        //remove.setGraphic(new ImageView(new Image("C:\\Users\\User\\IdeaProjects\\MedicalLab\\images\\remove.png")));

        phoneBox.getChildren().addAll(new TextField(), remove);

        labelBox.getChildren().add(++i, label);
        textBox.getChildren().add(i, phoneBox);
        addEmployeePane.setPrefHeight(addEmployeePane.getPrefHeight() + 50);
        root.setPrefHeight(root.getPrefHeight() + 50);
        buttonBar.setLayoutY(buttonBar.getLayoutY() + 50);

        remove.setOnAction(event -> {
            labelBox.getChildren().remove(i);
            textBox.getChildren().remove(i--);
            phoneCount--;
            addEmployeePane.setPrefHeight(addEmployeePane.getPrefHeight() - 50);
            buttonBar.setLayoutY(buttonBar.getLayoutY() - 50);
            root.setPrefHeight(root.getPrefHeight() - 50);
            scrollPane.setVvalue(0);
        });
    }

    @FXML
    private void add_closeEmployeeButton(ActionEvent e) {
        addEmployeePane.setExpanded(!addEmployeePane.isExpanded());
        MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, false, false, false, false, false, false, null);
        addEmployeePane.fireEvent(mouseEvent);
        male.setSelected(false);
        female.setSelected(false);
        empName.clear();
        yearPicker.getSelectionModel().clearSelection();
        monthPicker.getSelectionModel().clearSelection();
        dayPicker.getSelectionModel().clearSelection();
        empAddress.clear();
        phoneNumber.clear();
        branchId.clear();
        salary.clear();
        shift.getSelectionModel().clearSelection();
        jobDesc.getSelectionModel().clearSelection();

        for (; i >= 5; i--) {
            labelBox.getChildren().remove(i);
            textBox.getChildren().remove(i);
        }
        buttonBar.setLayoutY(545);
        addEmployeePane.setPrefHeight(470);
    }

    @FXML
    private void addNewEmployee(ActionEvent e) throws SQLException {
        /*String shi = shift.getSelectionModel().getSelectedItem();
        String name = empName.getText();
        LocalDate date = LocalDate.of(year, month+1, dayPicker.getSelectionModel().getSelectedIndex()+1);
        String address = empAddress.getText();
        LocalDate hireD = hireDate.getValue();
        String jobD = jobDesc.getSelectionModel().getSelectedItem();
        int bId = Integer.parseInt(branchId.getText());
        double sal = Double.parseDouble(salary.getText());
        ArrayList<String> phones = new ArrayList<>();
        phones.add(phoneNumber.getText());

        for (int j = 5; j <= i; j++) {
            TextField field = (TextField) ((HBox)textBox.getChildren().get(j)).getChildren().get(0);
            phones.add(field.getText());
            field.clear();
        }
        textBox.getChildren().remove(5, i+1);
        labelBox.getChildren().remove(5, i+1);

        //Main.insetEmployee(dataList, name, date, gender, email, address, hasInsurance, phones);

        //add.setDisable(true);
        empName.clear();
        // clear the selection
        yearPicker.getSelectionModel().clearSelection();
        monthPicker.getSelectionModel().clearSelection();
        dayPicker.getSelectionModel().clearSelection();
        patientAddress.clear();
        phoneNumber.clear();
        patientEmail.clear();
        male.setSelected(false);
        female.setSelected(false);
        insuranceBox.setSelected(false);
        gender = "";
        phoneCount = 1;
        i = 4;*/
    }

    @FXML
    private void cancelEdit(ActionEvent e) {
        scrollPane.setPadding(new Insets(0, 0, 0, 0));

        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(300));
            }
            @Override
            protected void interpolate(double v) {
                scrollPane.setHvalue(1 - v);
            }
        };
        transition.play();
        transition.setOnFinished(e2 -> {
            root.setPrefWidth(920);
            editPane.setVisible(false);
        });
    }

    @FXML
    private void editThisEmployee(ActionEvent e) {
        setAllFieldsEditable(true);
    }

    @FXML
    private void updateButton(ActionEvent e) {
        String shi = shift.getSelectionModel().getSelectedItem();
        String name = empName.getText();
        LocalDate date = editDate.getValue();
        String address = empAddress.getText();
        LocalDate hireD = hireDate.getValue();
        String jobD = jobDesc.getSelectionModel().getSelectedItem();
        int bId = Integer.parseInt(branchId.getText());
        double sal = Double.parseDouble(salary.getText());

        if (!employee.getName().equalsIgnoreCase(name)) {
            alteredFields.put("emp_name", "'" + name + "'");
            employee.setName(Main.capitalize(name));
        }
        if (!employee.getAddress().equalsIgnoreCase(address)) {
            alteredFields.put("address", "'" + address + "'");
            employee.setAddress(address);
        }
        if (!employee.getShift().equalsIgnoreCase(shi)) {
            alteredFields.put("shift", "'" + shi + "'");
            employee.setShift(shi);
        }
        if (!employee.getGender().equalsIgnoreCase(gender)) {
            alteredFields.put("gender", "'" + gender + "'");
            employee.setGender(gender);
        }
        if (employee.getBirthDate() != date) {
            alteredFields.put("date_of_birth", "'" + date + "'");
            employee.setBirthDate(date);
        }
        if (employee.getSalary() != sal) {
            alteredFields.put("salary", String.valueOf(sal));
            employee.setSalary(sal);
        }
        if (hireD != employee.getHireDate()) {
            alteredFields.put("hire_date", String.valueOf(hireD));
            employee.setHireDate(hireD);
        }
        if (!employee.getJobDesc().equalsIgnoreCase(jobD)) {
            alteredFields.put("job_description", "'" + jobD + "'");
            employee.setJobDesc(jobD);
        }
        if (employee.getBranchId() != bId) {
            alteredFields.put("branch_id", String.valueOf(bId));
            employee.setJobDesc(jobD);
        }
        try {
            Main.updateRecord("Patient", primaryKey, alteredFields);
            table.refresh();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            alteredFields.clear();
        }
    }

    @FXML
    private void openEditPhonesPage(ActionEvent e) {

    }

    private void setAllFieldsEditable(boolean editable) {
        editName.setEditable(editable);
        editAddress.setEditable(editable);
        editShift.setEditable(editable);
        editDate.setEditable(editable);
        editSalary.setEditable(editable);
        editJobDesc.setEditable(editable);
        editBranch.setEditable(editable);
        editFemale.setDisable(!editable);
        editMale.setDisable(!editable);
        editPatientLink.setVisible(!editable);
        update.setDisable(!editable);
    }
}
