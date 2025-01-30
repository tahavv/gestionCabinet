package com.clinique.clinic.service;

import com.clinique.clinic.model.*;
import com.clinique.clinic.model.dto.DoctorAvailabilityDTO;
import com.clinique.clinic.model.enums.AppointmentStatus;
import com.clinique.clinic.model.enums.Type;
import com.clinique.clinic.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRecurringScheduleRepository recurringRepo;
    @Autowired
    private DoctorScheduleOverrideRepository overrideRepo;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private MedicalRecordService medicalRecordService;

    public List<LocalDateTime> findAvailableSlotsForInterval(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        List<LocalDateTime> potentialSlots = generatePotentialSlots(doctor, start, end);
        List<Appointment> existingAppointments =
                appointmentRepository.findByDoctorAndDateDebutBetween(doctor, start, end);

        return removeOverlappingSlots(potentialSlots, existingAppointments);
    }

    public List<DoctorAvailabilityDTO> findAllAvailableSlotsForAllDoctors(
            LocalDateTime start,
            LocalDateTime end
    ) {
        List<Doctor> allDoctors = doctorRepository.findAll();
        List<DoctorAvailabilityDTO> results = new ArrayList<>();
        for (Doctor doc : allDoctors) {
            List<LocalDateTime> freeSlots = findAvailableSlotsForInterval(doc.getId(), start, end);
            DoctorAvailabilityDTO dto = new DoctorAvailabilityDTO(
                    doc,
                    doc.getFullName(),
                    freeSlots
            );
            results.add(dto);
        }

        return results;
    }

    private List<LocalDateTime> generatePotentialSlots(
            Doctor doctor,
            LocalDateTime start,
            LocalDateTime end
    ) {
        List<Interval> finalIntervals = new ArrayList<>();

        LocalDate startDay = start.toLocalDate();
        LocalDate endDay   = end.toLocalDate();

        List<DoctorRecurringSchedule> recurringList = recurringRepo.findByDoctor(doctor);
        List<DoctorScheduleOverride> overrideList =
                overrideRepo.findByDoctorAndDateBetween(doctor, startDay, endDay);

        Map<LocalDate, List<DoctorScheduleOverride>> overrideByDate = new HashMap<>();
        for (DoctorScheduleOverride ov : overrideList) {
            overrideByDate.computeIfAbsent(ov.getDate(), k -> new ArrayList<>())
                    .add(ov);
        }

        for (LocalDate d = startDay; !d.isAfter(endDay); d = d.plusDays(1)) {
            List<Interval> dailyIntervals = buildDailyIntervalsFromRecurring(recurringList, d);
            List<DoctorScheduleOverride> dailyOverrides = overrideByDate.getOrDefault(d, new ArrayList<>());
            List<Interval> afterOverride = applyOverrides(dailyIntervals, dailyOverrides, d);
            List<Interval> clipped = clipToRange(afterOverride, d, start, end);
            finalIntervals.addAll(clipped);
        }

        return intervalsToSlots(finalIntervals, Duration.ofHours(1));
    }

    private List<Interval> buildDailyIntervalsFromRecurring(
            List<DoctorRecurringSchedule> recurringList,
            LocalDate date
    ) {
        DayOfWeek dow = date.getDayOfWeek();
        List<Interval> result = new ArrayList<>();
        List<DoctorRecurringSchedule> relevant = recurringList.stream()
                .filter(r -> r.getDayOfWeek() == dow)
                .collect(Collectors.toList());

        for (DoctorRecurringSchedule r : relevant) {
            LocalDateTime startDt = LocalDateTime.of(date, r.getStartTime());
            LocalDateTime endDt   = LocalDateTime.of(date, r.getEndTime());
            if (endDt.isAfter(startDt)) {
                result.add(new Interval(startDt, endDt));
            }
        }
        return result;
    }

    private List<Interval> applyOverrides(
            List<Interval> dailyIntervals,
            List<DoctorScheduleOverride> dailyOverrides,
            LocalDate date
    ) {
        List<Interval> result = new ArrayList<>(dailyIntervals);
        for (DoctorScheduleOverride ov : dailyOverrides) {
            LocalTime st = ov.getStartTime();
            LocalTime et = ov.getEndTime();
            if (st == null || et == null) continue;

            LocalDateTime overrideStart = LocalDateTime.of(date, st);
            LocalDateTime overrideEnd   = LocalDateTime.of(date, et);

            if (!overrideEnd.isAfter(overrideStart)) continue;

            if (ov.isAvailable()) {
                result.add(new Interval(overrideStart, overrideEnd));
            } else {
                result = removeBlock(result, new Interval(overrideStart, overrideEnd));
            }
        }
        return mergeOverlappingIntervals(result);
    }

    private List<Interval> removeBlock(List<Interval> intervals, Interval blockToRemove) {
        List<Interval> output = new ArrayList<>();
        for (Interval in : intervals) {
            if (!overlaps(in, blockToRemove)) {
                output.add(in);
            } else {
                // split
                if (blockToRemove.getStart().isAfter(in.getStart()) && blockToRemove.getStart().isBefore(in.getEnd())) {
                    output.add(new Interval(in.getStart(), blockToRemove.getStart()));
                }
                if (blockToRemove.getEnd().isBefore(in.getEnd()) && blockToRemove.getEnd().isAfter(in.getStart())) {
                    output.add(new Interval(blockToRemove.getEnd(), in.getEnd()));
                }
            }
        }
        return output;
    }

    private boolean overlaps(Interval i1, Interval i2) {
        return i1.getStart().isBefore(i2.getEnd()) && i1.getEnd().isAfter(i2.getStart());
    }

    private List<Interval> mergeOverlappingIntervals(List<Interval> intervals) {
        List<Interval> sorted = intervals.stream()
                .sorted(Comparator.comparing(Interval::getStart))
                .collect(Collectors.toList());
        List<Interval> merged = new ArrayList<>();
        for (Interval current : sorted) {
            if (merged.isEmpty()) {
                merged.add(current);
            } else {
                Interval last = merged.get(merged.size() - 1);
                if (overlaps(last, current)) {
                    LocalDateTime newStart = last.getStart();
                    LocalDateTime newEnd = (last.getEnd().isAfter(current.getEnd()))
                            ? last.getEnd() : current.getEnd();
                    merged.set(merged.size() - 1, new Interval(newStart, newEnd));
                } else {
                    merged.add(current);
                }
            }
        }
        return merged;
    }

    private List<Interval> clipToRange(List<Interval> intervals, LocalDate date,
                                       LocalDateTime start, LocalDateTime end) {
        List<Interval> result = new ArrayList<>();
        for (Interval in : intervals) {
            LocalDateTime s = in.getStart();
            LocalDateTime e = in.getEnd();
            if (e.isBefore(start) || s.isAfter(end)) {
                continue;
            }
            LocalDateTime clippedStart = s.isBefore(start) ? start : s;
            LocalDateTime clippedEnd   = e.isAfter(end) ? end : e;
            if (clippedEnd.isAfter(clippedStart)) {
                result.add(new Interval(clippedStart, clippedEnd));
            }
        }
        return result;
    }

    private List<LocalDateTime> intervalsToSlots(List<Interval> intervals, Duration slotSize) {
        List<LocalDateTime> slots = new ArrayList<>();
        for (Interval in : intervals) {
            LocalDateTime cursor = in.getStart();
            while (!cursor.plus(slotSize).isAfter(in.getEnd())) {
                slots.add(cursor);
                cursor = cursor.plus(slotSize);
            }
        }
        return slots;
    }

    private List<LocalDateTime> removeOverlappingSlots(List<LocalDateTime> potentialSlots, List<Appointment> existingAppts) {
        List<LocalDateTime> result = new ArrayList<>();
        for (LocalDateTime slot : potentialSlots) {
            LocalDateTime slotEnd = slot.plusHours(1);
            boolean overlap = false;
            for (Appointment ap : existingAppts) {
                if (ap.getDateDebut().isBefore(slotEnd) && ap.getDateFin().isAfter(slot)) {
                    overlap = true;
                    break;
                }
            }
            if (!overlap) {
                result.add(slot);
            }
        }
        return result;
    }

    public Appointment createAppointment(Long doctorId,
                                         Long patientId,
                                         LocalDateTime dateDebut,
                                         BigDecimal cost) {

        LocalDateTime dateFin = dateDebut.plusHours(1);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        MedicalRecord record = medicalRecordService.findByPatientId(patientId);
        if (record == null) {
            record = new MedicalRecord();
            record.setPatient(patient);
            record.setNotes(new ArrayList<>());
            medicalRecordService.save(record);
        }

        List<Appointment> existing = appointmentRepository.findByDoctorAndDateDebutBetween(doctor, dateDebut, dateFin);
        for (Appointment ap : existing) {
            boolean overlaps = ap.getDateDebut().isBefore(dateFin)
                    && ap.getDateFin().isAfter(dateDebut);
            if (overlaps) {
                throw new RuntimeException("Slot not available");
            }
        }

        Appointment ap = new Appointment();
        ap.setDoctor(doctor);
        ap.setPatient(patient);
        ap.setDateDebut(dateDebut);
        ap.setDateFin(dateFin);
        ap.setPaid(false);
        ap.setCost(cost != null ? cost : BigDecimal.ZERO);
        ap.setType(Type.GENERAL_CONSULTATION);
        ap.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(ap);

        return saved;
    }

    public List<Appointment> findByPatient(Long id) {
        return appointmentRepository.findByPatientId(id);
    }

    private static class Interval {
        private LocalDateTime start;
        private LocalDateTime end;
        public Interval(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }
        public LocalDateTime getStart() { return start; }
        public LocalDateTime getEnd() { return end; }
    }

    public List<DoctorAvailabilityDTO> findAllAvailableSlotsBySpecialty(
            String specialty,
            LocalDateTime start,
            LocalDateTime end
    ) {
        List<Doctor> filteredDocs = doctorRepository.findBySpecialty(specialty);
        List<DoctorAvailabilityDTO> results = new ArrayList<>();
        for (Doctor doc : filteredDocs) {
            List<LocalDateTime> freeSlots = findAvailableSlotsForInterval(doc.getId(), start, end);
            results.add(new DoctorAvailabilityDTO(doc, doc.getFullName(), freeSlots));
        }
        return results;
    }
}
