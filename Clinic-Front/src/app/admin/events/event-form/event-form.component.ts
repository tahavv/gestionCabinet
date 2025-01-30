import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EventsService, EventDTO } from '../events.service';
import { UserService } from '../../users/users.service';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.scss'],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('staggerForm', [
      transition(':enter', [
        query('.form-field', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger('50ms', [
            animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ])
      ])
    ])
  ]
})
export class EventFormComponent implements OnInit {
  eventForm!: FormGroup;
  isEdit = false;
  eventId?: number;
  loading = false;
  submitAttempted = false;
  successMessage = '';
  errorMessage = '';

  categories: string[] = ['MEETING', 'WORKSHOP', 'CONFERENCE', 'TRAINING', 'OTHER'];
  // All users/rooms from backend
  users: any[] = [];
  rooms: any[] = [];

  // search controls & filtered arrays
  organizerSearchControl = new FormControl('');
  filteredUsers: any[] = [];
  roomSearchControl = new FormControl('');
  filteredRooms: any[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private eventsService: EventsService,
    private userService: UserService,
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadInitialData();
    this.setupAutocomplete();
  }

  private initializeForm(): void {
    this.isEdit = this.route.snapshot.data['isEdit'] ?? false;
    this.eventId = +this.route.snapshot.paramMap.get('id')!;

    this.eventForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.maxLength(500)]],
      location: ['', [Validators.required, Validators.minLength(3)]],
      category: ['', Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['', [Validators.required, this.dateFinValidator.bind(this)]],
      organizerId: [null, Validators.required],  // final chosen ID
      roomId: [null, Validators.required]        // final chosen ID
    });

    this.eventForm.valueChanges.subscribe(() => {
      if (this.submitAttempted) {
        this.validateAllFormFields();
      }
    });
  }

  private dateFinValidator(control: FormControl): {[key: string]: any} | null {
    const dateFin = control.value;
    const dateDebut = this.eventForm?.get('dateDebut')?.value;

    if (dateDebut && dateFin && new Date(dateFin) <= new Date(dateDebut)) {
      return { 'dateFinInvalid': true };
    }
    return null;
  }

  private validateAllFormFields(): void {
    Object.keys(this.eventForm.controls).forEach(field => {
      const control = this.eventForm.get(field);
      control?.markAsTouched();
    });
  }

  private async loadInitialData(): Promise<void> {
    this.loading = true;
    try {
      await Promise.all([
        this.loadUsers(),
        this.loadRooms()
      ]);
      if (this.isEdit && this.eventId) {
        await this.loadEvent(this.eventId);
      }
    } catch (error) {
      this.errorMessage = 'Failed to load data. Please try again.';
    } finally {
      this.loading = false;
    }
  }

  loadEvent(id: number): Promise<void> {
    return new Promise((resolve, reject) => {
      this.eventsService.getEventById(id).subscribe({
        next: (ev) => {
          // Patch form
          this.eventForm.patchValue({
            name: ev.name,
            description: ev.description,
            location: ev.location,
            category: ev.category,
            dateDebut: ev.dateDebut,
            dateFin: ev.dateFin,
            organizerId: ev.organizerId,
            roomId: ev.roomId
          });
          // Also set the search text for the dropdowns
          const orgUser = this.users.find(u => u.id === ev.organizerId);
          if (orgUser) this.organizerSearchControl.setValue(orgUser.email, { emitEvent: false });

          const theRoom = this.rooms.find(r => r.id === ev.roomId);
          if (theRoom) this.roomSearchControl.setValue(theRoom.name, { emitEvent: false });

          resolve();
        },
        error: (err) => {
          console.error('Error loading event', err);
          reject(err);
        }
      });
    });
  }

  loadUsers(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.userService.getUsers().subscribe({
        next: (data) => {
          this.users = data;
          this.filteredUsers = data;  // initially all
          resolve();
        },
        error: (err) => {
          console.error(err);
          reject(err);
        }
      });
    });
  }

  loadRooms(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.eventsService.getRooms().subscribe({
        next: (data) => {
          this.rooms = data;
          this.filteredRooms = data;
          resolve();
        },
        error: (err) => {
          console.error(err);
          reject(err);
        }
      });
    });
  }

  private setupAutocomplete() {
    // Organizer search
    this.organizerSearchControl.valueChanges.subscribe(value => {
      if (!value) {
        this.filteredUsers = this.users;
        // reset form if user cleared
        this.eventForm.patchValue({ organizerId: null }, { emitEvent: false });
        return;
      }
      const filterValue = value.toLowerCase();
      this.filteredUsers = this.users.filter(user => user.email.toLowerCase().includes(filterValue));
    });

    // Room search
    this.roomSearchControl.valueChanges.subscribe(value => {
      if (!value) {
        this.filteredRooms = this.rooms;
        this.eventForm.patchValue({ roomId: null }, { emitEvent: false });
        return;
      }
      const filterValue = value.toLowerCase();
      this.filteredRooms = this.rooms.filter(r => r.name.toLowerCase().includes(filterValue));
    });
  }

  selectOrganizer(selectedEmail: string) {
    const found = this.users.find(u => u.email === selectedEmail);
    if (found) {
      this.eventForm.patchValue({ organizerId: found.id }, { emitEvent: false });
    }
  }

  selectRoom(selectedName: string) {
    const found = this.rooms.find(r => r.name === selectedName);
    if (found) {
      this.eventForm.patchValue({ roomId: found.id }, { emitEvent: false });
    }
  }

  async onSubmit(): Promise<void> {
    this.submitAttempted = true;
    if (this.eventForm.invalid) {
      this.validateAllFormFields();
      return;
    }

    this.loading = true;
    const formValue: EventDTO = this.eventForm.value;
    // formValue.organizerId and roomId should already be set
    try {
      if (this.isEdit && this.eventId) {
        await this.eventsService.updateEvent(this.eventId, formValue).toPromise();
        this.successMessage = 'Event updated successfully!';
      } else {
        await this.eventsService.createEvent(formValue).toPromise();
        this.successMessage = 'Event created successfully!';
      }

      // Navigate away after short delay
      setTimeout(() => {
        this.router.navigate(['/admin/events']);
      }, 1500);
    } catch (error) {
      console.error(error);
      this.errorMessage = 'Failed to save event. Please try again.';
    } finally {
      this.loading = false;
    }
  }

  onCancel() {
    this.router.navigate(['/admin/events']);
  }

  // Helpers
  isInvalid(controlName: string): boolean {
    const ctrl = this.eventForm.get(controlName);
    return !!(ctrl && ctrl.invalid && ctrl.touched);
  }

  getErrorMessage(controlName: string): string {
    const ctrl = this.eventForm.get(controlName);
    if (!ctrl?.errors) return '';
    if (ctrl.errors['required']) return `${controlName} is required`;
    if (ctrl.errors['minlength']) return `${controlName} must be at least ${ctrl.errors['minlength'].requiredLength} chars`;
    if (ctrl.errors['maxlength']) return `${controlName} can't exceed ${ctrl.errors['maxlength'].requiredLength} chars`;
    if (ctrl.errors['dateFinInvalid']) return 'End date must be after start date';
    return 'Invalid field';
  }
}
