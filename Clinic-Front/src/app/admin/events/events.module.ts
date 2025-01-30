import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventsRoutingModule } from './events-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {EventsListComponent} from "./events-list/events-list.component";
import {EventFormComponent} from "./event-form/event-form.component";
import {FullCalendarModule} from "@fullcalendar/angular";
import { CalendarComponent } from './calendar/calendar.component';
import { EventDetailComponent } from './event-detail/event-detail.component';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
// Angular Material modules
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSelectModule } from '@angular/material/select'; // if using mat-select
import { MatIconModule } from '@angular/material/icon';


@NgModule({
  declarations: [
    EventsListComponent,
    EventFormComponent,
    CalendarComponent,
    EventDetailComponent,
  ],
  imports: [
    CommonModule,
    EventsRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    FullCalendarModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatIconModule,

  ]
})
export class EventsModule {}
