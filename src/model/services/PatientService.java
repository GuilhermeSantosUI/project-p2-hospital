package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.PatientDao;
import model.entities.Patient;

public class PatientService {
	
	private PatientDao dao = DaoFactory.createPatientDao();
	
	public List<Patient> findAll(){
		return dao.findAll();
	}

}