import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { CalendarOptions, EventClickArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import {EventDTO, EventsService} from "../events.service"; // for dateClick

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {
  // The calendar config
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth'
    },
    selectable: true,
    dateClick: this.handleDateClick.bind(this),
    eventClick: this.handleEventClick.bind(this),
    events: []
  };
  calendarDescription = `
    Welcome to the Event Calendar!
    Click on a date to see events happening that day,
    or click an event to view more details.
  `;

  constructor(
    private eventsService: EventsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchEvents();
  }

  fetchEvents() {
    this.eventsService.getAllEvents().subscribe({
      next: (data: EventDTO[]) => {
        const calendarEvents = data.map(ev => ({
          id: String(ev.id),
          title: ev.name,
          start: ev.dateDebut,
          end: ev.dateFin,
          extendedProps: {
            description: ev.description,
            location: ev.location,
            category: ev.category
          }
        }));
        this.calendarOptions.events = calendarEvents;
      },
      error: (err) => console.error('Error loading events:', err)
    });
  }

  handleDateClick(arg: any) {
    const dateStr = arg.dateStr; // e.g. '2025-01-15'
    this.router.navigate(['/admin/events'], { queryParams: { date: dateStr }});
  }

  handleEventClick(clickArg: EventClickArg) {
    const eventDate = clickArg.event.startStr;
    const justDate = eventDate.split('T')[0];
    this.router.navigate(['/admin/events'], { queryParams: { date: justDate }});
  }
}
