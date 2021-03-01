package com.smile.tech.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import com.smile.tech.model.Attendence;
import com.smile.tech.model.Users;
import com.smile.tech.payload.response.MessageResponse;
import com.smile.tech.repository.AttendenceRepository;

@Service
public class AttendenceService {

	@Autowired
	AttendenceRepository repository;

	public Attendence saveAttendence(Users user) {

		ZonedDateTime zdtAtET = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime date = zdtAtET.toLocalDateTime();

		Attendence attendence = new Attendence();
		attendence.setStartTime(date);
		attendence.setUserID(user.getId());
		attendence.setUsername(user.getUsername());

		return repository.save(attendence);
	};

	public List<Attendence> findAll() {
		return repository.findAll();
	}

	public Attendence findById(String id) {
		return repository.findById(id).orElseThrow(() -> new ResourecNotFoundException());
	}

	public Attendence updateAttendence(Attendence attendence) {
		
		ZonedDateTime zdtAtET = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime date = zdtAtET.toLocalDateTime();
        
		attendence.setEndTime(date);
		return repository.save(attendence);
	}

	public ResponseEntity<?> attendenceRecord(Users user, List<Attendence> attendence) {

		ZonedDateTime zdtAtET = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime date = zdtAtET.toLocalDateTime();

		LocalDate today = date.toLocalDate();
		List<Attendence> ls = new ArrayList<>();
		if (attendence.isEmpty()) {
			saveAttendence(user);
			return ResponseEntity.ok(new MessageResponse("Attendence Recorded!"));
		}
		for (int i = 0; i < attendence.size(); i++) {
			if (user.getId().equals(attendence.get(i).getUserID())) {
				ls.add(attendence.get(i));
			}
		}
		if (ls.size() > 0) {
			boolean check = false;
			for (int j = 0; j < ls.size(); j++) {
				if (ls.get(j).getStartTime().toLocalDate().equals(today)) {
					check = true;
					updateAttendence(ls.get(j));
					return ResponseEntity.ok(new MessageResponse("Attendence Details Updated!"));
				}
			}
			if (!check) {
				saveAttendence(user);
				return ResponseEntity.ok(new MessageResponse("Attendence Recorded!"));
			}
		} else {
			saveAttendence(user);
			return ResponseEntity.ok(new MessageResponse("Attendence Recorded!"));
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Error: something went wrong..."));
	}

	public List<Attendence> findListByMonth(List<Attendence> ls, int month) {
		List<Attendence> list = new ArrayList<>();
		for (int i = 0; i < ls.size(); i++) {
			if (ls.get(i).getStartTime().getMonthValue() == month) {
				list.add(ls.get(i));
			}
		}
		return list;
	}

	public List<Attendence> findListByDate(List<Attendence> ls, String date) {
		List<Attendence> list = new ArrayList<>();
		for (int i = 0; i < ls.size(); i++) {
			if (ls.get(i).getStartTime().toLocalDate().toString().equals(date)) {
				list.add(ls.get(i));
			}
		}
		return list;
	}

	public List<Attendence> findAbsentListByMonth(List<Users> users, int month) {
		List<Attendence> ls = findAll();
		List<Attendence> list = new ArrayList<>();

		for (int i = 0; i < ls.size(); i++) {
			if (ls.get(i).getStartTime().getMonthValue() == month) {
				list.add(ls.get(i));
			}
		}
		List<Attendence> absent = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			boolean checks = false;
			for (int j = 0; j < users.size(); j++) {
				if (list.get(i).getUserID().equals(users.get(j).getId())) {
					checks = true;
				}
			}
			if (checks == false) {
				absent.add(list.get(i));
			}
		}
		return absent;
	}

	public List<Attendence> findPresentListByMonth(List<Users> users, int month) {
		List<Attendence> lst = findAll();
		List<Attendence> list1 = new ArrayList<>();
		for (int i = 0; i < lst.size(); i++) {
			if (lst.get(i).getStartTime().getMonthValue() == month) {
				list1.add(lst.get(i));
			}
		}
		List<Attendence> present = new ArrayList<>();
		for (int i = 0; i < list1.size(); i++) {
			boolean checks = false;
			for (int j = 0; j < users.size(); j++) {
				if (list1.get(i).getUserID().equals(users.get(j).getId())) {
					checks = true;
				}
			}
			if (checks == true) {
				present.add(list1.get(i));
			}
		}
		return present;
	}

	public List<Attendence> findLisByDayandYear(List<Attendence> ls, int week, int year) {

		List<Attendence> list = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		String weekdayofyear = simpleDateFormat.format(cal.getTime());

		int i = 0, j = 0;
		for (i = 0; i < 7; i++) {
			LocalDate dateIncrement = LocalDate.parse(weekdayofyear).plusDays(i);
			for (j = 0; j < ls.size(); j++) {
				if (ls.get(j).getStartTime().toLocalDate().equals(dateIncrement)) {
					list.add(ls.get(j));
				}
			}
			j = 0;
		}
		return list;
	}

}
