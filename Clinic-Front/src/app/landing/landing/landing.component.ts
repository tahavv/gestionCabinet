import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {FormControl} from "@angular/forms";
import {AppoinmentsService} from "../../shared/services/appoinments.service";
import {debounceTime, distinctUntilChanged} from "rxjs";

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss']
})
export class LandingComponent implements OnInit {
  services = [
    {
      name: 'General Checkup',
      description: 'Comprehensive health evaluation and preventive care',
      icon: 'fa-user-md'
    },
    {
      name: 'Specialized Care',
      description: 'Expert treatment in various medical specialties',
      icon: 'fa-stethoscope'
    },
    {
      name: 'Lab Services',
      description: 'Advanced diagnostic and laboratory testing',
      icon: 'fa-flask'
    }
  ];
  specialties = ['Cardiology', 'Neurology', 'Pediatrics', 'Orthopedics'];
  doctors: any[] = [];
  filteredDoctors: any[] = [];
  doctorAvailability: { [key: number]: any } = {};
  searchControl = new FormControl('');
  selectedSpecialty = 'all';
  selectedDate: string = '';
  calendarDates: Date[] = [];
  showScheduleModal = false;
  selectedDoctor: any ;

  constructor(private appointmentsService: AppoinmentsService) {
    this.generateCalendarDates();
  }

  ngOnInit() {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.filterDoctors(searchTerm || '');
    });

    // Load initial data
    //this.loadDoctors();
    //this.loadAvailability();
  }

  loadDoctors() {
    this.appointmentsService.getAppointments()
      .subscribe((doctors:any) => {
        this.doctors = doctors;
        this.filteredDoctors = doctors;
      });
  }

  loadAvailability() {
    this.appointmentsService.getAppointments()
      .subscribe((availability:any) => {
        availability.forEach((a:any) => {
          this.doctorAvailability[a.doctorId.id] = a;
        });
      });
  }

  filterDoctors(searchTerm: string) {
    this.filteredDoctors = this.doctors.filter(doctor =>
      doctor.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
      (this.selectedSpecialty === 'all' || doctor.specialty === this.selectedSpecialty)
    );
  }

  filterBySpecialty() {
    this.appointmentsService.setSelectedSpecialty(this.selectedSpecialty);
    this.loadDoctors();
  }

  filterByDate() {
    // Implement date filtering logic
  }

  generateCalendarDates() {
    const today = new Date();
    this.calendarDates = Array.from({length: 28}, (_, i) => {
      const date = new Date();
      date.setDate(today.getDate() + i);
      return date;
    });
  }

  hasAvailability(doctorId: number | undefined, date: Date): boolean {
    if (!doctorId) return false;
    const availability = this.doctorAvailability[doctorId];
    if (!availability) return false;

    return availability.dateSlots.some((slot:any) =>
      new Date(slot).toDateString() === date.toDateString()
    );
  }

  getTimeSlots(doctorId: number | undefined, date: Date): Date[] {
    if (!doctorId) return [];
    const availability = this.doctorAvailability[doctorId];
    if (!availability) return [];

    return availability.dateSlots
      .filter((slot:any) => new Date(slot).toDateString() === date.toDateString())
      .map((slot:any) => new Date(slot));
  }

  viewFullSchedule(doctor: any) {
    this.selectedDoctor = doctor;
    this.showScheduleModal = true;
  }

  bookAppointment(doctor: any | null, slot: Date) {
    if (!doctor) return;
    console.log(`Booking appointment with Dr. ${doctor.name} at ${slot}`);
  }

  scrollToBooking() {
    document.getElementById('booking')?.scrollIntoView({ behavior: 'smooth' });
  }

}
