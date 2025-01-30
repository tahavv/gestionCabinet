import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventDTO, EventsService } from '../events.service';

interface GroupedEvents {
  key: string;
  events: EventDTO[];
}

@Component({
  selector: 'app-events-list',
  templateUrl: './events-list.component.html',
  styleUrls: ['./events-list.component.scss']
})
export class EventsListComponent implements OnInit {
  events: EventDTO[] = [];
  filteredEvents: EventDTO[] = [];
  searchText = '';
  dateParam: string | null = null;
  sortBy: string = '';
  groupBy: string = '';
  groupedData: GroupedEvents[] = [];

  constructor(
    private eventsService: EventsService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dateParam = this.route.snapshot.queryParamMap.get('date');
    this.fetchEvents();
  }

  fetchEvents() {
    this.eventsService.getAllEvents().subscribe({
      next: (data) => {
        this.events = data;
        // If there's a date param, filter
        if (this.dateParam) {
          this.filteredEvents = this.events.filter(ev => {
            const startDate = ev.dateDebut.split('T')[0];
            return startDate === this.dateParam;
          });
        } else {
          this.filteredEvents = [...this.events];
        }
        if (this.searchText) {
          this.onSearch();
        }
        this.updateDisplay();
      },
      error: (err) => console.error(err)
    });
  }

  onSearch() {
    const text = this.searchText.toLowerCase();
    this.filteredEvents = this.events.filter(ev =>
      ev.name.toLowerCase().includes(text) ||
      (ev.description && ev.description.toLowerCase().includes(text))
    );
    this.updateDisplay();
  }

  exportToCSV() {
    let csv = 'ID,Name,Location,DateDebut,DateFin\n';
    this.filteredEvents.forEach(ev => {
      csv += `${ev.id},${ev.name},${ev.location},${ev.dateDebut},${ev.dateFin}\n`;
    });
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'events_export.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  reloadAllEvents() {
    this.dateParam = null;
    this.fetchEvents();
  }

  onDelete(id?: number) {
    if (!id) return;
    if (!confirm('Are you sure you want to delete this event?')) return;
    this.eventsService.deleteEvent(id).subscribe({
      next: () => {
        this.events = this.events.filter(e => e.id !== id);
        this.filteredEvents = this.filteredEvents.filter(e => e.id !== id);
        this.updateDisplay();
      },
      error: (err) => alert('Delete failed.')
    });
  }

  getStatus(event: EventDTO): string {
    const now = new Date();
    const dateDebut = new Date(event.dateDebut);
    const dateFin = new Date(event.dateFin);
    if (now < dateDebut) {
      return 'UPCOMING';
    } else if (now >= dateDebut && now <= dateFin) {
      return 'ONGOING';
    } else {
      return 'PAST';
    }
  }

  onSortChange(event: Event) {
    const selectElem = event.target as HTMLSelectElement;
    const value = selectElem.value;
    this.sortBy = value;
    this.updateDisplay();
  }

  onGroupChange(event: Event) {
    const selectElem = event.target as HTMLSelectElement;
    const value = selectElem.value;
    this.groupBy = value;
    this.updateDisplay();
  }

  updateDisplay() {
    let list = [...this.filteredEvents];

    if (this.sortBy) {
      list.sort((a, b) => {
        switch (this.sortBy) {
          case 'name':
            return a.name.localeCompare(b.name);
          case 'dateDebut':
            return a.dateDebut.localeCompare(b.dateDebut);
          case 'dateFin':
            return a.dateFin.localeCompare(b.dateFin);
          default:
            return 0;
        }
      });
    }

    if (!this.groupBy) {
      this.groupedData = [{ key: '', events: list }];
      return;
    }

    const map = new Map<string, EventDTO[]>();

    list.forEach(ev => {
      let groupKey = '';
      if (this.groupBy === 'category') {
        groupKey = ev.category || 'UNKNOWN';
      } else if (this.groupBy === 'organizer') {
        //groupKey = ev.organizerEmail || ('Organizer #' + ev.organizerId);
      }
      if (!map.has(groupKey)) {
        map.set(groupKey, []);
      }
      map.get(groupKey)!.push(ev);
    });

    this.groupedData = Array.from(map.entries()).map(([key, events]) => {
      return { key, events };
    });
  }
}
