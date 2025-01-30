import { Component, OnInit } from '@angular/core';
import { FormControl } from "@angular/forms";
import { AppoinmentsService } from "../../shared/services/appoinments.service";
import { debounceTime, distinctUntilChanged } from "rxjs";

@Component({
  selector: 'app-doctor-reservation',
  templateUrl: './doctor-reservation.component.html',
  styleUrls: ['./doctor-reservation.component.scss']
})
export class DoctorReservationComponent implements OnInit {
  specialties = ['Cardiology', 'Neurology', 'Pediatrics', 'Orthopedics'];
  selectedSpecialty = 'all';
  searchControl = new FormControl('');
  selectedDate: string = '';
  availabilityData: any[] = [];
  filteredAvailability: any[] = [];
  startDateTime: string = '';
  endDateTime: string = '';
  showScheduleModal = false;
  selectedDoctor: any | null = null;
  calendarDates: Date[] = [];

  constructor(private appointmentsService: AppoinmentsService) {}

  ngOnInit(): void {
    const now = new Date();
    this.startDateTime = this.toIsoDateTimeString(now);
    const oneMonthLater = new Date(now.getTime());
    oneMonthLater.setMonth(oneMonthLater.getMonth() + 1);
    this.endDateTime = this.toIsoDateTimeString(oneMonthLater);

    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((searchTerm: any) => {
        this.filterAvailability(searchTerm || '');
      });
    this.generateCalendarDates(28);
    this.loadAllAvailability();
  }

  private toIsoDateTimeString(d: Date): string {
    // e.g. "2025-01-13T09:00:00"
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const mins = String(d.getMinutes()).padStart(2, '0');
    const secs = '00';
    return `${year}-${month}-${day}T${hours}:${mins}:${secs}`;
  }

  // Load availability for ALL doctors
  loadAllAvailability(): void {
    this.appointmentsService
      .getAllDoctorsAvailability(this.startDateTime, this.endDateTime)
      .subscribe((data: any[]) => {
        // data is an array of { doctor, doctorName, slots[] }
        this.availabilityData = data;
        this.filteredAvailability = [...data];
      }, err => {
        console.error('Error loading all doctors availability', err);
      });
  }

  // Load availability for the selected specialty
  loadAvailabilityBySpecialty(): void {
    if (this.selectedSpecialty === 'all') {
      this.loadAllAvailability();
    } else {
      this.appointmentsService
        .getDoctorsAvailabilityBySpecialty(this.selectedSpecialty, this.startDateTime, this.endDateTime)
        .subscribe((data: any[]) => {
          this.availabilityData = data;
          this.filteredAvailability = [...data];
        }, err => {
          console.error('Error loading specialty availability', err);
        });
    }
  }

  filterBySpecialty(): void {
    // triggered by the <select> change
    this.loadAvailabilityBySpecialty();
  }

  filterByDate(): void {
    // If you want to do more advanced date filtering beyond the 1-month range
    // or re-check the server, you can do it here.
  }

  filterAvailability(searchTerm: string): void {
    this.filteredAvailability = this.availabilityData.filter(item => {
      // e.g. item.doctorName might be "Dr. John Smith"
      const nameLower = item.doctorName?.toLowerCase() || '';
      return nameLower.includes(searchTerm.toLowerCase());
    });
  }

  // Return the next 'count' slots as Date objects
  getNextAvailableSlots(slotStrings: string[], count: number): Date[] {
    return slotStrings.slice(0, count).map(s => new Date(s));
  }

  // Book a slot
  bookAppointment(doctorItem: any, slotIso: string) {
    const dateObj = new Date(slotIso);
    console.log(`Booking with ${doctorItem.doctorName} at ${dateObj}`);
    // You might call a "createAppointment" endpoint here, passing doctorItem.doctor.id, etc.
  }

  viewFullSchedule(doctorItem: any) {
    this.selectedDoctor = doctorItem;
    this.showScheduleModal = true;
  }

  viewDoctorProfile(doctorItem: any) {
    console.log(`Viewing profile for Dr. ${doctorItem.doctorName}`);
    // Navigate or open a profile modal, etc.
  }

  // Create a list of dates for the next 'daysCount' days (28 by default)
  generateCalendarDates(daysCount: number) {
    const now = new Date();
    this.calendarDates = Array.from({ length: daysCount }, (_, i) => {
      const d = new Date(now.getTime());
      d.setDate(now.getDate() + i);
      return d;
    });
  }

  hasAvailabilityOnDate(doctorItem: any, date: Date): boolean {
    // check if doctorItem.slots has at least one slot matching 'date'
    const dateStr = date.toDateString();
    return doctorItem.slots?.some((slotString: string) => {
      return new Date(slotString).toDateString() === dateStr;
    });
  }

  getTimeSlots(doctorItem: any, date: Date): Date[] {
    // all slots that match this date
    return doctorItem.slots
      .map((s: string) => new Date(s))
      .filter((d: Date) => d.toDateString() === date.toDateString());
  }

  closeModal() {
    this.showScheduleModal = false;
    this.selectedDoctor = null;
  }
}
