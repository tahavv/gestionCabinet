import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventDTO {
  id?: number;
  name: string;
  description?: string;
  category?: string;
  dateDebut: string;
  dateFin: string;
  location: string;
  organizerId?: number;
  roomId?: number;
  participants?:any
  room?:any,
  showMenu?: boolean;
}

@Injectable({ providedIn: 'root' })
export class EventsService {
  private baseUrl = 'http://localhost:8083/api/events';

  constructor(private http: HttpClient) {}

  getAllEvents(): Observable<EventDTO[]> {
    return this.http.get<EventDTO[]>(this.baseUrl);
  }

  getEventById(id: number): Observable<EventDTO> {
    return this.http.get<EventDTO>(`${this.baseUrl}/${id}`);
  }

  createEvent(event: Partial<EventDTO>): Observable<EventDTO> {
    return this.http.post<EventDTO>(`${this.baseUrl}/add`, event);
  }

  updateEvent(id: number, event: Partial<EventDTO>): Observable<EventDTO> {
    return this.http.put<EventDTO>(`${this.baseUrl}/${id}`, event);
  }

  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }


  getRooms(): Observable<any> {
    return this.http.get<any>('http://localhost:8083/api/rooms');
  }

  // advanced features: export to CSV, filter, etc. can be separate endpoints or handled client side
}
