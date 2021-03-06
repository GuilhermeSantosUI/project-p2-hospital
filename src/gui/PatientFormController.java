package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import application.VistaNavigator;
import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Patient;
import model.exceptions.ValidateException;
import model.services.PatientService;

public class PatientFormController implements Initializable {

	public enum Gender {
		M("Masculino"), F("Feminino");

		private String label;

		Gender(String label) {
			this.label = label;
		}

		public String toString() {
			return label;
		}
	}

	@FXML
	private Label genderError;

	@FXML
	private Label dateError;

	@FXML
	private Label nameError;

	@FXML
	private TextField txtName;

	@FXML
	private ComboBox<Gender> cbGender;

	@FXML
	private DatePicker dpBirth;

	@FXML
	private TextField txtAddress;

	@FXML
	private TextField txtCell;

	@FXML
	private Label cellError;

	@FXML
	private TextField txtTelephone;

	@FXML
	private Label teleError;

	@FXML
	private Label addressError;

	@FXML
	private TextField txtEmail;

	@FXML
	private Label emailError;

	private Patient entity = new Patient();

	private PatientService service = new PatientService();

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void setServices(PatientService service) {
		this.service = service;
	}

	public void setPatient(Patient entity) {
		this.entity = entity;
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (entity == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
			VistaNavigator.loadVista(VistaNavigator.PATIENTLIST);
		} catch (ValidateException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Patient getData() {
		Patient pat = new Patient();
		ValidateException exception = new ValidateException("Validation error");

		pat.setIdpaciente(entity.getIdpaciente());
		if (txtName.getText().isEmpty() || txtName.getText().trim().equals("")) {
			exception.addError("nome", "Field can't be empty");
		}
		pat.setNome(txtName.getText());

		String selectedValue = (String) cbGender.getValue().name();
		pat.setSexo(selectedValue);

		if (dpBirth.getValue() == null) {
			exception.addError("datanascimento", "Field can't be empty");
		} else {
			LocalDate localDate = dpBirth.getValue();
			java.util.Date date = java.sql.Date.valueOf(localDate);
			pat.setDatanascimento(date);
		}

		if (txtAddress.getText().isEmpty() || txtAddress.getText().trim().equals("")) {
			exception.addError("endereco", "Field can't be empty");
		}
		pat.setEndereco(txtAddress.getText());

		if (txtTelephone.getText().isEmpty() || txtTelephone.getText().trim().equals("")) {
			exception.addError("telefone", "Field can't be empty");
		}
		pat.setTelefone(txtTelephone.getText());

		if (txtCell.getText().isEmpty() || txtCell.getText().trim().equals("")) {
			exception.addError("numcelular", "Field can't be empty");
		}
		pat.setNumcelular(txtCell.getText());

		if (txtEmail.getText().isEmpty() || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		pat.setEmail(txtEmail.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return pat;
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		nameError.setText((fields.contains("nome") ? errors.get("nome") : ""));
		genderError.setText((fields.contains("sexo") ? errors.get("sexo") : ""));
		dateError.setText((fields.contains("datanascimento") ? errors.get("datanascimento") : ""));
		addressError.setText((fields.contains("endereco") ? errors.get("endereco") : ""));
		cellError.setText((fields.contains("numcelular") ? errors.get("numcelular") : ""));
		teleError.setText((fields.contains("telefone") ? errors.get("numtelefone") : ""));
		emailError.setText((fields.contains("email") ? errors.get("email") : ""));
	}

	public void handleUpdateData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}

		txtName.setText(entity.getNome());

		if (entity.getSexo() == null) {
			cbGender.getSelectionModel().selectFirst();
		} else {
			if (entity.getSexo() == "M") {
				cbGender.setValue(Gender.M);
			} else {
				cbGender.setValue(Gender.F);
			}
		}
		
		Date x = entity.getDatanascimento();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (x != null) {
			String s = sdf.format(x.getTime());
			dpBirth.setValue(LOCAL_DATE(String.valueOf(s)));
		}
		
		txtAddress.setText(entity.getEndereco());
		txtCell.setText(entity.getNumcelular());
		txtTelephone.setText(entity.getTelefone());
		txtEmail.setText(entity.getEmail());

	}
	
	private final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		cbGender.getItems().setAll(Gender.values());
		Constraints.setTextFieldMaxLength(txtCell, 11);
		Constraints.setTextFieldMaxLength(txtTelephone, 11);
	}

}
