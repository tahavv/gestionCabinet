import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventDTO, EventsService } from '../events.service';
import { Chart, registerables } from 'chart.js';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-event-detail',
  templateUrl: './event-detail.component.html',
  styleUrls: ['./event-detail.component.scss'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(-20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ])
    ])
  ]
})
export class EventDetailComponent implements OnInit , AfterViewInit{
  eventId!: number;
  eventData?: EventDTO;
  loading = true;
  error: string | null = null;

  // Chart
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;
  participantsChart: Chart | null = null;

  constructor(
    private route: ActivatedRoute,
    private eventsService: EventsService
  ) {
    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    if (this.eventData?.participants?.length) {
      this.buildChart();
    }
  }

  ngOnInit(): void {
    this.eventId = +this.route.snapshot.paramMap.get('id')!;
    this.fetchEvent(this.eventId);
  }

  fetchEvent(id: number) {
    this.loading = true;
    this.eventsService.getEventById(id).subscribe({
      next: (data) => {
        this.eventData = data;
        this.loading = false;
        setTimeout(() => {
          if (this.chartCanvas) {
            this.buildChart();
          }
        });
      },
      error: (err) => {
        this.error = 'Failed to load event details';
        this.loading = false;
      }
    });
  }

  buildChart() {
    console.log(this.chartCanvas);
    if (!this.eventData?.participants?.length || !this.chartCanvas?.nativeElement) return;
    const roleCounts: { [role: string]: number } = {};
    this.eventData.participants.forEach((participant:any) => {
      const role = participant.role || 'UNKNOWN';
      roleCounts[role] = (roleCounts[role] || 0) + 1;
    });

    const labels = Object.keys(roleCounts);
    const counts = Object.values(roleCounts);

    const canvas = this.chartCanvas.nativeElement;
    console.log(canvas);
    if (!canvas) return;

    this.participantsChart?.destroy();
    this.participantsChart = new Chart(canvas, {
      type: 'pie',
      data: {
        labels,
        datasets: [
          {
            label: 'Participants by Role',
            data: counts,
            backgroundColor: ['#4299e1', '#ed8936', '#48bb78', '#ecc94b', '#9f7aea']
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom'
          }
        },
        layout: {
          padding: {
            top: 10,
            bottom: 10,
          },
        },
      }
    });
    console.log(this.participantsChart)
  }

  getRoleBadgeClass(role: string): string {
    switch ((role || '').toLowerCase()) {
      case 'speaker':
      case 'admin':
        return 'bg-blue-100 text-blue-600';
      case 'moderator':
        return 'bg-green-100 text-green-600';
      default:
        return 'bg-gray-100 text-gray-600';
    }
  }
}
